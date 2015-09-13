package application;
	
import controllers.editor.RootController;
import javafx.application.Application;
import javafx.stage.Stage;


public class ArcadoidEditor extends Application {
	
	private RootController rootController;
    
	@Override
	public void start(Stage primaryStage) {
		this.rootController = new RootController(primaryStage);
		this.rootController.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
