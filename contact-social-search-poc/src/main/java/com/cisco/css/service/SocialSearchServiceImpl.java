/**
 * 
 */
package com.cisco.css.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cisco.css.query.Tagger;
import com.cisco.css.store.ActivityType;
import com.cisco.css.store.ActivityTypeStore;
import com.cisco.css.store.Person;
import com.cisco.css.store.PersonStore;
import com.cisco.css.store.Relation;
import com.cisco.css.store.SocialGraphStore;

/**
 * 
 * @author zhefang
 * 
 */
@Service("socialSearchService")
public class SocialSearchServiceImpl implements SocialSearchService {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SocialSearchServiceImpl.class);
	private static SocialSearchServiceImpl service = new SocialSearchServiceImpl();
	
	public static SocialSearchServiceImpl getService(){
		return service;
	}
	

	@Override
	public List<Person> query(String queryStr) {
		// TODO if it is NOT to query person, but to query for things
		return this.queryPersons(queryStr);
	}

	
	@Override
	public List<Person> getPersons(String userName) {
		List<Person> rtList = new ArrayList<Person> ();
		List<Person> persons = PersonStore.getStore().getFullPersons();
		Map<String, Person> similarPersons = new HashMap<String, Person>();
		for (Person person : persons) {
			if (person.getUserName().indexOf(userName) >= 0) {// FIXME
				similarPersons.put(person.getId(), person);
			}
		}
		rtList.addAll(similarPersons.values());
		return rtList;
	}


	@Override
	public Person getPerson(String uuid) {
		Person mePerson = PersonStore.getStore().get(uuid).get(0);
		return mePerson;
	}
	
	
	public List<Person> queryPersons(String queryStr) {
		Tagger.init(queryStr, PersonStore.getStore().getMe().getUserName());
		Tagger.analysis();
		if(!Tagger.isSimple()) {
			System.err.println("Your query is too complex to analysis");
		}
		return this.queryPersons(Tagger.startP, Tagger.relation, Tagger.endP);
	}

	public List<Person> queryPersons(String subject, String relation, String endP) {

		// parse the relation to string, suppose relation is "have meeting",
		// hard coded
		String relat = subject + " " + relation;
		String[] relas = relat.split(" ");
		//
		List<ActivityType> types = new ArrayList<ActivityType>();
		for (String rela : relas) {
			ActivityType type = ActivityTypeStore.getStore().getActivityType(
					rela);
			if (null != type) {
				types.add(type);
			}
		}
		if (types.size() != 1) {
			throw new RuntimeException(" this is wrong parsing, please check "
					+ relation);
		}
		// FIXME
		Person mePerson = PersonStore.getStore().getMe();
		logger.info(mePerson.toString());
		// use endP to find similar persons, FIXME we need to use better data
		// structure rather than List, like Trie
		List<Person> persons = PersonStore.getStore().getFullPersons();
		Map<String, Person> similarPersons = new HashMap<String, Person>();
		for (Person person : persons) {
			if (person.getUserName().indexOf(endP) >= 0) {// FIXME
				similarPersons.put(person.getId(), person);
			}
		}
		List<Person> relatedPersons = SocialGraphStore.getStore().query(types,
				mePerson, similarPersons.keySet());
		// check if related person is in the similar persons
		// You looked for 'Mike Zhang'
		// And you put the 'Mike'
		// Th

		return relatedPersons;
	}
	

	@Override
	public List<Relation> getRelations(String personId) {
		return SocialGraphStore.getStore().queryRelation(personId, null);
	}


	@Override
	public List<Relation> getAllRelations() {
		return SocialGraphStore.getStore().queryAllRelations();
	}


	@Override
	public List<Relation> getRelations(String fromId, String toId) {
		
		return  SocialGraphStore.getStore().queryRelation(fromId, toId);
	}


}
