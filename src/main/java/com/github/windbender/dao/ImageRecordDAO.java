package com.github.windbender.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

import com.github.windbender.domain.ImageRecord;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class ImageRecordDAO extends AbstractDAO<ImageRecord>{

	public ImageRecordDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	

    public ImageRecord findById(String id) {
        return get(id);
    }
    
    public List<ImageRecord> findBestByVote(Long long1) {
    	String sql = "select count(*) as vote,image_id from image_flagged_good a, images b, cameras c  where a.image_id=b.id and camera_id=c.id and c.project_id=? group by image_id order by vote desc;";

		SQLQuery sqlQuery = this.currentSession().createSQLQuery(sql);
        Query query = sqlQuery.setParameter(0, long1);
        List<Object[]> l = query.list(); 
        List<ImageRecord> ol = new ArrayList<ImageRecord>();
        for(Object[] oa: l) {
        	//BigInteger vote = (BigInteger) oa[0];
        	String image_id = (String) oa[1];
        	ImageRecord ir = findById(image_id);
        	ol.add(ir);
        }
        return ol;
        
    }

    public String create(ImageRecord ir) {
        return persist(ir).getId();
    }

    public List<ImageRecord> findAll() {
        return list(namedQuery("com.github.windbender.ImageRecord.FindAllOrderByTime"));
    }


	public void save(ImageRecord addImage) {
		this.currentSession().saveOrUpdate(addImage);
	}

}
