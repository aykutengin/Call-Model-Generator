package model;

public class IncomingIWSignal extends Signal{
	public final static String INCOMINGIWSIGNAL = "Incoming IW Signal";
	public final static String SIGNAL = "Signal :"; 
	
	private LSC lsc;

	public LSC getLsc() {
		return lsc;
	}

	public void setLsc(LSC lsc) {
		this.lsc = lsc;
	}
}
