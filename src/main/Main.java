package main;

import java.util.Scanner;

import client.Client;
import server.Server;

public class Main {

	public static void main(String[] args) { 
	    Scanner in = new Scanner(System.in); 
	    System.out.println("Choose mode? (S(erver) / C(lient))");
	    char answer;
	    while (true) {
	        answer = Character.toLowerCase(in.nextLine().charAt(0)); 
	        if (answer == 's') {
	        	System.out.println("Server starting"); 
	            new Server(); 
	            break; 
	        } else if (answer == 'c') { 
	        	System.out.println("Client starting");
	        	new Client(); 
	            break; 
	        } else { 
	        	System.out.println("Parse error. Repeat please.");
	        } 
	    } 
	} 
}