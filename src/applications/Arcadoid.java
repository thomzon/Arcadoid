package applications;

import controllers.frontend.UIService;
import data.input.PlayerInputService;
import data.settings.Settings.PropertyId;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import utils.frontend.GameLaunchService;

public class Arcadoid extends Application {

	@Override
	public void start(Stage primaryStage) {
		ApplicationVersionService.updateVersionNumberForProperty(ApplicationVersionService.FRONTEND_VERSION_NUMBER, PropertyId.FRONTEND_VERSION_NUMBER);
		UIService.sharedInstance().startServiceInPrimaryStage(primaryStage);
		PlayerInputService.sharedInstance().startService();
		GameLaunchService.sharedInstance().startService();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		PlayerInputService.sharedInstance().stopService();
		Platform.exit();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
