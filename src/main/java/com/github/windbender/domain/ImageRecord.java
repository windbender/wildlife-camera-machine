package com.github.windbender.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import org.joda.time.DateTime;

import com.drew.lang.GeoLocation;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;


@NamedNativeQueries({
	@NamedNativeQuery(
	name = "com.github.windbender.ImageRecord.FindAllOrderByTime",
	query = "select * from images order by imageTime,id",
    resultClass = ImageRecord.class

	)
})

@Entity
@Table(name="images")
public class ImageRecord {

	String id;
	DateTime datetime;

	double lat;
	double lon;
	String originalFileName;
	
	@Id
	@Column(name="id")
	public String getId() {
		return id;
	}

	public static ImageRecord makeImageFromExif(ExifSubIFDDirectory directory,
			GpsDirectory gpsDirectory, String filename) {
		Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
		GeoLocation location = gpsDirectory.getGeoLocation();
		ImageRecord ir = new ImageRecord();
		ir.setLat(location.getLatitude());
		ir.setLon(location.getLongitude());
		ir.setOriginalFileName(filename);
		
		String stripped = filename.toUpperCase().replaceAll("[A-Z]", "").replaceAll("[^0-9]","");
		Long seq = Long.parseLong(stripped);
		
		long millis = date.getTime() + seq;
		ir.setDateTimeViaMillis(millis);
		long temp = ir.getLat() != +0.0d ? Double.doubleToLongBits(ir.getLat()) : 0L;
		int hash = (int) (temp ^ (temp >>> 32));
		temp = ir.getLon() != +0.0d ? Double.doubleToLongBits(ir.getLon()) : 0L;
		hash = 31 * hash + (int) (temp ^ (temp >>> 32));
		
		long m = ir.getDatetime().getMillis();
		String id = "id"+hash+":"+m+":"+stripped;
		ir.setId(id);
		
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

	
	

}
