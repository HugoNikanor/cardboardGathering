package database;

import java.io.File;
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

/**
 * A class for reading the JSon files
 * and writing their data to card obects
 * TODO this may use 'int`s' for card id's in a few places,
 * this might be a problem since the card id's are 'long`s'
 * @see gamePieces.Card
 */
public class JSONCardReader {

	/**
	 * A list of all cards
	 * used as a template to copy the cards to use of of
	 */
	private ArrayList<Card> cards;

	/**
	 * Loads all possible cards into memory
	 */
	public JSONCardReader() {
		cards = new ArrayList<Card>();

		ArrayList<JSONObject> cardBufferList;

		try {
			ArrayList<JSONTokener> tokenerList = new ArrayList<JSONTokener>();
			String pathRoot = "database/";

			/*
			 * Loads all files in database/ and makes them ready to be read.
			 */
			File folder = new File(pathRoot);
			File[] listOfFiles = folder.listFiles();
			for ( int i = 0; i < listOfFiles.length; i++ ) {
				if( !listOfFiles[i].isDirectory() ) {
					tokenerList.add( new JSONTokener( new FileReader( listOfFiles[i] )));
				}
			}

			/*
			 * Gets all the card objects from the json files
			 */
			JSONArray jsonCards = new JSONArray();
			for( int i = 0; i < tokenerList.size(); i++ ) {
				try {
					JSONObject tempJObject = new JSONObject(tokenerList.get(i));
					// tempJArray holds all the objects in the "cards" array
					JSONArray tempJArray = tempJObject.getJSONArray("cards");
					for( int j = 0; j < tempJArray.length(); j++) {
						jsonCards.put(tempJArray.get(j));
					}
				} catch (JSONException innerJException) {
					System.out.print  ("json problem, non fatal in file ");
					System.out.print  ( listOfFiles[i].getName() + "@");
					System.out.println(tokenerList.get(i).toString());
				}
			}

			/*
			 * Puts the card objects into a regular arraylist
			 */
			cardBufferList = new ArrayList<JSONObject>();
			for( int i = 0; i < jsonCards.length(); i++ ) {
				cardBufferList.add(jsonCards.getJSONObject(i));
			}

			/*
			 * Extract the data from the json card objects.
			 * Then put it into gamePieces.Card objects
			 */
			String name;
			String type;
			String subtype;
			String text;
			String flavor;
			int power;
			int toughness;
			int loyalty;
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
					for( int i = 0; i < typeArray.length(); i++ ) {
						type = type.concat(typeArray.getString(i));
						type = type.concat(" ");
					}
				} catch (JSONException e) {
					type = "Default type";
				}
				try {
					JSONArray typeArray = tempObject.getJSONArray("subtypes");
					subtype = new String("");
					for( int i = 0; i < typeArray.length(); i++ ) {
						subtype = subtype.concat(typeArray.getString(i));
						subtype = subtype.concat(" ");
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
					if( indata.equals("*") ) {
						power = -200;
					}
					else { 
						try {
							power = Integer.parseInt(indata);
						} catch (NumberFormatException nfe) {
							power = -300;
						}
					}
				} catch (JSONException e) {
					power = -100;
				}
				try {
					String indata = tempObject.getString("toughness");
					if( indata.equals("*") ) {
						toughness = -200;
					}
					else { 
						try {
							toughness = Integer.parseInt(indata);
						} catch (NumberFormatException nfe) {
							toughness = -300;
						}
					}
				} catch (JSONException e) {
					toughness = -100;
				}
				try {
					mana = tempObject.getString("manaCost");
				} catch (JSONException e) {
					mana = "";
				}
				try {
					loyalty = tempObject.getInt("loyalty");
				} catch (JSONException e) {
					loyalty = -100;
				}
				manaBlack = StringUtils.countMatches(mana, "{B}");
				manaBlue  = StringUtils.countMatches(mana, "{U}");
				manaGreen = StringUtils.countMatches(mana, "{G}");
				manaRed   = StringUtils.countMatches(mana, "{R}");
				manaWhite = StringUtils.countMatches(mana, "{W}");
				String possibleManaCost;
				try {
					possibleManaCost = mana.substring(mana.indexOf('{') + 1, mana.indexOf('}') - 1);
				} catch (StringIndexOutOfBoundsException e) {
					possibleManaCost = "0";
				}
				if( StringUtils.isNumeric(possibleManaCost) ) {
					manaBlank = Integer.parseInt(possibleManaCost);
				} else {
					manaBlank = 0;
				}

				cards.add(new Card(
					name,
					type,
					subtype,
					text,
					flavor,
					power,
					toughness,
					loyalty,
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
	 * @param cardName get a usable version of the wanted card
	 * @param cardId what id the returned card sholud have, make sure to incrament the value when calling the method
	 * @throws CardNotFoundException if there is no card with cardName in the json files
	 */
	public Card get( String cardName, long cardId ) throws CardNotFoundException {
		for( Card returnCard : cards ) {
			if( Objects.equals( returnCard.getCardName(), cardName ) ) {
				// returns a copy of returnCard
				return new Card( returnCard, cardId );
			}
		}
		throw new CardNotFoundException("No such card in cardlist (" + cardName + ")");
	}
}
