package data.model;

import java.io.File;

import data.settings.Messages;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a game that can be run from the Arcadoid front-end.
 * @author Thomas Debouverie
 *
 */
public abstract class Game extends BaseItem {

	protected Process process;
	private final ObservableList<Tag> assignedTags = FXCollections.observableArrayList();

	/**
	 * Enumerates all supported game platforms.
	 * @author Thomas Debouverie
	 *
	 */
	public enum Platform {
		MAME(1, Messages.get("platform.MAME")),
		STEAM(2, Messages.get("platform.Steam")),
		SNES(3, Messages.get("platform.Snes")),
		FUSION(4, Messages.get("platform.Fusion")),
		NES(5, Messages.get("platform.Nes"));
		
		public final int intValue;
		public final String stringValue;
		
		private Platform(final int i, final String s) {
			intValue = i;
			stringValue = s;
		}
		
		public String toString() {
			return stringValue;
		}
	}
	
	public Game(long identifier) {
		super(identifier);
	}
	
	public Game(Game model) {
		super(model.getIdentifier());
		this.setName(model.getName());
		this.setThumbnailArtworkPath(model.getThumbnailArtworkPath());
		this.setBackgroundArtworkPath(model.getBackgroundArtworkPath());
		this.assignedTags.addAll(model.getAssignedTags());
	}
	
	public ObservableList<Tag> getAssignedTags() {
		return this.assignedTags;
	}
	
	public abstract Platform getPlatform();
	
	/**
	 * Start the game within its own platform. Execution depends on actual subclass.
	 */
	public abstract void execute() throws Exception;
	
	protected void execute(String commandLine, File directory) throws Exception {
		if (this.process != null) return;
		this.process = Runtime.getRuntime().exec(commandLine, null, directory);
	}
	
	/**
	 * Stops the game process and return to Arcadoid front-end.
	 * Most games can be terminated by just destroying the associated emulator process.
	 */
	public void terminate() throws Exception {
		if (this.process != null) {
			this.process.destroy();
		}
		this.process = null;
	}

}
