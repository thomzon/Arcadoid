package applications;

import controllers.frontend.UIService;
import data.input.PlayerInputService;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Arcadoid extends Application {

	@Override
	public void start(Stage primaryStage) {
		Settings.setSetting(PropertyId.FRONTEND_VERSION_NUMBER, "1");
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
