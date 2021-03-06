package applications;
	
import controllers.editor.RootController;
import data.settings.Messages;
import data.settings.Settings.PropertyId;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import utils.global.GlobalUtils;

/**
 * Application class for the catalog editor GUI.
 * @author Thomas Debouverie
 *
 */
public class ArcadoidEditor extends Application {
	
	private RootController rootController;
    
	@Override
	public void start(Stage primaryStage) {
		ApplicationVersionService.updateVersionNumberForProperty(ApplicationVersionService.EDITOR_VERSION_NUMBER, PropertyId.EDITOR_VERSION_NUMBER);
		try {
            FXMLLoader loader = new FXMLLoader();
            Messages.setupLoader(loader);
            loader.setLocation(ArcadoidEditor.class.getResource("/views/editor/RootController.fxml"));
            loader.load();
            this.rootController = loader.getController();
            this.rootController.setPrimaryStage(primaryStage);
            this.rootController.show();
        } catch (Exception e) {
        	GlobalUtils.simpleErrorAlertForKeys("error.header.fxmlLoad", "error.body.fxmlLoad");
        }
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
