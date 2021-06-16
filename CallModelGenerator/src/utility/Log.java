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

import model.ContextApp;
import model.Handler;
import model.IncomingIWSignal;
import model.LSC;
import model.ProcessIncomingSignal;
import model.Signal;
import model.SIPMessageTrace;
import model.Transactor;
import model.UnifiedSIPMessageTrace;

public class Log {
	private final static String PROCESS = "Transactor PROCESS :";
	private final static String SWAP = "Transactor Swap Root ASE :";
	private final static String SUSPEND = "Transactor SUSPEND :";
	private final static String IDLE = "Transactor IDLE :";
	private final static String OPENSQUAREBRACKET = "[";
	private final static String CLOSESQUAREBRACKET = "]";

	private final static String PROCESSEVENT = "ASE ProcessEvent :";
	private final static String CAT_ROOT = "[CAT : ROOT]";

	//static List<Transactor> contextApp.getTransactorList();
	private ContextApp contextApp;
	
	

	/*
	 * public static void main(String[] args) { Main m = new Main();
	 * m.readLog("pbx2pbx.log");
	 * 
	 * Scriber s = new Scriber(); s.drawCallModel(contextApp.getTransactorList()); }
	 */

	/**
	 * Analyze the log file and creates objects.
	 * */
	public List<Transactor> readLog(String fileName) {
		int line = 1;
		boolean processDetected = false;
		boolean suspendDetected = false;
		boolean idleDetected = false;
		boolean swapDetected = false;
		boolean svcTraceDetected = false;
		boolean processIncomingSignalDetected = false;
		boolean incomingIWSignalDetected = false;
		boolean outgoingSIPMessageTraceDetected = false;
		boolean incomingSIPMessageTraceDetected = false;

		contextApp = ContextApp.getInstance();
		Transactor currentTransactor = null;
		SIPMessageTrace tempIncomingSignal = null;
		List<SIPMessageTrace> tempIncomingSignals = new ArrayList<SIPMessageTrace>();
		
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
						contextApp.getTransactorList().add(currentTransactor);
					}
					currentTransactor.setLastlyUsedTransactorId(id);
					currentTransactor.getWorkingLines().add(Arrays.asList(line, 0));

					if (!tempIncomingSignals.isEmpty()) {
						for (SIPMessageTrace signal : tempIncomingSignals) {
							if (!currentTransactor.getSipSignals().contains(signal)) {
								currentTransactor.getSipSignals().add(signal);
							}
						}
						tempIncomingSignals = new ArrayList<SIPMessageTrace>();
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
					// currentTransactor = null;
					suspendDetected = false;
				} else if (swapDetected) {
					int lastIndex = currentTransactor.getWorkingLines().size() - 1;
					currentTransactor.getWorkingLines().get(lastIndex).set(1, line);
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
					if (!currentTransactor.getLscList().contains(lsc)) {
						currentTransactor.getLscList().add(findLSCFromstrLine(strLine));
					}
				}
				// end of the LSC works

				// Finding ProcessIncomingSignal.
				if (strLine.contains(ProcessIncomingSignal.PROCESSINCOMINGSIGNAL)) {
					processIncomingSignalDetected = true;
				}

				if (processIncomingSignalDetected) {
					if(strLine.contains(ProcessIncomingSignal.MESSAGE)) {
						ProcessIncomingSignal processIncomingSignal = new ProcessIncomingSignal();
						String s = parseString(strLine, ProcessIncomingSignal.MESSAGE);
						String[] splittedStr = s.split(" ");

						processIncomingSignal.setId(Integer.parseInt(splittedStr[1]));

						// Parsing the type partition.
						int lastIndex = splittedStr.length - 1;
						if (splittedStr[lastIndex].contains("]")) {
							String[] secondPart = splittedStr[lastIndex].split("\\]");
							processIncomingSignal.setName(parseSignalName(secondPart[1]));
							processIncomingSignal.setType(secondPart[0].replace("[", ""));
							processIncomingSignal
									.setTransaction(Integer.parseInt(secondPart[secondPart.length - 1].replace("[", "")));
						}
						// currentTransactor.getTransactorSignal().add(signal);
						currentTransactor.getProcessIncomingSignalList().add(processIncomingSignal);
					}
					else if(strLine.contains(Signal.TRANSACTORSIGNAL_CALLID)) {
						int lastIndex = currentTransactor.getProcessIncomingSignalList().size() - 1;
						ProcessIncomingSignal processIncomingSignal = currentTransactor.getProcessIncomingSignalList().get(lastIndex);
						processIncomingSignal.setCallId(parseString(strLine, Signal.TRANSACTORSIGNAL_CALLID, CLOSESQUAREBRACKET));
					}
					else if (strLine.contains(LSC.LSC)) {
						LSC lsc = findLSCFromstrLine(strLine);
						int lastIndex = currentTransactor.getProcessIncomingSignalList().size() - 1;
						ProcessIncomingSignal processIncomingSignal = currentTransactor.getProcessIncomingSignalList()
								.get(lastIndex);
						processIncomingSignal.setLsc(lsc);
						processIncomingSignalDetected = false;
					}
					
				}
				// End of ProcessIncomingSignal.

				// Finding Incoming IW Signal.
				if (strLine.contains(IncomingIWSignal.INCOMINGIWSIGNAL)) {
					incomingIWSignalDetected = true;
				}

				if (incomingIWSignalDetected) {
					if (strLine.contains(IncomingIWSignal.SIGNAL)) {
						IncomingIWSignal incomingIWSignal = new IncomingIWSignal();
						String s = parseString(strLine, IncomingIWSignal.SIGNAL);
						String[] splittedStr = s.split(" ");

						incomingIWSignal.setId(Integer.parseInt(splittedStr[1]));

						// Parsing the type partition.
						int lastIndex = splittedStr.length - 1;
						if (splittedStr[lastIndex].contains("]")) {
							String[] secondPart = splittedStr[lastIndex].split("\\]");
							incomingIWSignal.setName(parseSignalName(secondPart[1]));
							incomingIWSignal.setType(secondPart[0].replace("[", ""));
							incomingIWSignal.setTransaction(
									Integer.parseInt(secondPart[secondPart.length - 1].replace("[", "")));
						}
						currentTransactor.getIncomingIWSignal().add(incomingIWSignal);
					}
					else if(strLine.contains(Signal.TRANSACTORSIGNAL_CALLID)) {
						int lastIndex = currentTransactor.getIncomingIWSignal().size() - 1;
						IncomingIWSignal incomingIWSignal = currentTransactor.getIncomingIWSignal().get(lastIndex);
						incomingIWSignal.setCallId(parseString(strLine, Signal.TRANSACTORSIGNAL_CALLID, CLOSESQUAREBRACKET));
					}
					else if (strLine.contains(LSC.LSC)) {
						LSC lsc = findLSCFromstrLine(strLine);
						int lastIndex = currentTransactor.getIncomingIWSignal().size() - 1;
						IncomingIWSignal incomingIWSignal = currentTransactor.getIncomingIWSignal().get(lastIndex);
						incomingIWSignal.setLsc(lsc);
						incomingIWSignalDetected = false;
					}
				}
				// End of Incoming IW Signal.

				// Finding incoming signal from transactor.
				if (strLine.contains(SIPMessageTrace.INCOMING)) {
					incomingSIPMessageTraceDetected = true;
				}
				// Finding outgoing signal from transactor.
				if (strLine.contains(SIPMessageTrace.OUTGOING)) {
					outgoingSIPMessageTraceDetected = true;
					//currentTransactor.setLeafTCM(true);
				}
				if ((incomingSIPMessageTraceDetected || outgoingSIPMessageTraceDetected) && strLine.contains("SIP_CB")) {
					// If there is any incoming and outgoing signal that means transactor suspended
					// here. Because when the transactor is processing we should not see any signal.
					// it happens after the work is done.

					//There is not any cseq for SipSignalAnswer. Therefore its skipped.
					if (strLine.contains("SipSignalAnswer")) {
						incomingSIPMessageTraceDetected = false;
						line++;
						continue;
					}
					
					SIPMessageTrace sipMessageTrace = new SIPMessageTrace();
					String s = parseString(strLine, "|");
					sipMessageTrace.setId(Integer.parseInt(parseString(s, " ")));
					// signal.setName(parseString(s, "SIP_", "_"));
					sipMessageTrace.setName(parseSignalName(s));
					sipMessageTrace.setType(parseTypeofSIPMessage(s));
					sipMessageTrace.setLine(line);
					if (incomingSIPMessageTraceDetected) {
						sipMessageTrace.setDirection(SIPMessageTrace.Direction.Incoming);
						tempIncomingSignals.add(sipMessageTrace);		
						contextApp.getSipMessageTraceList().add(sipMessageTrace);			
					} else if (outgoingSIPMessageTraceDetected) {
						sipMessageTrace.setDirection(SIPMessageTrace.Direction.Outgoing);
						Transactor lastUsedTransactor = getTransactorByID(currentTransactor.getId());
						lastUsedTransactor.getSipSignals().add(sipMessageTrace);						
						contextApp.getSipMessageTraceList().add(sipMessageTrace);
					}
				}
				if ((incomingSIPMessageTraceDetected || outgoingSIPMessageTraceDetected) && (strLine.contains(Signal.CSEQ) || strLine.contains(Signal.SMT_CALLID))) {
					int lastIndex = contextApp.getSipMessageTraceList().size() - 1;
					SIPMessageTrace sipMessageTrace = contextApp.getSipMessageTraceList().get(lastIndex);
					if (strLine.contains(Signal.CSEQ)) {
						sipMessageTrace.setTransaction(findFirstNumber(strLine));
					} else if (strLine.contains(Signal.SMT_CALLID)) {
						sipMessageTrace.setCallId(parseString(strLine, "Call-ID: "));
						if (incomingSIPMessageTraceDetected) {
							incomingSIPMessageTraceDetected = false;
						} else if (outgoingSIPMessageTraceDetected) {
							outgoingSIPMessageTraceDetected = false;
						}
					}
				}
				line++;
			}
			fstream.close();

			/*
			 * try (PrintWriter out = new PrintWriter("output.txt")) { for (Transactor
			 * transactor : contextApp.getTransactorList()) { out.println(transactor); } }
			 */

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();

		}
		//signalPath(contextApp.getTransactorList(), sipMessageTraceList);
		createReport(contextApp);
		return contextApp.getTransactorList();
	}

	/**
	 * Returns the first number of the given line.
	 */
	private int findFirstNumber(String strLine) {
		//Pattern p = Pattern.compile("([0-9])\\w+");
		Pattern p = Pattern.compile("-?\\d+");
		
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
		for (Transactor transactor : contextApp.getTransactorList()) {
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
		str.indexOf("@");
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

	private String parseTypeofSIPMessage(String str) {
		if(str.contains(Signal.REQUEST)) {
			return Signal.REQUEST;
		}else if(str.contains(Signal.RESPONSE)){
			return Signal.RESPONSE;
		}
		return null;
	}
	
	private void callModelHelper() {
		try (PrintWriter out = new PrintWriter("output.txt")) {
			if (contextApp.getTransactorList() != null) {
				for (Transactor transactor : contextApp.getTransactorList()) {
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
	 */
	private int searchSignal(Signal signal) {
		for (Transactor transactor : contextApp.getTransactorList()) {
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
	/**
	 * Finds the which signal passes over which transactors.
	 * */
	private void signalPath(List<Transactor> transactors, List<SIPMessageTrace> sipMessageTraces) {
		/*for (Transactor transactor : transactors) {
			System.out.println(transactor.getType() + " - " + transactor.getWorkingLines());
		}
		for (SIPMessageTrace sipMessageTrace : sipMessageTraces) {
			System.out.println(sipMessageTrace);
		}*/
		
		List<UnifiedSIPMessageTrace> unifiedSIPMessageTraceList = mergeSignals(sipMessageTraces);		
		for (UnifiedSIPMessageTrace unifiedSIPMessageTrace : unifiedSIPMessageTraceList) {
			//System.out.println(sipMessageTrace);
			
			System.out.println("\n" + unifiedSIPMessageTrace.getName() + " - In: " + unifiedSIPMessageTrace.getIncomingLine() + " - Out: " + unifiedSIPMessageTrace.getOutgoingLine());
			for (Transactor transactor : contextApp.getTransactorList()) {
				System.out.println(transactor.getType() + " - " + transactor.getWorkingLines());
			}
		}
		
		
	}
	
	/**
	 * Merges the incoming and outgoing signals.
	 */
	private List<UnifiedSIPMessageTrace> mergeSignals(List<SIPMessageTrace> sipMessageTraces) {
		List<UnifiedSIPMessageTrace> unifiedSIPMessageTraceList = new ArrayList<UnifiedSIPMessageTrace>();
		for (SIPMessageTrace sipMessageTrace : sipMessageTraces) {
			UnifiedSIPMessageTrace unifiedSIPMessageTrace = new UnifiedSIPMessageTrace();
			unifiedSIPMessageTrace.setSIPMessageTrace(sipMessageTrace);
			if (unifiedSIPMessageTrace.getDirection() == SIPMessageTrace.Direction.Incoming) {
				unifiedSIPMessageTrace.setIncomingLine(unifiedSIPMessageTrace.getLine());
			} else if (unifiedSIPMessageTrace.getDirection() == SIPMessageTrace.Direction.Outgoing) {
				//unifiedSIPMessageTrace.setOutgoingLine(unifiedSIPMessageTrace.getLine());
			}
			if (unifiedSIPMessageTraceList.contains(unifiedSIPMessageTrace)) {
				int index = unifiedSIPMessageTraceList.indexOf(unifiedSIPMessageTrace);
				unifiedSIPMessageTraceList.get(index).setOutgoingLine(unifiedSIPMessageTrace.getLine());
			} else {
				unifiedSIPMessageTraceList.add(unifiedSIPMessageTrace);
			}
		}
		return unifiedSIPMessageTraceList;
	}
	
	private void createReport(ContextApp context) {
		/*for (Transactor transactor : context.getTransactorList()) {
			System.out.println(transactor.getType() + " - " + transactor.getId());
			for (ProcessIncomingSignal incomingSig : transactor.getProcessIncomingSignalList()) {
				System.out.println("\t" + incomingSig.getName() + "(Incoming) - " + incomingSig.getId() + " - " + incomingSig.getCallId());
			}
			System.out.println("------------------------------------------------");
			for (IncomingIWSignal outgoingSig : transactor.getIncomingIWSignal()) {
				System.out.println("\t" + outgoingSig.getName() + "(Outgoing) - " + outgoingSig.getId() + " - " + outgoingSig.getCallId());
			}
		}
		System.out.println("\n-------------SipMessages-------------");
		for (SIPMessageTrace sipMessages : context.getSipMessageTraceList()) {
			System.out
					.println(sipMessages.getName() + " - " + sipMessages.getId() + " - " + sipMessages.getDirection() + " - " + sipMessages.getCallId());
		}*/
		
		/*for (Transactor transactor : context.getTransactorList()) {
			for (ProcessIncomingSignal incomingSig : transactor.getProcessIncomingSignalList()) {
				for (IncomingIWSignal outgoingSig : transactor.getIncomingIWSignal()) {
					if (incomingSig.equals(outgoingSig)) {

					}
				}
			}
		}*/
		
		for (SIPMessageTrace sipMsgTrace : context.getSipMessageTraceList()) {
			System.out.println(sipMsgTrace.getName());
			for (Transactor transactor : context.getTransactorList()) {
				for (ProcessIncomingSignal processIncSig : transactor.getProcessIncomingSignalList()) {
					if (sipMsgTrace.equals(processIncSig)) {
						System.out.println(transactor.getType());
					}
				}
			}
		}
		
		
	}
}