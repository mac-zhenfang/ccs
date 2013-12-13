/**
 * 
 */
package com.cisco.cc.store;

/**
 * @author zhefang
 *
 */
public class ActivityType {
	
	private String objectName;
	
	private String [] verbs;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String[] getVerbs() {
		return verbs;
	}

	public void setVerbs(String[] verbs) {
		this.verbs = verbs;
	}
}
