package bgu.spl171.net.srv;

import java.awt.SecondaryLoop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.security.auth.callback.ConfirmationCallback;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;

import bgu.spl171.net.ServerPackets.*;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.bidi.ConnectionHandler;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {
	Connections<T> t;
	private static HashMap<String, Integer> ClientByUserName = new HashMap<>();
	private byte[] Diredctory = new byte[0];
	private byte[] fileData = new byte[0];
	private FileOutputStream out;
	int myId;
	private File Myfile = null;
	boolean shouldTerminate = false;
	boolean firstLogin = false;
	String MyUserName = null;
	String filename = null;
	short blockNum = 0;
	int oneblockofmaxsize;

	@Override

	public void start(int connectionId, Connections<T> connections) {
		t = connections;
		myId = connectionId;
	}

	@SuppressWarnings("unchecked")
	@Override

	public void process(T message) {
		short opcode = ((MyServerPackets) message).getOpCode();
		switch (opcode) {
		case 1:// read
			if (!firstLogin) {
				t.send(myId, (T) new ErrorPacket((short) 5, (short) 6,
						"User not logged in -Any opcode received before login completes "));
			} else {
				String name = (((ReadReq) message).getFileName());
				if (name == "") {
					ErrorPacket err = new ErrorPacket((short) 5, (short) 0, "Didn't type a name of file");
					t.send(myId, (T) err);

				} else {
					File directory = new File("Files" + File.separator);
					File[] FileList = directory.listFiles();
					File fileToSend = null;
					boolean found = false;
					for (int i = 0; i < FileList.length && !found; i++) {
						if ((FileList[i].getName()).equals(name)) {
							found = true;
							fileToSend = FileList[i];
						}
					}
					if (fileToSend == null) {
						ErrorPacket err = new ErrorPacket((short) 5, (short) 1,
								"File not found -RRQ of non-existing file");
						t.send(myId, (T) err);

					} else {
						fileData = new byte[(int) fileToSend.length()];
						FileInputStream inp = null;
						try {
							inp = new FileInputStream(fileToSend);
							inp.read(fileData);
							inp.close();
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (fileData.length < 512) {
							DataPacket startData = new DataPacket((short) 3, (short) fileData.length, (short) 1,
									fileData);
							t.send(myId, (T) startData);
							fileData = new byte[0];
						} else {
							
							if(fileData.length!=512){
								byte[] a = Arrays.copyOfRange(fileData, 0, 512);
								fileData = Arrays.copyOfRange(fileData, 512, fileData.length);
								t.send(myId, (T) new DataPacket((short) 3, (short) 512, (short) 1, a));
						}else{
							t.send(myId, (T) new DataPacket((short) 3, (short) fileData.length, (short) 1,fileData ));
							fileData=new byte[0];
						}
							
						}
					}
				}
			}
			oneblockofmaxsize=0;
			break;

		case 2:// write
			if (!firstLogin) {
				t.send(myId, (T) new ErrorPacket((short) 5, (short) 6,
						"User not logged in -Any opcode received before login completes "));
			} else {
				String name = ((WriteReq) message).getFileName();
				filename=name;
				File directory = new File("Files" + File.separator);
				File[] FileList = directory.listFiles();
				boolean found = false;
				for (int i = 0; i < FileList.length && !found; i++) {
					if ((FileList[i].getName()).equals(name)) {
						found = true;
						ErrorPacket e = new ErrorPacket((short) 5, (short) 5, "file name exists on WRQ");
						t.send(myId, (T) e);
					}
				}
				if (found == false) {
					AckPacket ackp1 = new AckPacket((short) 4, (short) 0);
					t.send(myId, (T) ackp1);
				}
			}
			break;

		case 3:// data
			if (!firstLogin)

			{
				t.send(myId, (T) new ErrorPacket((short) 5, (short) 6,
						"User not logged in -Any opcode received before login completes "));
			} else

			{
				blockNum = ((DataPacket) message).getNumOfBlock();
				byte[] temp = ((DataPacket) message).getCurrentDataBlockSize();
				if (temp.length == 512) {
					byte[] combine = CombineByteArrays(fileData, temp);
					fileData = combine;
					AckPacket ackp4 = new AckPacket((short) 4, blockNum);
					t.send(myId, (T) ackp4);
					blockNum = 1;
				} else if (temp.length < 512) {
					byte[] combine = CombineByteArrays(fileData, temp);
					fileData = combine;
					File freeSpace = new File("Files" + File.separator);// check!
					long space = freeSpace.getFreeSpace();
					if (fileData.length > space) {
						ErrorPacket err = new ErrorPacket((short) 5, (short) 3, "disk full -no room in disk");
						t.send(myId, (T) err);
						
					} else {
						String path = (new File("Files" + File.separator)).getAbsolutePath() + File.separator+ filename;
						try {
							FileOutputStream fo = new FileOutputStream(path);
							fo.write(fileData);
							fo.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
						AckPacket ackp4 = new AckPacket((short) 4, blockNum);
						t.send(myId, (T) ackp4);
						BcastAll((byte) 1, filename);
						
					}
				}
				

			}
			break;
		case 4:// ack
			if (!firstLogin)

			{
				t.send(myId, (T) new ErrorPacket((short) 5, (short) 6,
						"User not logged in -Any opcode received before login completes "));
				oneblockofmaxsize=1;
			} else

			{	

				if (((AckPacket) message).getBlockNumber() != 0 && Diredctory.length > 0) {

					if (Diredctory.length >= 512) {

						byte[] a = Arrays.copyOfRange(Diredctory, 0, 512);
						Diredctory = Arrays.copyOfRange(Diredctory, 512, Diredctory.length);
						t.send(myId, (T) new DataPacket((short) 3, (short) 512,
								(short) (((AckPacket) message).getBlockNumber() + 1), a));
						oneblockofmaxsize=1;
					}
					if (Diredctory.length < 512) {

						t.send(myId, (T) new DataPacket((short) 3, (short) Diredctory.length,
								(short) (((AckPacket) message).getBlockNumber() + 1), Diredctory));
						Diredctory = new byte[0];
						oneblockofmaxsize=1;
					}
				} else if (((AckPacket) message).getBlockNumber() != 0 && Diredctory.length == 0) {// datapacket
					if (fileData.length > 512) {

						byte[] was = Arrays.copyOfRange(fileData, 0, 512);
						fileData = Arrays.copyOfRange(fileData, 512, fileData.length);
						t.send(myId, (T) new DataPacket((short) 3, (short) 512,
								(short) (((AckPacket) message).getBlockNumber() + 1), was));
						oneblockofmaxsize=1;
					}
					else if (fileData.length < 512 && fileData.length >0) {
						t.send(myId, (T) new DataPacket((short) 3, (short) (fileData.length),
								(short) (((AckPacket) message).getBlockNumber() + 1), fileData));
						fileData = new byte[0];
						oneblockofmaxsize=1;
					}
					else if (fileData.length == 512){
						t.send(myId, (T) new DataPacket((short) 3, (short) (fileData.length),
								(short) (((AckPacket) message).getBlockNumber() + 1), fileData));
						fileData = new byte[0];
						oneblockofmaxsize=0;
					}
					
				}
				
			}
			if(oneblockofmaxsize==0){
				t.send(myId, (T) new DataPacket((short) 3, (short) 1,
						(short) (((AckPacket) message).getBlockNumber() + 1), " ".getBytes()));
				oneblockofmaxsize=1;
			}
			break;
		case 6:// DIRQ
			if (!firstLogin)

			{//
				t.send(myId, (T) new ErrorPacket((short) 5, (short) 6,
						"User not logged in -Any opcode received before login completes "));
			} else

			{
				String toBeSent = "";

				String[] s = (new File("Files" + File.separator)).list();
				if (s != null) {
					for (String filename : s) {
						toBeSent += filename + "/0";
					}
					if (toBeSent.length() == 0) {
						DataPacket d = new DataPacket((short) 3, (short) 0, (short) 0, toBeSent.getBytes());
						t.send(myId, (T) d);
					}
					if (toBeSent.getBytes().length < 512) {
						Diredctory = new byte[0];
						t.send(myId, (T) new DataPacket((short) 3, (short) toBeSent.getBytes().length, (short) 1,
								toBeSent.getBytes()));
					} else {
						byte[] a = Arrays.copyOfRange(toBeSent.getBytes(), 0, 512);// remember!
						t.send(myId, (T) new DataPacket((short) 3, (short) a.length, (short) 1, a));

						Diredctory = Arrays.copyOfRange(toBeSent.getBytes(), 513, toBeSent.length());
					}
				}
			}
			break;
		case 7:// login
			if (ClientByUserName.containsKey(((LoginPacket) message).getUserName()))

			{
				t.send(myId, (T) new ErrorPacket((short) 5, (short) 7,
						"User already logged in -Login username already connected"));

			} else

			{
				ClientByUserName.put(((LoginPacket) message).getUserName(), myId);
				firstLogin = true;
				MyUserName = ((LoginPacket) message).getUserName();
				AckPacket ackp3 = new AckPacket((short) 4, (short) 0);
				t.send(myId, (T) ackp3);
			}

			break;
		case 8:// delete req
			if (!firstLogin)

			{
				t.send(myId, (T) new ErrorPacket((short) 5, (short) 6,
						"User not logged in -Any opcode received before login completes "));
			} else

			{
				String name1 = ((DeleteReq) message).getFileName();
				File directory = new File("Files" + File.separator);
				File[] FileList = directory.listFiles();
				boolean found = false;
				for (int i = 0; i < FileList.length && !found; i++) {
					if ((FileList[i].getName()).equals(name1)) {
						found = true;
						FileList[i].delete();
						AckPacket ackp2 = new AckPacket((short) 4, (short) 0);
						t.send(myId, (T) ackp2);
						BcastAll((byte) 0, name1);
					}
				}
				if (found == false) {
					ErrorPacket err = new ErrorPacket((short) 5, (short) 1, "non-existing file");
					t.send(myId, (T) err);
				}

			}
			break;

		case 10:// disconnect
			if (!firstLogin)

			{
				t.send(myId, (T) new ErrorPacket((short) 5, (short) 6,
						"User not logged in -Any opcode received before login completes "));
			} else

			{
				AckPacket ackp3 = new AckPacket((short) 4, (short) 0);
				t.send(myId, (T) ackp3);
				ClientByUserName.remove(MyUserName);
				shouldTerminate = true;
				t.disconnect(myId);
			}
			break;
		case 11:
			ErrorPacket err = new ErrorPacket((short) 5, (short) 0, "Illegal TFTP command");
			t.send(myId, (T) err);
			break;
		default:

			ErrorPacket err1 = new ErrorPacket((short) 5, (short) 0, "Not defined");
			t.send(myId, (T) err1);
			break;
		}

	}

	public byte[] CombineByteArrays(byte[] a, byte[] b) {
		byte[] ans = new byte[a.length + b.length];
		System.arraycopy(a, 0, ans, 0, a.length);
		System.arraycopy(b, 0, ans, a.length, b.length);
		return ans;

	}

	public void BcastAll(byte addOrDel, String name) {
		BcastPacket b = new BcastPacket((short) 9, (byte) addOrDel, name);
		for (int i :ClientByUserName.values()) {
			t.send(i, (T) b);
		}
	}

	@Override
	public boolean shouldTerminate() {
		return shouldTerminate;
	}

	public byte[] fileToBytes(File file) throws IOException {
		// init array with file length
		byte[] bytesArray = new byte[(int) file.length()];

		FileInputStream fis;
		fis = new FileInputStream(file);
		fis.read(bytesArray); // read file into bytes[]
		fis.close();

		return bytesArray;
	}

}
