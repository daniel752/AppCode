package com.bankingapplication.Model;

import java.util.ArrayList;

/**
 * Class User for all users that extend from this class
 * e.g - Admin,Clerk,Profile
 */
public class User
{
	public enum USER_TYPE
	{
		ADMIN(1),
		CLERK(2),
		PROFILE(3);

		int value;

		USER_TYPE(int i)
		{
			value = i;
		}
		public int getValue() {return value;}
	}

//	private String firstName;
//	private String lastName;
//	private String country;
	private String username;
	private String password;
	private long dbId;

	public User(String username, String password)
	{
//		this.firstName = firstName;
//		this.lastName = lastName;
//		this.country = country;
		this.username = username;
		this.password = password;
	}

	public User(String username, String password, long dbId)
	{
		this(username, password);
		this.dbId = dbId;
	}

//	public String getFirstName() {return firstName;}
//	public void setFirstName(String firstName) {this.firstName = firstName;}
//	public String getLastName() {return lastName;}
//	public void setLastName(String lastName) {this.lastName = lastName;}
//	public String getCountry() {return country;}
//	public void setCountry(String country) {this.country = country;}
	public String getUsername() {return username;}
	public void setUsername(String username) {this.username = username;}
	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}
	public long getDbId() {return dbId;}
	public void setDbId(long dbId) {this.dbId = dbId;}

	@Override
	public String toString()
	{
		return getUsername() + " - " + getDbId();
	}
}
