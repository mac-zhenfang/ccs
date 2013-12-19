/**
 * 
 */
package com.cisco.css.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhefang
 * 
 */
public class Relation {

	Person person;
	// FIXME remove duplication
	Map<String, Thing> things = new HashMap<String, Thing>();

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Map<String, Thing> getThings() {
		return things;
	}

	public void addThing(Thing thing) {
		if(!things.containsKey(thing))
			things.put(thing.getId(), thing);
	}
}
