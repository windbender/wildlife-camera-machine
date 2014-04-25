package com.github.windbender.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import com.github.windbender.core.Limiter;
import com.github.windbender.core.NV;
import com.github.windbender.core.Series;
import com.github.windbender.core.SpeciesCount;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.Species;
import com.github.windbender.domain.User;

public class ReportDAO {

	SessionFactory sessionFactory;
	public ReportDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	
	public List<Long> makeTopSpeciesIdList(int limitNumber,long project_id) {
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
        	l.add(id);
        	count++;
        	if(count > limitNumber) break;
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
						") x on h.hours = hour(sttime)) y group by hours";
		Series s = doSQLtoSeries(sql);
		s.setSeriesName("by day");
		List<Series> l = new ArrayList<Series>();
		l.add(s);
		return l;
	}

	public List<Series> makeByDay(Limiter limits) {
		String innerSQL = limits.makeSQL();
		String sql = "select sum(num),dates from (   select dates,ifnull(sttime,0) as strt,ifnull(number,0) as num from dates d left join ( " +
						"select species_id,CONVERT_TZ(event_start_time,'+00:00','-08:00') as sttime, number from identifications, events, cameras where cameras.id=events.camera_id and cameras.project_id = "+limits.getProjectId()+" and identifications.image_event_id=events.id "+innerSQL+" group by image_event_id      " +
					  ") x on date(d.dates) = date(x.sttime) where d.dates < ? and d.dates > ? ) y group by dates";
		
//		String sql = "select count(*) as cnt, date(event_start_time) from (  select species_id,event_start_time    from identifications,events   where identifications.image_event_id=events.id   "+innerSQL+" group by image_event_id  ) x group by date(event_start_time)";
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




}
