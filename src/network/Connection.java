package network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.Socket;

import serverPackets.NetworkPacket;

/**
 * "Talks" to the network
 * Both responsible for sending and receiving data
 * but doesn't do anything with that data more than passing it along
 */
public class Connection {

	/**
	 * How many milliseconds there should be between packets <br>
	 * Setting this to high causes one of the clients to not start...
	 */
	public static int UPDATE_TIME = 200;

	private Socket socket;
	private ObjectInputStream objInStream;
	private ObjectOutputStream objOutStream;

	private InputObjectHandler inObjHandler;

	/**
	 * If the client is connected to the server
	 * used to check if the client should close
	 */
	private boolean connected;

	/**
	 * Initiate the connection on the default port
	 * @param inputObjectHandler the class that takes care of the inputs
	 * @param ip the ip address of the server
	 */
	public Connection( InputObjectHandler inputObjectHandler, String ip ) {
		this( inputObjectHandler, ip, 23732 );
	}

	/**
	 * Initiates the connection
	 * Sets up a few threads and make sure that the other client answers
	 * @param inputObjectHandler the class that takes care of the packets gotten over the network
	 * @param ip the servers ip address
	 * @param port the port that the server is on
	 */
	public Connection( InputObjectHandler inputObjectHandler, String ip, int port ) {
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
				objInStream = new ObjectInputStream( socket.getInputStream() );
				streamsReady = true;
			} catch( IOException ie ) {
				ie.printStackTrace();
			}
		}

		System.out.println( "Streams set up" );

		new Thread( new NetworkObjectInputThread() ).start();

	}


	/**
	 * Treads which listens for incoming packets over the network
	 */
	private class NetworkObjectInputThread implements Runnable {
		@Override
		public void run() {
			synchronized( this ) {
				boolean running = true;
				while( running ) {
					try {
						NetworkPacket inPacket = (NetworkPacket) objInStream.readObject();
						inObjHandler.handleObject( inPacket );
					} catch( EOFException e ) {
						System.out.println("other client closed connection");
						running = false;
					} catch( StreamCorruptedException e ) { 
						e.printStackTrace();
						running = false;
					} catch( ClassCastException e ) {
						e.printStackTrace();
					} catch( OptionalDataException e ) {
						e.printStackTrace();
					} catch( ClassNotFoundException e ) {
						System.out.println( "Get your classes in order!" );
						e.printStackTrace();
						running = false;
					} catch( IOException e ) {
						e.printStackTrace();
						running = false;
					}
				}
			}
		}
	}

	/**
	 * Send a packet over the network is synchronized since only one packet can
	 * be handled at the other side at a time
	 *
	 * @param networkPacket
	 *            what should be sent over the network
	 * @see serverPackets.NetworkPacket
	 */
	public synchronized void sendPacket( NetworkPacket networkPacket ) {
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
