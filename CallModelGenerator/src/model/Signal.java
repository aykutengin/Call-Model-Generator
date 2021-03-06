package model;

/**
 * Incoming signal to Transactor.
 */
public class Signal {
	public final static String CTXTSIG = "[CTXTSIG : ";
	public final static String REQUEST = "Request";
	public final static String RESPONSE = "Response";
	public final static String REQ = "REQ";
	public final static String RESP = "RESP";
	public final static String CSEQ = "CSeq: ";
	public final static String TRANSACTORSIGNAL_CALLID = "[CALLID: ";// ProcessIncomingSignal | Incoming IW Signal
	public final static String SMT_CALLID = "Call-ID: ";// SIP Message Trace
	public final static String[] reqandResp = { "INVITE", "ACK", "BYE", "CANCEL", "REGISTER", "OPTIONS", "PRACK",
			"SUBSCRIBE", "NOTIFY", "PUBLISH", "INFO", "REFER", "MESSAGE", "UPDATE", "100", "180", "181", "200", "202",
			"301", "302", "403", "404", "408", "503" };

	private int id;
	private String name;
	private String type;
	private int transaction;
	private String callId;
	private int line;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTransaction() {
		return transaction;
	}

	public void setTransaction(int transaction) {
		this.transaction = transaction;
	}

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((callId == null) ? 0 : callId.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + transaction;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Signal other = (Signal) obj;
		if (callId == null) {
			if (other.callId != null)
				return false;
		} else if (id != other.id && !callId.equals(other.callId))
			return false;
		/*
		 * if (id != other.id) return false;
		 */
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (transaction != other.transaction)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Signal [id=" + id + ", name=" + name + ", type=" + type + ", transaction=" + transaction + ", callId="
				+ callId + "]";
	}

}
