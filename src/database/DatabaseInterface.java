package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import gamePieces.Card;

public class DatabaseInterface {

	Card[] cards;
	Card[] tempCards;
	int noCards;

	Connection connection = null;
	Statement statement   = null;
	ResultSet resultSet   = null;

	//String url      = "jdbc:mysql://localhost:3306/testdb";
	String url      = "jdbc:mysql://localhost:3306/cardlist";
	//String user     = "testuser";
	//String password = "test623";
	String user     = "root";
	String password = "";
	
	public DatabaseInterface(String cardList) {
		cards = new Card[60];


		try {
			connection = DriverManager.getConnection(url, user, password);

			statement = connection.createStatement();

			String querry1 = "SELECT COUNT(*) FROM cards;";
			resultSet = statement.executeQuery(querry1);
			if( resultSet.next() ) {
				noCards = resultSet.getInt("count(*)");
				tempCards = new Card[noCards];
			}

			String querry2 = "SELECT * FROM cards;";
			resultSet = statement.executeQuery(querry2);

			while( resultSet.next() ) {
				int currentRow = resultSet.getRow() - 1;
				tempCards[currentRow] = new Card(
					resultSet.getString("name"),
					resultSet.getString("type"),
					resultSet.getString("subtype"),
					resultSet.getString("ability"),
					resultSet.getString("flavour"),
					resultSet.getInt("power")    ,
					resultSet.getInt("toughness"),
					0,
					resultSet.getInt("manaBlack"),
					resultSet.getInt("manaBlue") ,
					resultSet.getInt("manaGreen"),
					resultSet.getInt("manaRed")  ,
					resultSet.getInt("manaWhite"),
					resultSet.getInt("manaBlank")
				);
			}
			for( int i = 0; i < 60; i++ ) {
				Random rand = new Random();
				int c = rand.nextInt(noCards);
				cards[i] = new Card(
					tempCards[c].getCardName(),
					tempCards[c].getType(),
					tempCards[c].getSubtype(),
					tempCards[c].getAbility(),
					tempCards[c].getFlavour(),
					tempCards[c].getPower(),
					tempCards[c].getToughness(),
					tempCards[c].getLoyalty(),
					tempCards[c].getManaCostBlack(),
					tempCards[c].getManaCostBlue(),
					tempCards[c].getManaCostGreen(),
					tempCards[c].getManaCostRed(),
					tempCards[c].getManaCostWhite(),
					tempCards[c].getManaCostBlank()
				);
			}
		} catch (SQLException e) {
			Logger lgr = Logger.getLogger(DatabaseInterface.class.getName());
			lgr.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				if( resultSet != null ) {
					resultSet.close();
				}
				if( statement!= null ) {
					statement.close();
				}
				if( connection != null ) {
					connection.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseInterface.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}


	}

	public Card[] getCards() {
		return cards;
	}
}
