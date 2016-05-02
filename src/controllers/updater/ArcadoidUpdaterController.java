package controllers.updater;

import java.net.URL;
import java.util.ResourceBundle;

import data.settings.Messages;
import data.transfer.CompletionCallable;
import data.transfer.CompletionResult;
import data.transfer.updater.ApplicationExecutable;
import data.transfer.updater.ApplicationUpdateChecker;
import data.transfer.updater.ApplicationUpdater;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import utils.transfer.TransferUtils;

/**
 * View controller in charge of offering simple controls to start an application update.
 * @author Thomas Debouverie
 *
 */
public class ArcadoidUpdaterController implements Initializable {

	@FXML
	private Button updateEditorButton, updateFrontendButton;
	private ApplicationUpdateChecker updateChecker = new ApplicationUpdateChecker();
	private Stage primaryStage;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.updateEditorButton.setDisable(true);
		this.updateFrontendButton.setDisable(true);
		this.updateChecker.checkForUpdate(new CompletionCallable() {
			@Override
			public Void call() throws Exception {
				Platform.runLater(() -> {
					handleUpdateCheckerResult(this.result);
				});
				return null;
			}
		});
	}
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Arcadoid Updater");
	}
	
	@FXML private void updateEditorAction() {
		this.startUpdateForExecutable(ApplicationExecutable.EDITOR, false);
	}
	
	@FXML private void updateFrontendAction() {
		this.startUpdateForExecutable(ApplicationExecutable.FRONTEND, false);
	}
	
	private void handleUpdateCheckerResult(CompletionResult result) {
		if (result != null && !result.success) {
			TransferUtils.showRepositoryOperationError(result);
		} else if (this.updateChecker.updateAvailableForUpdater) {
			this.startUpdateForExecutable(ApplicationExecutable.UPDATER, true);
		} else if (this.updateChecker.anyUpdateAvailable()) {
			this.handleSomeUpdatesAvailable();
		} else {
			this.handleNoUpdateAvailable();
		}
	}
	
	private void handleNoUpdateAvailable() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("info.header.updateCheckSuccess"));
		alert.setContentText(Messages.get("info.body.noUpdatesAvailable"));
		alert.show();
	}
	
	private void handleSomeUpdatesAvailable() {
		this.updateEditorButton.setDisable(!this.updateChecker.updateAvailableForEditor);
		this.updateFrontendButton.setDisable(!this.updateChecker.updateAvailableForFrontend);
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(Messages.get("alert.title"));
		alert.setHeaderText(Messages.get("info.header.updateCheckSuccess"));
		alert.setContentText(Messages.get("info.body.someUpdatesAvailable"));
		alert.show();
	}

	private void startUpdateForExecutable(ApplicationExecutable executable, boolean executeWhenDone) {
		new ApplicationUpdater(executable).startUpdate(this.primaryStage, executeWhenDone, null);
	}
	
}
