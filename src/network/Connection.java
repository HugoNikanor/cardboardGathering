package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import gamePieces.Battlefield;

public class Connection {

	private Battlefield battlefield;
	private Battlefield otherBattlefield;

	private String ip = "127.0.0.1";

	private Socket socket;
	private ObjectInputStream objInStream;
	private ObjectOutputStream objOutStream;
	private DataInputStream dataInStream;
	private DataOutputStream dataOutStream;

	private boolean connected;

	public Connection( Battlefield battlefield ) {
		this( battlefield, 23732 );
	}

	public Connection( Battlefield battlefield, int port ) {
		this.battlefield = battlefield;
		
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
				System.out.println( "check 1" );
				objInStream = new ObjectInputStream( socket.getInputStream() );
				System.out.println( "check 2" );
				streamsReady = true;
			} catch( IOException ie ) {
				ie.printStackTrace();
			}
		}

		System.out.println( "Streams set up" );

		new Thread( new NetworkInputThread() ).start();
		new Thread( new NetworkOutputThread() ).start();

	}
	
	private void sendData() throws IOException {
		objOutStream.writeObject( battlefield );
	}

	public Battlefield getOponent() throws ClassNotFoundException, IOException {
		return otherBattlefield;
	}

	private class NetworkInputThread implements Runnable {
		Battlefield temp;
		@Override
		public void run() {
			synchronized( this ) {
				//while( true ) {
				try {
					temp = (Battlefield) objInStream.readObject();
					otherBattlefield = temp;
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}

				//}
			}
		}
	}

	private class NetworkOutputThread implements Runnable {
		@Override
		public void run() {
			synchronized( this ) {
				try {
					sendData();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}
}
