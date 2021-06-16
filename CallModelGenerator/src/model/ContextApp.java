package model;

import java.util.ArrayList;
import java.util.List;

public class ContextApp {
	private static ContextApp instance = null;

	private List<Transactor> transactorList = new ArrayList<Transactor>();
	private List<SIPMessageTrace> sipMessageTraceList = new ArrayList<SIPMessageTrace>();

	public static ContextApp getInstance() {
		if (instance == null)
			instance = new ContextApp();

		return instance;
	}

	public List<Transactor> getTransactorList() {
		return transactorList;
	}

	public void setTransactorList(List<Transactor> transactorList) {
		this.transactorList = transactorList;
	}

	public List<SIPMessageTrace> getSipMessageTraceList() {
		return sipMessageTraceList;
	}

	public void setSipMessageTraceList(List<SIPMessageTrace> sipMessageTraceList) {
		this.sipMessageTraceList = sipMessageTraceList;
	}

}
