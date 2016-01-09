package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import gamePieces.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class CardController implements Initializable {

	@FXML 
	private Text name;

	@FXML 
	private Text ability;

	@FXML 
	private Text flavour;

	@FXML
	private Pane imagePane;	

	public void updateFields( Card card ) {
		name.setText( card.getCardName() );
		ability.setText( card.getAbility() );
		flavour.setText( card.getFlavour() );
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
}

