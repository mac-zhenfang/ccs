/**
 * 
 */
package com.cisco.cc.store;

import java.util.UUID;

/**
 * @author zhefang
 * 
 */
public class Person {

	private String firstName;
	private String lastName;
	private String userName;
	private String pictureUrl;
	private String id = UUID.randomUUID().toString();
	
	public String getId() {
		return id;
	}


	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	@Override
	public String toString() {
		return "firstName: " + firstName + " lastName: " + lastName
				+ " id: " + id;
	}
	
	
	public String getUserName() {
		if(null == userName) {
			return firstName + " " + lastName;
		}
		return userName;
	}
	//FIXME
	public void setUserName(String userName) {
		if(null == userName) {
			this.userName = firstName + " " + lastName;
		}
	}
}
