package central;


public class Main {
	public static void main(String[] args) {
		//System.out.println("Debug: Start of main");
		
		System.out.println(
			"===============\n" + 
			"PROGRAM STARTED\n" + 
			"==============="  
		);
		GameLogic.launch(GameLogic.class);

		//System.out.println("Debug: End of main");
	}
}
