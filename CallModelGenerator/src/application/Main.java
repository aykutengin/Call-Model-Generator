package application;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.LSC;
import model.Signal;
import model.SipSignal;
import model.Transactor;
import utility.Log;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		Log log = new Log();
		try {
			primaryStage.setTitle("Call Model Generator");
			BorderPane root = new BorderPane();
			FileChooser fileChooser = new FileChooser();
			Button button = new Button("Select log file");
			button.setOnAction(e -> {
				File selectedFile = fileChooser.showOpenDialog(primaryStage);
				List<Transactor> transactorList = log.readLog(selectedFile.getAbsolutePath());				
				DrawCallModel(transactorList);
			});
			root.setCenter(button);
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Draw a call model by given transactor list.
	 * 
	 * @param transactorList
	 */
	private void DrawCallModel(List<Transactor> transactorList) {
		int startX = 100;
		
		int transactorGapX = 100;
		int transactorGapY = 50;
		int transactorWidth = 200;
		int transactorHeight = 300;
		int lscWidth = 50;
		int lscHeight = 50;
		int lscGapX = 10;
		
		if (transactorList != null && !transactorList.isEmpty()) {
			Stage callModelStage = new Stage();
			Group group = new Group();
			for (int i = 0; i < transactorList.size(); i++) {	
				Transactor transactor = transactorList.get(i);		
				Rectangle transactorShape = new Rectangle(transactorGapX + i * (transactorGapX + transactorWidth), transactorGapY, transactorWidth, transactorHeight);
				transactorShape.setStroke(Color.YELLOW);
				transactorShape.setFill(Color.LIGHTGRAY);
				group.getChildren().add(transactorShape);
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
					
					SipSignal sipSignal = transactor.getSipSignals().get(j);
					if (transactor.getType().equals(Transactor.Type.OCM)) {
						if (sipSignal.getDirection().equals(SipSignal.Direction.Incoming)) {
							startLineX = i * (transactorGapX + transactorWidth);
							startLineY = (j * arrowGap) + transactorGapY;
							endLineX = i * (transactorGapX + transactorWidth) + arrowWidh;
							endLineY = (j * arrowGap) + transactorGapY;
						} else if (sipSignal.getDirection().equals(SipSignal.Direction.Outgoing)) {
							startLineX = i * (transactorGapX + transactorWidth) + arrowWidh;
							startLineY = (j * arrowGap) + transactorGapY;
							endLineX = i * (transactorGapX + transactorWidth);
							endLineY = (j * arrowGap) + transactorGapY;
						}
					} else if (transactor.getType().equals(Transactor.Type.TCM)) {
						if (sipSignal.getDirection().equals(SipSignal.Direction.Incoming)) {
							startLineX = transactorWidth + arrowWidh + i * (transactorGapX + transactorWidth);
							startLineY = (j * arrowGap) + transactorGapY;
							endLineX = transactorWidth + arrowWidh + i * (transactorGapX + transactorWidth) + arrowWidh;
							endLineY = (j * arrowGap) + transactorGapY;

						} else if (sipSignal.getDirection().equals(SipSignal.Direction.Outgoing)) {
							startLineX = transactorWidth + arrowWidh + i * (transactorGapX + transactorWidth)
									+ arrowWidh;
							startLineY = (j * arrowGap) + transactorGapY;
							endLineX = transactorWidth + arrowWidh + i * (transactorGapX + transactorWidth);
							endLineY = (j * arrowGap) + transactorGapY;
						}
					}
					drawArrowLine(startLineX, startLineY, endLineX, endLineY, group);
				}
			}
			Scene scene = new Scene(group, 1920, 1080);
			callModelStage.setTitle("Call Model");
			callModelStage.setScene(scene);
			callModelStage.show();		
		}
	}
	
	public static void drawArrowLine(double startX, double startY, double endX, double endY, Group group) {
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
