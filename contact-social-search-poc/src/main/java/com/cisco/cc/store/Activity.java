/**
 * 
 */
package com.cisco.cc.store;

/**
 * @author zhefang
 *
 */
public class Activity {
	
	private String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private long timestamp;
	
	private String actorObjectType;
	
	private String actorId;
	
	private String actorDisplayName;
	
	private String verb;
	
	private String targetObjectType;
	
	private String targetId;
	
	private String targetDisplayName;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getActorObjectType() {
		return actorObjectType;
	}

	public void setActorObjectType(String actorObjectType) {
		this.actorObjectType = actorObjectType;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getActorDisplayName() {
		return actorDisplayName;
	}

	public void setActorDisplayName(String actorDisplayName) {
		this.actorDisplayName = actorDisplayName;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getTargetObjectType() {
		return targetObjectType;
	}

	public void setTargetObjectType(String targetObjectType) {
		this.targetObjectType = targetObjectType;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetDisplayName() {
		return targetDisplayName;
	}

	public void setTargetDisplayName(String targetDisplayName) {
		this.targetDisplayName = targetDisplayName;
	}
}
