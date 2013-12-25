package com.cisco.css.service;

import java.util.List;

import com.cisco.css.store.Person;
import com.cisco.css.store.Relation;

public interface SocialSearchService {
	
	public List<Person> query(String queryStr);
	
	public List<Person> getPersons(String whom);
	
	public Person getPerson(String uuid);
	
	public List<Relation> getRelations(String personId);
	
	public List<Relation> getRelations(String fromId, String toId);
	
	public List<Relation> getAllRelations ();
}
