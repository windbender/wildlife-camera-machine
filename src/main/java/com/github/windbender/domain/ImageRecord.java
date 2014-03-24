package com.github.windbender.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import org.joda.time.DateTime;

import com.drew.lang.GeoLocation;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.jersey.api.ConflictException;


@NamedNativeQueries({
	@NamedNativeQuery(
	name = "com.github.windbender.ImageRecord.FindAllOrderByTime",
	query = "select * from images order by imageTime,id",
    resultClass = ImageRecord.class

	)
})

@Entity
@Table(name="images")
public class ImageRecord implements Comparable<ImageRecord>{

	String id;
	DateTime datetime;
	DateTime uploadTime;
	
	double lat;
	double lon;
	String originalFileName;
	
	Long cameraID;
	
	public int compareTo(ImageRecord other) {
		int x = this.datetime.compareTo(other.datetime);  
		if (x !=0) return x;
		x = this.id.compareTo(other.getId());
		return x;
	}
	
	@Column(name="camera_id", nullable=true)
	public long getCameraID() {
		return cameraID;
	}

	public void setCameraID(long cameraID) {
		this.cameraID = cameraID;
	}

	@Id
	@Column(name="id")
	public String getId() {
		return id;
	}

	
	public static ImageRecord makeImageFromExif(ExifSubIFDDirectory directory,
			GpsDirectory gpsDirectory, String filename, long cameraId, String latStr, String lonStr) {
		Double lat = null;
		if(latStr != null) {
			try {
				lat = Double.parseDouble(latStr);
			} catch (NumberFormatException e) {
			}
		}
		Double lon = null;
		if(lonStr != null) {
			try {
				lon = Double.parseDouble(lonStr);
			} catch (NumberFormatException e) {
			}
		}
		
		Date date = new Date();
		if(directory != null) {
			date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
		}
		GeoLocation location = null;
		if(gpsDirectory != null) {
			location = gpsDirectory.getGeoLocation();
		}
		ImageRecord ir = new ImageRecord();
		if(location != null) {
			ir.setLat( location.getLatitude());
			ir.setLon(location.getLongitude());
		} else {
			if((lat == null) || (lon == null) ) throw new ConflictException("sorry either image must have EXIF based GPS info, or you must supply it on the upload form");
			if(lat != null) 
				ir.setLat( lat );
			if(lon != null)
				ir.setLon( lon );
		}
		ir.setOriginalFileName(filename);

		ir.setCameraID(cameraId);
		
		String stripped = filename.toUpperCase().replaceAll("[A-Z]", "").replaceAll("[^0-9]","");
		Long seq = 0l;
		try {
			int endIndex = stripped.length();
			String lastPart = stripped.substring(endIndex-3, endIndex);
			seq =Long.parseLong(lastPart);
		} catch (NumberFormatException e) {
		}
		
		long millis = date.getTime() + seq;
		ir.setDateTimeViaMillis(millis);
		long temp = ir.getLat() != +0.0d ? Double.doubleToLongBits(ir.getLat()) : 0L;
		int locationHash = (int) (temp ^ (temp >>> 32));
		temp = ir.getLon() != +0.0d ? Double.doubleToLongBits(ir.getLon()) : 0L;
		locationHash = 31 * locationHash + (int) (temp ^ (temp >>> 32));
		
		long timeMillis = ir.getDatetime().getMillis();
		String id = "id"+locationHash+":"+timeMillis+":"+seq;
		ir.setId(id);
		ir.setUploadTime(new DateTime());
		return ir;
	}

	private void setDateTimeViaMillis(long millis) {
		datetime = new DateTime(millis);
		
	}

	@Column(name="lat", nullable=true)
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	@Column(name="lon", nullable=true)
	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	@Column(name="imageTime", nullable=false)
	public DateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(DateTime datetime) {
		this.datetime = datetime;
	}

	@Column(name="filename", nullable=false)
	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public void setId(String id) {
		this.id = id;
	}
	

	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="event_id")
	public ImageEvent getEvent() {
		return event;
	}

	 public ImageEvent event;

	public void setEvent(ImageEvent event) {
		this.event = event;
	}

	@Column(name="upload_time", nullable=false)
	public DateTime getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(DateTime uploadTime) {
		this.uploadTime = uploadTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cameraID == null) ? 0 : cameraID.hashCode());
		result = prime * result
				+ ((datetime == null) ? 0 : datetime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((originalFileName == null) ? 0 : originalFileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageRecord other = (ImageRecord) obj;
		if (cameraID == null) {
			if (other.cameraID != null)
				return false;
		} else if (!cameraID.equals(other.cameraID))
			return false;
		if (datetime == null) {
			if (other.datetime != null)
				return false;
		} else if (!datetime.equals(other.datetime))
			return false;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
			return false;
		if (originalFileName == null) {
			if (other.originalFileName != null)
				return false;
		} else if (!originalFileName.equals(other.originalFileName))
			return false;
		return true;
	}

	
	

}
