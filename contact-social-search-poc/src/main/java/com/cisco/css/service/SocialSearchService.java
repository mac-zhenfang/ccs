package com.cisco.css.service;

import java.util.List;

import com.cisco.css.store.Person;

public interface SocialSearchService {
	
	public List<Person> query(String queryStr);
	
	public List<Person> getRelatedPersons(String whom);
	
	public Person getPerson(String uuid);
}
