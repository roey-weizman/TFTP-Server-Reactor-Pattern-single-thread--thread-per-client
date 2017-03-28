package bgu.spl171.net.ServerPackets;

public class WriteReq  extends MyServerPackets{
	String fileName;
	
	public WriteReq(short opcode,String filename){
		super(opcode);
		fileName=filename;
	}
	public String toString(){
		return "WRQ "+fileName;
	}
	public String getFileName(){
		return fileName;
	}
}
