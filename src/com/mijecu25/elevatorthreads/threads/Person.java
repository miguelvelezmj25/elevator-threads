package com.mijecu25.elevatorthreads.threads;

import com.mijecu25.elevatorthreads.resources.Building;

public class Person implements Runnable {
	
	private String 		name;
	private Building	building;
	private int 		waitTime;
	private int 		currentFloor;
	private int 		nextFloor;
	private int[] 		listFloor;
		
	public Person (Building building, String personName, int waitTime, int currentFloor, int[] listFloor) {
		this.name = personName;
		this.building = building;
		this.waitTime = waitTime;
		this.currentFloor = currentFloor;
		this.listFloor = listFloor;
		this.nextFloor = 0;			
	}
	
	@Override
	/** Runs the Person thread
	 */
	public void run() {
		// Get the list of floors
		int[] listFloors = this.getListFloor();
				
		try {
			// Go through all the floor that each person wants to visit
			for(int i = 0; i < listFloors.length; i++) {
				System.out.println(this.getName() + " waiting on floor " + this.getCurrentFloor() + " for floor " + listFloors[this.getNextFloor()]);
				
				// Save the destination floor
				int destinationFloor = this.getListFloor()[this.getNextFloor()];
				
				// Get on the elevator
				this.getBuilding().getOnElevator(this);
												
				// TODO Once the person enters the elevator, it needs to wait until it reaches its floor
				while(this.getCurrentFloor() != destinationFloor) {
					Thread.sleep(20);
				}
				
				// Do stuff in that floor
				Thread.sleep(this.getWaitTime());
				
				// Increment the floor
				this.setNextFloor(this.getNextFloor() + 1);
			}
		}
		catch(InterruptedException ie) {
			ie.printStackTrace();
		}		
				
	}
	
	/** Update the floor of this person assuming that they have reached
	 * a new floor
	 */
	public void updateFloor(int floor) {
		this.setCurrentFloor(floor);
	}

	// Getters 
	public String getName() { return this.name; }
	
	public int getNextFloor() { return this.nextFloor;	}
	
	public int getWaitTime() { return this.waitTime; }
	
	public int getCurrentFloor() { return this.currentFloor; }
	
	public Building getBuilding() {	return this.building; }
	
	public int[] getListFloor() { return this.listFloor; }
	

	// Setters
	private void setCurrentFloor(int floor) {
		this.currentFloor = floor;
	}
	
	private void setNextFloor(int floor) {
		this.nextFloor = floor;
	}
	
}