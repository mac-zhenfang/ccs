/**
 * 
 */
package com.cisco.cc.store;

import java.util.UUID;

/**
 * @author zhefang
 *
 */
public class Target {
	
	public String getTargetObjectType() {
		return targetObjectType;
	}

	public void setTargetObjectType(String targetObjectType) {
		this.targetObjectType = targetObjectType;
	}

	public String getId() {
		return id;
	}


	public String getTargetDisplayName() {
		return targetDisplayName;
	}

	public void setTargetDisplayName(String targetDisplayName) {
		this.targetDisplayName = targetDisplayName;
	}

	private String targetObjectType;
	
	private String id = UUID.randomUUID().toString();
	
	private String targetDisplayName;
}
