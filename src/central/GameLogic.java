package central;

import exceptions.CardNotFoundException;
import gamePieces.Battlefield;

public class GameLogic {

	private Battlefield ownBattlefield;
	private Battlefield otherBattlefield;

	//private Network network;
	private NetworkThread networkThread;

	//private Gui gui;

	/**
	 * TODO everything
	 */
	public GameLogic() {

		//System.out.println("Debug: start of GameLogic");

		ownBattlefield = new Battlefield("cardList");

		// These lines are debug
		/*
		try {
			System.out.println(ownBattlefield.getCards().getCard(0).toString());
			System.out.println(ownBattlefield.getCards().getCard(1).toString());
		} catch (CardNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

		//networkThread = new NetworkThread();


		//gui = new Gui();
		//gui.addListeners(listeners);
		
		//System.out.println("Debug: end of GameLogic");

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
