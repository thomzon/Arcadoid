package application.frontend;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Arcadoid extends Application {

	@Override
	public void start(Stage primaryStage) {
		Pane rootPane = new Pane();
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		Scene scene = new Scene(rootPane, screenBounds.getWidth(), screenBounds.getHeight());
//		scene.getStylesheets().add("Arcadoid.css");
		primaryStage.setScene(scene);
		primaryStage.setFullScreen(true);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
