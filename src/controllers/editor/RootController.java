package controllers.editor;

import java.io.IOException;

import application.ArcadoidEditor;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class RootController {

    private BorderPane rootLayout;
	private Stage primaryStage;

	public RootController(Stage stage) {
		this.primaryStage = stage;
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
            loader.setLocation(ArcadoidEditor.class.getResource("/views/editor/TabsController.fxml"));
            AnchorPane tabsControllerPane = (AnchorPane) loader.load();
            rootLayout.setCenter(tabsControllerPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}