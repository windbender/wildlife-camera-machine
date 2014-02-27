package com.github.windbender.core;

import com.github.windbender.domain.ImageRecord;

public class ImageRecordTO {
//	src: '/api/images/MFDC'+out+'.JPG',
//	  title: 'Pic '+out
	String src;
	String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	String title;
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ImageRecordTO(String src, String title) {
		super();
		this.src = src;
		this.title = title;
	}
	public ImageRecordTO(ImageRecord ir) {
		String name = ir.getOriginalFileName();
		this.title = name;
		this.src = "/api/images/"+ir.getId();
		this.id = ir.getId();
	}
	
}
