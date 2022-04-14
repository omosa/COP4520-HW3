import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Problem1 {
	private static final int NUM_THREADS = 4;
	/** Unordered list of gifts */
	private static final Gift[] ALL_GIFTS = new Gift[500_000];
	/** Ordered chain of gifts */
	private static Set<Gift> chain = new Set<>();
	private static Gift[] checkGift = new Gift[NUM_THREADS];
	private static boolean[] inChain = new boolean[NUM_THREADS];
	
	public static void main(String[] args) throws InterruptedException {
		// create unordered list of gifts
		for (int i = 0; i < ALL_GIFTS.length; i++) {
			ALL_GIFTS[i] = new Gift(i);
		}
		Collections.shuffle(Arrays.asList(ALL_GIFTS));
		
		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			final int j = i;
			threads[j] = new Thread(() -> servant(j));
			threads[j].start();
		}
		
		// ask about 100 random gifts
		Random rand = new Random();
		for (int i = 0; i < 100; i++) {
			int servant = rand.nextInt(NUM_THREADS);
			int gift = rand.nextInt(ALL_GIFTS.length);
			checkGift[servant] = ALL_GIFTS[gift];
			while (checkGift[servant] != null) {
				// spin
			}
			if (inChain[servant])
				System.out.println("Servant " + servant + ": Gift " + gift + " is in the chain");
			else
				System.out.println("Servant " + servant + ": Gift " + gift + " is not in the chain");
		}
		
		for (Thread thread : threads) {
			thread.join();
		}
		int count = 0;
		for (Gift gift : ALL_GIFTS) {
			if (gift.hasNote)
				count++;
		}
		System.out.println(count + " notes written");
	}
	
	public static void servant(int id) {
		// each servant takes every 4th gift -- there's no need for contention
		// servant puts the gift in the ordered chain
		// then takes a gift out to add a note to it
		
		Gift gift;
		for (int i = id; i < ALL_GIFTS.length; i += NUM_THREADS) {
			// add gift to chain
			gift = ALL_GIFTS[i];
			chain.add(gift);
			
			// take gift from chain
			gift = chain.pop();
			gift.addNote();
			
			// answer minotaur
			gift = checkGift[id];
			if (gift != null) {
				inChain[id] = chain.contains(gift);
				checkGift[id] = null;
			}
		}
	}

}
