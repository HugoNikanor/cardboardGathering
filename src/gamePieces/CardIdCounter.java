package gamePieces;

public class CardIdCounter {
	int counter;

	public CardIdCounter( int initialValue ) {
		counter = initialValue;
	}
	public void incrament() {
		counter++;
	}
	public int getCounter() {
		return counter;
	}
	public int getCounterAndIncrament() {
		int temp = counter;
		counter++;
		return temp;
	}
}
