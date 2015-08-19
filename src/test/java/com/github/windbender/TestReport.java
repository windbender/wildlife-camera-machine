package com.github.windbender;

import com.github.windbender.core.Limiter;
import com.github.windbender.core.ReportParams;
import com.github.windbender.dao.ReportDAO;
import com.github.windbender.domain.Project;

public class TestReport extends DAOTests {


	
	public void testMakeByMonth() {
		ReportDAO rd = new ReportDAO( sessionFactory, null,null,null);
		
		ReportParams reportParams = new ReportParams();
		Project currentProject = new Project();
		Limiter limits = new Limiter(reportParams, currentProject);
		rd.makeByMonth(limits);
		

	}

}
