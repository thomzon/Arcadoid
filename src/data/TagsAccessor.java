package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TagsAccessor {

	private final ObservableList<Tag> allTags = FXCollections.observableArrayList();

	public TagsAccessor() {
		
	}
	
	public void addTagsListListener(ListChangeListener<Tag> listener) {
		this.allTags.addListener(listener);
	}
	
	public Iterator<Tag> getAllTags() {
		return this.allTags.iterator();
	}
	
	public Tag createNewTag() {
		long newIdentifier = this.firstAvailableTagIdentifier();
		Tag newTag = new Tag(newIdentifier);
		this.allTags.add(newTag);
		return newTag;
	}
	
	private long firstAvailableTagIdentifier() {
		ArrayList<Long> allUsedIdentifiers = new ArrayList<Long>();
		for (Tag tag : allTags) {
			allUsedIdentifiers.add(tag.getIdentifier());
		}
		Collections.sort(allUsedIdentifiers);
		for (int index = 0; index < allUsedIdentifiers.size(); ++index) {
			if (allUsedIdentifiers.get(index) != index) {
				return index;
			}
		}
		return allUsedIdentifiers.size();
	}
	
}