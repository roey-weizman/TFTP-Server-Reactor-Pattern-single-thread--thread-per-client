package bgu.spl171.net.ServerPackets;

public class AckPacket extends MyServerPackets {
	private short blockNum;
	
	public AckPacket(short opcode,short  blockNum){
		super(opcode);
		this.blockNum=blockNum;
	}
	public String toString(){
		return "ACK " +blockNum;
	}
	public short getBlockNumber(){
		return blockNum;
	}
	
}
