package data.model;

import data.settings.Messages;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Game extends BaseItem {

	protected Process process;
	private final ObservableList<Tag> assignedTags = FXCollections.observableArrayList();

	public enum Platform {
		MAME(Messages.get("platform.MAME")),
		STEAM(Messages.get("platform.Steam"));
		
		public final String stringValue;
		
		private Platform(final String s) {
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
	public abstract void execute();
	public abstract void terminate();

}
