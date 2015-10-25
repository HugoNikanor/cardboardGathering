package database;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import exceptions.CardNotFoundException;

import gamePieces.Card;

public class JSONCardReader {

	ArrayList<Card> cards;
	ArrayList<JSONObject> cardBufferList;

	/**
	 * Loads all possible cards into memory,
	 * the other method allows to access the data
	 */
	public JSONCardReader() {
		cards = new ArrayList<Card>();

		ArrayList<JSONObject> cardBufferList;

		try {

			JSONTokener jTokener = new JSONTokener(new FileReader("/home/hugo/code/java/cardboardGathering/database/SOM-x.json"));
			JSONObject obj = new JSONObject(jTokener);

			JSONArray jsonCards = obj.getJSONArray("cards");

			cardBufferList = new ArrayList<JSONObject>();

			for( int i = 0; i < jsonCards.length(); i++ ) {
				cardBufferList.add(jsonCards.getJSONObject(i));
			}

			String name;
			String type;
			String subtype;
			String text;
			String flavor;
			int power;
			int toughness;
			String mana;
			int manaBlack;
			int manaBlue;
			int manaGreen;
			int manaRed;
			int manaWhite;
			int manaBlank;

			int tempIndex = 0;
			while( cards.size() < cardBufferList.size() ) {
				JSONObject tempObject = cardBufferList.get(tempIndex++);

				try {
					name = tempObject.getString("name");
				} catch (JSONException e) {
					name = "Default name";
				}
				try {
					JSONArray typeArray = tempObject.getJSONArray("types");
					type = new String("");
					for( int i = 0; i > typeArray.length(); i++ ) {
						type.concat(typeArray.getString(i));
					}
				} catch (JSONException e) {
					type = "Default type";
				}
				try {
					JSONArray typeArray = tempObject.getJSONArray("subtypes");
					subtype = new String("");
					for( int i = 0; i > typeArray.length(); i++ ) {
						subtype.concat(typeArray.getString(i));
					}
				} catch (JSONException e) {
					subtype = "";
				}
				try {
					text = tempObject.getString("text");
				} catch (JSONException e) {
					text = "";
				}
				try {
					flavor = tempObject.getString("flavor");
				} catch (JSONException e) {
					flavor = "";
				}
				try {
					String indata = tempObject.getString("power");
					if( indata == "*" ) {
						power = -2;
					}
					else { 
						try {
							power = Integer.parseInt(indata);
						} catch (NumberFormatException nfe) {
							power = -3;
						}
					}
				} catch (JSONException e) {
					power = -1;
				}
				try {
					String indata = tempObject.getString("toughness");
					if( indata == "*" ) {
						toughness = -2;
					}
					else { 
						try {
							toughness = Integer.parseInt(indata);
						} catch (NumberFormatException nfe) {
							toughness = -3;
						}
					}
				} catch (JSONException e) {
					toughness = -1;
				}
				try {
					mana = tempObject.getString("manaCost");
				} catch (JSONException e) {
					mana = "";
				}
				manaBlack = StringUtils.countMatches(mana, "{B}");
				manaBlue  = StringUtils.countMatches(mana, "{U}");
				manaGreen = StringUtils.countMatches(mana, "{G}");
				manaRed   = StringUtils.countMatches(mana, "{R}");
				manaWhite = StringUtils.countMatches(mana, "{W}");
				manaBlank = 0;

				cards.add(new Card(
					name,
					type,
					subtype,
					text,
					flavor,
					power,
					toughness,
					0,
					manaBlack,
					manaBlue,
					manaGreen,
					manaRed,
					manaWhite,
					manaBlank 
				));
			}
		} catch (JSONException je) {
			je.printStackTrace();
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		}

	}

	/**
	 * @return the card with the desired name
	 */
	public Card get( String cardName ) throws CardNotFoundException {
		for( Card returnCard : cards ) {
			if( Objects.equals( returnCard.getCardName(), cardName ) ) {
				return returnCard;
			}
		}
		throw new CardNotFoundException("No such card in cardlist");
	}
}
