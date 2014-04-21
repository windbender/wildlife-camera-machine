package com.github.windbender.core;

public class ReviewParams {
	
		
		String eventId;
		int review;

		public String getEventId() {
			return eventId;
		}
		public void setEventId(String eventId) {
			this.eventId = eventId;
		}
		public int getReview() {
			return review;
		}
		public void setReview(int review) {
			this.review = review;
		}
		public ReviewParams(String imageId, int review) {
			super();
			this.eventId = eventId;
			this.review = review;
		}
		public ReviewParams() {
			
		}
}
