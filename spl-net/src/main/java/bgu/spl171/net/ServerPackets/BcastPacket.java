package bgu.spl171.net.ServerPackets;

public class BcastPacket extends MyServerPackets{
	 private byte delOrAdd;
	 private String fileName;
	 
	 public BcastPacket(short opcode, byte deloradd, String name){
		 super(opcode);
		 delOrAdd=deloradd;
		 fileName=name;
	 }
	 public String toString(){
		 if(((Byte)delOrAdd).intValue()==0){
		 return "BCAST "+" del" +fileName;}
	 else return "BCAST "+" add" +fileName;
	 }
	 public byte getDelOrAdd(){
		 return delOrAdd;
	 }
	 public String getFileName(){
			return fileName;
		}

}
