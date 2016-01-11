package central;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {

	private Properties prop;

	public Settings( String path ) {
		prop = new Properties();
		try {
			FileInputStream propInStream = new FileInputStream( path );
			prop.load(propInStream);
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}

	public String getProperty( String key, String fallback ) {
		return prop.getProperty( key, fallback );
	}
}
