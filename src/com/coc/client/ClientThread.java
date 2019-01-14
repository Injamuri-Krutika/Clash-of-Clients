package com.coc.client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;

import com.coc.utils.Constants;

public class ClientThread implements Runnable
{
	Socket sock=null;
	BufferedReader recieve=null;
	ObjectInputStream ois = null;
	public ClientThread(Socket sock) {
		this.sock = sock;
	}//end constructor
	public void run() {
		try{
			ObjectOutputStream oos = null;
	        ObjectInputStream ois = null;
	        boolean teamLeader=false;
	        oos = new ObjectOutputStream(this.sock.getOutputStream());
	        System.out.println("Sending request to Socket Server");
	        int myPort=this.sock.getLocalPort();
	        oos.writeObject("Clent connecting with port :"+myPort);
	        ois = new ObjectInputStream(this.sock.getInputStream());
	        while(true){
	            String message = null;
	            if((message = (String) ois.readObject())!=null)
	            	System.out.println("Message: " + message);
	            	if(message.equalsIgnoreCase("exit")||message.equalsIgnoreCase(Constants.ROUND1))
	            		break;
	            	
	            	if(message.contains("Team Leader is :")){
	            		if(this.sock.getLocalPort()==getPortNo(message))
	            			teamLeader=true;
	            	}
	        }
	        
	        round1(teamLeader,myPort,oos,ois);
	        round2(oos,ois,teamLeader,myPort);
	        round3(oos,ois,myPort);
	        oos.close();
	        ois.close();
	        sock.close();
	        
		}catch(ConnectException e){
			System.out.println("The Server is not yet started. Please Start the Server first and then the Clients");
			
		}catch(SocketException e){
			System.out.println("There was some issue with the you or server. Hence closing the socket");
			try {
				sock.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}catch(EOFException e){
			System.out.println("One of the clients got disconnected hence ending the show!!");
			try {
				sock.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		catch(Exception e){
			System.out.println("Exception-- ");
			e.printStackTrace();
		}
		
		System.out.println("End of Thread");
	}//end run
	private void round3(ObjectOutputStream oos, ObjectInputStream ois,
			int myPort) throws NumberFormatException, ClassNotFoundException, IOException {
		// ROUND 3
        while(true)
        {
        	String message = null;
            if((message = (String) ois.readObject())!=null)
            {
            	if(message.contains("close socket"))
            	{
            		String[] arr=message.split(":");
            		if(Integer.parseInt(arr[0])==myPort)
            		{
            			System.out.println("Closing Connection");
            			this.sock.close();
            			System.exit(0);
            			break;
            		}
            	}
            	
            	if(message.equalsIgnoreCase("exit")){
            		break;
            	}
            	else if(message.contains("Winner Port Number is")){
            		String winnerMessage[]=message.split(":");
            		if(Integer.parseInt(winnerMessage[1])==myPort){
            			System.out.println("Yippieeee!!! I am the Winner of title Clash Of Clients!!");
            		}else{
            			System.out.println("Oh No!!! Just missed :(");
            		}
            		System.out.println("Game Over.. Hence Closing connection");
            		break;
            	}
            	   System.out.println(message);
            	  if(message.contains("QUESTION"))
            	  {
            		String queArr[]=message.split(":");
            		System.out.println(queArr[1]);
        			System.out.println("Please type your answer");
        			Scanner sc =new Scanner(System.in);
        			String ans=sc.nextLine();
        			
        			oos.writeObject(ans);
        			System.out.println("Answer Sent to server");
	            		 	
            	  }
           
            } 
          
        }
	}
	private void round2(ObjectOutputStream oos, ObjectInputStream ois, boolean teamLeader, int myPort) throws NumberFormatException, ClassNotFoundException, IOException {
		while(true){
        	String message = null;
            if((message = (String) ois.readObject())!=null){
            	if(message.contains("close socket")){
            		String[] arr=message.split(":");
            		if(Integer.parseInt(arr[0])==myPort){
            			System.out.println("Closing Connection");
            			this.sock.close();
            			System.exit(0);
            			break;
            		}
            	}
            	
            	if(message.equalsIgnoreCase("exit")||message.equalsIgnoreCase(Constants.ROUND3))
            		break;
            	System.out.println(message);
            	if(message.contains("QUESTION"))
            	{
            		String queArr[]=message.split(":");
	            	if(Integer.parseInt(queArr[0])==myPort ||Integer.parseInt(queArr[1])==myPort)
	            	{
	            		if(teamLeader)
	            		{
	            			System.out.println("Team Leader please type your input");
	            			Scanner sc =new Scanner(System.in);
	            			String ans=sc.nextLine();
	            			
	            			oos.writeObject(ans);
	            			System.out.println("Answer Sent to server");
	            		}
	            	}
            	}
            	
            }
        }		
		
	}
	private void round1(boolean teamLeader, int myPort, ObjectOutputStream oos, ObjectInputStream ois) throws NumberFormatException, ClassNotFoundException, IOException, InterruptedException {
		while(true){

        	String message = null;
            if((message = (String) ois.readObject())!=null){
            	if(message.contains("close socket")){
            		String[] arr=message.split(":");
            		if(Integer.parseInt(arr[0])==myPort){
            			System.out.println("Closing Connection");
            			this.sock.close();
            			System.exit(0);
            			break;
            		}
            	}
            	
            	if(message.equalsIgnoreCase("exit")||message.equalsIgnoreCase(Constants.ROUND2))
            		break;
            	System.out.println(message);
            	if(message.contains("FFF")){
            		long startTime = System.nanoTime();
            		
            		String queArr[]=message.split(":");
            		int arr[]=new int[queArr.length-2];
	            		if(teamLeader){
	            			for (int i = 2; i < queArr.length; i++) {
	            				System.out.println(queArr[i]);
								arr[i-2]=Integer.parseInt(queArr[i]);
							}
	            			Arrays.sort(arr);
	            			String ans=new String();
	            			for (int i = 0; i < arr.length-1; i++) {
								ans+=arr[i]+",";
							}
	            			ans+=arr[arr.length-1]+":";
	            			Thread.sleep(100);
	            			long endTime   = System.nanoTime();
		            		long totalTime = endTime - startTime;
		            		ans+=totalTime;
	            			oos.writeObject(ans);
	            			System.out.println("ans->"+ans);
	            		}
            	}
            	
            }
        }
	}
	private int getPortNo(String message) {
		String arr[]=message.split(":");
		return Integer.parseInt(arr[1]);
	}
}
