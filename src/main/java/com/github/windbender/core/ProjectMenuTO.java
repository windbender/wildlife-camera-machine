package com.github.windbender.core;

import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;

public class ProjectMenuTO {

	public ProjectMenuTO(Project p, User user) {
		this.id = p.getId();
		this.name = p.getName();
		if(p.getPrimaryAdmin().getId().equals(user.getId())) {
			this.menuText = this.name + " (YOURS)";
		} else {
			this.menuText = this.name + " ("+p.getPrimaryAdmin().getUsername()+")";
		}
	}

	Long id;
	String name;
	String menuText;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMenuText() {
		return menuText;
	}
	public void setMenuText(String menuText) {
		this.menuText = menuText;
	}
	
	
//	[{"id":1,"name":"My First Project","description":"for testing purposes","publicReport":true,"publicCategorize":false,"cameras":null}
			
}
