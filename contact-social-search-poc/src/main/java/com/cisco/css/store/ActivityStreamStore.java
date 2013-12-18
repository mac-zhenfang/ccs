/**
 * 
 */
package com.cisco.css.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.cisco.css.util.Utils;

/**
 * @author zhefang
 * 
 *         DAO
 */
public class ActivityStreamStore extends Store {

	private Map<String, Activity> activityMap = new HashMap<String, Activity>();

	private List<Thing> usedThings = new ArrayList<Thing>();

	private Map<String, Person> usedPersons = new HashMap<String, Person>();

	private int countPerThing = 2;
	private int thingCount = 10;
	private AtomicBoolean isInsane = new AtomicBoolean(true);

	private static ActivityStreamStore store = new ActivityStreamStore();

	public static ActivityStreamStore getStore() {
		return store;
	}

	public void init() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerSubtypes(Activity.class);
			activityMap = mapper.readValue(readFromFile("data/activityMap"),
					new TypeReference<Map<String, Activity>>() {
					});
			mapper.registerSubtypes(Person.class);
			usedPersons = mapper.readValue(readFromFile("data/usedPersons"),
					new TypeReference<Map<String, Person>>() {
					});
			mapper.registerSubtypes(Thing.class);
			usedThings = mapper.readValue(readFromFile("data/usedThings"),
					new TypeReference<List<Thing>>() {
					});
			System.out.println(" --- ActivityStreamStore init done --- ");
			isInsane.set(false);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 
	 * */
	public void prepareData() {

		usedThings = initActivityThing();

		for (Thing thing : usedThings) {
			int i = 0;
			int j = 0;
			int randomInt = Utils.getRandomInt(countPerThing);
			do {
				String objectName = thing.getThingDisplayName();
				List<String> verbsOfObjectType = ActivityTypeStore.getStore()
						.getVerbs(objectName);
				String mainVerb = verbsOfObjectType.get(0);
				String mainKey = thing.getId() + mainVerb;
				for (Person person : PersonStore.getStore().getSocialPersons()) {
					for (String verb : verbsOfObjectType) {

						Activity activity = new Activity();
						activity.setId(UUID.randomUUID().toString());
						activity.setActorDisplayName(person.getFirstName()
								+ " " + person.getLastName());
						// FIXME person is a person for sure
						activity.setActorObjectType("person");
						activity.setActorId(person.getId());
						// FIXME, should be all
						activity.setActorDisplayName(person.getUserName());
						activity.setThingId(thing.getId());
						activity.setThingObjectType(thing.getThingObjectType());
						activity.setThingDisplayName(thing
								.getThingDisplayName());
						activity.setTimestamp(System.currentTimeMillis());
						activity.setVerb(verb);
						// the mainKey means the start, init, for 1 thing
						// instance,
						// e.g meeting, only can start for once
						if (!activityMap.containsKey(mainKey)
								&& mainVerb.equals(verb)) {
							activityMap.put(mainKey, activity);
							if (!usedPersons.containsKey(person.getId()))
								usedPersons.put(person.getId(), person);
						} else {
							if (!verb.equals(mainVerb)) {
								activityMap.put(thing.getId() + verb + j++,
										activity);
								if (!usedPersons.containsKey(person.getId()))
									usedPersons.put(person.getId(), person);
							}
						}
					}
				}
			} while (i++ < randomInt);
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			writeToFile("data/activityMap", activityMap, mapper);
			writeToFile("data/usedPersons", usedPersons, mapper);
			writeToFile("data/usedThings", usedThings, mapper);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(" --- ActivityStreamStore init done --- ");
		isInsane.set(false);
	}

	public Map<String, Activity> getActivityMap() {
		if (isInsane.get())
			throw new RuntimeException("Activity Stream Store is NOT ready");
		return activityMap;
	}

	public List<Thing> getUsedThings() {
		if (isInsane.get())
			throw new RuntimeException("Activity Stream Store is NOT ready");
		return usedThings;
	}

	public Map<String, Person> getUsedPersons() {
		if (isInsane.get())
			throw new RuntimeException("Activity Stream Store is NOT ready");
		return usedPersons;
	}

	/**
	 * init 1000 things
	 * */
	private List<Thing> initActivityThing() {
		int i = 0;
		List<Thing> list = new ArrayList<Thing>();
		List<ActivityType> types = ActivityTypeStore.getStore()
				.getFullActivityTypes();
		for (ActivityType activityType : types) {
			do {
				Thing thing = new Thing();
				// FIXME, should be thing in the object itself
				thing.setThingObjectType("thing");
				thing.setThingDisplayName(activityType.getObjectName());
				list.add(thing);
			} while (i++ < thingCount);
		}

		return list;
	}

	public static void main(String[] args) {
		PersonStore.getStore().init();
		ActivityTypeStore.getStore().init();
		ActivityStreamStore.getStore().prepareData();
		ActivityStreamStore.getStore().init();
		// System.out.println(ActivityStreamStore.getStore().getActivityMap());
		System.out.println(ActivityStreamStore.getStore().getUsedPersons());
		// System.out.println(ActivityStreamStore.getStore().getUsedThings());
	}
}
