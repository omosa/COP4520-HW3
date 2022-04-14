import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Problem2 {
	private static final int NUM_THREADS = 8;
	private static AtomicInteger min = new AtomicInteger();
	private static int[] temps = new int[NUM_THREADS * 60];

	public static void main(String[] args) throws InterruptedException {
		Thread[] threads = new Thread[NUM_THREADS];
		
		for (int i = 0; i < NUM_THREADS; i++) {
			final int j = i;
			threads[j] = new Thread(() -> sensor(j));
			threads[j].start();
		}
		
		System.out.println("Working...");
		while (true) {
			Thread.sleep(50);
			int minute = min.getAndIncrement();
			
			// generate report
			if (minute > 0 && minute % 60 == 0) {
				int hour = minute / 60;
				LinkedList<Integer> top5 = getTop5();
				LinkedList<Integer> bottom5 = getBottom5();
				Variance variance = getLargestVariance();
				int end = variance.pos / NUM_THREADS;
				int start = end - 10;
				
				System.out.println("=======Hour " + hour + " report=======");
				System.out.println("Top 5 temperatures: " + top5);
				System.out.println("Bottom 5 temperatures: " + bottom5);
				System.out.println("Largest temperature difference interval: Minutes " + start + " to " + end);
				System.out.println();
			}
			
			// start next minute, wake up threads
			for (Thread thread : threads) {
				thread.interrupt();
			}
		}
	}
	
	private static LinkedList<Integer> getTop5() {
		LinkedList<Integer> top = new LinkedList<>();
		for (int i = 0; i < temps.length; i++) {
			int temp = temps[i];
			if (top.size() < 5)
				top.add(temp);
			else if (!top.contains(temp)) {
				int index = 0;
				for (int t : top) {
					if (temp > t) {
						top.removeLast();
						top.add(index, temp);
						break;
					}
					index++;
				}
			}
		}
		return top;
	}

	private static LinkedList<Integer> getBottom5() {
		LinkedList<Integer> bottom = new LinkedList<>();
		for (int i = 0; i < temps.length; i++) {
			int temp = temps[i];
			if (bottom.size() < 5)
				bottom.add(temp);
			else if (!bottom.contains(temp)) {
				int index = 0;
				for (int t : bottom) {
					if (temp < t) {
						bottom.removeLast();
						bottom.add(index, temp);
						break;
					}
					index++;
				}
			}
		}
		return bottom;
	}
	
	private static Variance getLargestVariance() {
		LinkedList<Integer> range = new LinkedList<>();
		int delta = 0;
		int max = 0;
		int pos = 0;
		LinkedList<Integer> sublist = new LinkedList<>();
		range.add(temps[0]);
		for (int i = 1; i < temps.length; i++) {
			int temp = temps[i];
			delta += Math.abs(range.getLast() - temp);
			range.add(temp);
			if (i >= 10 * NUM_THREADS) {
				delta -= Math.abs(range.pop() - range.getFirst());
			}
			// only check if delta is maximum at minute boundaries
			if (i % NUM_THREADS == 0 && delta > max) {
				max = delta;
				pos = i;
				sublist.clear();
				sublist.addAll(range);
			}
		}
		return new Variance(pos, max, sublist);
	}

	public static void sensor(int id) {
		Random rand = new Random();
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// next minute
			}
			int temp = rand.nextInt(270) - 100;
			temps[(min.get() % 60) * NUM_THREADS + id] = temp;
		}
	}

}
