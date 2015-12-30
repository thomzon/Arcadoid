package application;
	
import java.io.IOException;

import controllers.editor.RootController;
import data.settings.Messages;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;


public class ArcadoidEditor extends Application {
	
	private RootController rootController;
    
	@Override
	public void start(Stage primaryStage) {
		try {
            FXMLLoader loader = new FXMLLoader();
            Messages.setupLoader(loader);
            loader.setLocation(ArcadoidEditor.class.getResource("/views/editor/RootController.fxml"));
            loader.load();
            this.rootController = loader.getController();
            this.rootController.setPrimaryStage(primaryStage);
            this.rootController.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
