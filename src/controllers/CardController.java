package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import gamePieces.Card;

import graphicsObjects.Token;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class CardController implements Initializable {

	@FXML 
	private Text name;
	@FXML 
	private FlowPane manaCont;
	@FXML
	private Text manaCostBlank;

	@FXML 
	private Text ability;
	@FXML 
	private Text flavour;

	@FXML
	private GridPane imagePane;
	@FXML
	private StackPane redTokenPane;
	@FXML
	private StackPane greenTokenPane;
	@FXML
	private StackPane blueTokenPane;
	@FXML
	private StackPane blackTokenPane;
	@FXML
	private StackPane whiteTokenPane;
	@FXML
	private StackPane greyTokenPane;

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


		
		manaCostBlank.setText( Integer.toString(card.getManaCostBlank()) );
		for( int i = 0; i < card.getManaCostBlack(); i++ ) {
			manaCont.getChildren().add( new Circle(5, Paint.valueOf("Black")) );
		}
		for( int i = 0; i < card.getManaCostBlue(); i++ ) {
			manaCont.getChildren().add( new Circle(5, Paint.valueOf("Blue")) );
		}
		for( int i = 0; i < card.getManaCostRed(); i++ ) {
			manaCont.getChildren().add( new Circle(5, Paint.valueOf("Red")) );
		}
		for( int i = 0; i < card.getManaCostGreen(); i++ ) {
			manaCont.getChildren().add( new Circle(5, Paint.valueOf("Green")) );
		}
		for( int i = 0; i < card.getManaCostWhite(); i++ ) {
			manaCont.getChildren().add( new Circle(5, Paint.valueOf("White")) );
		}
		// TODO X mana


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

		ArrayList<Token> tokens = new ArrayList<Token>();
		tokens.add( new Token( Paint.valueOf("Red") ) );
		tokens.add( new Token( Paint.valueOf("Green") ) );
		tokens.add( new Token( Paint.valueOf("Blue") ) );
		tokens.add( new Token( Paint.valueOf("Black") ) );
		tokens.add( new Token( Paint.valueOf("White") ) );
		tokens.add( new Token( Paint.valueOf("Grey") ) );

		card.setTokenAccess( tokens );

		redTokenPane.getChildren().add( tokens.get(0) );
		greenTokenPane.getChildren().add( tokens.get(1) );
		blueTokenPane.getChildren().add( tokens.get(2) );
		blackTokenPane.getChildren().add( tokens.get(3) );
		whiteTokenPane.getChildren().add( tokens.get(4) );
		greyTokenPane.getChildren().add( tokens.get(5) );

		// TODO shorten this
		greyTokenPane.setOnMouseClicked( e -> {
			if( e.getButton() == MouseButton.PRIMARY)
				tokens.get(5).incrament();
			if( e.getButton() == MouseButton.SECONDARY)
				tokens.get(5).decrament();
			e.consume();
		});
		redTokenPane.setOnMouseClicked( e -> {
			if( e.getButton() == MouseButton.PRIMARY)
				tokens.get(0).incrament();
			if( e.getButton() == MouseButton.SECONDARY)
				tokens.get(0).decrament();
			e.consume();
		});
		blueTokenPane.setOnMouseClicked( e -> {
			if( e.getButton() == MouseButton.PRIMARY)
				tokens.get(2).incrament();
			if( e.getButton() == MouseButton.SECONDARY)
				tokens.get(2).decrament();
			e.consume();
		});
		greenTokenPane.setOnMouseClicked( e -> {
			if( e.getButton() == MouseButton.PRIMARY)
				tokens.get(1).incrament();
			if( e.getButton() == MouseButton.SECONDARY)
				tokens.get(1).decrament();
			e.consume();
		});
		whiteTokenPane.setOnMouseClicked( e -> {
			if( e.getButton() == MouseButton.PRIMARY)
				tokens.get(4).incrament();
			if( e.getButton() == MouseButton.SECONDARY)
				tokens.get(4).decrament();
			e.consume();
		});
		blackTokenPane.setOnMouseClicked( e -> {
			if( e.getButton() == MouseButton.PRIMARY)
				tokens.get(3).incrament();
			if( e.getButton() == MouseButton.SECONDARY)
				tokens.get(3).decrament();
			e.consume();
		});

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
}

