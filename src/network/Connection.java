package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import central.GameLogic;
import inputObjects.NetworkPacket;

public class Connection {

	//How many ms there should be between sending data to the server
	public static int UPDATE_TIME = 1000;

	private String ip = "127.0.0.1";

	private Socket socket;
	private ObjectInputStream objInStream;
	private ObjectOutputStream objOutStream;

	private GameLogic.InputObjectHandler inObjHandler;

	private boolean connected;

	public Connection( GameLogic.InputObjectHandler inputObjectHandler ) {
		this( inputObjectHandler, 23732 );
	}

	public Connection( GameLogic.InputObjectHandler inputObjectHandler, int port ) {
		this.inObjHandler = inputObjectHandler;

		connected = false;
		System.out.println( "Trying to connect to server at : " + ip + ":" + port );
		while( !connected ) {
			try {
				socket = new Socket( ip, port );
				connected = true;
			} catch( IOException e ) {
				System.out.println( "Connection attempt failed" );

				try {
					Thread.sleep( 2000 );
				} catch (InterruptedException ie) {
					e.printStackTrace();
				}
			}
		}
		System.out.println( "Connected to server" );
		
		boolean streamsReady = false;
		while( !streamsReady ) {
			try {
				objOutStream = new ObjectOutputStream( socket.getOutputStream() );
				//System.out.println( "check 1" );
				objInStream = new ObjectInputStream( socket.getInputStream() );
				//System.out.println( "check 2" );
				streamsReady = true;
			} catch( IOException ie ) {
				ie.printStackTrace();
			}
		}

		System.out.println( "Streams set up" );

		new Thread( new NetworkObjectInputThread() ).start();

	}

	private class NetworkObjectInputThread implements Runnable {
		@Override
		public void run() {
			synchronized( this ) {
				boolean running = true;
				while( running ) {
					try {
						NetworkPacket inPacket = (NetworkPacket) objInStream.readObject();
						inObjHandler.handleObject( inPacket );
						System.out.println( "object read" );
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
						running = false;
					}
				}
			}
		}
	}

	public void sendPacket( NetworkPacket networkPacket ) {
		try {
			System.out.println( networkPacket );
			objOutStream.writeObject( networkPacket );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}
}
