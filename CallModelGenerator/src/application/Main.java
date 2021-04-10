package application;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.LSC;
import model.Transactor;
import utility.Log;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
		int initialSpaceX = 100;
		int initialSpaceY = 50;
		int transactorWidth = 200;
		int transactorHeight = 300;
		int lscWidth = 50;
		int lscHeight = 50;
		
		if (transactorList != null && !transactorList.isEmpty()) {
			Stage callModelStage = new Stage();
			Group group = new Group();
			for (int i = 0; i < transactorList.size(); i++) {
				Transactor transactor = transactorList.get(i);
				
				Rectangle transactorShape = new Rectangle(initialSpaceX + i * 300, initialSpaceY, transactorWidth, transactorHeight);
				transactorShape.setStroke(Color.YELLOW);
				transactorShape.setFill(Color.LIGHTGRAY);
				group.getChildren().add(transactorShape);
				for (int j = 0; j < transactor.getLscList().size(); j++) {
					LSC lsc = transactor.getLscList().get(j);
					Rectangle lscShape = new Rectangle(initialSpaceX + i * (transactorWidth + 100) + (j * 55), transactorHeight + initialSpaceY, lscWidth, lscHeight);
					lscShape.setFill(Color.AQUA);	
					group.getChildren().add(lscShape);
				}
			}
			Scene scene = new Scene(group, 1920, 1080);
			callModelStage.setTitle("Call Model");
			callModelStage.setScene(scene);
			callModelStage.show();
		}
	}
}
