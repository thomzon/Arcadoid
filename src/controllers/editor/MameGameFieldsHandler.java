package controllers.editor;

import data.model.Game;
import data.model.MameGame;
import data.settings.Messages;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Concrete PlatformSpecificGameFieldsHandler implementation to handle MAME games specific fields.
 * @author Thomas Debouverie
 *
 */
class MameGameFieldsHandler extends PlatformSpecificGameFieldsHandler {

	private Label label = new Label();
	private TextField textField = new TextField();
	
	@Override
	void teardownForGridPane(GridPane gridPane) {
		super.teardownForGridPane(gridPane);
		gridPane.getChildren().removeAll(this.label, this.textField);
	}

	@Override
	void setupInGridPane(GridPane gridPane) {
		gridPane.add(this.label, 0, 4);
		gridPane.add(this.textField, 1, 4, 3, 1);
		this.label.setText(Messages.get("field.mameRomName"));
		this.textField.textProperty().addListener((observable, oldValue, newValue) -> textFieldChanged(newValue));
	}
	
	@Override
	void setEditedGame(Game editedGame) {
		super.setEditedGame(editedGame);
		if (this.getMameGame() != null) {
			this.textField.setText(this.getMameGame().gameName());
		}
	}
	
	private MameGame getMameGame() {
		if (this.getEditedGame() instanceof MameGame) {
			return (MameGame)this.getEditedGame();
		} else {
			return null;
		}
	}

	private void textFieldChanged(String newValue) {
		if (this.getMameGame() != null) {
			this.getMameGame().setGameName(newValue);
		}
		this.fieldModified();
	}
	
}
