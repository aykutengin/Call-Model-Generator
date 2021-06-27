package utility;

import java.util.List;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.LSC;
import model.SIPMessageTrace;
import model.Signal;
import model.Transactor;

public class DrawUtility {

	/**
	 * Draw a call model by given transactor list.
	 * 
	 * @param transactorList
	 */

	public Stage drawCallModel(List<Transactor> transactorList) {
		Stage callModelStage = new Stage();
		
		int startX = 100;
		
		int transactorGapX = 100;
		int transactorGapY = 50;
		int transactorWidth = 200;
		int transactorHeight = 300;
		int lscWidth = 50;
		int lscHeight = 50;
		int lscGapX = 10;
		
		int startXOCM;
		int startXCM;
		
		int ocmCount = 1;
		int cmCount = 0;
		int tcmCount = 1;
		
		if (transactorList != null && !transactorList.isEmpty()) {
			Group group = new Group();
			for (int i = 0; i < transactorList.size(); i++) {	
				Transactor transactor = transactorList.get(i);
				
				Rectangle transactorShape = null;
				if (transactor.getType().equals(Transactor.Type.OCM)) {
					transactorShape = new Rectangle(transactorGapX + i * (transactorGapX + transactorWidth), transactorGapY, transactorWidth, transactorHeight);
					ocmCount += 1;
				} else if (transactor.getType().equals(Transactor.Type.CM)) {
					transactorShape = new Rectangle(transactorGapX + i * (transactorGapX + transactorWidth), (cmCount * transactorHeight) + transactorGapY, transactorWidth, transactorHeight);
					cmCount += 1;
				} else if (transactor.getType().equals(Transactor.Type.TCM)) {
					transactorShape = new Rectangle(transactorGapX + i * (transactorGapX + transactorWidth), transactorGapY, transactorWidth, transactorHeight);
					tcmCount += 1;

				}
				
				//Rectangle transactorShape = new Rectangle(transactorGapX + i * (transactorGapX + transactorWidth), transactorGapY, transactorWidth, transactorHeight);

				switch (transactor.getType()) {
				case OCM:
					transactorShape.setFill(Color.RED);
					break;
				case CM:
					transactorShape.setFill(Color.GREEN);
					break;
				case TCM:
					transactorShape.setFill(Color.GOLD);
					break;
				default:
					//transactorShape.setStroke(Color.YELLOW);
					transactorShape.setFill(Color.LIGHTGRAY);
					break;
				}
				
				group.getChildren().add(transactorShape);
				Text text = new Text("This is some text");
				//text.setFont(FontWeight.BOLD, 36);
				//group.g
				
				
				
				for (int j = 0; j < transactor.getLscList().size(); j++) {
					LSC lsc = transactor.getLscList().get(j);
					Rectangle lscShape = new Rectangle(transactorGapX + i * (transactorGapX + transactorWidth) + j * (lscWidth + lscGapX), transactorHeight + transactorGapY, lscWidth, lscHeight);
					lscShape.setFill(Color.AQUA);	
					group.getChildren().add(lscShape);
				}
				//drawing incoming and outgoing signals.
				for (int j = 0; j < transactor.getSipSignals().size(); j++) {
					double startLineX = 0;
					double startLineY = 0;
					double endLineX = 0;
					double endLineY = 0;
					double arrowWidh = 100;
					double arrowGap = 50;
					
					SIPMessageTrace sipSignal = transactor.getSipSignals().get(j);
					if (transactor.getType().equals(Transactor.Type.OCM)) {
						if (sipSignal.getDirection().equals(SIPMessageTrace.Direction.Incoming)) {
							startLineX = i * (transactorGapX + transactorWidth);
							startLineY = (j * arrowGap) + transactorGapY;
							endLineX = i * (transactorGapX + transactorWidth) + arrowWidh;
							endLineY = (j * arrowGap) + transactorGapY;
						} else if (sipSignal.getDirection().equals(SIPMessageTrace.Direction.Outgoing)) {
							startLineX = i * (transactorGapX + transactorWidth) + arrowWidh;
							startLineY = (j * arrowGap) + transactorGapY;
							endLineX = i * (transactorGapX + transactorWidth);
							endLineY = (j * arrowGap) + transactorGapY;
						}
					} else if (transactor.getType().equals(Transactor.Type.TCM)) {
						if (sipSignal.getDirection().equals(SIPMessageTrace.Direction.Incoming)) {
							startLineX = transactorWidth + arrowWidh + i * (transactorGapX + transactorWidth);
							startLineY = (j * arrowGap) + transactorGapY;
							endLineX = transactorWidth + arrowWidh + i * (transactorGapX + transactorWidth) + arrowWidh;
							endLineY = (j * arrowGap) + transactorGapY;

						} else if (sipSignal.getDirection().equals(SIPMessageTrace.Direction.Outgoing)) {
							startLineX = transactorWidth + arrowWidh + i * (transactorGapX + transactorWidth)
									+ arrowWidh;
							startLineY = (j * arrowGap) + transactorGapY;
							endLineX = transactorWidth + arrowWidh + i * (transactorGapX + transactorWidth);
							endLineY = (j * arrowGap) + transactorGapY;
						}
					}
					drawArrowLine(startLineX, startLineY, endLineX, endLineY, group);
				}
				
				//drawing transactor signals.
				for (int j = 0; j < transactor.getTransactorSignal().size(); j++) {
					if (transactor.getType().equals(Transactor.Type.OCM)) {
						
					} else if (transactor.getType().equals(Transactor.Type.CM)) {

					} else if (transactor.getType().equals(Transactor.Type.TCM)) {

					}
				}
				
			}
			Scene scene = new Scene(group, 1920, 1080);
			callModelStage.setTitle("Call Model");
			callModelStage.setScene(scene);
			//callModelStage.show();		
		}
		return callModelStage;
	}
	
	/**
	 * Draws inter transactor signals. */
	@Deprecated
	private void interTransactorSignal(Transactor transactor) {
		List<Signal> signals = transactor.getTransactorSignal();
		for (Signal signal : signals) {
			/*if (transactor.getType().equals(Transactor.Type.OCM)) {
				if(signal.getLsc().getType().equals(LSC.Type.SipBBUALSC)) {
					
				}
				else if(signal.getLsc().getType().equals(LSC.Type.IWIPTelLSC)){
					
				}
			} else if (transactor.getType().equals(Transactor.Type.CM)) {

			} else if (transactor.getType().equals(Transactor.Type.TCM)) {

			}*/
		}
	}
	
	private static void drawArrowLine(double startX, double startY, double endX, double endY, Group group) {
		// get the slope of the line and find its angle
		double slope = (startY - endY) / (startX - endX);
		double lineAngle = Math.atan(slope);

		double arrowAngle = startX > endX ? Math.toRadians(45) : -Math.toRadians(225);

		Line line = new Line(startX, startY, endX, endY);

		double lineLength = Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
		double arrowLength = lineLength / 10;

		// create the arrow legs
		Line arrow1 = new Line();
		arrow1.setStartX(line.getEndX());
		arrow1.setStartY(line.getEndY());
		arrow1.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle - arrowAngle));
		arrow1.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle - arrowAngle));

		Line arrow2 = new Line();
		arrow2.setStartX(line.getEndX());
		arrow2.setStartY(line.getEndY());
		arrow2.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle + arrowAngle));
		arrow2.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle + arrowAngle));

		group.getChildren().addAll(line, arrow1, arrow2);
	}
}
