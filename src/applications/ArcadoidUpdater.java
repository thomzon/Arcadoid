package applications;

import java.io.IOException;

import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class ArcadoidUpdater extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Settings.setSetting(PropertyId.UPDATER_VERSION_NUMBER, "1");
		this.showUpdaterController(primaryStage);
	}
	
	public void stop() throws Exception {
		super.stop();
		Platform.exit();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void showUpdaterController(Stage primaryStage) {
		try {
            FXMLLoader loader = new FXMLLoader();
            Messages.setupLoader(loader);
            loader.setLocation(ArcadoidEditor.class.getResource("/views/updater/ArcadoidUpdaterController.fxml"));
            FlowPane pane = (FlowPane)loader.load();
            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
