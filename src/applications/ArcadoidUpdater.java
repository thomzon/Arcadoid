package applications;

import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ArcadoidUpdater extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Settings.setSetting(PropertyId.UPDATER_VERSION_NUMBER, "1");
	}
	
	public void stop() throws Exception {
		super.stop();
		Platform.exit();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
