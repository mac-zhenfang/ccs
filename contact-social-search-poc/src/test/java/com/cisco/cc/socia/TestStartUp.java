/**
 * 
 */
package com.cisco.cc.socia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import com.cisco.css.service.SocialSearchServiceImpl;
import com.cisco.css.store.ActivityStreamStore;
import com.cisco.css.store.ActivityTypeStore;
import com.cisco.css.store.Person;
import com.cisco.css.store.PersonStore;
import com.cisco.css.store.SocialGraphStore;

/**
 * @author zhefang
 * 
 */
public class TestStartUp {

	@Test
	public void testStartup() {

		PersonStore.getStore().init();

		ActivityTypeStore.getStore().init();

		ActivityStreamStore.getStore().init();

		SocialGraphStore.getStore().prepareData();
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
		
		System.out.println(SocialGraphStore.getStore().queryRelation("744133ea-78e3-43ab-a173-e4ccdd77374e"));
		
	}
	public static void main(String[] args) throws IOException {

		PersonStore.getStore().init();

		ActivityTypeStore.getStore().init();

		ActivityStreamStore.getStore().init();

		SocialGraphStore.getStore().init();

		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.print("Enter a query string:");
		SocialSearchServiceImpl service = SocialSearchServiceImpl.getService();
		while (true) {
			String queryStr = stdin.readLine();
			System.out.println(queryStr);
			List<Person> persons = service.queryPersons(queryStr);
			System.out.println(persons);
		}
	}
}
