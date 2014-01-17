package com.cisco.css.query;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.cisco.css.store.ActivityType;
import com.cisco.css.store.ActivityTypeStore;

/**
 *  covert user input relation key word to internal relation word
 *  
 * @author brui
 *
 */
public class RelationMapper {

	private static ActivityTypeStore activities = new ActivityTypeStore();
	private static Map<String, String> mapper = new HashMap<String, String>();
	private static List<String> activitiesWords = new ArrayList<String>();
	
	private static RelationMapper rm ;
	
	private static Stemmer stemmer = new Stemmer();
	
	private RelationMapper() {
		init();
	}
	
	/**
	 * get init data from local ,including word stem and synonym word
	 * 
	 */
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
			 String rls = at.getObjectName();
			String ty = p.getProperty(at.getObjectName().replace(" ", ""));//delete white space
			if(ty != null && !ty.isEmpty()) {
				String [] split = ty.split(",");
				
				for(String word : split) {
					mapper.put(word, rls);						
				}				
			}
			activitiesWords.add(rls);
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
			stemmer.add(s.getKey());
			stemmer.stem();
			System.out.println(s.getKey() + " --> " + stemmer.toString());
		}
	}
	
	/**
	 * 
	 * mapping a word to a existed relationship(activity)
	 * @param word
	 * @return
	 */
	public List<String> mappingRelation(String word) {
		String[] b = word.split(" ");
		List<String> stems = new ArrayList<String>();
		for(String s : b) {
			stemmer.add(s);
			stemmer.stem();
			stems.add(stemmer.toString());
		}		
		
		return getMax(calculateWeigth(word, stems));
	}
	
	/**
	 * calculate the distance between input query key word and internal relation word
	 * @param word
	 * @param stems
	 * @return
	 */
	private static Map<String, Double> calculateWeigth(String word, List<String> stems) {
		Map<String, Double> mapperW = initMapper();
		Double max = 0d;
		
		for(Map.Entry<String, String> en : mapper.entrySet()) {
			
			String key = en.getKey();
			//equals original or stem word make success
			if(key.equals(word)) {
				mapperW.put(en.getValue(), 2d);
				return mapperW;
			}
//			Double dis1 = StringDistance.LevenshteinDistancePercent(word, key);
//			Double dis2 = StringDistance.LevenshteinDistancePercent(stem, key);
//			max = dis1 > dis2 ? dis1 :dis2;
			
			max = maxSimilar(key, word, stems);
			Double old = mapperW.get(en.getValue());
			if(max > old) {
				mapperW.put(en.getValue(), max);
			}			
		}
				
		return mapperW;
	}

	/**
	 * got max similarity for each internal relation word
	 * @param key
	 * @param word
	 * @param stems
	 * @return
	 */
	private static Double maxSimilar(String key, String word, List<String> stems) {
		//calculate distance of 'word' and 'key'
		Double max = StringDistance.LevenshteinDistancePercent(word, key);

		//DOTO: some improvement should be implemented 
		for(String str : stems) {
			if(key.contains(str)) {
				max = Math.max(max * 2, 0.6);
			}
			Double dis = StringDistance.LevenshteinDistancePercent(str, key);
			max = Math.max(dis, max);
		}		
		return max;
	}

	private static Map<String, Double> initMapper() {
		Map<String, Double> map = new HashMap<String, Double>();
		for(String str : activitiesWords) {
			map.put(str, 0d);
		}
		return map;
	}

	private static List<String> getMax(Map<String, Double> mapperW) {
		List<String> result = new ArrayList<String>();
		Double max = Collections.max(mapperW.values());
	
		for(Map.Entry<String, Double> en : mapperW.entrySet()) {
			if((max - en.getValue()) < 0.2 || en.getValue() >= 1) {
				result.add(en.getKey());
			}
		}
		
		return result;
	}

	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
	
		RelationMapper rm = RelationMapper.getInstance();
	//	rm.printMapper();
		String test = "meets";
		System.out.println(test + " --> " + rm.mappingRelation(test));
		test = "meetings";
		System.out.println(test + " --> " + rm.mappingRelation(test));
		test = "met";
		System.out.println(test + " --> " + rm.mappingRelation(test));
		test = "share";
		System.out.println(test + " --> " + rm.mappingRelation(test));
		test = "share";
		System.out.println(test + " --> " + rm.mappingRelation(test));
		test = "phone";
		System.out.println(test + " --> " + rm.mappingRelation(test));
		test = "call";
		System.out.println(test + " --> " + rm.mappingRelation(test));
		test = "phone call";
		System.out.println(test + " --> " + rm.mappingRelation(test));
		test = "conference";
		System.out.println(test + " --> " + rm.mappingRelation(test));
	}

}
