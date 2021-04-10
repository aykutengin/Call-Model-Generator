package model;

public class Handler {
	public final static String HANDLER = "Handler";
	public final static String EVENTHANDLER = "Event Handler :";
	public final static String STATE = "[STATE : ";
	public final static String EVENT = "[EVENT : ";
	public final static String SVC_TRACE = "[SVC TRACE : ";
	public final static String EDRC_RC = "[EDRC  : RC: ";

	public enum ReturnCodes {
		INITIATE, CONTINUE, PASS, CONSUME, REENTER, SWAP_ROOT_ASE
	}

	private String name;
	private Signal signal = new Signal();
	private String state;
	private String event;
	private String svcTrace;
	private ReturnCodes rc;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Signal getSignal() {
		return signal;
	}

	public void setSignal(Signal signal) {
		this.signal = signal;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getSvcTrace() {
		return svcTrace;
	}

	public void setSvcTrace(String svcTrace) {
		this.svcTrace = svcTrace;
	}

	public ReturnCodes getRc() {
		return rc;
	}

	public void setRc(ReturnCodes rc) {
		this.rc = rc;
	}

	@Override
	public String toString() {
		return "Handler [name=" + name + ", signal=" + signal + ", state=" + state + ", event=" + event + ", svcTrace="
				+ svcTrace + ", rc=" + rc + "]";
	}

}
