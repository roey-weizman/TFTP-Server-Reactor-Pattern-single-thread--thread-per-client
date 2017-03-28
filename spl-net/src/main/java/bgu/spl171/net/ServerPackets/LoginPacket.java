package bgu.spl171.net.ServerPackets;

public class LoginPacket extends MyServerPackets {

	String username;
	public LoginPacket(short opcode,String user){
		super(opcode);
		username=user;
	}
	public String toString(){
		return "LOGRQ "+username;
	}
	public String getUserName(){
		return username;
	}
	
	
}
