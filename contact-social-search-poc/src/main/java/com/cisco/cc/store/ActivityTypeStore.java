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
import org.codehaus.jackson.map.ObjectMapper;

import com.cisco.cc.util.Utils;

public class ActivityTypeStore implements IStore{
	
	Map<String, List<String>> activitiesMap = new HashMap<String, List<String>>();
	static ActivityTypeStore store = new ActivityTypeStore();
	ActivityType[] activityTypes;
	
	public static ActivityTypeStore getStore(){
		return store;
	}
	
	public void init() {
		
		File verbs;
		try {
			ObjectMapper mapper = new ObjectMapper();
			verbs = new File(ActivityTypeStore.class.getClassLoader()
					.getResource("data/verb").toURI().getPath());
			InputStream is = new FileInputStream(verbs);
			byte[] filebt = Utils.readStream(is);
			System.out.println(" types : " + new String(filebt));
			activityTypes = mapper.readValue(new String(filebt),
					ActivityType[].class);
			for (ActivityType type : activityTypes) {
				String objectName = type.getObjectName();
				List<String> lst = new ArrayList<String>();
				if (activitiesMap.containsKey(objectName)) {
					
					lst.addAll(activitiesMap.get(objectName));
				}
				lst.addAll(Arrays.asList(type.getVerbs()));
				activitiesMap.put(type.getObjectName(), lst);
			}
			System.out.println(" --- ActivityTypeStore init done --- ");
		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}
	
	public List<ActivityType> getFullActivityTypes(){
		return Arrays.asList(activityTypes);
	}
	public List<String> getVerbs (String objectName) {
		return activitiesMap.get(objectName);
	}
	
	public Map<String, List<String>> getVerbMap () {
		return activitiesMap;
	}
	
	public Set<String> getVerbs () {
		Set<String> verbs = new HashSet<String>();
		Iterator<String> verbIter = activitiesMap.keySet().iterator();
		while(verbIter.hasNext()){
			List<String> verbList = activitiesMap.get(verbIter.next());
			for(String verb : verbList){
				verbs.add(verb);
			}
		}
		return verbs;
	}
}
