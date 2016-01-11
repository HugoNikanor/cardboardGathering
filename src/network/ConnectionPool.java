package network;

public class ConnectionPool {

	private static String ip;
	private static int port;

	private static InputObjectHandler handler;

	private static Connection connection;

	private ConnectionPool() { }

	public static void setDefaultIp( String ip ) {
		ConnectionPool.ip = ip;
	}
	public static void setDefaultPort( int port ) {
		ConnectionPool.port = port;
	}
	public static void setDefaultHandler( InputObjectHandler handler ) {
		ConnectionPool.handler = handler;
	}	

	public static Connection getConnection() throws NullPointerException {
		if( connection == null ) {
			if( ip      == null ||
			    port    == 0    ||
			    handler == null ) {
				throw new NullPointerException( "Some values are missing" );
			}
			connection = new Connection( handler, ip, port );
		}
		return connection;
	}
}
