package bgu.spl171.net.ServerPackets;

public  class MyServerPackets {
	private short opcode;
	
	public MyServerPackets(short opcode){
		this.opcode=opcode;
	}
	public String toString(){
		return "";
	}
	public short getOpCode(){
		return opcode;
	}
}
