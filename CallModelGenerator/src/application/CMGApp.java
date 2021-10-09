package application;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Transactor;
import utility.DrawUtility;
import utility.LogUtility;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class CMGApp extends Application {
	@Override
	public void start(Stage primaryStage) {
		LogUtility log = new LogUtility();
		try {
			primaryStage.setTitle("Call Model Generator");
			BorderPane root = new BorderPane();
			FileChooser fileChooser = new FileChooser();
			Button button = new Button("Select log file");
			button.setOnAction(e -> {
				File selectedFile = fileChooser.showOpenDialog(primaryStage);
				if (selectedFile != null && selectedFile.getName().contains(".log")) {
					List<Transactor> transactorList = log.readLog(selectedFile.getAbsolutePath());
					/*DrawUtility du = new DrawUtility();
					du.drawCallModel(transactorList).show();*/
				}
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

}
