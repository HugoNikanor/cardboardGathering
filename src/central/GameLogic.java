package central;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameLogic extends Application {

	private int port;
	private String ipAddr;
	private Path deckFilepath;
	private boolean showTitle;

	private Settings settings;

	public GameLogic() {
		settings = new Settings( "settings/settings.properties" );

		ipAddr = settings.getProperty("defIpAddress", "127.0.0.1" );
		port = Integer.parseInt(settings.getProperty( "defPort", "23732" ));

		deckFilepath = Paths.get(
		               settings.getProperty( "defCardDir", "decks/" ) +
		               settings.getProperty( "defCardList", "defCardList" ));

		String showTitleStr = settings.getProperty( "showTitle", "true" );
		if( showTitleStr.equalsIgnoreCase( "true" ) )
			showTitle = true;
		else
			showTitle = false;
   	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("cardboardGathering");
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle( WindowEvent event ) {
				Platform.exit();
				System.exit(0);
			}
		});

		if( showTitle ) {
			System.out.println( "showTitle is true" );
			new TitleScene( primaryStage, settings );
		} else {
			System.out.println( "showTitle is false" );
			new GameScene( primaryStage, ipAddr, port, deckFilepath );
		}


		// TODO maybe bind exit fullsceen keys properly
		primaryStage.setFullScreenExitHint("Intuitive controlls are for noobs!");
	}
}
