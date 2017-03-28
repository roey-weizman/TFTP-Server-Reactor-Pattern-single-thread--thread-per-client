package bgu.spl171.net.impl.TFTPreactor;

import java.io.IOException;
import java.util.function.Supplier;

import bgu.spl171.net.ServerPackets.MyServerPackets;
import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.srv.BidiMessagingProtocolImpl;
import bgu.spl171.net.srv.EncoderDecoderImpl;
import bgu.spl171.net.srv.Server;

public class ReactorMain {
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
	Server<MyServerPackets> myServer= Server.reactor(4, port, protocolFactory, encoderDecoderFactory);

	myServer.serve();


}
	
}
