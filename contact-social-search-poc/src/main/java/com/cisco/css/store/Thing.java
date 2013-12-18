/**
 * 
 */
package com.cisco.css.store;

import java.util.UUID;

/**
 * @author zhefang
 *
 */
public class Thing {
	
	public String getThingObjectType() {
		return targetObjectType;
	}

	public void setThingObjectType(String targetObjectType) {
		this.targetObjectType = targetObjectType;
	}

	public String getId() {
		return id;
	}


	public String getThingDisplayName() {
		return targetDisplayName;
	}

	public void setThingDisplayName(String targetDisplayName) {
		this.targetDisplayName = targetDisplayName;
	}

	private String targetObjectType;
	
	private String id = UUID.randomUUID().toString();
	
	private String targetDisplayName;
}
