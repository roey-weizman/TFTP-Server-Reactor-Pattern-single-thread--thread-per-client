package bgu.spl171.net.ServerPackets;

public class DirectoryReq extends MyServerPackets {
	public DirectoryReq(short opcode){
		super(opcode);
	}
	public String toString(){
		return "DIRQ";
	}
}
