package controllers.editor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.dialog.ProgressDialog;

import applications.ArcadoidEditor;
import data.access.ArcadoidData;
import data.access.NotificationCenter;
import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import data.settings.editor.EditorSettings;
import data.settings.editor.EditorSettingsValidator;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import data.transfer.DataUpdateChecker;
import data.transfer.SendToRepositoryService;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.global.GlobalUtils;
import utils.transfer.LoadFromRepositoryHandler;
import utils.transfer.TransferUtils;

/**
 * Master view controller for the Arcadoid Editor application.
 * Handles the top toolbar and associated actions.
 * @author Thomas Debouverie
 *
 */
public class RootController implements Initializable {

	public static final String ASK_ABOUT_DATA_UPDATE_NOTIFICATION = "ASK_ABOUT_DATA_UPDATE_NOTIFICATION";
	
	@FXML
	private Button saveToFileButton, resetFromFileButton, sendToRepositoryButton, getFromRepositoryButton;
	
    private BorderPane rootLayout;
	private Stage primaryStage;
	private DataUpdateChecker updateChecker = new DataUpdateChecker();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void setPrimaryStage(Stage primaryStage) {
		NotificationCenter.sharedInstance().addObserver(Settings.SETTINGS_VALIDITY_CHANGED_NOTIFICATION, this, "updateToolbarState");
		NotificationCenter.sharedInstance().addObserver(ASK_ABOUT_DATA_UPDATE_NOTIFICATION, this, "askAboutDataUpdate");
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Arcadoid Editor");
	}
	
	public void show() {
		this.initStage();
		this.showMainView();
		TransferUtils.resetFromFileWithUnknownFileAlert(false);
		this.checkMandatorySettingsAtStartup();
	}
	
	private void checkMandatorySettingsAtStartup() {
		EditorSettings settings = new EditorSettings();
		EditorSettingsValidator validator = new EditorSettingsValidator(settings);
		CompletionResult result = validator.validate();
		if (result == null || result.success) {
			this.updateChecker.checkForUpdate(new CompletionCallable() {
				@Override public Void call() throws Exception {
					Platform.runLater(() -> askAboutDataUpdate());
					return null;
				}
			});
		}
	}
	
	public void updateToolbarState() {
		boolean settingsValid = Settings.getSettingAsBoolean(PropertyId.EDITOR_SETTINGS_VALID);
		this.saveToFileButton.setDisable(!settingsValid);
		this.resetFromFileButton.setDisable(!settingsValid);
		this.sendToRepositoryButton.setDisable(!settingsValid);
		this.getFromRepositoryButton.setDisable(!settingsValid);
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
        	GlobalUtils.simpleErrorAlertForKeys("error.header.fxmlLoad", "error.body.fxmlLoad");
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
        	GlobalUtils.simpleErrorAlertForKeys("error.header.fxmlLoad", "error.body.fxmlLoad");
        }
    }
	
	public void askAboutDataUpdate() {
		Optional<ButtonType> result = GlobalUtils.simpleConfirmationAlertForKeys("confirmation.header.dataUpdateAvailable", "confirmation.body.dataUpdateAvailable");
		if (result.isPresent() && result.get() == ButtonType.OK) {
			this.getFromRepositoryAction();
		}
	}
	
	@FXML
	private void saveToFileAction() {
		this.doSaveToFile();
	}
	
	private boolean doSaveToFile() {
		try {
			ArcadoidData.sharedInstance().saveData();
			return true;
		} catch (FileNotFoundException e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.saveToFile", "error.body.cannotAccessFile");
		} catch (IOException e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.saveToFile", "error.body.errorDuringFileIO");
		} catch (Exception e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.saveToFile", "error.body.unexpectedFileError");
		}
		return false;
	}
	
	@FXML
	private void resetFromFileAction() {
		TransferUtils.resetFromFileWithUnknownFileAlert(true);
	}
	
	@FXML
	private void sendToRepositoryAction() {
		if (!this.doSaveToFile()) return;
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
		LoadFromRepositoryHandler handler = new LoadFromRepositoryHandler();
		handler.startInWindow(this.primaryStage);
	}
	
	private void handleSendToRepositoryResult(CompletionResult result) {
		if (result != null && !result.success) {
			TransferUtils.showRepositoryOperationError(result);
		}
	}
	
}