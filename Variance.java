import java.util.LinkedList;

public class Variance {
	public final int pos, delta;
	public final LinkedList<Integer> sublist;
	
	public Variance(int pos, int delta, LinkedList<Integer> sublist) {
		this.pos = pos;
		this.delta = delta;
		this.sublist = sublist;
	}

}
