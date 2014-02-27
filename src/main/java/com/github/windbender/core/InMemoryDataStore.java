package com.github.windbender.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.joda.time.DateTime;

import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.User;

public class InMemoryDataStore implements DataStore {


	private Map<String,ImageRecord> map = new HashMap<String,ImageRecord>();
	private TreeMap<DateTime,ImageRecord> tmap = new TreeMap<DateTime,ImageRecord>();
	
	private TreeMap<DateTime,ImageEvent> eventMap = new TreeMap<DateTime,ImageEvent>();
	private Map<String,IdRecord> idMap = new HashMap<String,IdRecord>();

	@Override
	public void addImage(ImageRecord newImage) {
		map .put(newImage.getId(),newImage);
		tmap.put(newImage.getDatetime(),newImage);
		DateTime imageTime = newImage.getDatetime();
		ImageEvent ie = findOrMakeImageEventFor(imageTime);
		ie.addImage(newImage);
	}

	private ImageEvent findOrMakeImageEventFor(DateTime imageTime) {
		int beforeAfter = 5;
		DateTime before = imageTime.minusSeconds(beforeAfter);
		DateTime after = imageTime.plusSeconds(beforeAfter);
		SortedMap<DateTime, ImageEvent> part = eventMap.subMap(before, after);
		if(part.size() > 0) {
			if(part.size() > 1) {
				//GAH!  should not happen.
				// perhaps find the closest ?
				return null;
			} else {
				DateTime key = part.firstKey();
				return part.get(key);
			}
		} else {
			ImageEvent ie = new ImageEvent();
			eventMap.put(imageTime, ie);
			return ie;
		}
	}

	@Override
	public List<ImageRecord> getTimeOrderedImages() {
		
		return new ArrayList<ImageRecord>(tmap.values());
		
	}
	
	@Override
	public List<ImageEvent> getImageEvents() {
		return new ArrayList<ImageEvent>(eventMap.values());
	}

	@Override
	public void recordIdentification(IdentificationRequest idRequest, User u) {
		String id = idRequest.getImageid();
		IdRecord idr = idMap .get(id);
		if(idr == null) {
			idr = new IdRecord(id);
			idMap.put(id,idr);
		}
		idr.add(idRequest);
		
		
	}

	@Override
	public ImageRecord getRecordFromId(String id) {
		return map.get(id);
		
	}

}
