package central;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TitleScene {

	Scene scene;

	StackPane root;
	VBox elementPane;
	HBox portAndIpPane;

	Button startGameBtn;

	TextField ipField;
	TextField portField;

	TextField deckField;

	private String ip;
	private int port;
	private String deckDir;
	private String defDeck;
	private String fullPath;
	private Path deckFilepath;

	public TitleScene( Stage stage, Settings settings ) {

		root = new StackPane();
		elementPane = new VBox( 20 );
		elementPane.setFillWidth( true );
		elementPane.setPrefWidth( 200d );
		portAndIpPane = new HBox( 10 );

		root.getChildren().add( elementPane );



		ip = settings.getProperty("defIpAddress", "127.0.0.1" );
		port = Integer.parseInt(settings.getProperty( "defPort", "23732" ));

	   deckDir = settings.getProperty( "defCardDir", "decks/" );
	   defDeck = settings.getProperty( "defCardList", "defCardList" );


		startGameBtn = new Button( "Start Game" );
		ipField = new TextField();
		ipField.setPromptText( ip );
		portField = new TextField();
		portField.setPromptText( Integer.toString(port) );

		deckField = new TextField();
		fullPath = deckDir.concat( defDeck );
		deckField.setPromptText( fullPath );

		portAndIpPane.getChildren().addAll( ipField, new Text(":"), portField );
		elementPane.getChildren().addAll( portAndIpPane, deckField, startGameBtn );

		startGameBtn.setOnAction( e -> {
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

			new GameScene( stage, ip, port, deckFilepath );
		});
		
		scene = new Scene( root );
		stage.setScene( scene );

		stage.show();

	}
}
