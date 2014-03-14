package com.github.windbender.core;

public class IdentificationRequest {

		Integer numberOfAnimals;
		String speciesName;
		long speciesId;
		String imageid;
		Long eventid;
		
		public Long getEventid() {
			return eventid;
		}
		public void setEventid(Long eventid) {
			this.eventid = eventid;
		}
		public long getSpeciesId() {
			return speciesId;
		}
		public void setSpeciesId(long speciesId) {
			this.speciesId = speciesId;
		}
		public Integer getNumberOfAnimals() {
			return numberOfAnimals;
		}
		public void setNumberOfAnimals(Integer numberOfAnimals) {
			this.numberOfAnimals = numberOfAnimals;
		}
		public String getSpeciesName() {
			return speciesName;
		}
		public void setSpeciesName(String speciesName) {
			this.speciesName = speciesName;
		}
		public String getImageid() {
			return imageid;
		}
		public void setImageid(String imageid) {
			this.imageid = imageid;
		}
		@Override
		public String toString() {
			return "IdentificationRequest [numberOfAnimals=" + numberOfAnimals
					+ ", speciesName=" + speciesName + ", speciesId="
					+ speciesId + ", imageid=" + imageid + ", eventid="
					+ eventid + "]";
		}
		
		
		
}
