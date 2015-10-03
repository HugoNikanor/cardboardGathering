package central;

import exceptions.CardNotFoundException;

import gamePieces.Battlefield;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import gui.Gui;

public class GameLogic {

	private Battlefield ownBattlefield;
	private Battlefield otherBattlefield;

	//private Network network;
	private NetworkThread networkThread;

	private Gui gui;

	private ActionEventHandler actionEventHandler;

	/**
	 * TODO everything
	 */
	public GameLogic() {

		//System.out.println("Debug: start of GameLogic");

		//ownBattlefield = new Battlefield("cardList");

		// These lines are debug
		/*
		try {
			System.out.println(ownBattlefield.getCards().getCard(0).toString());
			System.out.println(ownBattlefield.getCards().getCard(1).toString());
		} catch (CardNotFoundException e) {
			e.printStackTrace();
		}
		*/

		//networkThread = new NetworkThread();

		actionEventHandler = new ActionEventHandler();
		gui = new Gui();
		gui.addActionEventHandler(actionEventHandler);

		Gui.launch(Gui.class);
		//gui.addListeners(listeners);
		
		//System.out.println("Debug: end of GameLogic");

	}

	public class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			System.out.println("Hello World");
		}
	}

	private class NetworkThread implements Runnable {
		@Override
		public void run() {
			/**
			 * TODO
			 * Ask the network class if anything has happenend,
			 * and if it has, tell 'GameLogic'
			 */
		}
	} //private class NetworkThread

	/**
	 * TODO
	 * There should be input listeners for the GUI here
	 */

}
