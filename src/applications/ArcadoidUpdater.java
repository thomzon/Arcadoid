package applications;

import java.io.IOException;

import controllers.updater.ArcadoidUpdaterController;
import data.settings.Messages;
import data.settings.Settings.PropertyId;
import data.transfer.updater.ApplicationUpdater;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class ArcadoidUpdater extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		ApplicationVersionService.updateVersionNumberForProperty(ApplicationVersionService.UPDATER_VERSION_NUMBER, PropertyId.UPDATER_VERSION_NUMBER);
		if (this.getParameters().getRaw().size() == 2 && this.getParameters().getRaw().get(0).equals("--update")) {
			this.runDirectUpdateForExecutableName(this.getParameters().getRaw().get(1));
		} else {
			this.showUpdaterController(primaryStage);
		}
	}
	
	public void stop() throws Exception {
		super.stop();
		Platform.exit();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void runDirectUpdateForExecutableName(String executableName) {
		try {
			ApplicationUpdater updater = new ApplicationUpdater(executableName);
			updater.startUpdate(null, true, null);
		} catch (IllegalArgumentException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(Messages.get("alert.title"));
			alert.setHeaderText(Messages.get("error.header.illegalApplicationName"));
			alert.setContentText(Messages.get("error.body.illegalApplicationName", executableName));
			alert.show();
		}
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
            ((ArcadoidUpdaterController)loader.getController()).setPrimaryStage(primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
