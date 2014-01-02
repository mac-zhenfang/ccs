/**
 * 
 */
package com.cisco.cc.socia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.cisco.css.service.SocialSearchServiceImpl;
import com.cisco.css.store.ActivityStreamStore;
import com.cisco.css.store.ActivityType;
import com.cisco.css.store.ActivityTypeStore;
import com.cisco.css.store.Person;
import com.cisco.css.store.PersonStore;
import com.cisco.css.store.Relation;
import com.cisco.css.store.SocialGraphStore;
import com.cisco.css.util.Utils;
import com.clarkware.junitperf.ConstantTimer;
import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.TimedTest;
import com.clarkware.junitperf.Timer;
import com.tinkerpop.rexster.client.RexsterClient;
import com.tinkerpop.rexster.client.RexsterClientFactory;

/**
 * @author zhefang
 * 
 */
public class TestStartUp {

	@Before
	public void setUp() {
		PersonStore.getStore().init();

		ActivityTypeStore.getStore().init();

		ActivityStreamStore.getStore().init();

		SocialGraphStore.getStore().init();

	}

	@Test
	public void testCreateGraphLocal() {

		PersonStore.getStore().init();

		ActivityTypeStore.getStore().init();

		ActivityStreamStore.getStore().prepareData();

		SocialGraphStore.getStore().prepareData();
	}

	@Test
	public void testCreateGraphRemote() {

		PersonStore.getStore().init();

		ActivityTypeStore.getStore().init();

		ActivityStreamStore.getStore().init();

		SocialGraphStore.getStore().prepareData();
	}

	@Test
	public void testListAllRelations() {
		PersonStore.getStore().init();

		ActivityTypeStore.getStore().init();

		ActivityStreamStore.getStore().init();

		SocialGraphStore.getStore().init();

		List<Relation> relations = SocialGraphStore.getStore()
				.queryAllRelations();

		System.out.println(relations.size());
	}

	@Test
	public void test2Performance() throws Exception {
		RexsterClient client = RexsterClientFactory.open("10.224.194.174",
				8184, "mac11");
		// client.execute("g = rexster.getGraph('graph')");
		long start = System.currentTimeMillis();
		// List<Map<String, Object>> result = client
		// .execute("g.V('vid','152df38d-9035-4adf-9f74-c5c6a950c760').out('start', 'join').in('start', 'join').has('vid', T.neq, '152df38d-9035-4adf-9f74-c5c6a950c760').groupBy{it.name}{it}{it.unique()}.cap.next()");
		List<Map<String, Object>> result = client
				.execute("g.V('vid','152df38d-9035-4adf-9f74-c5c6a950c760').out('start', 'join').in('start', 'join').has('vid', T.in, ['744133ea-78e3-43ab-a173-e4ccdd77374e','cc44d35a-d21f-4e58-9f6c-c923f83da641']).dedup()");

		// ObjectMapper mapper = new ObjectMapper();

		System.out.println(" cost: " + (System.currentTimeMillis() - start));
		System.out.println(result.size());
		System.out.println(result);
	}

	@Test
	public void testPerformanceQuery2() throws Exception {
		ActivityType type = ActivityTypeStore.getStore().getActivityType(
				"meeting");

		List<ActivityType> types = new ArrayList<ActivityType>();
		types.add(type);
		Person rootVertexPerson = PersonStore.getStore().getMe();
		System.out.println(rootVertexPerson);
		List<Person> persons = PersonStore.getStore().getFullPersons();
		Map<String, Person> similarPersons = new HashMap<String, Person>();
//		for (Person person : persons) {
//			if (person.getUserName().indexOf("Vagou") >= 0) {// FIXME
//				similarPersons.put(person.getId(), person);
//			}
//		}
		// SocialGraphStore.getStore().query2(types, rootVertexPerson,
		// similarPersons.keySet());
		for (int i = 0; i < 100; i++) {
			Person personTobeCompare = persons.get(Utils.getRandomInt(persons
					.size() - 1));
			for (Person person : persons) {
				if (person
						.getUserName()
						.toLowerCase()
						.indexOf(personTobeCompare.getFirstName().toLowerCase()) >= 0) {// FIXME
					similarPersons.put(person.getId(), person);
				}
			}
			Thread.currentThread().sleep(100);
			List<Person> rtPersons = SocialGraphStore.getStore().query2(types,
					rootVertexPerson, similarPersons.keySet());
			System.out.println(i + "--- ready,  " + rtPersons);
		}
		// System.out.println(rtPersons);
	}

	@Test
	public void testPerformanceQuery() throws Exception {

		ActivityType type = ActivityTypeStore.getStore().getActivityType(
				"meeting");

		List<ActivityType> types = new ArrayList<ActivityType>();
		types.add(type);
		Person rootVertexPerson = PersonStore.getStore().getMe();
		System.out.println(rootVertexPerson);
		List<Person> persons = PersonStore.getStore().getFullPersons();
		Map<String, Person> similarPersons = new HashMap<String, Person>();

		// SocialGraphStore.getStore().query2(types, rootVertexPerson,
		// similarPersons.keySet());
		for (int i = 0; i < 100; i++) {
			Person personTobeCompare = persons.get(Utils.getRandomInt(persons
					.size() - 1));
			for (Person person : persons) {
				if (person
						.getUserName()
						.toLowerCase()
						.indexOf(personTobeCompare.getFirstName().toLowerCase()) >= 0) {// FIXME
					similarPersons.put(person.getId(), person);
				}
			}
			Thread.currentThread().sleep(100);
			List<Person> rtPersons = SocialGraphStore.getStore().query(types,
					rootVertexPerson, similarPersons.keySet());
			System.out.println(i + "--- ready,  " + rtPersons);
		}
		// System.out.println(rtPersons);
	}

	@Test
	public void testInitGraph() {
		PersonStore.getStore().prepareData();

		ActivityTypeStore.getStore().prepareData();

		ActivityStreamStore.getStore().prepareData();

		SocialGraphStore.getStore().prepareData();

	}

	@Test
	public void testQueryRelation() throws IOException {
		PersonStore.getStore().init();

		ActivityTypeStore.getStore().init();

		ActivityStreamStore.getStore().init();

		SocialGraphStore.getStore().init();

		System.out.println(SocialGraphStore.getStore().queryRelation(
				"744133ea-78e3-43ab-a173-e4ccdd77374e", null));

	}

	@Test
	public void testQueryRelation2() throws IOException {
		PersonStore.getStore().init();

		ActivityTypeStore.getStore().init();

		ActivityStreamStore.getStore().init();

		SocialGraphStore.getStore().init();

		System.out.println(SocialGraphStore.getStore().queryRelation(
				"744133ea-78e3-43ab-a173-e4ccdd77374e",
				"8b593839-a30b-447c-8cd1-8dd413584d14"));

	}

	public static void main(String[] args) throws IOException {
		// Test testCase = new StringUtilTest("testPerformanceQuery2");
		// TestSuite suite = new TestSuite();
		// PersonStore.getStore().init();
		//
		// ActivityTypeStore.getStore().init();
		//
		// ActivityStreamStore.getStore().init();
		//
		// SocialGraphStore.getStore().init();
		//
		// BufferedReader stdin = new BufferedReader(new InputStreamReader(
		// System.in));
		// System.out.print("Enter a query string:");
		// SocialSearchServiceImpl service =
		// SocialSearchServiceImpl.getService();
		// while (true) {
		// String queryStr = stdin.readLine();
		// System.out.println(queryStr);
		// List<Person> persons = service.queryPersons(queryStr);
		// System.out.println(persons);
		// }
	}

}
