/**
 * This would not be used after we can access the person
 * Do NOT need to produce the fake user list
 */
package com.cisco.css.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author zhefang
 * 
 */
public class PersonStore extends Store {

	Map<String, List<Person>> personMap = new HashMap<String, List<Person>>();

	List<Person> socialPersons;
	List<Person> fullPersons;
	int fullFactor = 5;// means Mac Fang 1, Mac Fang 2 ... 5
	Person me;
	static PersonStore store = new PersonStore();

	public static PersonStore getStore() {
		return store;
	}

	/**
	 * Read from local file
	 * */
	public void init() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerSubtypes(Person.class);
			personMap = mapper.readValue(readFromFile("data/personMap"),
					new TypeReference<Map<String, List<Person>>>() {
					});
			socialPersons = mapper.readValue(
					readFromFile("data/socialPersons"),
					new TypeReference<List<Person>>() {
					});
			fullPersons = mapper.readValue(readFromFile("data/fullPersons"),
					new TypeReference<List<Person>>() {
					});
			me = mapper.readValue(readFromFile("data/me"), Person.class);
			// FIXME need to add me into themap
			if (!personMap.containsKey(me.getId())) {
				List<Person> meList = new ArrayList();
				meList.add(me);
				personMap.put(me.getId(), meList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Init the list, save to the local file system
	 * */
	public void prepareData() {

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerSubtypes(Person.class);
			Person[] persons = mapper.readValue(readFromFile("data/linkin"),
					Person[].class);

			for (Person person : persons) {
				String key = person.getId();
				List<Person> lst = new ArrayList<Person>();
				if (personMap.containsKey(key)) {
					lst.addAll(personMap.get(key));
				}
				lst.add(person);
				personMap.put(key, lst);
			}
			socialPersons = new ArrayList<Person>();

			socialPersons.addAll(Arrays.asList(persons));
			initFullList();
			addMeIn("Mac", "Fang");
			writeToFile("data/personMap", personMap, mapper);
			writeToFile("data/socialPersons", socialPersons, mapper);
			writeToFile("data/fullPersons", fullPersons, mapper);
			writeToFile("data/me", me, mapper);
			System.out.println(" --- personStore init done --- ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initFullList() {
		fullPersons = new ArrayList<Person>();
		fullPersons.addAll(socialPersons);
		for (Person person : socialPersons) {
			int i = 0;
			while (i++ < fullFactor) {
				Person fakePerson = new Person();
				String firstName = person.getFirstName() + " " + i;
				String lastName = person.getLastName() + " " + i;
				fakePerson.setFirstName(firstName);
				fakePerson.setLastName(lastName);
				fakePerson.setUserName(person.getUserName());
				fullPersons.add(fakePerson);
			}
		}
	}

	/**
	 * FIXME addMein
	 * */
	public void addMeIn(String firstName, String lastName) {
		Person person = new Person();
		person.setFirstName(firstName);
		person.setLastName(lastName);
		me = person;
		List<Person> lst = new ArrayList<Person>();
		socialPersons.add(me);
		fullPersons.add(me);
		if (personMap.containsKey(firstName + " " + lastName)) {
			lst.addAll(personMap.get(firstName + " " + lastName));
		}
		personMap.put(firstName + " " + lastName, lst);
	}

	public Person getMe() {
		return me;
	}

	// include me in
	public List<Person> getSocialPersons() {
		return socialPersons;
	}

	public List<Person> getFullPersons() {
		return fullPersons;
	}

	public List<Person> get(String key) {

		List<Person> rtList = personMap.get(key);
		if (null == rtList) {
			rtList = new ArrayList<Person>();
		}
		return rtList;

	}

	public Person getOne(String key) {
		List<Person> rtList = personMap.get(key);
		if (null != rtList && !rtList.isEmpty()) {
			return rtList.get(0);
		}
		return null;
	}

	public Map<String, List<Person>> getPersons() {
		return personMap;
	}

	public static void main(String[] args) throws Exception {
		PersonStore.getStore().prepareData();
		PersonStore.getStore().init();
		System.out.println(PersonStore.getStore().getFullPersons());
		System.out.println(PersonStore.getStore().getPersons());
		System.out.println(PersonStore.getStore().getSocialPersons());
	}
}
