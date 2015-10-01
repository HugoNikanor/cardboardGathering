package central;

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


		ownBattlefield = new Battlefield("cardList");

		networkThread = new NetworkThread();


		//gui = new Gui();
		//gui.addListeners(listeners);
		
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
