package mum.edu.model;
import java.io.Serializable;

public class Location implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String location;
    private int count;
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Location(String location, int count) {
		super();
		this.location = location;
		this.count = count;
	}
	public Location(String location, Integer count) {
		this.location = location;
		this.count = count;
	}
	public Location() {
		super();
	}
    
    
    

}
