package model;

public class UnifiedSIPMessageTrace extends SIPMessageTrace {
	private int incomingLine;
	private int outgoingLine;

	public int getIncomingLine() {
		return incomingLine;
	}

	public void setIncomingLine(int incomingLine) {
		this.incomingLine = incomingLine;
	}

	public int getOutgoingLine() {
		return outgoingLine;
	}

	public void setOutgoingLine(int outgoingLine) {
		this.outgoingLine = outgoingLine;
	}

	@Override
	public String toString() {
		return "UnifiedSIPMessageTrace [incomingLine=" + incomingLine + ", outgoingLine=" + outgoingLine + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	public void setSIPMessageTrace(SIPMessageTrace sipMessageTrace) {
		super.setId(sipMessageTrace.getId());
		super.setName(sipMessageTrace.getName());
		super.setType(sipMessageTrace.getType());
		super.setTransaction(sipMessageTrace.getTransaction());
		super.setDirection(sipMessageTrace.getDirection());
		super.setLine(sipMessageTrace.getLine());
	}

}
