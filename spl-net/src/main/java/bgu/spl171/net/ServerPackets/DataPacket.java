package bgu.spl171.net.ServerPackets;

import java.nio.charset.StandardCharsets;

public class DataPacket extends MyServerPackets {
	private short packetSize;
	private short numOfBlock;
	private byte[] currentDataBlockSize;

	public DataPacket(short opcode, short packetSize, short numOfBlock, byte[] currentDataBlockSize) {
		super(opcode);
		this.packetSize=packetSize;
		this.numOfBlock=numOfBlock;
		this.currentDataBlockSize=currentDataBlockSize;
	}
	public short getPacketSize(){
		return packetSize;
	}
	public byte[] getCurrentDataBlockSize(){
		return currentDataBlockSize;
	}
	public short getNumOfBlock(){
		return numOfBlock;
	}
	public String toString(){
	
		return "DataPacket, current packet size is: "+ packetSize+" in chunk number: "+numOfBlock+" ";
	}
	
}
