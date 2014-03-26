package com.github.windbender.core;

import java.util.List;

public class MenuItem {
	String title;
	String route;
	List<MenuItem> submenus;
	
	
	public List<MenuItem> getSubmenus() {
		return submenus;
	}
	public void setSubmenus(List<MenuItem> submenus) {
		this.submenus = submenus;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	@Override
	public String toString() {
		return "MenuItem [title=" + title + ", route=" + route + "]";
	}
	public MenuItem(String title, String route, List<MenuItem> submenus) {
		super();
		this.title = title;
		this.route = route;
		this.submenus = submenus;
	}
	public MenuItem(String title, String route) {
		super();
		this.title = title;
		this.route = route;
	}
	
	
}
