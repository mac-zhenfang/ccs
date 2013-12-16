package com.cisco.cc.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import com.cisco.cc.util.Utils;

public class ActivityTypeStore extends Store {

	Map<String, List<String>> activityTypeMap = new HashMap<String, List<String>>();
	static ActivityTypeStore store = new ActivityTypeStore();
	ActivityType[] activityTypes;

	public static ActivityTypeStore getStore() {
		return store;
	}

	public void init() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			activityTypes = mapper.readValue(readFromFile("data/verb"),
					ActivityType[].class);
			mapper.registerSubtypes(List.class);
			activityTypeMap = mapper.readValue(readFromFile("data/activityTypeMap"),
					HashMap.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void prepareData() {

		File verbs;
		try {
			ObjectMapper mapper = new ObjectMapper();
			activityTypes = mapper.readValue(readFromFile("data/verb"),
					ActivityType[].class);
			for (ActivityType type : activityTypes) {
				String objectName = type.getObjectName();
				List<String> lst = new ArrayList<String>();
				if (activityTypeMap.containsKey(objectName)) {

					lst.addAll(activityTypeMap.get(objectName));
				}
				lst.addAll(Arrays.asList(type.getVerbs()));
				activityTypeMap.put(type.getObjectName(), lst);
			}
			writeToFile("data/activityTypeMap", activityTypeMap, mapper);
			System.out.println(" --- ActivityTypeStore init done --- ");
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public List<ActivityType> getFullActivityTypes() {
		return Arrays.asList(activityTypes);
	}

	public ActivityType getActivityType(String type) {
		if (activityTypeMap.containsKey(type)) {
			ActivityType activity = new ActivityType();
			activity.setObjectName(type);
			activity.setVerbs(activityTypeMap.get(type).toArray(new String[0]));
			return activity;
		}
		return null;
	}

	public List<String> getVerbs(String objectName) {
		return activityTypeMap.get(objectName);
	}

	public Map<String, List<String>> getVerbMap() {
		return activityTypeMap;
	}

	public Set<String> getVerbs() {
		Set<String> verbs = new HashSet<String>();
		Iterator<String> verbIter = activityTypeMap.keySet().iterator();
		while (verbIter.hasNext()) {
			List<String> verbList = activityTypeMap.get(verbIter.next());
			for (String verb : verbList) {
				verbs.add(verb);
			}
		}
		return verbs;
	}
	public static void main(String [] args) {
		ActivityTypeStore.getStore().init();
		System.out.println(ActivityTypeStore.getStore().getVerbs());
		System.out.println(ActivityTypeStore.getStore().getFullActivityTypes());
		
	}
}
