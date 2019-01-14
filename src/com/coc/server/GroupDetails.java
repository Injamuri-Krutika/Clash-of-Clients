package com.coc.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GroupDetails implements Comparable<GroupDetails>{
	private ArrayList<Socket> sockets;
	private ArrayList<ObjectOutputStream> oosList;
	private ArrayList<ObjectInputStream> oisList;
	private int score;
	private int leaderPort;
	
	public int getLeaderPort() {
		return leaderPort;
	}
	public void setLeaderPort(int leaderPort) {
		this.leaderPort = leaderPort;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public GroupDetails(ArrayList<Socket> newGroupSocList,
			ArrayList<ObjectOutputStream> oosList,
			ArrayList<ObjectInputStream> oisList) {
		
		this.sockets=newGroupSocList;
		this.oosList=oosList;
		this.oisList=oisList;
		
		
	}
	public ArrayList<Socket> getSockets() {
		return sockets;
	}
	public void setSockets(ArrayList<Socket> sockets) {
		this.sockets = sockets;
	}
	public ArrayList<ObjectOutputStream> getOosList() {
		return oosList;
	}
	public void setOosList(ArrayList<ObjectOutputStream> oosList) {
		this.oosList = oosList;
	}
	public ArrayList<ObjectInputStream> getOisList() {
		return oisList;
	}
	public void setOisList(ArrayList<ObjectInputStream> oisList) {
		this.oisList = oisList;
	}

	public int compareTo(GroupDetails grpDtl) {
		System.out.println(score+" : "+grpDtl.getScore());
		return score-grpDtl.getScore();
	}
}
