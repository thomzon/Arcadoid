package controllers.editor;

import data.model.Game;
import data.model.SteamGame;
import data.settings.Messages;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Concrete PlatformSpecificGameFieldsHandler implementation to handle Steam game fields.
 * @author Thomas Debouverie
 *
 */
public class SteamGameFieldsHandler extends PlatformSpecificGameFieldsHandler {

	private Label appIdLabel = new Label(), processNameLabel = new Label();
	private TextField appIdField = new TextField(), processNameField = new TextField();
	
	@Override
	void teardownForGridPane(GridPane gridPane) {
		super.teardownForGridPane(gridPane);
		gridPane.getChildren().removeAll(this.appIdLabel, this.processNameLabel, this.appIdField, this.processNameField);
	}

	@Override
	void setupInGridPane(GridPane gridPane) {
		gridPane.add(this.appIdLabel, 0, 4);
		gridPane.add(this.appIdField, 1, 4, 3, 1);
		gridPane.add(this.processNameLabel, 0, 5);
		gridPane.add(this.processNameField, 1, 5, 3, 1);
		this.appIdLabel.setText(Messages.get("field.steamAppId"));
		this.processNameLabel.setText(Messages.get("field.steamProcessName"));
		this.appIdField.textProperty().addListener((observable, oldValue, newValue) -> appIdChanged(newValue));
		this.processNameField.textProperty().addListener((observable, oldValue, newValue) -> processNameChanged(newValue));
	}

	@Override
	void setEditedGame(Game editedGame) {
		super.setEditedGame(editedGame);
		if (this.getSteamGame() != null) {
			this.appIdField.setText(this.getSteamGame().appId());
			this.processNameField.setText(this.getSteamGame().processName());
		}
	}
	
	private SteamGame getSteamGame() {
		if (this.getEditedGame() instanceof SteamGame) {
			return (SteamGame)this.getEditedGame();
		} else {
			return null;
		}
	}

	private void appIdChanged(String newValue) {
		if (this.getSteamGame() != null) {
			this.getSteamGame().setAppId(newValue);
		}
		this.fieldModified();
	}
	
	private void processNameChanged(String newValue) {
		if (this.getSteamGame() != null) {
			this.getSteamGame().setProcessName(newValue);
		}
		this.fieldModified();
	}

}
