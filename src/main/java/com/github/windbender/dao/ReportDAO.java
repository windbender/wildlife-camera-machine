package com.github.windbender.dao;

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

public class ReportDAO {

	SessionFactory sessionFactory;
	public ReportDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	
	public List<StringSeries> makeBySpecies(Limiter limits) {
		String innerSQL = limits.makeSQL();
		String sql = "select count(*) as cnt,  common_name from (   	" +
				"select species_id,event_start_time,number   	from identifications,events   	where identifications.image_event_id=events.id "+innerSQL+" group by image_event_id   " +
						") x, species s where x.species_id = s.id group by species_id  order by cnt";		

		return doSQLtoSeriesString(sql);

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
				"select number,species_id,CONVERT_TZ(event_start_time,'+00:00','-08:00') as sttime    from identifications,events   where identifications.image_event_id=events.id "+innerSQL+" group by image_event_id     " +
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
						"select species_id,CONVERT_TZ(event_start_time,'+00:00','-08:00') as sttime, number from identifications, events where identifications.image_event_id=events.id "+innerSQL+" group by image_event_id      " +
					  ") x on date(d.dates) = date(x.sttime) where d.dates < ? and d.dates > ? ) y group by dates";
		
//		String sql = "select count(*) as cnt, date(event_start_time) from (  select species_id,event_start_time    from identifications,events   where identifications.image_event_id=events.id   "+innerSQL+" group by image_event_id  ) x group by date(event_start_time)";
		Series s = doSQLtoSeriesFromDate(sql, limits.getTimeInterval());
		s.setSeriesName("by day");
		List<Series> l = new ArrayList<Series>();
		l.add(s);
		return l;
		
	}

}
