package controllers.editor;

import java.util.concurrent.Callable;

import data.model.Game;
import data.model.Game.Platform;
import javafx.scene.layout.GridPane;

/**
 * Has the responsability to modifiy the GamesViewController interface to fit the selected game platform.
 * @author Thomas Debouverie
 *
 */
abstract class PlatformSpecificGameFieldsHandler {

	private Callable<Void> fieldModifiedCallable;
	private Game editedGame;
	
	/**
	 * Does any required cleanup in the given GridPane.
	 * @param gridPane GridPane that contained platform specific field.
	 */
	void teardownForGridPane(GridPane gridPane) {
		this.editedGame = null;
	}
	
	/**
	 * Sets up all required platform specific fields in the given GridPane.
	 * @param gridPane GridPane in which platform-specific fields must be managed.
	 */
	abstract void setupInGridPane(GridPane gridPane);
	 
	Game getEditedGame() {
		return editedGame;
	}
	
	void setEditedGame(Game editedGame) {
		this.editedGame = editedGame;
	}
	
	/**
	 * The given callable will be called each time any of the platform specific fields content is modified.
	 * @param fieldModifiedCallable Code to be executed.
	 */
	void setFieldModifiedCallable(Callable<Void> fieldModifiedCallable) {
		this.fieldModifiedCallable = fieldModifiedCallable;
	}
	
	/**
	 * Used by concrete subclasses when a field content is modified.
	 */
	void fieldModified() {
		if (this.fieldModifiedCallable != null) {
			try {
				this.fieldModifiedCallable.call();
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Factory method to create a PlatformSpecificGameFieldsHandler object for the given platform.
	 * @param platform Platform to consider.
	 * @return A concrete PlatformSpecificGameFieldsHandler object.
	 */
	static PlatformSpecificGameFieldsHandler handlerForPlatform(Platform platform) {
		switch (platform) {
			case MAME:
				return new MameGameFieldsHandler();
			case STEAM:
				return new SteamGameFieldsHandler();
			case SNES:
				return new SnesGameFieldsHandler();
			case FUSION:
				return new FusionGameFieldsHandler();
			default:
				return null;
		}
	}
	
}
