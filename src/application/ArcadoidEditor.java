package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class ArcadoidEditor extends Application {
	
	private Stage primaryStage;
    private BorderPane rootLayout;
    
	@Override
	public void start(Stage primaryStage) {
			this.primaryStage = primaryStage;
	        this.primaryStage.setTitle("Arcadoid Editor");
	        this.initRootLayout();
	        this.showMainView();
	}
	
	public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ArcadoidEditor.class.getResource("/views/editor/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	 public void showMainView() {
	        try {
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(ArcadoidEditor.class.getResource("/views/editor/MainView.fxml"));
	            AnchorPane personOverview = (AnchorPane) loader.load();
	            rootLayout.setCenter(personOverview);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
