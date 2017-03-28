package bgu.spl171.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import bgu.spl171.net.ServerPackets.*;
import bgu.spl171.net.api.MessageEncoderDecoder;

public class EncoderDecoderImpl<T> implements MessageEncoderDecoder<MyServerPackets> {

	private byte[] decoderBuffer = new byte[518];
	private int index = 0;
	private short currentOpcode = 0;
	private byte[] opcodeArray = new byte[2];
	private short packetSize=0;

	@Override
	public MyServerPackets decodeNextByte(byte nextByte) {
		decoderBuffer[index] = nextByte;
		if (index == 1 && currentOpcode == 0) {
			opcodeArray[0] = decoderBuffer[0];
			opcodeArray[1] = nextByte;
		}
		currentOpcode = bytesToShort(opcodeArray);
		if (index >= 1) {
			if( currentOpcode == 10 || currentOpcode == 6||currentOpcode==11){
				return CreateEasyPackets(currentOpcode, decoderBuffer);
			}}
		if (index > 1) {		
			if( currentOpcode == 10 || currentOpcode == 6||currentOpcode==11){
				return CreateEasyPackets(currentOpcode, decoderBuffer);
			}
			if(currentOpcode==4 && index==3){
				byte[] blocks= Arrays.copyOfRange(decoderBuffer, 2, 4);
				short numOfBlocks=bytesToShort(blocks);
				AckPacket ans=new AckPacket(currentOpcode, numOfBlocks);
				clearFields();
				return ans;
			}
			if(currentOpcode==3){
				
				if(index >5){
					 packetSize= bytesToShort(Arrays.copyOfRange(decoderBuffer, 2, 4));
				if(index+1==packetSize+6){
					DataPacket ans=new DataPacket(currentOpcode, packetSize, bytesToShort(Arrays.copyOfRange(decoderBuffer, 4, 6)), Arrays.copyOfRange(decoderBuffer, 6,index+1));
					clearFields();
					return ans;
				}
				}
			}
			if (nextByte == '\0') {
				if (currentOpcode == 1 || currentOpcode == 2 || (currentOpcode == 5)&&index>3|| currentOpcode == 7
						|| currentOpcode == 8 || currentOpcode == 9) {
					return CreateDelimiter0Packets(currentOpcode);
				}
				
			}
		}
		index++;
		return null;
	}

	@Override
	public byte[] encode(MyServerPackets message) {
		byte[] ans = null;
		short opcode = message.getOpCode();
		byte[] temp0 = shortToBytes(opcode);
		byte[] delimeter;
		switch (opcode) {
		
		case 1:
			byte[] temp1 = (((ReadReq) message).getFileName()).getBytes();
			delimeter = "\0".getBytes();
			ans = CombineByteArrays(temp0, temp1);
			ans = CombineByteArrays(ans, delimeter);
			break;
		case 2:
			byte[] temp2 = (((WriteReq) message).getFileName()).getBytes();
			delimeter = "\0".getBytes();
			ans = CombineByteArrays(temp0, temp2);
			ans = CombineByteArrays(ans, delimeter);
			break;
		case 3:
			byte[] temp3 = shortToBytes((((DataPacket) message).getPacketSize()));
			byte[] temp31 = shortToBytes((((DataPacket) message).getNumOfBlock()));
			byte[] temp32 = ((DataPacket) message).getCurrentDataBlockSize();
			ans = CombineByteArrays(temp0, temp3);
			ans = CombineByteArrays(ans, temp31);
			ans = CombineByteArrays(ans, temp32);
			break;
		case 4:
			byte[] temp4 = shortToBytes((((AckPacket) message).getBlockNumber()));
			ans = CombineByteArrays(temp0, temp4);
			break;
		case 5:
			byte[] temp5 = shortToBytes(((ErrorPacket) message).getErrorCode());
			byte[] temp51 = (((ErrorPacket) message).getErrorMessage()).getBytes();
			delimeter = "\0".getBytes();
			ans = CombineByteArrays(temp0, temp5);
			ans = CombineByteArrays(ans, temp51);
			ans = CombineByteArrays(ans, delimeter);
			break;
		case 6:
			ans = temp0;
			break;
		case 7:
			byte[] temp7 = ((LoginPacket) message).getUserName().getBytes();
			delimeter = "\0".getBytes();
			ans = CombineByteArrays(temp0, temp7);
			ans = CombineByteArrays(ans, delimeter);
			break;
		case 8:
			byte[] temp8 = ((DeleteReq) message).getFileName().getBytes();
			delimeter = "\0".getBytes();
			ans = CombineByteArrays(temp0, temp8);
			ans = CombineByteArrays(ans, delimeter);
			break;
		case 9:
			byte[] temp9 = new byte[1];
			temp9[0] = ((BcastPacket) message).getDelOrAdd();
			byte[] temp91 = ((BcastPacket) message).getFileName().getBytes();
			delimeter = "\0".getBytes();
			ans = CombineByteArrays(temp0, temp9);
			ans = CombineByteArrays(ans, temp91);
			ans = CombineByteArrays(ans, delimeter);
			break;
		case 10:
			ans = temp0;
			break;
		case 11:
			ans = temp0;
			break;
		}
		
		clearFields();
		return ans;

	}

	public void clearFields(){
		decoderBuffer = new byte[518];
		index = 0;
		currentOpcode = 0;
		opcodeArray = new byte[2];
		packetSize=0;
	}
	
	
	/* Decode 2 bytes to short */

	public short bytesToShort(byte[] byteArr) {
		short result = (short) ((byteArr[0] & 0xff) << 8);
		result += (short) (byteArr[1] & 0xff);
		return result;
	}

	/* Encode short to 2 bytes */

	public byte[] shortToBytes(short num) {
		byte[] bytesArr = new byte[2];
		bytesArr[0] = (byte) ((num >> 8) & 0xFF);
		bytesArr[1] = (byte) (num & 0xFF);
		return bytesArr;
	}

	public byte[] CombineByteArrays(byte[] a, byte[] b) {
		byte[] ans = new byte[a.length + b.length];
		System.arraycopy(a, 0, ans, 0, a.length);
		System.arraycopy(b, 0, ans, a.length, b.length);
		return ans;
	}
	public MyServerPackets CreateEasyPackets(short opcode, byte[] myBuffer){
		MyServerPackets s = new MyServerPackets(opcode);
		switch (opcode) {
		case 6:
			s= new DirectoryReq(opcode);
			break;
		case 10:
			s= new Disconnect(opcode);
			break;
		case 11:
			s= new BullShitPacket(opcode);
			break;
		}
		clearFields();
		return s;
	}
	public MyServerPackets CreateDelimiter0Packets(short opcode) {
		MyServerPackets s = new MyServerPackets(opcode);

		switch (opcode) {
		case 1:
			String filename = new String(decoderBuffer, 2, index - 2, StandardCharsets.UTF_8);
			s = new ReadReq(opcode, filename);
			break;
		case 2:
			String filename1 = new String(decoderBuffer, 2, index - 2, StandardCharsets.UTF_8);
			s = new WriteReq(opcode, filename1);
			break;
		case 5:
			short errCode = bytesToShort(Arrays.copyOfRange(decoderBuffer, 2, 4));
			String errMsg = new String(decoderBuffer, 4, index - 4, StandardCharsets.UTF_8);
			s = new ErrorPacket(opcode, errCode, errMsg);
			break;
		case 7:
			String filename3 = new String(decoderBuffer, 2, index - 2, StandardCharsets.UTF_8);
			s = new LoginPacket(opcode, filename3);
			break;
		case 8:
			String filename4 = new String(decoderBuffer, 2, index - 2, StandardCharsets.UTF_8);
			s = new DeleteReq(opcode, filename4);
			break;
		case 9:
			byte delOrAdd = new String(decoderBuffer, 2, 1, StandardCharsets.UTF_8).getBytes()[0];
			String filename5 = new String(decoderBuffer, 3, index - 3, StandardCharsets.UTF_8);
			s = new BcastPacket(opcode, delOrAdd, filename5);
			break;

		}
		return s;

	}

}