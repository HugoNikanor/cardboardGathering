package central;


public class Main {
	public static void main(String[] args) {
		System.out.println(
			"===============\n" + 
			"PROGRAM STARTED\n" + 
			"==============="  
		);

		// This also calls the constructor,
		// but I don't know why...
		GameLogic.launch(GameLogic.class);
	}
}
