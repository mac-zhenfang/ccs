/**
 * 
 */
package com.cisco.cc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cisco.cc.query.Tagger;
import com.cisco.cc.store.ActivityType;
import com.cisco.cc.store.ActivityTypeStore;
import com.cisco.cc.store.Person;
import com.cisco.cc.store.PersonStore;
import com.cisco.cc.store.SocialGraphStore;

/**
 * @author zhefang
 * 
 */
public class SocialSearchService {
	private static SocialSearchService service = new SocialSearchService();
	
	public static SocialSearchService getService(){
		return service;
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
				mePerson);
		List<Person> retList = new ArrayList<Person>();
		// check if related person is in the similar persons
		// You looked for 'Mike Zhang'
		// And you put the 'Mike'
		// Th
		for (Person relatedPerson : relatedPersons) {
			if (similarPersons.containsKey(relatedPerson.getId())) {
				retList.add(relatedPerson);
			}
		}

		return retList;
	}
}
