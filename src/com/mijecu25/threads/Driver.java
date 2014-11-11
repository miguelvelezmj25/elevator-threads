package com.mijecu25.threads;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.mijecu25.resources.Building;

public class Driver { 
	
	public static void main(String[] args) throws IOException {
		int n = 1; // Number of elevators
		int f = 3; // Number of floors
		String file = "src/input.txt"; // Input file
		
		// Creating the building class which creates elevators and starts their threads
		Building building = new Building(n,f);

		// Reading the file which creates persons
		ArrayList<Person> people = Driver.readFile(file,building);
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		
		
		for(int i = 0; i < n; i++) {
			elevators.add(new Elevator(i, building, people));
			new Thread(elevators.get(i)).start();;
		}
		
		building.setElevators(elevators);
			
		// Starting the thread for each person.
		for(int i = 0; i < people.size(); i++) {
			new Thread(people.get(i)).start();;
		}
	}
	
	/** Reads the input file and creates a person for each line
	 * */
	public static ArrayList<Person> readFile(String filename, Building building) throws IOException {
        ArrayList<Person> everyPerson = new ArrayList<Person>(1);
        Path path = Paths.get(filename);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for(String line : lines) {
            String[] commandString = line.split(" ");

            String personName = "";
            int waitTime;
            int startFloor;
            int[] floorArray = new int[commandString.length-3];

            personName = commandString[0];
            waitTime = Integer.parseInt(commandString[commandString.length-2]);
            startFloor = Integer.parseInt(commandString[commandString.length-1]);
            
            for(int i=1;i<commandString.length-2;i++) {
                floorArray[i-1] = Integer.parseInt(commandString[i].replaceAll("[;\\]\\[]", ""));
            }
            
            Person p = new Person(building, personName, waitTime * 1000, startFloor, floorArray);
            everyPerson.add(p);
        }
        
        return everyPerson;
    }

}
