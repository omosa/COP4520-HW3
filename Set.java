import java.util.concurrent.atomic.AtomicInteger;

public class Set<T> {
	final Node head, tail;
	final AtomicInteger size = new AtomicInteger();
	
	public Set() {
		head = new Node(Integer.MIN_VALUE);
		tail = new Node(Integer.MAX_VALUE);
		head.next = tail;
	}
	
	public boolean add(T value) {
		Node prev = head;
		Node curr = head.next;
		int key = value.hashCode();
		
		while (true) {
			if (curr == null) {
				// restart
				prev = head;
				curr = head.next;
				continue;
			}
			if (curr.key >= key) {
				// add safely
				synchronized (prev) {
					synchronized (curr) {
						if (prev.removed || curr.removed) {
							// restart
							prev = head;
							curr = head.next;
							continue;
						}
						if (prev.next != curr) {
							curr = prev.next;
							continue;
						}
						Node node = new Node(value);
						prev.next = node;
						node.next = curr;
						size.incrementAndGet();
						return true;
					}
				}
			}
			else {
				prev = curr;
				curr = curr.next;
			}
		}
	}
	
	public T pop() {
		Node head = this.head;
		do {
			Node node = head.next;
			if (node == tail)
				return null;
			else {
				synchronized (head) {
					synchronized (node) {
						if (head.next != node || node.removed)
							continue;
						node.removed = true;
						head.next = node.next;
						size.decrementAndGet();
						return node.value;
					}
				}
			}
		} while (true);
	}
	
	public boolean contains(T value) {
		int key = value.hashCode();
		Node node = head;
		while (node != null && node.key <= key) {
			if (node.key == key && !node.removed && node.value.equals(value))
				return true;
			node = node.next;
		}
		return false;
	}
	
	public int size() {
		return size.get();
	}
	
	public class Node {
		final int key;
		final T value;
		boolean removed = false;
		Node next = null;
		
		private Node(int key) {
			this.key = key;
			this.value = null;
		}
		
		public Node(T value) {
			this.key = value.hashCode();
			this.value = value;
		}
	}
}