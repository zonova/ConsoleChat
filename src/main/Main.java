package main;

import java.util.Scanner;

import client.Client;
import server.Server;

public class Main {

	public static void main(String[] args) { 
	    Scanner in = new Scanner(System.in); 
	    System.out.println("Запустить программу в режиме сервера или клиента? (S(erver) / C(lient))");
	    char answer;
	    while (true) {
	        answer = Character.toLowerCase(in.nextLine().charAt(0)); 
	        if (answer == 's') {
	        	System.out.println("Server started"); 
	            new Server(); 
	            break; 
	        } else if (answer == 'c') { 
	        	System.out.println("Client started");
	        	new Client(); 
	            break; 
	        } else { 
	        	System.out.println("Некорректный ввод. Повторите.");
	        } 
	    } 
	} 
}