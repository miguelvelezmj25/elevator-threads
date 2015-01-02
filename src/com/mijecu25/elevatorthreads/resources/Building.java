package com.mijecu25.elevatorthreads.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.mijecu25.elevatorthreads.threads.Elevator;
import com.mijecu25.elevatorthreads.threads.Person;

public class Building {
	
	private List<Elevator> 	elevators;
	private int 			floors;
	 
	private Lock[] 			floorLock;
	private Condition[] 	availableCondition;
	
	public Building(int elevatorCount, int floorCount) {	
		this.elevators = new ArrayList<Elevator>();
		this.floors = floorCount + 1;
		this.floorLock = new Lock[this.floors];
		this.availableCondition = new Condition[this.floors];
		
		for(int i = 0; i < this.floors; i++) {
			this.floorLock[i] = new ReentrantLock();
			this.availableCondition[i] = this.floorLock[i].newCondition(); // Condition tied to lock
			
		}
	}
	
	/** Called by the person object when they want to get on the elevator
	 */
	public void getOnElevator(Person person) throws InterruptedException {
		this.getFloorLock()[person.getCurrentFloor()].lock();
		
		try {	
			int elevatorId = -1;
			boolean canGetIn = false;
			
			while(!canGetIn) {			
				for(int i = 0; i < this.getElevators().size(); i++) {
					// Current elevator
					elevatorId = i;
					
					// Check direction
					boolean sameDirection = true;
					int personDirection = 0;
					
					// Get the person's direction
					personDirection = person.getListFloor()[person.getNextFloor()] - person.getCurrentFloor();
					
					// Check if the current elevator is going on a different direction
					if(personDirection > 0) {
						if(this.getElevators().get(i).getDirection() < 0) {
							sameDirection = false;
						}
					}
					else {
						if(this.getElevators().get(i).getDirection() > 0) {
							sameDirection = false;
						}
					}
										
					// Only get in to the elevator if it is in the same floor, there is space, and it is going in the 
					// same direction
					if(person.getCurrentFloor() == this.getElevators().get(i).getCurrentFloor() &&
							this.getElevators().get(i).getCurrentNumPeople() < this.getElevators().get(i).getMaxCapacity() &&
							sameDirection) {
						canGetIn = true;
						break;
					}
				}
				
				if(!canGetIn) {	
					// Wait
					this.getAvailableCondition()[person.getCurrentFloor()].await();				
				}
			}
			
			System.out.println(person.getName() + " taking elevator " + this.getElevators().get(elevatorId).getId());
			
			// Put person in the elevator
			this.getElevators().get(elevatorId).insertPerson(person);
		}
		finally {
			// Release the lock
			this.getFloorLock()[person.getCurrentFloor()].unlock();
		}
	}
	
	/** Executes the elevator logic
	 */
	public void service(Elevator elevator) {
		// Move the elevator by 1 floor
		elevator.move();
		System.out.println("\t\t\t\tElevator " + elevator.getId() + " arrived at floor " + elevator.getCurrentFloor());

		this.getFloorLock()[elevator.getCurrentFloor()].lock();
		
		try {
			// Check the people that have arrived to their desired floor
			ArrayList<Person> peopleToRemove = new ArrayList<Person>();
			
			for(int i = 0; i < elevator.getPeopleInElevator().size(); i++) {
				// Get the person
				Person person = elevator.getPeopleInElevator().get(i);
				
				// If the person is in their destination floor, add to the list to remove
				if(person.getListFloor()[person.getNextFloor()] == elevator.getCurrentFloor()) {
					peopleToRemove.add(person);
				}
								
			}
			
			for(int j = 0; j < peopleToRemove.size(); j ++) {
				// Remove person
				elevator.removePerson(peopleToRemove.get(j));
				
				// Update the person's current floor
				peopleToRemove.get(j).updateFloor(elevator.getCurrentFloor());
		
				System.out.println(peopleToRemove.get(j).getName() + " arrived at floor " + peopleToRemove.get(j).getCurrentFloor());
			}
			
			// If the elevator has reached the last floor or the first floor, switch directions
			if(elevator.getCurrentFloor() == this.getFloors() -1 || elevator.getCurrentFloor() == 0) {
				elevator.changeDirection();
			}
			
			// TODO might not work if things happen to fast. We got: 
			// Exception in thread "Thread-0" java.lang.NullPointerException
			// Signal all waiting people that the elevator has arrived to a floor
			this.getAvailableCondition()[elevator.getCurrentFloor()].signalAll(); 
									
		}
		finally {
			// Release the lock
			this.getFloorLock()[elevator.getCurrentFloor()].unlock();
		}
	}
	
	// Getters	
	public int getFloors() { return this.floors; }
	
	public List<Elevator> getElevators() { return this.elevators; }

	public Lock[] getFloorLock() {
		return this.floorLock;
	}

	public Condition[] getAvailableCondition() {
		return this.availableCondition;
	}

	
//	// Setters
//	private void setElevators(ArrayList<Elevator> elevators) {
//		this.elevators = elevators;	
//	}
	
}