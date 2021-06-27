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

	private static int lastlyUsedTransactorId; // For setting the outgoing signals. Outgoing signal goes from the lastly
												// used transactor.

	private int id;
	private Type type;
	private List<String> initiatedServices = new ArrayList<String>();
	private List<Handler> handlerList = new ArrayList<Handler>();
	private List<LSC> lscList = new ArrayList<LSC>();
	private List<List<Integer>> workingLines = new ArrayList<>();
	private List<SIPMessageTrace> sipSignals = new ArrayList<SIPMessageTrace>();
	private List<Signal> transactorSignal = new ArrayList<Signal>();

	private List<ProcessIncomingSignal> processIncomingSignalList = new ArrayList<ProcessIncomingSignal>();
	private List<IncomingIWSignal> incomingIWSignal = new ArrayList<IncomingIWSignal>();
	private boolean leafTCM;

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

	public List<Signal> getTransactorSignal() {
		return transactorSignal;
	}

	public void setTransactorSignal(List<Signal> transactorSignal) {
		this.transactorSignal = transactorSignal;
	}

	public List<SIPMessageTrace> getSipSignals() {
		return sipSignals;
	}

	public void setSipSignals(List<SIPMessageTrace> sipSignals) {
		this.sipSignals = sipSignals;
	}

	@Override
	public String toString() {
		return "Transactor [id=" + id + ", type=" + type + ", initiatedServices=" + initiatedServices + ", handlerList="
				+ handlerList + ", lscList=" + lscList + ", workingLines=" + workingLines + ", sipSignals=" + sipSignals
				+ ", transactorSignal=" + transactorSignal + ", processIncomingSignalList=" + processIncomingSignalList
				+ ", incomingIWSignal=" + incomingIWSignal + ", leafTCM=" + leafTCM + "]";
	}

	public List<ProcessIncomingSignal> getProcessIncomingSignalList() {
		return processIncomingSignalList;
	}

	public void setProcessIncomingSignalList(List<ProcessIncomingSignal> processIncomingSignalList) {
		this.processIncomingSignalList = processIncomingSignalList;
	}

	public List<IncomingIWSignal> getIncomingIWSignal() {
		return incomingIWSignal;
	}

	public void setIncomingIWSignal(List<IncomingIWSignal> incomingIWSignal) {
		this.incomingIWSignal = incomingIWSignal;
	}

	public boolean isLeafTCM() {
		return leafTCM;
	}

	public void setLeafTCM(boolean leafTCM) {
		this.leafTCM = leafTCM;
	}

}
