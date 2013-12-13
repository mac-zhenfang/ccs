/**
 * 
 */
package com.cisco.cc.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cisco.cc.util.Utils;

/**
 * @author zhefang
 * 
 *         DAO
 */
public class ActivityStreamStore implements IStore {

	private Map<String, Activity> activityMap = new HashMap<String, Activity>();

	private List<Target> usedTargets = new ArrayList<Target>();

	private Map<String, Contact> usedContacts = new HashMap<String, Contact>();

	private int countPerTarget = 5;
	private int targetCount = 2000;
	private AtomicBoolean isInsane = new AtomicBoolean(true);

	private static ActivityStreamStore store = new ActivityStreamStore();

	public static ActivityStreamStore getStore() {
		return store;
	}

	/**
	 * 
	 * */
	public void init() {

		usedTargets = initActivityTarget();
		for (Target target : usedTargets) {
			int i = 0;
			do { 
				String objectName = target.getTargetDisplayName();
				List<String> verbsOfObjectType = ActivityTypeStore.getStore()
						.getVerbs(objectName);
				String mainVerb = verbsOfObjectType.get(0);
				String mainKey = target.getId() + mainVerb;
				for (String verb : verbsOfObjectType) {
					Contact contact = getRandomContact();
					Activity activity = new Activity();
					activity.setId(UUID.randomUUID().toString());
					activity.setActorDisplayName(contact.getFirstName() + " "
							+ contact.getLastName());
					// FIXME contact is a person for sure
					activity.setActorObjectType("person");
					activity.setActorId(contact.getId());
					// FIXME, should be all
					activity.setActorDisplayName(contact.getFirstName());
					activity.setTargetId(target.getId());
					activity.setTargetObjectType(target.getTargetObjectType());
					activity.setTargetDisplayName(target.getTargetDisplayName());
					activity.setTimestamp(System.currentTimeMillis());
					activity.setVerb(verb);
					// the mainKey means the start, init, for 1 target instance,
					// e.g meeting, only can start for once
					if (!activityMap.containsKey(mainKey)) {
						activityMap.put(mainKey, activity);
						if (!usedContacts.containsKey(contact.getId()))
							usedContacts.put(contact.getId(), contact);
					} else {
						if (!verb.equals(mainVerb)) {
							activityMap
									.put(target.getId() + verb + i, activity);
							if (!usedContacts.containsKey(contact.getId()))
								usedContacts.put(contact.getId(), contact);
						}
					}

				}
			} while (i++ < countPerTarget);
		}
		System.out.println(" --- ActivityStreamStore init done --- ");
		isInsane.set(false);
	}

	public Map<String, Activity> getActivityMap() {
		if (isInsane.get())
			throw new RuntimeException("Activity Stream Store is NOT ready");
		return activityMap;
	}

	public List<Target> getUsedTargets() {
		if (isInsane.get())
			throw new RuntimeException("Activity Stream Store is NOT ready");
		return usedTargets;
	}

	public Map<String, Contact> getUsedContacts() {
		if (isInsane.get())
			throw new RuntimeException("Activity Stream Store is NOT ready");
		return usedContacts;
	}

	/**
	 * init 1000 targets
	 * */
	private List<Target> initActivityTarget() {
		int i = 0;
		List<Target> list = new ArrayList<Target>();
		do {
			ActivityType activityType = getRandomActivityType();
			Target target = new Target();
			// FIXME, should be target in the object itself
			target.setTargetObjectType("target");
			target.setTargetDisplayName(activityType.getObjectName());
			list.add(target);
		} while (i++ < targetCount);

		return list;
	}

	private Contact getRandomContact() {
		List<Contact> contacts = ContactStore.getStore().getFullContacts();
		int size = contacts.size();
		return contacts.get(Utils.getRandomInt(size - 1));
	}

	private ActivityType getRandomActivityType() {
		List<ActivityType> types = ActivityTypeStore.getStore()
				.getFullActivityTypes();
		int size = types.size();
		return types.get(Utils.getRandomInt(size - 1));
	}
}
