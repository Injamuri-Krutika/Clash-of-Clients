package com.coc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.coc.utils.Constants;
import com.coc.utils.QuestionAnswers;
public class Server {
	//static ServerSocket variable
	private static ServerSocket server;
	//socket server port on which it will listen
	private static int port = 9876;
	static ClientGroup groups=new ClientGroup();
	private static QuestionAnswers qA=new QuestionAnswers();
	private static Map<String,String> queMap=qA.getQuestions();
	private static Map<String,String> ansMap=qA.getAnswers();
	public static void main(String args[]){
		try{
			server = new ServerSocket(port);

			Map<String, GroupDetails> groupMap=new HashMap<String, GroupDetails>();
			ArrayList<Socket> newGroupSocList=null;
			ArrayList<ObjectOutputStream> oosList=new ArrayList<ObjectOutputStream>();
			ArrayList<ObjectInputStream> oisList=new ArrayList<ObjectInputStream>();
			int count=0;
			while(true){
				System.out.println("Waiting for client request");

				Socket clientSocket = server.accept();
				System.out.println("Client Joined : "+ (count+1));
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
				String message = (String) ois.readObject();

				System.out.println("Message Received from Client "+(count+1)+" : " + message);
				if((count)%2==0){
					newGroupSocList=new ArrayList<Socket>();
					oisList=new ArrayList<ObjectInputStream>();
					oosList=new ArrayList<ObjectOutputStream>();
					newGroupSocList.add(clientSocket);
					oosList.add(oos);
					oisList.add(ois);
					String msg="Welcome!! Your group number will be decided once your partner joins.. ";
					oos.writeObject(msg);
				}else{
					newGroupSocList.add(clientSocket);
					oosList.add(oos);
					oisList.add(ois);
					GroupDetails details=new GroupDetails(newGroupSocList,oosList,oisList);

					if(groups.getGroups()==null)
						groups.setGroups(groupMap);
					String key=newGroupSocList.get(0).getPort()+":"+newGroupSocList.get(1).getPort();
					groups.getGroups().put(key, details);
					String msg="Welcome!!";
					oos.writeObject(msg);
					int leaderPort=0;
					for (int i = 0; i < 2; i++) {
						if(i==0){
							leaderPort=newGroupSocList.get(i).getPort();
							details.setLeaderPort(leaderPort);
						}
						oosList.get(i).writeObject("You are added to Group No : "+(groups.getGroups().size()));
						oosList.get(i).writeObject("Your Team Members : "+key);
						oosList.get(i).writeObject("Team Leader is :"+leaderPort);
						oosList.get(i).writeObject("Question is sent to both the team members but, answers are expected only from TEAM LEADER port.");
					}

				}
				count++;
				if(count==6)
					break;
				if(message.equalsIgnoreCase("exit")) break;
			}
			sendAll(groups,Constants.LETS_BEGIN);
			sleep(5000);
			System.out.println("Lets begin Round 1");
			//Round 1
			sendAll(groups,Constants.ROUND1);
			sendAll(groups,Constants.ROUND1_RULES);
			sleep(15000);
			fastestFingersFirst(groups);
			System.out.println("Group size - After round 1 : "+groups.getGroups().size());
			sleep(10000);
			//Round 2
			sendAll(groups,Constants.ROUND2);
			sendAll(groups,Constants.ROUND2_RULES);
			sleep(15000);
			quizRound();
			System.out.println("Group size - After round 2 : "+groups.getGroups().size());
			sleep(10000);

			//round 3
			sendAll(groups,Constants.ROUND3);
			sendAll(groups,Constants.ROUND3_RULES);
			sleep(15000);
			String groupKey=groups.getGroups().entrySet().iterator().next().getKey();
			kitnepratishad(groups.getGroups().get(groupKey),groupKey);
			System.out.println("Group size - After round 3 : "+groups.getGroups().size());
			sleep(1000);
			System.out.println("Game Over.. Hence Closing connection");
			server.close();
		}catch (SocketException e) {
			handleSocketException(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}



	private static void closeConnections(ClientGroup groups, String key) {
		GroupDetails groupDetails=groups.getGroups().get(key);
		for(int i=0;i<groupDetails.getSockets().size();i++){
			int port=groupDetails.getSockets().get(i).getPort();
			try {
				groupDetails.getOosList().get(i).writeObject(port+":close socket");
				System.out.println("Closing socket with port : "+port);
				groupDetails.getSockets().get(i).close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}


	private static ArrayList<String> getMinimumScoreGroupKeys(int minScore,
			Map<String, GroupDetails> groups) {
		ArrayList<String> keysList=new ArrayList<String>();
		System.out.println("minScore : "+minScore);
		for(Map.Entry<String, GroupDetails> entry:groups.entrySet()){
			if(entry.getValue().getScore()==minScore)
				keysList.add(entry.getKey());

		}
		return keysList;
	}


	private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
		// Any Open port on other machine
		// openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
		try {
			try (Socket soc = new Socket()) {
				soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
			}
			return true;
		} catch (IOException ex) {
			return false;
		}
	}


	public static int getCount(ArrayList<Socket> clientSockets) {
		int count=0;
		for (Socket socket : clientSockets) {
			if(isReachable(socket.getInetAddress().toString(),socket.getPort(),1000))
				count++;

		}
		System.out.println("Count : "+count);
		return count;
	}

	public static void sendAll(ClientGroup groups,String msg) {
		System.out.println("Sending All :"+msg);
		for (Entry<String, GroupDetails> entry : groups.getGroups().entrySet()) {
			GroupDetails group=entry.getValue();
			for(int i=0;i<group.getOosList().size();i++){
				try {
					group.getOosList().get(i).writeObject(msg);
				} catch (SocketException e) {
					int port=group.getSockets().get(i).getPort();
					group.getSockets().remove(i);
					handleSocketException(port);
				} catch (IOException e) {
					e.printStackTrace();
				}    			
			}


		}
	}

	private static void closeAllConnections(ClientGroup groups) {
		for (Entry<String, GroupDetails> entry : groups.getGroups().entrySet()) {
			closeConnections(groups, entry.getKey());
		}
	}


	public static void sendToAGroup(GroupDetails groupDetails,String msg, String groupKey) {
		System.out.println("Sending to Group : "+groupKey+" :"+msg);

		for (int i = 0; i < groupDetails.getOosList().size(); i++) {
			try {
				groupDetails.getOosList().get(i).writeObject(msg);
			}catch (SocketException e) {
				int port=groupDetails.getSockets().get(i).getPort();
				groupDetails.getSockets().remove(i);
				handleSocketException(port);
			}  catch (IOException e) {
				e.printStackTrace();
			}
		}


	}

	private static void handleSocketException(int i) {
		if(i!=0)
			System.out.println("Sorry!! Client "+i+" got disconnected.. Because of which we have to stop the game according to protocol!!");
		else
			System.out.println("Sorry!! due to some glitch we have to stop the game according to protocol!!");
		try {
			System.out.println("Closing Server!!");
			closeAllConnections(groups);
			server.close();
			System.exit(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public static void fastestFingersFirst(ClientGroup groups) {
		//Round 1

		ArrayList<String> closeConnKeys=new ArrayList<String>();
		int maxTime=0;
		String maxTimeKey=null;
		sendAll(groups,"FFF:"+queMap.get("FFF"));
		sleep(5000);
		for(Map.Entry<String, GroupDetails> entry:groups.getGroups().entrySet()){
			GroupDetails group=entry.getValue();
			String key=entry.getKey();
			for (int j = 0; j < group.getSockets().size(); j++) {
				if(group.getSockets().get(j).getPort()==group.getLeaderPort()){
					try {
						String ans=(String) group.getOisList().get(j).readObject();
						String[] ansArr=ans.split(":");
						if(ansArr[0].equalsIgnoreCase(ansMap.get("FFF"))){
							int timeTaken=Integer.parseInt(ansArr[1]);
							if(maxTime<timeTaken){
								maxTime=timeTaken;
								maxTimeKey=key;
							}
						}else{
							sendToAGroup(group, "Wrong way of sorting!! Sorry , you are out of the league!!",key);
							closeConnKeys.add(key);
						}
					} catch (SocketException e) {
						int port=group.getSockets().get(j).getPort();
						group.getSockets().remove(j);
						handleSocketException(port);
					}catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}


		}
		sendToAGroup(groups.getGroups().get(maxTimeKey), "Sorry!! You have taken long time to sort!! You are out of the league.",maxTimeKey);
		closeConnections(groups, maxTimeKey);
		groups.getGroups().remove(maxTimeKey);
		for (int i = 0; i < closeConnKeys.size(); i++)
		{
			closeConnections(groups, closeConnKeys.get(i));
			groups.getGroups().remove(closeConnKeys.get(i));
		}
	}

	//ROUND 2
	private static void quizRound() throws ClassNotFoundException, IOException {
		int minScore=99999;
		ArrayList<String> keysList=new ArrayList<String>(groups.getGroups().keySet());
		for (int i = 0; i <groups.getGroups().size()*3; i++) {
			if(i%groups.getGroups().size()==0)
				minScore=99999;
			String queKey="QUE"+(i+1);
			String ansKey="ANS"+(i+1);
			String groupKey=keysList.get(i%groups.getGroups().size());
			String msg=groupKey+":\nQUESTION :"+queMap.get(queKey);
			GroupDetails grpDetails=groups.getGroups().get(groupKey);
			sendToAGroup(grpDetails,msg,groupKey);
			System.out.println("Leader port :"+grpDetails.getLeaderPort());
			String clientAns=null;
			for (int j = 0; j < grpDetails.getSockets().size(); j++) {
				if(grpDetails.getSockets().get(j).getPort()==grpDetails.getLeaderPort())
					clientAns=(String) grpDetails.getOisList().get(j).readObject();
			}
			if(clientAns.equalsIgnoreCase(ansMap.get(ansKey))){
				System.out.println("Correct Answer. Awarded 10 points");
				int grpScore=grpDetails.getScore()+10;
				grpDetails.setScore(grpScore);
				if(minScore>grpDetails.getScore()){
					minScore=grpScore;
					System.out.println(minScore);
				}

			}else{
				System.out.println("Wrong Answer. No marks awarded");
				if(minScore>grpDetails.getScore()){
					minScore=grpDetails.getScore();
					System.out.println(minScore);
				}
			}

		}
		String msg="Results after Round 2\n";
		for (Entry<String, GroupDetails> entry : groups.getGroups().entrySet()) {
			msg+="Team ->"+entry.getKey()+" :::: Score ->"+entry.getValue().getScore()+"\n";
		}
		sendAll(groups, msg);

		ArrayList<String> minScoreKeysList=getMinimumScoreGroupKeys(minScore,groups.getGroups());
		if(minScoreKeysList.size()>1){

			sendAll(groups, "As there is draw, lets play Musical Chair game to eleminate one user");
			sleep(1000);
			sendAll(groups, "Playing music");
			sleep(5000);
			Random r=new Random();
			String key=minScoreKeysList.get(r.nextInt(minScoreKeysList.size()));
			sendAll(groups,"Stop");
			sendAll(groups,"Loser of Musical chair is team "+key);
			closeConnections(groups,key);
			groups.getGroups().remove(key);

		}else{
			closeConnections(groups,minScoreKeysList.get(0));
			groups.getGroups().remove(minScoreKeysList.get(0));
		}

	}
	//ROUND3

	public static void kitnepratishad(GroupDetails groupDetails, String groupKey) {
		sendToAGroup(groupDetails, "ROUND 3:Kitne Pratishad", groupKey);
		int[] score={0,0};
		for (int i = 0; i < 3; i++) {
			String ques=queMap.get("KP"+(i+1));
			String ans=ansMap.get("KP"+(i+1));
			System.out.println("the solution range between 1-100%");
			sendToAGroup(groupDetails,ques,groupKey);
			int j=0;
			for (ObjectInputStream ois : groupDetails.getOisList()) {
				String clientAns=new String();
				try {
					clientAns = (String)ois.readObject();
					try{
						score[j++]+=Math.abs(Math.abs(Integer.parseInt(ans))-Math.abs(Integer.parseInt(clientAns)));
					}catch(NumberFormatException ex){
						score[j++]+=100;
					}

				}catch (SocketException e) {
					int port=groupDetails.getSockets().get(i).getPort();
					groupDetails.getSockets().remove(i);
					handleSocketException(port);
				}catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		String msg=null;
		if(score[0]==score[1]){
			sendToAGroup(groupDetails,"Score of both the Clients is : "+score[0],groupKey);
			msg="As there is a draw, lets play musical chair!!";
			sendToAGroup(groupDetails,msg,groupKey);
			sleep(1000);
			msg="Playing Music..";
			sendToAGroup(groupDetails,msg,groupKey);
			sleep(5000);
			Random r=new Random();
			int loserIndex=r.nextInt(2);
			System.out.println("Stop");
			msg="Port number of Loser of Musical chair is: "+groupDetails.getSockets().get(loserIndex).getPort();
			sendToAGroup(groupDetails,msg,groupKey);
			msg="Winner Port Number is :"+groupDetails.getSockets().get(1-loserIndex).getPort();
		}else{
			int minIndex=-1,minVal=9999;
			for (int i = 0; i < score.length; i++) {
				msg="Score of "+groupDetails.getSockets().get(i).getPort()+" is "+score[i];
				sendToAGroup(groupDetails,msg,groupKey);
				if(score[i]<minVal){
					minIndex=i;
					minVal=score[i];
				}
			}
			msg="Winner Port Number is :"+groupDetails.getSockets().get(minIndex).getPort();
		}
		sendToAGroup(groupDetails,msg,groupKey);
	}
	private static void sleep(int val) {
		try {
			Thread.sleep(val);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}
}
