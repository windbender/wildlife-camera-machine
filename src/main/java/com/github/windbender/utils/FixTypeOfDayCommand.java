package com.github.windbender.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.core.HibernateDataStore;
import com.github.windbender.core.TypeOfDay;
import com.github.windbender.service.CachingTimeZoneGetter;
import com.github.windbender.service.CompositeTimeZoneGetter;
import com.github.windbender.service.GeoNameTimeZoneGetter;
import com.github.windbender.service.StupidTimeZoneGetter;
import com.github.windbender.service.TimeZoneGetter;

public class FixTypeOfDayCommand {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/wlcdm";

	// Database credentials
	static final String USER = "wlcdm";
	static final String PASS = "none";

	static Logger log = LoggerFactory.getLogger(FixTypeOfDayCommand.class);

	public static void main(String[] args) {
		String geoNameUsername = "x";
		TimeZoneGetter tzGetter = new CompositeTimeZoneGetter(new CachingTimeZoneGetter(new GeoNameTimeZoneGetter(geoNameUsername)), new StupidTimeZoneGetter());

		String pass = PASS;
		if(args.length == 1) {
			pass = args[0];
		}
		Connection conn = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, pass);
			
			String sql = "select * from events";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			PreparedStatement pstmt2 = conn.prepareStatement("select * from images where event_id=? limit 1");
			ResultSet rs = pstmt.executeQuery();
			DateTimeFormatter ferMatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S Z");
			while(rs.next()) {
				Long id = rs.getLong("id");
				String ts = rs.getString("event_start_time");
				ts = ts+" -0000";
				DateTime whenDT = ferMatter.parseDateTime(ts);
//				Date start = rs.getTimestamp("event_start_time");
//				DateTime whenDT = new DateTime(start);
				pstmt2.setLong(1, id);
				DateTime dsTimeChange = new DateTime(2014,3,9,5,0);
				ResultSet rs2 = pstmt2.executeQuery();
				if(rs2.next()) {
					double lat = rs2.getDouble("lat");
					double lon = rs2.getDouble("lon");
					// convert whenDT to LOCAL time...
					DateTime localTZWhen = whenDT;
					DateTimeZone zone = localTZWhen.getZone();
					DateTimeZone utcZone =  DateTimeZone.forID("Etc/UTC");
					if(zone.equals(utcZone)) {
						if(whenDT.isAfter(dsTimeChange)) {
							localTZWhen = whenDT.minusHours(7);
						} else {
							localTZWhen = whenDT.minusHours(8);
						}
					}
					
					TypeOfDay x = HibernateDataStore.makeTimeOfDay(localTZWhen, lat, lon, tzGetter, log);
					update(conn,id,x);
					System.out.println(ts+ " is "+x);
				}
				
//				TypeOfDay tod = ds.dayNightTwilight(ie, ie.getImageRecords()
//						.first());
//				System.out.println("New TOD for " + ie + "  is  " + tod);
//				ie.setTypeOfDay(tod);
			}
			
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("oops could not do something "+e.getMessage());
		}

	}

	private static void update(Connection conn, Long id, TypeOfDay x) throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement("update events set time_of_day=? where id=?");
		pstmt.setString(1, x.toString());
		pstmt.setLong(2, id);
		int y = pstmt.executeUpdate();
		System.out.println("updated "+y+" rows");
		
	}
	
	

}
