package model;

/**
 * Incoming/Outgoing signal to AS.
 */
public class SIPMessageTrace extends Signal {
	public final static String INCOMING = "SIP Message Trace : Incoming";
	public final static String OUTGOING = "SIP Message Trace : Outgoing";

	public enum Direction {
		Incoming, Outgoing
	}

	private Direction direction;
	private int line;

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	@Override
	public String toString() {
		return super.toString() + "\nSIPMessageTrace [direction=" + direction + ", line=" + line + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}
