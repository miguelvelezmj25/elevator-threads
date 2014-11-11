package com.mijecu25.threads;

import java.util.ArrayList;

import com.mijecu25.resources.Building;

public class Elevator implements Runnable {
	
	private static final int MAX_CAPACITY = 3;
	
	private ArrayList<Person> 	peopleInElevator;
	private Building 			building;
	private int 				id;
	private int 				currentFloor;
	private int					direction;
	private ArrayList<Person> 	allPeople;
			
	public Elevator(int id, Building building, ArrayList<Person> people) {
		this.id = id; 
		this.peopleInElevator = new ArrayList<Person>(MAX_CAPACITY);
		this.currentFloor = 0;
		this.building = building;
		this.direction = 1;
		this.allPeople = people;
	}
	
	
	@Override  
	/** Runs the Person thread
	 */
	public void run() {
		try {
			while(!checkNotDone(this.allPeople)) {
				// Move the elevator
				this.getBuilding().service(this);
				
				// Sleep for 1 second so that people exit and enter the elevator
				Thread.sleep(2000);
				System.out.println("\t\t\t\tElevator " + this.getId() + " serviced floor " + this.getCurrentFloor() + "\n");
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}	
	}
	
	public boolean checkNotDone(ArrayList<Person> people) {
		boolean result = true;
		
		for(Person person : people) {
			if(result && (person.getNextFloor() < person.getListFloor().length)) {
				result = false;
			}
		}
		
		return result;
	} 
	/** Inserts a person in the elevator
	 */
	public void insertPerson(Person person) {
		this.peopleInElevator.add(person);
	}
	
	/** Removes a person from the elevator
	 */
	public void removePerson(Person person) {
		this.peopleInElevator.remove(person);
	}
	
	/** Changes the direction of the elevator
	 */
	public void changeDirection() {
		this.setDirection(this.getDirection() * -1);
	}
	
	public void move() {
		this.setCurrentFloor(this.getCurrentFloor() + this.getDirection());
	}
	
	// Getters 
	public int getId() { return this.id; }
		
	public int getCurrentFloor() { return this.currentFloor; }
	
	public int getMaxCapacity() { return Elevator.MAX_CAPACITY; }
	
	public int getDirection() {	return this.direction; }
	
	public ArrayList<Person> getPeopleInElevator() { return this.peopleInElevator; }

	public Building getBuilding() { return this.building; }
	
	public int getCurrentNumPeople() { return this.peopleInElevator.size(); }

	
	// Setters
	
	public void setDirection(int direction) { this.direction = direction; }

	public void setCurrentFloor(int currentFloor) { this.currentFloor = currentFloor; }
	
}
