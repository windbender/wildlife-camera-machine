package com.github.windbender.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

import com.github.windbender.core.NV;

public class ReportDAO {

	SessionFactory sessionFactory;
	public ReportDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	
	public List<NV> makeBySpecies() {
		List<NV> l = new ArrayList<NV>();
		String sql = "  select count(*) as cnt,  common_name from (   	select species_id,event_start_time,number   	from identifications,events   	where identifications.image_event_id=events.id    	group by image_event_id   ) x, species s where x.species_id = s.id group by species_id  order by cnt";
        SQLQuery sqlQuery = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
//        Query query = sqlQuery.setParameter(0, at.getId());
        Query query = sqlQuery;
        List<Object[]> result = query.list();
        for(Object[] ar: result) {
        	NV nv = new NV();
        	nv.val = (BigInteger)ar[0];
        	nv.name = (String) ar[1];
        	l.add(nv);
        }
        return l;

	}

}
