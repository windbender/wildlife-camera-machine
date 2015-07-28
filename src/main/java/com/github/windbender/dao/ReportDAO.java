package com.github.windbender.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.core.IdHist;
import com.github.windbender.core.IdHistEntry;
import com.github.windbender.core.ImageRec;
import com.github.windbender.core.Limiter;
import com.github.windbender.core.NV;
import com.github.windbender.core.NameHist;
import com.github.windbender.core.NameHistEntry;
import com.github.windbender.core.Series;
import com.github.windbender.core.SpeciesCount;
import com.github.windbender.core.TypeOfDay;
import com.github.windbender.domain.Identification;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.Species;

public class ReportDAO {

	Logger log = LoggerFactory.getLogger(ReportDAO.class);

	SessionFactory sessionFactory;
	private EventDAO eventDAO;
	private IdentificationDAO identificationDAO;
	public ReportDAO(SessionFactory sessionFactory, EventDAO eventDAO,IdentificationDAO identificationDAO) {
		this.sessionFactory = sessionFactory;
		this.eventDAO = eventDAO;
		this.identificationDAO = identificationDAO;
	}
	
	
	
	public List<Long> makeTopSpeciesIdList(Integer limitNumber,long project_id) {
		String speciesSQL = "select count(*) as cnt,  species_id from (   	" +
				"select species_id,event_start_time,number   	from identifications,events, cameras where cameras.id=events.camera_id and cameras.project_id = "+project_id+" and identifications.image_event_id=events.id group by image_event_id   " +
						") x, species s where x.species_id = s.id group by species_id  order by cnt desc";		
		SQLQuery sqlQuery = this.sessionFactory.getCurrentSession().createSQLQuery(speciesSQL);
        Query query = sqlQuery;
        List<Object[]> result = query.list();
        List<Long> l = new ArrayList<Long>();
        int count =0;
        for(Object[] ar: result) {
        	Long id = ((Integer)ar[1]).longValue();
        	Integer cnt = (Integer)ar[1];
        	if(cnt > 0) {
	        	l.add(id);
	        	count++;
	        	if(limitNumber != null) {
	        		if(count > limitNumber) break;
	        	}
        	}
        }
        return l;
	}
	public List<StringSeries> makeBySpecies(Limiter limits) {
		String innerSQL = limits.makeSQL();
		String speciesSQL = "select count(*) as cnt,  common_name from (   	" +
				"select species_id,event_start_time,number from identifications,events, cameras where cameras.id=events.camera_id and cameras.project_id = "+limits.getProjectId()+" and identifications.image_event_id=events.id "+innerSQL+" group by image_event_id   " +
						") x, species s where x.species_id = s.id group by species_id  order by cnt desc";		

		return doSQLtoSeriesString(speciesSQL);

	}

	private List<StringSeries> doSQLtoSeriesString(String sql) {
		SQLQuery sqlQuery = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
        Query query = sqlQuery;
        List<Object[]> result = query.list();
        
        StringSeries s = new StringSeries();
        for(Object[] ar: result) {
        	s.addPoint((String)ar[1], (BigInteger)ar[0]);
        }
        s.setSeriesName("by species");
		List<StringSeries> l = new ArrayList<StringSeries>();
		l.add(s);
        return l;
	}


	private List<NV> doSQL(String sql) {
		List<NV> l = new ArrayList<NV>();
        SQLQuery sqlQuery = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
        Query query = sqlQuery;
        List<Object[]> result = query.list();
        for(Object[] ar: result) {
        	NV nv = new NV();
        	nv.val = (BigInteger)ar[0];
        	nv.name = ar[1].toString();
        	l.add(nv);
        }
        return l;
	}
	
	private Series doSQLtoSeries(String sql) {
		SQLQuery sqlQuery = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
        Query query = sqlQuery;
        List<Object[]> result = query.list();
        
        Series s = new Series();
        for(Object[] ar: result) {
        	s.addPoint( (Integer)ar[1],(BigDecimal)ar[0]);
        }
        return s;
	}
	private Series doSQLtoSeriesFromDate(String sql, Interval interval) {
		SQLQuery sqlQuery = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
		Date st = new Date(interval.getStartMillis());
		Date en = new Date(interval.getEndMillis());
		sqlQuery.setParameter(0, en).setParameter(1, st);
        Query query = sqlQuery;
        List<Object[]> result = query.list();
        
        Series s = new Series();
        for(Object[] ar: result) {
        	DateTime dateTime = new DateTime((Date)ar[1],DateTimeZone.UTC);
			Long dt = dateTime.getMillis();
			BigDecimal val = (BigDecimal)ar[0];
			s.addPoint( dt,val);
        }
        return s;
	}

	public List<Series> makeByHour(Limiter limits) {
		String innerSQL = limits.makeSQL();
//		String sql = "select count(*) as cnt, hour(event_start_time) as hour from (  select species_id,event_start_time    from identifications,events   where identifications.image_event_id=events.id "+innerSQL+" group by image_event_id  ) x group by hour(event_start_time)";
		String sql = "select sum(num), hours from (select h.hours, ifnull(number,0) as num from hours h left join (       " +
				"select number,species_id,CONVERT_TZ(event_start_time,'+00:00','-08:00') as sttime    from identifications,events, cameras where cameras.id=events.camera_id and cameras.project_id = "+limits.getProjectId()+" and identifications.image_event_id=events.id "+innerSQL+" group by image_event_id     " +
						") x on h.hours = hour(sttime)) y group by hours order by hours";
		log.info("it looks like we're going to run this SQL "+sql);
		Series s = doSQLtoSeries(sql);
		s.setSeriesName("by hour");
		List<Series> l = new ArrayList<Series>();
		l.add(s);
		return l;
	}

	public List<Series> makeByDay(Limiter limits) {
		String innerSQL = limits.makeSQL();
		String sql = "select sum(num),dates from (   select dates,ifnull(sttime,0) as strt,ifnull(number,0) as num from dates d left join ( " +
						"select species_id,CONVERT_TZ(event_start_time,'+00:00','-08:00') as sttime, number from identifications, events, cameras where cameras.id=events.camera_id and cameras.project_id = "+limits.getProjectId()+" and identifications.image_event_id=events.id "+innerSQL+" group by image_event_id      " +
					  ") x on date(d.dates) = date(x.sttime) where d.dates < ? and d.dates > ? ) y group by dates order by dates";
		
//		String sql = "select count(*) as cnt, date(event_start_time) from (  select species_id,event_start_time    from identifications,events   where identifications.image_event_id=events.id   "+innerSQL+" group by image_event_id  ) x group by date(event_start_time)";
		log.info("it looks like we're going to run this SQL "+sql);
		log.info("the interval will be "+limits.getTimeInterval());
		Series s = doSQLtoSeriesFromDate(sql, limits.getTimeInterval());
		s.setSeriesName("by day");
		List<Series> l = new ArrayList<Series>();
		l.add(s);
		return l;
		
	}



	public List<Long> makeImageEvents(Limiter limits) {
		String innerSQL = limits.makeSQL();
		String sql = "select imageTime, e.id from identifications ids, events e,images i, cameras c where e.camera_id=c.id and c.project_id="+limits.getProjectId()+" and ids.image_event_id=e.id and e.id=i.event_id "+innerSQL+"group by e.id order by imageTime;";
		
		SQLQuery sqlQuery = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
        Query query = sqlQuery;
        List<Object[]> result = query.list();
        List<Long> l = new ArrayList<Long>();
		for(Object[] ar: result) {
        	Long event_id = ((Integer)ar[1]).longValue();
        	l.add(event_id);
        }
        return l;
	}



	public List<SpeciesCount> findCategorizationData(ImageEvent e) {
		long id = e.getId();
		String sql = "select species_id, count(*) from identifications where image_event_id = "+id+" group by species_id order by count(*) desc";
		SQLQuery sqlQuery = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
        Query query = sqlQuery;
        List<Object[]> result = query.list();
        List<SpeciesCount> l = new ArrayList<SpeciesCount>();
        for(Object[] ar: result) {
        	long species_id = ((Integer)ar[0]).longValue();
        	Integer count = ((BigInteger)ar[1]).intValue();
            Species s =  (Species) this.sessionFactory.getCurrentSession().get(Species.class, checkNotNull(species_id));
            l.add(new SpeciesCount(s,count));
        }
		return l;
	}



	public List<ImageRec> makeImageRecsOld(Limiter limits) {
		long start = System.currentTimeMillis();
		List<ImageRec> lout = new ArrayList<ImageRec>();
		
		String innerSQL = limits.makeSQL();
		String sql = "select imageTime, e.id from identifications ids, events e,images i, cameras c where e.camera_id=c.id and c.project_id="+limits.getProjectId()+" and ids.image_event_id=e.id and e.id=i.event_id "+innerSQL+"group by e.id order by imageTime;";
		
		SQLQuery sqlQuery = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
        Query query = sqlQuery;
        List<Object[]> result = query.list();
		for(Object[] ar: result) {
        	Long event_id = ((Integer)ar[1]).longValue();
        	ImageEvent ie = eventDAO.findById(event_id);
			for(ImageRecord ir : ie.getImageRecords()) {
				ir.getId();
			}
			List<Identification> ids = identificationDAO.findAllIdentificationForEvent(ie);
			IdHist idHist = new IdHist(ids);
			NameHist nameHist = new NameHist(ids);
			ImageRec irec = new ImageRec(ie,idHist,nameHist);
			lout.add(irec);
        }
		long end = System.currentTimeMillis();
		long delta = end - start;
		System.out.println("that took "+delta);
        return lout;
	}
	
	public List<ImageRec> makeImageRecs(Limiter limits) {
		long start = System.currentTimeMillis();
		List<ImageRec> lout = new ArrayList<ImageRec>();
		
		String innerSQL = limits.makeSQL();
		String sql = "select eid, species_name, species_id, event_start_time,camera_id,time_of_day, count(iid), imageid from (" +
				" select e.id as eid, common_name as species_name, species_id, event_start_time,e.camera_id,time_of_day, i.id as imageid, ids.id as iid  " +
				"from identifications ids, species spc, events e,images i, cameras c   " +
				"where e.camera_id=c.id and c.project_id="+limits.getProjectId()+" and spc.id = ids.species_id and ids.image_event_id=e.id and e.id=i.event_id  " +
				innerSQL +
				" group by imageid, ids.id " +
				") x  group by imageid, eid, species_id " +
				"order by eid,count(iid) desc, species_id, imageid";
				
		//String sql = "select eid, species_name, species_id, event_start_time,camera_id,time_of_day, count(iid), imageid from ("
		//		+" select e.id as eid, common_name as species_name, species_id, event_start_time,e.camera_id,time_of_day, i.id as imageid, ids.id as iid "
		//		+" from identifications ids, species spc, events e,images i, cameras c  "
		//		+" where e.camera_id=c.id and c.project_id="+limits.getProjectId()+" and spc.id = ids.species_id and ids.image_event_id=e.id and e.id=i.event_id "
		//		+innerSQL
		//		+" group by ids.id "
		//		+" ) x "
		//		+" group by eid, species_id";
		
		// 0 select eid, 
		// 1 species_name, 
		// 2 species_id, 
		// 3 event_start_time,
		// 4 camera_id,
		// 5 time_of_day, 
		// 6 count(iid)
		// 7 imageid
		
		DateTime now = new DateTime();

		SQLQuery sqlQuery = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
        Query query = sqlQuery;
        List<Object[]> result = query.list();
        Iterator<Object[]> iter = result.iterator();
        try {
        	Object[] ar = iter.next();
	        do {
	        	ImageRec irec = makeIrec(ar,now);
	        	lout.add(irec);
	        	do {
	        		ar = iter.next();
	        	} while(addIfCan(irec,ar,now));
	        	// ar should now be used to create the next one
	        } while(ar != null);
        } catch(NoSuchElementException nsee) {
        	// what a shitty way to end a loop
        }
        
		long end = System.currentTimeMillis();
		long delta = end - start;
		System.out.println("that took "+delta);
        return lout;
	}

	private boolean addIfCan(ImageRec irec, Object[] ar, DateTime now) {
		Long event_id = ((Integer)ar[0]).longValue();
		if(irec.getImageEvent().getId() == event_id.longValue()) {
			// ok we can have multiple image within the same ID .  for the images all we need is the ID.
			String imageid = (String)ar[7];
			ImageEvent ie = irec.getImageEvent();
			
			ImageRecord nir = new ImageRecord();
			nir.setId(imageid);
			nir.setDatetime(now);
			ie.getImageRecords().add(nir);
			
			// or we can have multiple identifications within the same event. We need the id and the count;
			// for the ids we should check for duplicates no ?
			IdHistEntry ihe = new IdHistEntry(((Integer)ar[2]).longValue(),((BigInteger)ar[6]).intValue());
			boolean alreadyThere = false;
			for(IdHistEntry cur : irec.getIdHist()) {
				if(cur.getId().equals(ihe.getId())) {
					alreadyThere = true;
				}
			}
			if(!alreadyThere) {
				// we need one of these too:
				NameHistEntry nhe = new NameHistEntry((String)ar[1], ((BigInteger)ar[6]).intValue());
				irec.getIdHist().add(ihe);
				irec.getNameHist().add(nhe);
			}
			
			return true;
		} else {
			
			return false;
		}
	}



	public ImageRec makeIrec(Object[] ar, DateTime now) {
		Long event_id = ((Integer)ar[0]).longValue();
		ImageEvent ie = new ImageEvent();
		ie.setCameraID(((Integer)ar[4]).longValue());
		ie.setEventStartTime(new DateTime(ar[3]));
		ie.setId(event_id);
		String todstr = (String)ar[5];
		ie.setTypeOfDay(TypeOfDay.valueOf(todstr));
		
		// add ONE image record
		String imageid = (String)ar[7];
		ie.setImageRecords(new TreeSet<ImageRecord>());
		ImageRecord nir = new ImageRecord();
		nir.setId(imageid);
		nir.setDatetime(now);
		ie.getImageRecords().add(nir);
		
		
		// add ONE set of record for histogram.
		IdHist idHist = new IdHist();
		NameHist nameHist = new NameHist();
		NameHistEntry nhe = new NameHistEntry((String)ar[1], ((BigInteger)ar[6]).intValue());
		nameHist.add(nhe);
		IdHistEntry ihe = new IdHistEntry(((Integer)ar[2]).longValue(),((BigInteger)ar[6]).intValue());
		idHist.add(ihe);
		ImageRec irec = new ImageRec(ie,idHist,nameHist);
		return irec;
	}



	public List<Series> makeByMonth(Limiter limits) {
		String innerSQL = limits.makeSQL();
		String sql = "select sum(num), months from (select m.months, ifnull(number,0) as num from months m left join (       " +
				"select number,species_id,CONVERT_TZ(event_start_time,'+00:00','-08:00') as sttime    from identifications,events, cameras where cameras.id=events.camera_id and cameras.project_id = "+limits.getProjectId()+" and identifications.image_event_id=events.id "+innerSQL+" group by image_event_id     " +
						") x on m.months = month(sttime)) y group by months order by months";
		log.info("it looks like we're going to run this SQL "+sql);
		Series s = doSQLtoSeries(sql);
		s.setSeriesName("by month");
		List<Series> l = new ArrayList<Series>();
		l.add(s);
		return l;
	}




}
