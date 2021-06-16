package model;

public class LSC {
	public final static String LSC = "[LSC: ";
	public final static String SUSPEND = "LSC Suspend :";
	public final static String PORT = "[PORT:";

	public enum Type {
		SipBBUALSC, IWIPTelLSC, CallLegIWIPTelLSC, CMIWLSC, CPLLSC, DBLSC
	}

	private int id;
	private Type type;
	private String port;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LSC other = (LSC) obj;
		if (id != other.id)
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LSC [id=" + id + ", type=" + type + ", port=" + port + "]";
	}

}
