package model;

public class ProcessIncomingSignal extends Signal {
	public final static String PROCESSINCOMINGSIGNAL = "ProcessIncomingSignal : ";
	public final static String MESSAGE = "Message : ";

	private LSC lsc;

	public LSC getLsc() {
		return lsc;
	}

	public void setLsc(LSC lsc) {
		this.lsc = lsc;
	}

}
