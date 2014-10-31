package at.jku.tk.steinbauer.bigdata.mr;

import java.io.Serializable;

public class WebTrafficElement implements Serializable {

	private static final long serialVersionUID = 7288929966449349214L;
	
	private int count;
	
	private long timestamp;
	
	private String from;
	
	private String to;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
