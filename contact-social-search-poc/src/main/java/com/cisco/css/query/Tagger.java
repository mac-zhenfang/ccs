package com.cisco.css.query;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Tagger {

	/**
	 * NN--Noun, singular or mass ;NNP--Proper Noun, singular ;NNPS--Proper Noun, plural 
	 * Noun, plural;
	 * PRP--Personal Pronoun e.g. I, me, you, he..
	 * VB--Verb, base form 
	 * VB*--
	 * 
	 */
	private static MaxentTagger tagger = new MaxentTagger("taggers/english-caseless-left3words-distsim.tagger");
	private static String tagged;
	private static String[] taggedA;
	private static String[] untaggedA;
	private static int len;
	private static String firstVB = null;
	private static int firstVBindex = 0;
	private static String secondVB = null;
	private static int secondVBindex = 0;
	private static String thrdVB = null;
	private static int thrdVBindex = 0;
	
	private static String firstNN = null;
	private static int firstNNindex = 0;
	private static String secondNN = null;
	private static int secondNNindex = 0;
	private static String thrdNN = null;
	private static int thrdNNindex = 0;
	
	private static int numVB = 0;
	private static int numNN = 0;
	
	public static String startP = null;
	public static String endP = null;
	public static String relation = null;
	public static List<String> relationMapped = null;
	public static List<String> timeWords = new ArrayList<String>();
	public static String time = null;
	
	private static String prp = "Mac";
	
	private static Map<String, Object> graph = new HashMap<String, Object>();
	private static RelationMapper rm;
	static {
		if(rm == null) {
			rm = RelationMapper.getInstance();
		}
		InputStream inputStream = null;
		Properties p = new Properties();   
		 try {   
			 inputStream = Object.class.getResourceAsStream("/data/timeWordsData");   
			 p.load(inputStream);  
			 String tws = p.getProperty("timeWords");
			 String[] twl = tws.split(",");
			 for(String tw : twl) {
				 timeWords.add(tw);
			 }
		 } catch (IOException e1) {   
			 e1.printStackTrace();   
		 } finally {
			 if(inputStream != null) {
				 try {
					inputStream.close();
				} catch (IOException e) {					
					e.printStackTrace();
				}
			 }
		 }
	}
	
	public static void init(String queryStr, String prpStr) {
		prp = prpStr;
		
		init(queryStr);				
		 
	}
	
	private static String dateP = "(\\d{2,4}[-|/]\\d{1,2}[-|/]\\d{1,2}( +\\d{1,2}:\\d{1,2}(:\\d{1,2}){0,1}){0,1}( *to *((\\d{2,4}[-|/]\\d{1,2}[-|/]\\d{1,2}){0,1}( *\\d{1,2}:\\d{1,2}(:\\d{1,2}){0,1}){0,1})){0,1})";
	private static String timeP = "(\\d{1,2}:\\d{1,2}(:\\d{1,2}){0,1}( *to *(\\d{1,2}:\\d{1,2}(:\\d{1,2}){0,1})))";

	
	/**
	 * init all static paramters 
	 * @param queryStr
	 */
	public static void init(String queryStr) {		
		graph.put("startP", startP);
		graph.put("endP", endP);
		if(relationMapped != null) {
			relationMapped.clear();
		}
		graph.put("relation", relationMapped);
		graph.put("time", time);
		
		tagged = getTaggedStr(queryStr.toLowerCase());
		System.out.println(tagged);
		untaggedA = queryStr.toLowerCase().split(" ");
		taggedA = tagged.split(" ");
		len = taggedA.length;
		startP = null;
		endP = null;
		relation = null;
		time = getTime(queryStr.toLowerCase());
		numNN = 0;
		numVB = 0;
		firstVB = null;
		firstVBindex = 0;
		secondVB = null;
		secondVBindex = 0;
		thrdVB = null;
		thrdVBindex = 0;
		
		firstNN = null;
		firstNNindex = 0;
		secondNN = null;
		secondNNindex = 0;
		thrdNN = null;
		thrdNNindex = 0;
	}
	
	private static String getTime(String src) {
		String result = null;
		result = getDateTime(src);
		if(result != null && result.contains("to")) {
			return result;
		}
		Pattern pattern = Pattern.compile(timeP);
		Matcher matcher = pattern.matcher(src);
		
		if(matcher.find()) {			
			if(result != null ) {
				result += " " + matcher.group(1);
				return result;
			} else if(result == null) {
				result = matcher.group(1);
			}			
		} 
		//have no specified time/date.check words
		if(result == null) {
			for(int i = 0 ; i < len; i++) {
				if(timeWords.contains(untaggedA[i])){
					return untaggedA[i];
				} else if((untaggedA[i].equals("past") || untaggedA[i].equals("last"))) {
					if(isNummber(taggedA[i+1])) {
						return untaggedA[i] + " " + untaggedA[i+1] + " " + untaggedA[i+2];
					} else if(isNN(taggedA[i+1])){
						return untaggedA[i] + " " + untaggedA[i+1];
					}
				}
			}
		}
		
		return result;
	}

	private static String getDateTime(String src) {
		Pattern pattern = Pattern.compile(dateP);
		Matcher matcher = pattern.matcher(src);
		String result = null;
		if (matcher.find()) {
			result = matcher.group(1);						
		} 
		
		return result;
	}
	
	private static String getTaggedStr(String src) {
		return tagger.tagString(src);
	}

	private static boolean isVB(String word) {
		return word.indexOf("_VB") > 0;
	}
	
	private static boolean isNN(String word) {
		return word.indexOf("_NN") > 0;
	}
	
	private static String getWord(String word) {
		return word.substring(0, word.indexOf("_"));
	}
	
	private static boolean isNummber(String word) {
		return word.indexOf("_CD") > 0;
	}
	/**
	 * analysis the content String to split out those key words.  including vb and nn
	 * then generate a result startP/endP/relation
	 * 
	 */
	public static void analysis() {
		int vbCount = 0;
		int nnCount = 0;
		
		/*whether a sentence start with a WP word*/
		if(taggedA[0].endsWith("_WP")) {
			firstNN = getWord(taggedA[0]);
			nnCount++;
		}
		
		for(int i = 0; i < len; i++) {
			if(isVB(taggedA[i])) {	
				String vb = getWord(taggedA[i]);
				int skip = 0;
				//like or hate
				if(i+2 < len && (taggedA[i+1].equals("and_CC") || taggedA[i+1].equals("or_CC")) && isVB(taggedA[i+2])) {
					vb += " " + getWord(taggedA[i+1]) + " " + getWord(taggedA[i+2]);
					skip += 2;
				}				
				//have meetings (with)
				if(i+1 < len && i+2 >= len && (isNN(taggedA[i+1]) || isVB(taggedA[i+1]))) {
					vb += " " + getWord(taggedA[i+1]);
					skip += 1;
				}
		
				//do like bean and milk/do have meeting
//				if(i+3 < len && skip == 0 && isVB(taggedA[i+1]) && isNN(taggedA[i+2]) && !taggedA[i+3].equals("and_CC") && !taggedA[i+3].equals("or_CC")) {
//					vb += " " + getWord(taggedA[i+1]) + " " + getWord(taggedA[i+2]);
//					skip += 2;
//				} else if(i+3 < len && isVB(taggedA[i+1]) && !isNN(taggedA[i+2]) && !isVB(taggedA[i+2]) && !taggedA[i+3].equals("and_CC") && !taggedA[i+3].equals("or_CC")) {
//					vb += " " + getWord(taggedA[i+1]);
//					skip += 1;
//				}
//				//like bean and milk
//				if(i+2 < len && skip == 0 && (isNN(taggedA[i+1]) || isVB(taggedA[i+1]))&& !taggedA[i+2].equals("and_CC") && !taggedA[i+2].equals("or_CC")) {
//					vb += " " + getWord(taggedA[i+1]);
//					skip += 1;
//				}
				
				int dis = disOfNextNN(i);
				if(dis > 0 && dis < 3) {
					int disi = 0;
					while(disi < dis) {
						vb += " " + getWord(taggedA[i + disi + 1]);
						disi++;
					}	
					skip = dis;
					if(i + dis + 1 < len && isNN(taggedA[i + dis + 1])) {
						vb += " " + getWord(taggedA[i + dis + 1]);
						skip++;
					}			
				}
				
				if(vbCount == 0) {
					firstVBindex = i;
					firstVB = vb;					
				} else if(vbCount == 1){
					secondVBindex = i;
					secondVB = vb;					
				} else if(vbCount == 2){
					thrdVBindex = i;
					thrdVB = vb;
				}
				vbCount ++;
				i += skip;
				continue;
			}
			if(isNN(taggedA[i]) 
					|| taggedA[i].endsWith("me_PRP") 
					|| taggedA[i].endsWith("I_PRP") 
					|| taggedA[i].endsWith("i_PRP")) {	
				String nn = getWord(taggedA[i]);
				
				//replace 'me' with a real Name
				if(taggedA[i].endsWith("me_PRP")) {
					nn = prp;
				}
				
				//replace 'me' with a real Name
				if(taggedA[i].endsWith("I_PRP") || taggedA[i].endsWith("i_PRP")) {
					nn = prp;
				}
				
				int skip = 0;
				if(i+2 < len && (taggedA[i+1].equals("and_CC") || taggedA[i+1].equals("or_CC")) && isNN(taggedA[i+2])) {
					nn += " " + getWord(taggedA[i+1]) + " " + getWord(taggedA[i+2]);
					skip += 2;
				}
				
				if(nnCount == 0) {
					firstNNindex = i;
					firstNN = nn;
				} else if(nnCount == 1) {
					secondNNindex = i;
					secondNN = nn;
				} else if(nnCount == 2) {
					thrdNNindex = i;
					thrdNN = nn;
				}
				nnCount ++;
				i += skip;
			}
		}
		numNN = nnCount;
		numVB = vbCount;
		
		generate();
	}
	
	private static int disOfNextNN(int i) {
		i++;
		int dis = 0;
		while(i < len) {
			dis++;
			if(isNN(taggedA[i])) {
				return dis;
			}
			i++;			
		}
		//if no NN 
		return 0;
	}

	private static void generate() {
		if(numNN == 1) {
			
			if(firstVB != null && firstVB.split(" ").length == 2) {
				String[] s = firstVB.split(" ");
				firstVB = s[0];
				secondNN = s[1];
			}
		}
		
		if(firstVB != null && secondNN == null && firstVB.split(" ").length > 1) {
			String [] s = firstVB.split(" ");
			secondNN = s[s.length-1];
			relation = firstVB.substring(0, firstVB.length() - secondNN.length() - 1);
		} else {
			relation = firstVB;
		}
		if(firstVB != null && firstVB.split(" ").length == 1) {
			relation += " " + firstNN;
		}
		
		if(relation != null) {
			relationMapped = rm.mappingRelation(StopWords.stop(relation));
		}		
		endP = firstNN;
		startP = secondNN;
	}
	
	private static String getFirstVB() {
		return firstVB;
	}
	
	private static String getFirstNN() {
		return firstNN;
	}
	
	private static String getSecondVB() {
		return secondVB;
	}
	
	private static String getSecondNN() {
		return secondNN;
	}
	
	public static boolean isSimple() {
		if(numVB > 2 || numNN > 3) {
			return false;
		}
		return true;
	}
	
	private static String getTarget() {
		return firstNN;
	}
	
	public static void print() {
		String t = " firstVB:" + firstVB;
		t += "\n secondVB:" + secondVB;
		t += "\n firstNN:" + firstNN;
		t += "\n secondNN:" + secondNN;
		t += "\n VB index" + firstVBindex + ", " + secondVBindex;
		t += "\n NN index" + firstNNindex + ", " + secondNNindex;
		
		System.out.println(t);
	}
	
	public static void printTarget() {
		String t = "startP: " + startP;
		t += "\nrelation: " + relation;
		t += "\nrelationMapped: ";
		for(String s : relationMapped) {
			t += s + "\n";
		}
		t += "\nendP: " + endP;
		t += "\ntime: " + time;
		System.out.println(t);
	}
	
	public static Map<String, Object> getGraph() {
		return graph;
	}
	
	public static void main(String[] args) {
		// http://www.computing.dcu.ie/~acahill/tagset.html the tagger 

		String sample = "Tom who have meeting call with peter last week";		 
		Tagger.init(sample, "Mac");
		Tagger.analysis();
		if(!Tagger.isSimple()) {
			System.err.println("Your query is too complex to analysis");
		}				
		Tagger.printTarget();
		System.out.println("-----------------------------------------------------");	
		//----------------------------------------------------------------------------
		// Someone who i meet with between 2012 to 2013 
		sample = "Vagou who I have content share with past 4 day";		 
		Tagger.init(sample, "Mac");
		Tagger.analysis();
		if(!Tagger.isSimple()) {
			System.err.println("Your query is too complex to analysis");
		}				
		Tagger.printTarget();
		System.out.println("-----------------------------------------------------");
		//----------------------------------------------------------------------------
		sample = "Vagou who I do have meeting 2013-2-3 23:3:4 to 2013-2-4 23:3:4";		 
		Tagger.init(sample, "Mac");
		Tagger.analysis();
		if(!Tagger.isSimple()) {
			System.err.println("Your query is too complex to analysis");
		}				
		Tagger.printTarget();
		System.out.println("-----------------------------------------------------");
		//----------------------------------------------------------------------------
		sample = "Vagou who I  have a conference call with 23:3:4 to 34:34";		 
		Tagger.init(sample, "Mac");
		Tagger.analysis();
		if(!Tagger.isSimple()) {
			System.err.println("Your query is too complex to analysis");
		}				
		Tagger.printTarget();
		System.out.println("-----------------------------------------------------");
		//----------------------------------------------------------------------------
		sample = "meeting i have 2014/1/4 3:34 to 4:56";		 
		Tagger.init(sample, "Mac");
		Tagger.analysis();
		if(!Tagger.isSimple()) {
			System.err.println("Your query is too complex to analysis");
		}				
		Tagger.printTarget();
		System.out.println("-----------------------------------------------------");

	}

}
