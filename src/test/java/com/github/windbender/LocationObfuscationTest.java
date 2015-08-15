package com.github.windbender;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.junit.Test;

import com.github.windbender.core.LatLonPair;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;

public class LocationObfuscationTest {

	@Test
	public void testRange() {
		double maxLat = Double.MIN_VALUE;
		double maxLon = Double.MIN_VALUE;
		double minLat = Double.MAX_VALUE;
		double minLon = Double.MAX_VALUE;
		for(int i=0;i < 10000;i++) {
			ImageEvent ie = new ImageEvent();
			SortedSet<ImageRecord> images = new TreeSet<ImageRecord>();
			ImageRecord ir = new ImageRecord();
			ir.setLat(0+0.00000001*i);
			ir.setLon(0+0.00000001*i);
			ir.setId(""+new Random().nextLong());
			ir.setDatetime(new DateTime());
			images.add(ir);
			ie.setImageRecords(images);
			LatLonPair ol = ie.getObfuscatedLocation();
			maxLat = Math.max(maxLat, ol.getLat());
			maxLon = Math.max(maxLon, ol.getLon());
			minLat = Math.min(minLat, ol.getLat());
			minLon = Math.min(minLon, ol.getLon());	
		}
		int mlat = new Double(maxLat * 1000).intValue();
		int nlat = new Double(minLat * 1000).intValue();
		int mlon = new Double(maxLon * 1000).intValue();
		int nlon = new Double(minLon * 1000).intValue();
		assertEquals(7,mlat);
		assertEquals(-1,nlat);
		assertEquals(7,mlon);
		assertEquals(-1,nlon);
		
		
	}

}
