package controllers.editor;

import java.util.concurrent.Callable;

import data.model.Game;
import data.model.Game.Platform;
import javafx.scene.layout.GridPane;

abstract class PlatformSpecificGameFieldsHandler {

	private Callable<Void> fieldModifiedCallable;
	private Game editedGame;
	
	void teardownForGridPane(GridPane gridPane) {
		this.editedGame = null;
	}
	
	abstract void setupInGridPane(GridPane gridPane);
	 
	Game getEditedGame() {
		return editedGame;
	}
	
	void setEditedGame(Game editedGame) {
		this.editedGame = editedGame;
	}
	
	void setFieldModifiedCallable(Callable<Void> fieldModifiedCallable) {
		this.fieldModifiedCallable = fieldModifiedCallable;
	}
	
	void fieldModified() {
		if (this.fieldModifiedCallable != null) {
			try {
				this.fieldModifiedCallable.call();
			} catch (Exception e) {
			}
		}
	}
	
	static PlatformSpecificGameFieldsHandler handlerForPlatform(Platform platform) {
		switch (platform) {
			case MAME:
				return new MameGameFieldsHandler();
			case STEAM:
				return new SteamGameFieldsHandler();
			default:
				return null;
		}
	}
	
}
