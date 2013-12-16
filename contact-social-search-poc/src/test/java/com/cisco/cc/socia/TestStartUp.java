/**
 * 
 */
package com.cisco.cc.socia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import com.cisco.cc.service.SocialSearchService;
import com.cisco.cc.store.ActivityStreamStore;
import com.cisco.cc.store.ActivityTypeStore;
import com.cisco.cc.store.Person;
import com.cisco.cc.store.PersonStore;
import com.cisco.cc.store.SocialGraphStore;

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

	public static void main(String[] args) throws IOException {
		
		PersonStore.getStore().init();

		ActivityTypeStore.getStore().init();

		ActivityStreamStore.getStore().init();

		SocialGraphStore.getStore().init();
		
		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.print("Enter a query string:");
		SocialSearchService service = SocialSearchService.getService();
		while (true) {
			String queryStr = stdin.readLine();
			System.out.println(queryStr);
			List<Person> persons = service.queryPersons(queryStr);
			System.out.println(persons);
		}	}
}
