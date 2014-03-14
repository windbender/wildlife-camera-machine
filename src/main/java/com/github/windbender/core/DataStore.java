package com.github.windbender.core;

import java.util.List;

import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.User;

public interface DataStore {

	void addImage(ImageRecord newImage);

	List<ImageRecord> getTimeOrderedImages();

	List<ImageEvent> getImageEvents();

	void recordIdentification(IdentificationRequest idRequest, User u);

	ImageRecord getRecordFromId(String id);

	ImageEvent getGoodEventToIdentify(User user);
}
