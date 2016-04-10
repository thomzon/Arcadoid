package application.frontend;

import controllers.frontend.UIService;
import data.input.PlayerInputObserver;
import data.input.PlayerInputService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Arcadoid extends Application implements PlayerInputObserver {

	@Override
	public void start(Stage primaryStage) {
		UIService.getInstance().startServiceInPrimaryStage(primaryStage);
		PlayerInputService.sharedInstance().startService();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		Platform.exit();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
