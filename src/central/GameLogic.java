package central;

import exceptions.CardNotFoundException;

import gamePieces.Battlefield;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import gui.Gui;

public class GameLogic {

	private Battlefield ownBattlefield;
	private Battlefield otherBattlefield;

	//private Network network;
	private NetworkThread networkThread;

	private Gui gui;

	private ActionEventHandler actionEventHandler;
	private KeyEventHandler keyEventHandler;

	private Stage stage;

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
		keyEventHandler    = new KeyEventHandler();
		gui = new Gui();
		//gui.addActionEventHandler(actionEventHandler);

		//stage = new Stage();


		Gui.addActionEventHandler(actionEventHandler);
		Gui.addKeyEventHandler(keyEventHandler);
		//Gui.launch(Gui.class);
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
	public class KeyEventHandler implements EventHandler<KeyEvent> {
		@Override
		public void handle(KeyEvent event) {
			if(event.getCode() == KeyCode.M &&
			   event.isControlDown()) {
				Gui.close();
			   }

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

}
