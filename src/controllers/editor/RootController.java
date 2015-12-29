package controllers.editor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.ArcadoidEditor;
import data.settings.Messages;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class RootController implements Initializable {

    private BorderPane rootLayout;
	private Stage primaryStage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Arcadoid Editor");
	}
	
	public void show() {
		this.initStage();
		this.showMainView();
	}
	
	private void initStage() {
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ArcadoidEditor.class.getResource("/views/editor/RootController.fxml"));
            rootLayout = (BorderPane)loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Messages.setupLoader(loader);
            loader.setLocation(ArcadoidEditor.class.getResource("/views/editor/TabsController.fxml"));
            AnchorPane tabsControllerPane = (AnchorPane)loader.load();
            rootLayout.setCenter(tabsControllerPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	
}