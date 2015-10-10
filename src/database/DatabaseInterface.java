package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import gamePieces.Card;

public class DatabaseInterface {

	Card[] cards;

	/**
	 * TODO
	 * The final product will NOT use 'properties' but rather aproper database
	 * The following varaibles and objects are mearly a placehalder.
	 */
	Properties pr;
	
	
	public DatabaseInterface(String cardList) {
		pr = new Properties();
		try {
			pr.load(new FileInputStream("/home/hugo/code/java/cardboardGathering/database/cards.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cards = new Card[2];

		cards[0] = new Card(
			pr.getProperty("card1Name"),
			pr.getProperty("card1Type"),
			pr.getProperty("card1Subtype"),
			pr.getProperty("card1Ability"),
			pr.getProperty("card1Flavour"),
			Integer.parseInt(pr.getProperty("card1Power")),
			Integer.parseInt(pr.getProperty("card1Toughness")),
			Integer.parseInt(pr.getProperty("card1Loyalty")),
			Integer.parseInt(pr.getProperty("card1ManaCostBlack")),
			Integer.parseInt(pr.getProperty("card1ManaCostBlue")),
			Integer.parseInt(pr.getProperty("card1ManaCostGreen")),
			Integer.parseInt(pr.getProperty("card1ManaCostRed")),
			Integer.parseInt(pr.getProperty("card1ManaCostWhite")),
			Integer.parseInt(pr.getProperty("card1ManaCostBlank"))
		);

		cards[1] = new Card(
			pr.getProperty("card2Name"),
			pr.getProperty("card2Type"),
			pr.getProperty("card2Subtype"),
			pr.getProperty("card2Ability"),
			pr.getProperty("card2Flavour"),
			Integer.parseInt(pr.getProperty("card2Power")),
			Integer.parseInt(pr.getProperty("card2Toughness")),
			Integer.parseInt(pr.getProperty("card2Loyalty")),
			Integer.parseInt(pr.getProperty("card2ManaCostBlack")),
			Integer.parseInt(pr.getProperty("card2ManaCostBlue")),
			Integer.parseInt(pr.getProperty("card2ManaCostGreen")),
			Integer.parseInt(pr.getProperty("card2ManaCostRed")),
			Integer.parseInt(pr.getProperty("card2ManaCostWhite")),
			Integer.parseInt(pr.getProperty("card2ManaCostBlank"))
		); 
	}

	public Card[] getCards() {
		return cards;
	}

}
