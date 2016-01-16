package controllers;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import central.GameScene;
import central.Settings;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TitleSceneController implements Initializable {

	@FXML
	private TextField ipField;

	@FXML
	private TextField portField;

	@FXML
	private TextField deckField;

	@FXML
	private Text infoText;

	private String ip;
	private int port;
	private String deckDir;
	private String defDeck;
	private String fullPath;
	private Path deckFilepath;

	private Stage stage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Settings settings = new Settings( "settings/settings.properties" );

		ip = settings.getProperty("defIpAddress", "127.0.0.1" );
		port = Integer.parseInt(settings.getProperty( "defPort", "23732" ));

		ipField.setPromptText( ip );
		portField.setPromptText( Integer.toString(port) );

		deckDir = settings.getProperty( "defCardDir", "decks/" );
		defDeck = settings.getProperty( "defCardList", "defCardList" );

		fullPath = deckDir.concat( defDeck );
		deckField.setPromptText( fullPath );
	}

	public void addStage( Stage stage ) {
		this.stage = stage;
	}

	@FXML
	private void startGame() {
		System.out.println( "game started" );
		if( !ipField.getText().equals("") )
			ip = ipField.getText();
		if( !portField.getText().equals("") ) {
			try {
				int tempPort = Integer.parseInt( portField.getText() );
				port = tempPort;
			} catch( NumberFormatException ex ) {
			}
		}
		if( !deckField.getText().equals("") )
			fullPath = deckDir + deckField.getText();

		deckFilepath = Paths.get( fullPath );

		System.out.println( fullPath + "\n" + deckFilepath.toString() + "\n----------\n" );

		infoText.setText( "game started, please wait" );

		// This locks up the FX thread.
		// Windows take this as a program crash,
		// this is best fixed by making the code in gameScene and beyond
		// better threaded
		Platform.runLater( new Thread(() -> {
			new GameScene( stage, ip, port, deckFilepath );
		}));
	}
}
