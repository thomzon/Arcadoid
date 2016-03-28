package application.frontend;

import controllers.frontend.UIService;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
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
		scene.getStylesheets().add("Frontend.css");
		scene.setCursor(Cursor.NONE);
		primaryStage.setScene(scene);
		primaryStage.setFullScreen(true);
		primaryStage.show();
		UIService.getInstance().setRootPane(rootPane);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
