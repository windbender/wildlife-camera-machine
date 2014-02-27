package com.github.windbender.dao;

import java.util.List;

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

    public String create(ImageRecord ir) {
        return persist(ir).getId();
    }

    public List<ImageRecord> findAll() {
        return list(namedQuery("com.github.windbender.ImageRecord.FindAllOrderByTime"));
    }

}
