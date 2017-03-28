package bgu.spl171.net.ServerPackets;

public class ReadReq extends MyServerPackets{
	String fileName;
	
	public ReadReq(short opcode, String filename){
		super(opcode);
		fileName=filename;
	}
	public String toString(){
		return "RRQ "+fileName;
	}
	public String getFileName(){
		return fileName;
	}


}
