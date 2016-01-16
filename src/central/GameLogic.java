package central;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import controllers.TitleSceneController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameLogic extends Application {

	private int port;
	private String ipAddr;
	private Path deckFilepath;
	private boolean showTitle;


	public GameLogic() {
		Settings settings = new Settings( "settings/settings.properties" );

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
			URL titleURL = Paths.get( "fxml/TitleScene.fxml" ).toUri().toURL();
			FXMLLoader titleLoader = new FXMLLoader( titleURL );
			Pane titleRoot = titleLoader.load();
			((TitleSceneController) titleLoader.getController()).addStage( primaryStage );
			primaryStage.setScene( new Scene( titleRoot ));
			primaryStage.show();
		} else {
			System.out.println( "showTitle is false" );
			new GameScene( primaryStage, ipAddr, port, deckFilepath );
		}


		// TODO maybe bind exit fullsceen keys properly
		primaryStage.setFullScreenExitHint("Intuitive controlls are for noobs!");
	}
}
