package com.github.windbender.service;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MakeDatesService {
	Logger log = LoggerFactory.getLogger(MakeDatesService.class);

	public MakeDatesService(SessionFactory sf) {
		this.sessionFactory = sf;
	}
	private SessionFactory sessionFactory;
	final DateTime minDate = new DateTime(2013,01,01, 0, 0);
	final DateTime biggestDate = new DateTime(2020,01,01,0,0);
	public void makeDates() {
		Session session = null;
		try {
			session = sessionFactory.openSession();

			session.doWork(new Work() {

				@Override
				public void execute(java.sql.Connection connection) {
					try {
						DateTime maxDate = null;
						PreparedStatement ps = connection.prepareStatement("select max(dates) from dates");
						ResultSet rs = ps.executeQuery();
						while(rs.next()) {
							Date date = rs.getDate(1);
							if(date == null) break;
							maxDate = new DateTime(date);
						}
						if(maxDate == null) {
							maxDate = minDate;
						}
						while(maxDate.isBefore(biggestDate.minusDays(2))) {
							String sql = "insert into dates (dates) values (?)";
							PreparedStatement pstmt = connection.prepareStatement(sql);
							pstmt.setDate(1, new Date(maxDate.getMillis()));
							pstmt.executeUpdate();
							maxDate = maxDate.plusDays(1);
						}
					} catch(SQLException e) {
						log.error("can't insert because ",e);
					}
				}
			});
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
}
