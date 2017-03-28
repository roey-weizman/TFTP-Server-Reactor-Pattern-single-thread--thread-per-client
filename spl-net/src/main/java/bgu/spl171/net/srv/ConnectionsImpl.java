package bgu.spl171.net.srv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import bgu.spl171.net.ServerPackets.MyServerPackets;
import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.bidi.Connections;
import bgu.spl171.net.srv.bidi.ConnectionHandler;

public class ConnectionsImpl<T> implements Connections<T> {
	// Fields
	private ConcurrentHashMap<Integer, ConnectionHandler<T>> myClients;
	
	public ConnectionsImpl(){
		 myClients = new ConcurrentHashMap<>();
	    }
	
	
	// BlockingConnectionHandler<T> handler = new
	// BlockingConnectionHandler<>(clientSock,encdecFactory.get(),protocolFactory.get());

	@Override
	public boolean send(int connectionId, Object msg) {
		ConnectionHandler<T> t = myClients.get(connectionId);
		if (t == null)
			return false;
		t.send((T) msg);
		return true;
	}

	@Override
	public void broadcast(Object msg) {
		for (ConnectionHandler<T> value : myClients.values()) {
			value.send((T) msg);
		}

	}

	public void setClientsMap(int id, ConnectionHandler<T> handler) {

		myClients.put(id, handler);
	            
	        }
		
		
	

	public ConcurrentHashMap<Integer, ConnectionHandler<T>> getClientsMap() {
		return myClients;
	}

	public ConnectionHandler<T> getHandler(int connectionId){
		return myClients.get(connectionId);
	}

	@Override
	public void disconnect(int connectionId) {
		try {
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			myClients.get(connectionId).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		myClients.remove(connectionId);
	}

}
