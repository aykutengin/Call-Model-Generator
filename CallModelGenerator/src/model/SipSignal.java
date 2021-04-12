package model;

public class SipSignal extends Signal {
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
	
	
}
