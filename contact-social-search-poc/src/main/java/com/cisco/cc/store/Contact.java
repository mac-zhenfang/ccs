/**
 * 
 */
package com.cisco.cc.store;

import java.util.UUID;

/**
 * @author zhefang
 * 
 */
public class Contact {

	private String firstName;
	private String lastName;
	private String pictureUrl;
	private String headline;
	private String id = UUID.randomUUID().toString();
	
	public String getId() {
		return id;
	}


	public String getFirstName() {
		return firstName;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
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
				+ " headline: " + headline + " id: " + id;
	}

}
