package data.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Game extends BaseItem {

	protected Process process;
	private final ObservableList<Tag> assignedTags = FXCollections.observableArrayList();

	public enum GameType {
		MAME,
		STEAM,
	}
	
	public Game(long identifier) {
		super(identifier);
	}
	
	public ObservableList<Tag> getAssignedTags() {
		return this.assignedTags;
	}
	
	public abstract void execute();
	public abstract void terminate();

}
