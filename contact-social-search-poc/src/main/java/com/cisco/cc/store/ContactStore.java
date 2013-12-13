/**
 * This would not be used after we can access the Contact
 * Do NOT need to produce the fake user list
 */
package com.cisco.cc.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.cisco.cc.util.Utils;

/**
 * @author zhefang
 * 
 */
public class ContactStore implements IStore {

	Map<String, List<Contact>> contactMap = new HashMap<String, List<Contact>>();

	Contact[] contacts;

	static ContactStore store = new ContactStore();

	public static ContactStore getStore() {
		return store;
	}

	/**
	 * Init the list
	 * */
	public void init() {

		try {
			ObjectMapper mapper = new ObjectMapper();
			File linkedin = new File(ContactStore.class.getClassLoader()
					.getResource("data/linkin").toURI().getPath());
			InputStream is = new FileInputStream(linkedin);
			byte[] filebt = Utils.readStream(is);
			contacts = mapper.readValue(new String(filebt), Contact[].class);
			for (Contact contact : contacts) {
				String key = contact.getFirstName();
				List<Contact> lst = new ArrayList<Contact>();
				if (contactMap.containsKey(key)) {
					lst.addAll(contactMap.get(key));
				}
				lst.add(contact);
				contactMap.put(contact.getFirstName(), lst);
			}
			System.out.println(" --- ContactStore init done --- ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Contact> getFullContacts() {
		return Arrays.asList(contacts);
	}

	public List<Contact> get(String key) {

		List<Contact> rtList = contactMap.get(key);
		if (null == rtList) {
			rtList = new ArrayList<Contact>();
		}
		return rtList;

	}

	public Map<String, List<Contact>> getContacts() {
		return contactMap;
	}

	public static void main(String[] args) throws Exception {
		try {
			Map<String, List<Contact>> contactMap = new HashMap<String, List<Contact>>();
			ObjectMapper mapper = new ObjectMapper();
			File linkedin = new File(ContactStore.class.getClassLoader()
					.getResource("data/linkin").toURI().getPath());
			InputStream is = new FileInputStream(linkedin);
			byte[] filebt = Utils.readStream(is);
			Contact[] contacts = mapper.readValue(new String(filebt),
					Contact[].class);
			for (Contact contact : contacts) {
				String key = contact.getFirstName();
				List<Contact> lst = new ArrayList<Contact>();
				if (contactMap.containsKey(key)) {
					lst.addAll(contactMap.get(key));
				}
				lst.add(contact);
				contactMap.put(contact.getFirstName(), lst);
			}

			Iterator<String> it = contactMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (contactMap.get(key).size() > 1) {
					System.out.println(key);
					List<Contact> rstList = contactMap.get(key);
					for (Contact contact : rstList) {
						System.out.println(contact.toString());
					}
				}

			}
			System.out.println("old : " + contacts.length);
			System.out.println("total : " + contactMap.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
