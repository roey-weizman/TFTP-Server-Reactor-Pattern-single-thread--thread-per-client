package bgu.spl171.net.ServerPackets;

public class DeleteReq extends MyServerPackets{

	String fileName;
	
	public DeleteReq(short opcode,String filename){
		super(opcode);
		fileName=filename;
	}
	public String toString(){
		return "DELRQ "+fileName;
	}
	public String getFileName(){
		return fileName;
	}
}
