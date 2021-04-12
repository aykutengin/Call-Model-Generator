package model;

import java.util.ArrayList;
import java.util.List;

public class Transactor {

	public final static String OCM = "IPTelASE(OCM)";
	public final static String IWCM = "IWCMSvcASE";
	public final static String CM = "CallMgrASE";
	public final static String TCM = "IPTelASE(TCM)";

	public enum Type {
		OCM, CM, IWCM, TCM
	}
	
	private static int lastlyUsedTransactorId; //For setting the outgoing signals. Outgoing signal goes from the lastly used transactor.
	
	private int id;
	private Type type;
	private List<String> initiatedServices = new ArrayList<String>();
	private List<Handler> handlerList = new ArrayList<Handler>();
	private List<LSC> lscList = new ArrayList<LSC>();
	private List<List<Integer>> workingLines = new ArrayList<>();
	//private List<Signal> incomingSignals = new ArrayList<Signal>();
	//private List<Signal> outgoingSignals = new ArrayList<Signal>();
	private List<SipSignal> sipSignals = new ArrayList<SipSignal>();
	private List<Signal> transactorSignal = new ArrayList<Signal>();

	public Transactor(int id) {
		this.id = id;
	}

	public Transactor(Type type) {
		this.type = type;
	}

	public static int getLastlyUsedTransactorId() {
		return lastlyUsedTransactorId;
	}

	public static void setLastlyUsedTransactorId(int lastlyUsedTransactorId) {
		Transactor.lastlyUsedTransactorId = lastlyUsedTransactorId;
	}

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

	public List<String> getInitiatedServices() {
		return initiatedServices;
	}

	public void setInitiatedServices(List<String> initiatedServices) {
		this.initiatedServices = initiatedServices;
	}

	public List<Handler> getHandlerList() {
		return handlerList;
	}

	public void setHandlerList(List<Handler> handlerList) {
		this.handlerList = handlerList;
	}

	public List<LSC> getLscList() {
		return lscList;
	}

	public void setLscList(List<LSC> lscList) {
		this.lscList = lscList;
	}

	public List<List<Integer>> getWorkingLines() {
		return workingLines;
	}

	public void setWorkingLines(List<List<Integer>> workingLines) {
		this.workingLines = workingLines;
	}

	/*public List<Signal> getIncomingSignals() {
		return incomingSignals;
	}

	public void setIncomingSignals(List<Signal> incomingSignals) {
		this.incomingSignals = incomingSignals;
	}

	public List<Signal> getOutgoingSignals() {
		return outgoingSignals;
	}

	public void setOutgoingSignals(List<Signal> outgoingSignals) {
		this.outgoingSignals = outgoingSignals;
	}*/

	public List<Signal> getTransactorSignal() {
		return transactorSignal;
	}

	public List<SipSignal> getSipSignals() {
		return sipSignals;
	}

	public void setSipSignals(List<SipSignal> sipSignals) {
		this.sipSignals = sipSignals;
	}

	public void setTransactorSignal(List<Signal> transactorSignal) {
		this.transactorSignal = transactorSignal;
	}

	@Override
	public String toString() {
		return "Transactor [initiatedServices=" + initiatedServices + "]";
	}

}
