
public class Gift {
	final int id;
	boolean hasNote = false;
	
	public Gift(int id) {
		this.id = id;
	}
	
	public void addNote() {
		hasNote = true;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}