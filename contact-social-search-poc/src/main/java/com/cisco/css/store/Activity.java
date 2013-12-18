/**
 * 
 */
package com.cisco.css.store;

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
	
	private String thingObjectType;
	
	private String thingId;
	
	private String thingDisplayName;

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

	public String getThingObjectType() {
		return thingObjectType;
	}

	public void setThingObjectType(String thingObjectType) {
		this.thingObjectType = thingObjectType;
	}

	public String getThingId() {
		return thingId;
	}

	public void setThingId(String thingId) {
		this.thingId = thingId;
	}

	public String getThingDisplayName() {
		return thingDisplayName;
	}

	public void setThingDisplayName(String thingDisplayName) {
		this.thingDisplayName = thingDisplayName;
	}
}
