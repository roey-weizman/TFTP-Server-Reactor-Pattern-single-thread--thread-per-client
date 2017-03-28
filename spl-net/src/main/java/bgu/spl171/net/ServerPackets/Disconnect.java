package bgu.spl171.net.ServerPackets;

public class Disconnect extends MyServerPackets {
	
	public Disconnect(short opcode){
		super(opcode);
	}
	public String toString(){
		return "DISC";
	}
	

}
