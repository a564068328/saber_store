package com.icloud.listenbook.socket.pro;

public class objectbuffer {

	private static objectbuffer instance;
	
	private HallSocket hallSocket;
	
	private HallPacketIml hallPacketIml;
	
	private objectbuffer() {
	}
	
	public static objectbuffer instance(){
		if(instance==null){
			instance=new objectbuffer();
		}
		return instance;
	}
	
	public HallSocket getHallSocket() {
		return hallSocket;
	}

	public void setHallSocket(HallSocket hallSocket) {
		this.hallSocket = hallSocket;
	}

	public HallPacketIml getHallPacketIml() {
		return hallPacketIml;
	}

	public void setHallPacketIml(HallPacketIml hallPacketIml) {
		this.hallPacketIml = hallPacketIml;
	}

	
	
}
