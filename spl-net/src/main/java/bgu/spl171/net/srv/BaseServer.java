package bgu.spl171.net.srv;

import bgu.spl171.net.api.MessageEncoderDecoder;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.Connections;


public abstract class BaseServer<T> implements Server<T> {

	private final int port;
	private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
	private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
	private ServerSocket sock;  
	private Connections<T> connection;
	AtomicInteger id=new AtomicInteger();

	public BaseServer(
			int port,
			Supplier<BidiMessagingProtocol<T>> protocolFactory,
			Supplier<MessageEncoderDecoder<T>> encdecFactory) {

		this.port = port;
		this.protocolFactory = protocolFactory;
		this.encdecFactory = encdecFactory;
		this.sock = null;

	}


	@Override
	public void serve() {
		try (ServerSocket serverSock = new ServerSocket(port)) {
			this.sock = serverSock; //just to be able to close			
			this.connection=new ConnectionsImpl<T>();
			id.getAndIncrement();
			while (!Thread.currentThread().isInterrupted()) {
				Socket MyClient=serverSock.accept();

				BidiMessagingProtocol messagingProtocol =  protocolFactory.get();
				BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<T>(MyClient,encdecFactory.get(),messagingProtocol);
				messagingProtocol.start(id.get(), connection);
				((ConnectionsImpl) connection).setClientsMap(id.getAndIncrement(), handler);

				execute(handler);
			}

		} catch (IOException ex) {
		}

	}

	@Override
	public void close() throws IOException {
		if (sock != null)
			sock.close();
	}
	protected abstract void execute(BlockingConnectionHandler<T>  handler);

}
