package utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Handler;
import model.LSC;
import model.Signal;
import model.SipSignal;
import model.Transactor;

public class Log {
	private final static String PROCESS = "Transactor PROCESS :";
	private final static String SWAP = "Transactor Swap Root ASE :";
	private final static String SUSPEND = "Transactor SUSPEND :";
	private final static String IDLE = "Transactor IDLE :";
	private final static String OPENSQUAREBRACKET = "[";
	private final static String CLOSESQUAREBRACKET = "]";

	private final static String PROCESSEVENT = "ASE ProcessEvent :";
	private final static String CAT_ROOT = "[CAT : ROOT]";

	static List<Transactor> transactorList;

	/*
	 * public static void main(String[] args) { Main m = new Main();
	 * m.readLog("pbx2pbx.log");
	 * 
	 * Scriber s = new Scriber(); s.drawCallModel(transactorList); }
	 */

	public List<Transactor> readLog(String fileName) {	
		int line = 1;
		boolean processDetected = false;
		boolean suspendDetected = false;
		boolean idleDetected = false;
		boolean swapDetected = false;
		boolean svcTraceDetected = false;
		boolean outgoingSignalDetected = false;
		boolean incomingSignalDetected = false;
		
		Transactor currentTransactor = null;
		transactorList = new ArrayList<Transactor>();
		SipSignal tempIncomingSignal = null;
		List<SipSignal> tempIncomingSignals = new ArrayList<SipSignal>();

		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				// start of the transactor works
				if (strLine.contains(PROCESS)) {
					processDetected = true;
					
					int id = findFirstNumber(strLine);
					currentTransactor = getTransactorByID(id);
					if (currentTransactor == null) {
						currentTransactor = new Transactor(id);
						if (tempIncomingSignal != null) {
							currentTransactor.getSipSignals().add(tempIncomingSignal);
							tempIncomingSignal = null;
						}
						transactorList.add(currentTransactor);
					}
					currentTransactor.setLastlyUsedTransactorId(id);
					currentTransactor.getWorkingLines().add(Arrays.asList(line, 0));
					
					if (!tempIncomingSignals.isEmpty()) {
						for (SipSignal signal : tempIncomingSignals) {
							if (!currentTransactor.getSipSignals().contains(signal)) {
								currentTransactor.getSipSignals().add(signal);
							}
						}
						
						tempIncomingSignals = new ArrayList<SipSignal>(); 
					}

				} else if (strLine.contains(SUSPEND)) {
					suspendDetected = true;

				} else if (strLine.contains(IDLE)) {
					idleDetected = true;

				} else if (strLine.contains(SWAP)) {
					swapDetected = true;

				}

				if (processDetected && strLine.contains(PROCESSEVENT) && strLine.contains(CAT_ROOT)) {
					if (strLine.contains(Transactor.OCM)) {
						currentTransactor.setType(Transactor.Type.OCM);
					} else if (strLine.contains(Transactor.CM)) {
						currentTransactor.setType(Transactor.Type.CM);
					} else if (strLine.contains(Transactor.IWCM)) {
						currentTransactor.setType(Transactor.Type.IWCM);
					} else if (strLine.contains(Transactor.TCM)) {
						currentTransactor.setType(Transactor.Type.TCM);
					}
					processDetected = false;
				} else if (suspendDetected) {
					int lastIndex = currentTransactor.getWorkingLines().size() - 1;
					currentTransactor.getWorkingLines().get(lastIndex).set(1, line);
					//currentTransactor = null;
					suspendDetected = false;
				} else if (swapDetected) {
					/*
					 * int lastIndex = runningTransactor.getWorkingLines().size() - 1;
					 * runningTransactor.getWorkingLines().get(lastIndex).set(1, line);
					 */
					swapDetected = false;
				} else if (idleDetected) {
					int lastIndex = currentTransactor.getWorkingLines().size() - 1;
					currentTransactor.getWorkingLines().get(lastIndex).set(1, line);
					idleDetected = false;
				}
				// end of the transactor works

				// start of the transactor works
				// Process for Handlers // Finding handlers.
				if (strLine.contains(Handler.EVENTHANDLER)) {
					Handler handler = new Handler();
					handler.setName(parseString(strLine, Handler.EVENTHANDLER));
					currentTransactor.getHandlerList().add(handler);

				} else if (strLine.contains(Signal.CTXTSIG)) {
					int lastIndex = currentTransactor.getHandlerList().size() - 1;
					Handler handler = currentTransactor.getHandlerList().get(lastIndex);
					handler.getSignal().setId(findFirstNumber(strLine));
					handler.getSignal().setName(parseString(strLine, Signal.CTXTSIG, " "));

				} else if (strLine.contains(Handler.STATE) && strLine.contains(Handler.EVENT)) {
					int lastIndex = currentTransactor.getHandlerList().size() - 1;
					Handler handler = currentTransactor.getHandlerList().get(lastIndex);
					handler.setState(parseString(strLine, Handler.STATE, CLOSESQUAREBRACKET));
					String s = parseString(strLine, Handler.EVENT);
					handler.setEvent(findFirstString(s));
				} else if (strLine.contains(Handler.SVC_TRACE)) {
					int lastIndex = currentTransactor.getHandlerList().size() - 1;
					Handler handler = currentTransactor.getHandlerList().get(lastIndex);
					if (strLine.contains(CLOSESQUAREBRACKET)) {
						// 1 line trace
						handler.setSvcTrace(parseString(strLine, Handler.SVC_TRACE, CLOSESQUAREBRACKET));
					} else {
						handler.setSvcTrace(parseString(strLine, Handler.SVC_TRACE));
						svcTraceDetected = true;
					}

				} else if (svcTraceDetected) {
					int lastIndex = currentTransactor.getHandlerList().size() - 1;
					Handler handler = currentTransactor.getHandlerList().get(lastIndex);
					if (strLine.contains(CLOSESQUAREBRACKET)) {
						// trace is finished at this point.
						handler.setSvcTrace(handler.getSvcTrace() + strLine.substring(0, strLine.length() - 1));
						svcTraceDetected = false;
					} else {
						// trace is continuing...
						handler.setSvcTrace(handler.getSvcTrace() + strLine);
					}
				}
				if (strLine.contains(Handler.EDRC_RC)) {
					int lastIndex = currentTransactor.getHandlerList().size() - 1;
					Handler currentHandler = currentTransactor.getHandlerList().get(lastIndex);
					if (currentHandler.getRc() == null) {
						currentHandler.setRc(Handler.ReturnCodes.valueOf(parseString(strLine, Handler.EDRC_RC, " ")));
					}
					if (strLine.contains(Handler.ReturnCodes.INITIATE.toString())) {
						String serviceName = parseString(currentHandler.getName(), "", Handler.HANDLER);
						currentTransactor.getInitiatedServices().add(serviceName);
					}
				}
				// end of the handler works

				// finding LSC.
				if (strLine.contains(LSC.SUSPEND)) {
					LSC lsc = findLSCFromstrLine(strLine);
					// Checking to prevent duplicate data.
					//suspendedLSC.add(lsc);	
					//suspendedTransactor.getLscList().add(findLSCFromstrLine(strLine));
					if (!currentTransactor.getLscList().contains(lsc)) {
						currentTransactor.getLscList().add(findLSCFromstrLine(strLine));
					}
				}
				// end of the LSC works
				
				// Finding processed signal in transactor.
				if (strLine.contains(Signal.MESSAGE)) {
					Signal signal = new Signal();
					String s = parseString(strLine, Signal.MESSAGE);
					String[] splittedStr = s.split(" ");

					signal.setId(Integer.parseInt(splittedStr[1]));

					// Parsing the type partition.
					int lastIndex = splittedStr.length - 1;
					if (splittedStr[lastIndex].contains("]")) {
						String[] secondPart = splittedStr[lastIndex].split("\\]");
						signal.setName(parseSignalName(secondPart[1]));
						signal.setType(secondPart[0].replace("[", ""));
						signal.setTransaction(Integer.parseInt(secondPart[secondPart.length - 1].replace("[", "")));
					}
					currentTransactor.getTransactorSignal().add(signal);
				}
				 

				// Finding incoming signal from transactor.
				if (strLine.contains(SipSignal.INCOMING)) {
					incomingSignalDetected = true;
				}
				// Finding outgoing signal from transactor.
				if (strLine.contains(SipSignal.OUTGOING)) {
					outgoingSignalDetected = true;
				}
				if ((incomingSignalDetected || outgoingSignalDetected) && strLine.contains("SIP_CB")) {
					//If there is any incoming and outgoing signal that means transactor suspended here. Because when the transactor is processing we should not see any signal. it happens after the work is done.
					
					
					SipSignal sipSignal = new SipSignal();
					String s = parseString(strLine, "|");
					sipSignal.setId(Integer.parseInt(parseString(s, " ")));
					//signal.setName(parseString(s, "SIP_", "_"));
					sipSignal.setName(parseSignalName(s));
					
					if (incomingSignalDetected) {
						sipSignal.setDirection(SipSignal.Direction.Incoming);
						tempIncomingSignals.add(sipSignal);
						incomingSignalDetected = false;

					} else if (outgoingSignalDetected) {
						sipSignal.setDirection(SipSignal.Direction.Outgoing);
						Transactor lastUsedTransactor = getTransactorByID(currentTransactor.getId());		
						//lastUsedTransactor.getOutgoingSignals().add(signal);
						lastUsedTransactor.getSipSignals().add(sipSignal);
						outgoingSignalDetected = false;
					}
				}

				line++;
			}
			fstream.close();

			/*
			 * for (Transactor transactor : transactorList) {
			 * System.out.println(transactor); }
			 */

			/*
			 * try (PrintWriter out = new PrintWriter("output.txt")) { for (Transactor
			 * transactor : transactorList) { out.println(transactor); } }
			 */

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			//e.printStackTrace();
		}
		return transactorList;
	}

	/**
	 * Returns the first number of the given line.
	 */
	private int findFirstNumber(String strLine) {
		Pattern p = Pattern.compile("([0-9])\\w+");
		Matcher m = p.matcher(strLine);
		while (m.find()) {
			return Integer.parseInt(m.group());
		}
		return 0;
	}

	private String findFirstString(String strLine) {
		Pattern p = Pattern.compile("^([\\w\\-]+)");
		Matcher m = p.matcher(strLine);
		while (m.find()) {
			return m.group();
		}
		return "";
	}

	private String findTypeofTransactor(String strLine) {
		if (strLine.contains(Transactor.OCM)) {
			return Transactor.OCM;
		} else if (strLine.contains(Transactor.CM)) {
			return Transactor.CM;
		} else if (strLine.contains(Transactor.TCM)) {
			return Transactor.TCM;
		}
		return null;
	}

	/**
	 * Returns transactor by id from the list.
	 */
	private Transactor getTransactorByID(int id) {
		for (Transactor transactor : transactorList) {
			if (transactor.getId() == id) {
				return transactor;
			}
		}
		return null;
	}

	/**
	 * Returns lsc object according to given strLine.
	 */
	private LSC findLSCFromstrLine(String strLine) {
		LSC lsc = new LSC();

		// type of LSC
		if (strLine.contains(LSC.Type.SipBBUALSC.toString())) {
			lsc.setType(LSC.Type.SipBBUALSC);
		} else if (strLine.contains(LSC.Type.IWIPTelLSC.toString())) {
			lsc.setType(LSC.Type.IWIPTelLSC);
		} else if (strLine.contains(LSC.Type.CallLegIWIPTelLSC.toString())) {
			lsc.setType(LSC.Type.CallLegIWIPTelLSC);
		} else if (strLine.contains(LSC.Type.CMIWLSC.toString())) {
			lsc.setType(LSC.Type.CMIWLSC);
		} else if (strLine.contains(LSC.Type.CPLLSC.toString())) {
			lsc.setType(LSC.Type.CPLLSC);
		} else if (strLine.contains(LSC.Type.DBLSC.toString())) {
			lsc.setType(LSC.Type.DBLSC);
		}

		// ID
		Pattern p = Pattern.compile("([0-9])\\w+");
		Matcher m = p.matcher(strLine);
		while (m.find()) {
			lsc.setId(Integer.parseInt(m.group()));
			break;
		}

		// Port Type
		lsc.setPort(parseString(strLine, LSC.PORT, CLOSESQUAREBRACKET));

		return lsc;
	}

	/**
	 * Returns specific text according to given strLine.
	 * 
	 * @param str  it returns according to this parameter.
	 * @param from Start point.
	 * @param to   End point.
	 */
	private String parseString(String str, String from, String to) {
		int begin = str.indexOf(from);
		begin += from.length();
		int end = str.indexOf(to, begin);
		return str.substring(begin, end).trim();

	}

	/**
	 * Returns specific text according to given strLine.
	 * 
	 * @param str  it returns according to this parameter.
	 * @param from Start point.
	 */
	private String parseString(String str, String from) {
		int begin = str.indexOf(from);
		begin += from.length();
		return str.substring(begin).trim();

	}

	private String parseSignalName(String str) {
		for (String type : Signal.reqandResp) {
			if (str.contains(type)) {
				return type;
			}
		}
		return null;
	}

	private void callModelHelper() {
		try (PrintWriter out = new PrintWriter("output.txt")) {
			if (transactorList != null) {
				for (Transactor transactor : transactorList) {
					out.println(transactor.getType() + " " + transactor.getId());
					for (String svc : transactor.getInitiatedServices()) {
						out.println("\t" + svc);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns id of the transactor which has the given signal.
	 * */
	private int searchSignal(Signal signal) {
		for (Transactor transactor : transactorList) {
			for (Handler handler : transactor.getHandlerList()) {
				if (handler.getSignal().getName() == signal.getName()
						&& handler.getSignal().getType() == signal.getType()
						&& handler.getSignal().getTransaction() == signal.getTransaction()) {
					return transactor.getId();
				}
			}
		}
		return 0;
	}
	
}
