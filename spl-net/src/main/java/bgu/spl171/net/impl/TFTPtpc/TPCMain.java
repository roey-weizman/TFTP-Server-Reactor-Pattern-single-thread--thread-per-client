package bgu.spl171.net.impl.TFTPtpc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import bgu.spl171.net.ServerPackets.*;
import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.srv.BidiMessagingProtocolImpl;
import bgu.spl171.net.srv.BlockingConnectionHandler;
import bgu.spl171.net.srv.ConnectionsImpl;
import bgu.spl171.net.srv.EncoderDecoderImpl;
import bgu.spl171.net.srv.*;
public class TPCMain {

		public static void main(String[] args) {
			
	Supplier<MessageEncoderDecoder<MyServerPackets>> encoderDecoderFactory=new Supplier<MessageEncoderDecoder<MyServerPackets>>() {
		
		@Override
		public MessageEncoderDecoder<MyServerPackets> get() {
			// TODO Auto-generated method stub
			return new EncoderDecoderImpl<MyServerPackets>();
		}
	};
	Supplier <BidiMessagingProtocol<MyServerPackets>>protocolFactory=new Supplier<BidiMessagingProtocol<MyServerPackets>>() {
		
		@Override
		public BidiMessagingProtocol<MyServerPackets> get() {
			// TODO Auto-generated method stub
			return new BidiMessagingProtocolImpl<MyServerPackets>();
		}
	};
	int port=Integer.parseInt(args[0]);
	Server<MyServerPackets> myServer= Server.threadPerClient(port, protocolFactory, encoderDecoderFactory);

	myServer.serve();

			
			
			
			
		}
	}
