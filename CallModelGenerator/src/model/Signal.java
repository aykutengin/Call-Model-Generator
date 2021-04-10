package model;

public class Signal {
	public final static String INCOMING = "SIP Message Trace : Incoming";
	public final static String OUTGOING = "SIP Message Trace : Outgoing";
	public final static String CTXTSIG = "[CTXTSIG : ";
	public final static String MESSAGE = "Message : ";

	public final static String[] reqandResp = { "INVITE", "ACK", "BYE", "CANCEL", "REGISTER", "OPTIONS", "PRACK", "SUBSCRIBE",
			"NOTIFY", "PUBLISH", "INFO", "REFER", "MESSAGE", "UPDATE", "100", "180", "181", "200", "202", "301", "302",
			"403", "404", "408", "503" };

	private int id;
	private String name;
	private String type;
	private byte transaction;
	

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

	public byte getTransaction() {
		return transaction;
	}

	public void setTransaction(byte transaction) {
		this.transaction = transaction;
	}

	@Override
	public String toString() {
		return "Signal [id=" + id + ", name=" + name + ", type=" + type + ", transaction=" + transaction + "]";
	}

}
