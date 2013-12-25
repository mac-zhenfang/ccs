package com.cisco.css.query;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.cisco.css.store.ActivityType;
import com.cisco.css.store.ActivityTypeStore;

public class RelationMapper {

	ActivityTypeStore activities = new ActivityTypeStore();
	Map<String, String> mapper = new HashMap<String, String>();
	
	private static RelationMapper rm ;
	
	private RelationMapper() {
		init();
	}
	
	private void init() {
		 activities.init();	
		 InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("data/relationMapperData");   
		 Properties p = new Properties();   
		 try {   
		  p.load(inputStream);   
		 } catch (IOException e1) {   
		  e1.printStackTrace();   
		 }   
		  
		 List<ActivityType> list = getFullActivityTypes();
		 for(ActivityType at : list) {
			String ty = p.getProperty(at.getObjectName().replace(" ", ""));//delete white space
			if(ty != null && !ty.isEmpty()) {
				String [] split = ty.split(",");
				
				for(String word : split) {
					mapper.put(word, at.getObjectName());
				}				
			}
		 }		
	}
	
	public static RelationMapper getInstance() {
		if(rm == null) {
			rm = new RelationMapper();
		}
		return rm;
	}
	
	public List<ActivityType> getFullActivityTypes() {
		return activities.getFullActivityTypes();
	}
	
	public void printMapper() {
		for(Map.Entry<String, String> s : mapper.entrySet()) {
			System.out.println(s.getKey() + " --> " + s.getValue());
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		RelationMapper rm = RelationMapper.getInstance();
		rm.printMapper();
	}

}
