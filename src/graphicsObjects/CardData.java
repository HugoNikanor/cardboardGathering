package graphicsObjects;

import gamePieces.Card;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * TODO clean this up
 * class for everything that should be on a card
 * @see gamePieces.Card
 */
public class CardData extends VBox {

	/**
	 * @param card the card that the data should be taken from
	 * @see gamePieces.Card
	 */
	public CardData( Card card ) {
		HBox nameLine = new HBox();
		nameLine.setMaxWidth( 116 );
		Label cardNameLabel = new Label( card.getCardName() );
		cardNameLabel.setWrapText( true );
		//cardNameLabel.setFont( new Font("Arial", 8) );
		Label manaBlankLabel = new Label( Integer.toString(card.getManaCostBlank()) );
		manaBlankLabel.setWrapText( true );
		//manaBlankLabel.setFont( new Font("Arial", 8) );
		nameLine.getChildren().add( cardNameLabel );
		nameLine.getChildren().add( manaBlankLabel );
		this.getChildren().add( nameLine );

		HBox typeLine = new HBox();
		typeLine.setMaxWidth( 116 );
		Label typeLabel = new Label( card.getType() );
		typeLabel.setWrapText( true );
		typeLabel.setFont( new Font("Arial", 8) );
		Label dashLabel = new Label( "― " );
		dashLabel.setWrapText( true );
		dashLabel.setFont( new Font("Arial", 8) );
		Label subtypeLabel = new Label( card.getSubtype() );
		subtypeLabel.setWrapText( true );
		subtypeLabel.setFont( new Font("Arial", 8) );
		typeLine.getChildren().add( typeLabel );
		typeLine.getChildren().add( dashLabel );
		typeLine.getChildren().add( subtypeLabel );
		this.getChildren().add( typeLine );

		VBox infoBox = new VBox();
		infoBox.setMaxWidth( 116 );
		Label abilityLabel = new Label( card.getAbility() );
		abilityLabel.setWrapText( true );
		abilityLabel.setFont( new Font("Arial", 8) );
		Label flavourLabel = new Label( card.getFlavour() );
		flavourLabel.setWrapText( true );
		flavourLabel.setFont( new Font("Arial", 8) );
		infoBox.getChildren().add( abilityLabel );
		infoBox.getChildren().add( flavourLabel );
		this.getChildren().add( infoBox );

		HBox statLine = new HBox();
		statLine.setMaxWidth( 116 );
		Label powerLabel = new Label( Integer.toString(card.getPower()) );
		powerLabel.setWrapText( true );
		Label slashLabel = new Label( "/" );
		slashLabel.setWrapText( true );
		Label toughnessLabel = new Label( Integer.toString(card.getToughness()) );
		toughnessLabel.setWrapText( true );
		// Note that loyalty is completely ignored here
		statLine.getChildren().add( powerLabel );
		statLine.getChildren().add( slashLabel );
		statLine.getChildren().add( toughnessLabel );
		this.getChildren().add( statLine );
	}
}
