package bgu.spl171.net.ServerPackets;

public class ErrorPacket extends MyServerPackets {
	short errorCode;
	String errorMesssage;
	
	public ErrorPacket (short opcode, short errorcode, String errormessage){//remember delimiter
		super(opcode);
		errorCode=errorcode;
		errorMesssage=errormessage;
	}
	
	public short getErrorCode(){
		return errorCode;
	}
	public String getErrorMessage(){
		return errorMesssage;
	}
	public String toString(){
		return "Error "+errorCode;
	}
}
