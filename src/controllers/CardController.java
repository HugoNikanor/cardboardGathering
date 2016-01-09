package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import gamePieces.Card;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class CardController implements Initializable {

	@FXML 
	private Text name;
	@FXML HBox manaCont;

	@FXML 
	private Text ability;
	@FXML 
	private Text flavour;

	@FXML
	private Pane imagePane;	

	@FXML
	private Text type;
	@FXML
	private Text typeDash;
	@FXML
	private Text subType;

	@FXML
	private Text power;
	@FXML
	private Text statSlash;
	@FXML
	private Text toughness;

	public void updateFields( Card card ) {
		name.setText( card.getCardName() );
		ability.setText( card.getAbility() );
		flavour.setText( card.getFlavour() );

		type.setText( card.getType() );
		if( !card.getSubtype().equals( "" ) ) {
			subType.setText( card.getSubtype() );
		} else {
			typeDash.setText( "" );
		}


		int pow = card.getPower();
		if( pow == -100 )
			power.setText( "" );
		else if( pow == -200 )
			power.setText( "*" );
		else if( pow == -300 )
			power.setText( "E" );
		else
			power.setText( Integer.toString(pow) );

		int tough = card.getToughness();
		if( tough == -100 ) {
			toughness.setText( "" );
			statSlash.setText( "" );
		} else if( tough == -200 )
			toughness.setText( "*" );
		else if( tough == -300 )
			toughness.setText( "E" );
		else
			toughness.setText( Integer.toString(tough) );


	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
}

