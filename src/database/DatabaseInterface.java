package database;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import gamePieces.Card;

public class DatabaseInterface {

	Card[] cards;
	ArrayList<JSONObject> cardArrayList;

	public DatabaseInterface(String cardList) {
		cards = new Card[60];

		ArrayList<JSONObject> cardArrayList;

		try {

			JSONTokener jTokener = new JSONTokener(new FileReader("/home/hugo/code/java/cardboardGathering/database/cards.json"));
			JSONObject obj = new JSONObject(jTokener);

			JSONArray jsonCards = obj.getJSONArray("cards");

			cardArrayList = new ArrayList<JSONObject>();

			for( int i = 0; i < jsonCards.length(); i++ ) {
				cardArrayList.add(jsonCards.getJSONObject(i));
			}

			for( JSONObject tempObj : cardArrayList ) {
				System.out.println(tempObj.getString("name"));
			}

			Random rand = new Random();
			for( int i = 0; i < 60; i++ ) {
				int tempIndex = rand.nextInt(cardArrayList.size());
				JSONObject tempObject = cardArrayList.get(tempIndex);
				cards[i] = new Card(
					tempObject.getString("name"),
					tempObject.getString("type"),
					tempObject.getString("subtype"),
					"This be the ability",
					tempObject.getString("flavour"),
					tempObject.getInt("power"),
					tempObject.getInt("toughness"),
					0,
					tempObject.getInt("manaBlack"),
					tempObject.getInt("manaBlue"),
					tempObject.getInt("manaGreen"),
					tempObject.getInt("manaRed"),
					tempObject.getInt("manaWhite"),
					tempObject.getInt("manaBlank")
				);
			}
		} catch (JSONException je) {
			je.printStackTrace();
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		}


	}

	public Card[] getCards() {
		return cards;
	}
}
