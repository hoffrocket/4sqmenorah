package com.foursquare.examples.push.models;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * A record class to store google id -> foursquare auth token relationships.
 */
@PersistenceCapable
public class LinkedUser {
	@PrimaryKey
	private String googleId;

	@Persistent
	public String foursquareAuth;

	@Persistent
  public String vid;


	public LinkedUser(String googleId, String foursquareAuth, String vid) {
		this.googleId = googleId;
		this.foursquareAuth = foursquareAuth;
		this.vid = vid;
	}
	
	public String googleId() { return googleId; }
	public String foursquareAuth() { return foursquareAuth; }
	public String vid() { return vid; }

	public void save(PersistenceManager pm) {
		pm.makePersistent(this);
	}
	
	public static LinkedUser loadOrCreate(PersistenceManager pm, String googid) {
    try {
      return pm.getObjectById(LinkedUser.class, googid);
    } catch (JDOObjectNotFoundException e) {
      return new LinkedUser(googid, null, null);
    }
  }
	
	@Override
	public String toString(){
	  return this.googleId + " " + this.foursquareAuth + " " + this.vid;
	}
}
