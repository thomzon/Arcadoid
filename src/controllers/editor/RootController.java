package controllers.editor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;

import application.ArcadoidEditor;
import data.access.ArcadoidData;
import data.settings.Messages;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class RootController implements Initializable {

	@FXML
	private Button sendToRepositoryButton, getFromRepositoryButton;
	
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
		this.loadFromFileAction();
	}
	
	private void initStage() {
		try {
            FXMLLoader loader = new FXMLLoader();
            Messages.setupLoader(loader);
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
	
	private void loadFromFileAction() {
		try {
			ArcadoidData.sharedInstance().loadData();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void saveToFileAction() {
		try {
			ArcadoidData.sharedInstance().saveData();
		} catch (FileNotFoundException e) {
			this.showFileErrorForMessage(Messages.get("error.body.cannotAccessFile"));
		} catch (IOException e) {
			this.showFileErrorForMessage(Messages.get("error.body.errorDuringWriting"));
		} catch (Exception e) {
			this.showFileErrorForMessage(Messages.get("error.body.unexpectedFileError"));
		}
	}
	
	@FXML
	private void sendToRepositoryAction() {
		
	}
	
	@FXML
	private void getFromRepositoryAction() {
		
	}
	
	private void showFileErrorForMessage(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("error.header.saveToFile"));
		alert.setContentText(message);
		alert.show();
	}
	
}