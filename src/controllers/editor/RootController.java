package controllers.editor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.dialog.ProgressDialog;

import application.ArcadoidEditor;
import data.access.ArcadoidData;
import data.settings.Messages;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import data.transfer.LoadFromRepositoryService;
import data.transfer.SendToRepositoryService;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
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
		this.resetFromFileWithUnknownFileAlert(false);
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
	
	@FXML
	private void saveToFileAction() {
		try {
			ArcadoidData.sharedInstance().saveData();
		} catch (FileNotFoundException e) {
			this.showFileSaveErrorForMessage(Messages.get("error.body.cannotAccessFile"));
		} catch (IOException e) {
			this.showFileSaveErrorForMessage(Messages.get("error.body.errorDuringFileIO"));
		} catch (Exception e) {
			this.showFileSaveErrorForMessage(Messages.get("error.body.unexpectedFileError"));
		}
	}
	
	@FXML
	private void resetFromFileAction() {
		this.resetFromFileWithUnknownFileAlert(true);
	}
	
	@FXML
	private void sendToRepositoryAction() {
		CompletionCallable sendCompletion = new CompletionCallable() {
			@Override public Void call() throws Exception {
				handleSendToRepositoryResult(this.result);
				return null;
			}
		};
		Service<Void> service = new SendToRepositoryService(sendCompletion);
		ProgressDialog dialog = new ProgressDialog(service);
		dialog.initOwner(this.primaryStage);
		dialog.setTitle(Messages.get("alert.title"));
		dialog.setHeaderText(Messages.get("progress.header.sendToRepo"));
		dialog.initModality(Modality.APPLICATION_MODAL);
		service.start();
	}
	
	@FXML
	private void getFromRepositoryAction() {
		CompletionCallable sendCompletion = new CompletionCallable() {
			@Override public Void call() throws Exception {
				handleLoadFromRepositoryResult(this.result);
				return null;
			}
		};
		Service<Void> service = new LoadFromRepositoryService(sendCompletion);
		ProgressDialog dialog = new ProgressDialog(service);
		dialog.initOwner(this.primaryStage);
		dialog.setTitle(Messages.get("alert.title"));
		dialog.setHeaderText(Messages.get("progress.header.loadFromRepo"));
		dialog.initModality(Modality.APPLICATION_MODAL);
		service.start();
	}
	
	private void resetFromFileWithUnknownFileAlert(boolean showUnknownFileAlert) {
		try {
			ArcadoidData.sharedInstance().loadData();
		} catch (FileNotFoundException e) {
			if (showUnknownFileAlert) {
				this.showFileLoadErrorForMessage(Messages.get("error.body.cannotAccessFile"));
			}
		} catch (IOException e) {
			this.showFileLoadErrorForMessage(Messages.get("error.body.errorDuringFileIO"));
		} catch (Exception e) {
			this.showFileLoadErrorForMessage(Messages.get("error.body.unexpectedFileError"));
		}
	}
	
	private void showFileSaveErrorForMessage(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("error.header.saveToFile"));
		alert.setContentText(message);
		alert.show();
	}
	
	private void showFileLoadErrorForMessage(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("error.header.resetFromFile"));
		alert.setContentText(message);
		alert.show();
	}
	
	private void handleSendToRepositoryResult(CompletionResult result) {
		if (result != null && !result.success) {
			this.showRepositoryOperationError(result);
		}
	}
	
	private void handleLoadFromRepositoryResult(CompletionResult result) {
		if (result != null && !result.success) {
			this.showRepositoryOperationError(result);
		} else {
			this.resetFromFileWithUnknownFileAlert(true);
		}
	}
	
	private void showRepositoryOperationError(CompletionResult result) {
		String message = null;
		switch (result.errorType) {
		case CANNOT_READ_REMOTE_FILE:
			message = Messages.get("error.body.ftpReadError");
			break;
		case CANNOT_WRITE_REMOTE_FILE:
			message = Messages.get("error.body.ftpWriteError");
			break;
		default:
			message = Messages.get("error.body.invalidFtpSettings");
			break;
		}
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("error.header.ftpOperationError"));
		alert.setContentText(message);
		alert.show();
	}
	
}