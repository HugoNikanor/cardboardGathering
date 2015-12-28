package gamePieces;

public class CardIdCounter {
	long counter;

	public CardIdCounter( int initialValue ) {
		counter = initialValue;
	}
	public void incrament() {
		counter++;
	}
	public long getCounter() {
		return counter;
	}
	public long getCounterAndIncrament() {
		long temp = counter;
		counter++;
		return temp;
	}
}
