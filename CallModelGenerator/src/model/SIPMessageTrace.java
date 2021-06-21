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

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return super.toString() + "\nSIPMessageTrace [direction=" + direction + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}
