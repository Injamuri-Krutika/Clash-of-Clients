package com.coc.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args) {
        InetAddress host;
		try {
			host = InetAddress.getLocalHost();
			Socket socket = null;
	        socket = new Socket(host.getHostName(), 9876);
	        System.out.println("My port No : "+socket.getLocalPort());
	        ClientThread thread =new ClientThread(socket);
	        Thread t=new Thread(thread);
	        t.start();
		} catch(ConnectException e){
			System.out.println("The Server is not yet started. Please Start the Server first and then the Clients");
		}catch (UnknownHostException e) {
			System.out.println("Host is not known. Please use correct host");
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        
       // throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException
    }
}