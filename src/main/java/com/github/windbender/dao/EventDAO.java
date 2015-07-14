package com.github.windbender.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.User;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class EventDAO extends AbstractDAO<ImageEvent> {

	public EventDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public ImageEvent findById(Long id) {

		return get(id);
	}

	public long create(ImageEvent ir) {
		return persist(ir).getId();
	}

	public List<ImageEvent> findEventsForUserToID(User u, int number) {
		List<ImageEvent> list = new ArrayList<ImageEvent>();
		String sql = "select * from ( select count(y.image_event_id) as totalActions, events.id as eventId  from events  left join (select * from identifications where user_id != ?) y on y.image_event_id = events.id  group by events.id  ) x where x.totalActions < ? order by totalActions,eventId";
		SQLQuery sqlQuery = this.currentSession().createSQLQuery(sql);
		int limit = 2;
        Query query = sqlQuery.setParameter(0, u.getId()).setParameter(1, limit);
        List<Object[]> l = query.list(); 
        int count = 0;
        for(Object[] oa: l) {
        	int numberOfIdentifications = 0;
        	Long event_id = null;
        	BigInteger numberOfIdentifications_bi = (BigInteger)oa[0];
        	Integer event_id_bi = (Integer)oa[1];
        	if(numberOfIdentifications_bi != null) {
        		numberOfIdentifications = numberOfIdentifications_bi.intValue();
        	}
        	if(event_id_bi != null) {
        		event_id = event_id_bi.longValue();
        	}
        	if(event_id != null) {
        		ImageEvent e = this.findById(event_id);
	        	list.add(e);
        		count++;
	        	if(count > number) break;
        	}
        }
        
        return list;
	}
	public List<ImageEvent> findEventsBetween(DateTime before, DateTime after) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(ImageEvent.class);
		crit.add(Restrictions.ge("eventStartTime", before));
		crit.add(Restrictions.le("eventStartTime", after));
		crit.addOrder( Property.forName("eventStartTime").desc() );
		List<ImageEvent> findList = crit.list();
		return findList;
	}

	public List<ImageEvent> findEventsBetween(long l, DateTime before,
			DateTime after) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(ImageEvent.class);
		crit.add(Restrictions.ge("eventStartTime", before));
		crit.add(Restrictions.le("eventStartTime", after));
		crit.add(Restrictions.eq("cameraID",l));
		crit.addOrder( Property.forName("eventStartTime").desc() );
		List<ImageEvent> findList = crit.list();
		return findList;
		
	}

	public void save(ImageEvent ie) {
		this.currentSession().saveOrUpdate(ie);
		
	}

	public List<ImageEvent> findAll() {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(ImageEvent.class);
		crit.addOrder( Property.forName("eventStartTime").desc() );
		List<ImageEvent> findList = crit.list();
		return findList;
	}

	public List<Integer> findEventIdsDoneByUser(User u) {
		List<ImageEvent> list = new ArrayList<ImageEvent>();
		String sql = "select image_event_id from identifications where user_id=? group by image_event_id order by image_event_id";
		SQLQuery sqlQuery = this.currentSession().createSQLQuery(sql);
        Query query = sqlQuery.setParameter(0, u.getId());
        List<Integer> l = query.list(); 
        return l;
	}

	public List<Integer> findEventsIdsWithFewerThanIdentifications(int number) {
		List<ImageEvent> list = new ArrayList<ImageEvent>();
		String sql = "select eventId from (select count(identifications.image_event_id) as totalActions, events.id as eventId from events left join identifications  on identifications.image_event_id = events.id group by events.id ) x where x.totalActions < ? order by totalActions,eventId";
		SQLQuery sqlQuery = this.currentSession().createSQLQuery(sql);
        Query query = sqlQuery.setParameter(0, number);
        List<Integer> l = query.list(); 
        return l;
    }

	public List<Integer> findEventIdsIdentifiedByUser(Long project_id, User u) {
		List<ImageEvent> list = new ArrayList<ImageEvent>();
		String sql = "select image_event_id from identifications i, events e, cameras c where i.image_event_id=e.id and e.camera_id = c.id and c.project_id=? and user_id=? group by e.id order by e.id";
		SQLQuery sqlQuery = this.currentSession().createSQLQuery(sql);
        Query query = sqlQuery.setParameter(0, project_id).setParameter(1, u.getId());
        List<Integer> l = query.list(); 
        return l;
	}

	public List<Integer> findEventIdsUploadedByUser(Long project_id, User u) {
		List<ImageEvent> list = new ArrayList<ImageEvent>();
		String sql = "select e.id from images i, events e, cameras c where i.event_id = e.id and e.camera_id = c.id and c.project_id=? and i.user_id=? group by e.id order by e.id";
		SQLQuery sqlQuery = this.currentSession().createSQLQuery(sql);
        Query query = sqlQuery.setParameter(0, project_id).setParameter(1, u.getId());
        List<Integer> l = query.list(); 
        return l;
	}

	public List<Integer> findEventsIdsWithFewerThanIdentifications(Long project_id,
			int number) {
		List<ImageEvent> list = new ArrayList<ImageEvent>();
		String sql = "select eventId from (select count(i.image_event_id) as totalActions, e.id as eventId from cameras c, events e left join identifications i  on i.image_event_id = e.id where c.id = e.camera_id and c.project_id=? group by e.id ) x where x.totalActions < ? order by totalActions,eventId";
		SQLQuery sqlQuery = this.currentSession().createSQLQuery(sql);
        Query query = sqlQuery.setParameter(0, project_id).setParameter(1, number);
        List<Integer> l = query.list(); 
        return l;
	}
	public List<Integer> findFlaggedEventsIdsWithFewerThanIdentifications(Long project_id,int number) {
		List<ImageEvent> list = new ArrayList<ImageEvent>();
		String sql = "select eventId from  ( "+
					"select count(i.image_event_id) as totalActions, e.id as eventId "+
				"from cameras c, events e left join identifications i  on i.image_event_id = e.id  "+
				"where c.id = e.camera_id and c.project_id=? group by e.id  "+
				") x, "+
				"(select image_event_id ,count(*) flags  from event_review_needed, events  "+
				"where event_review_needed.image_event_id = events.id and event_review_needed.flagged=1 group by events.id "+
				") y where x.eventId= y.image_event_id and totalActions < flags+? order by totalActions,eventId";
		SQLQuery sqlQuery = this.currentSession().createSQLQuery(sql);
        Query query = sqlQuery.setParameter(0, project_id).setParameter(1, number);
        List<Integer> l = query.list(); 
        return l;
	}

}
