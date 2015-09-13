package data;

import java.util.ArrayList;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TagsAccessor {

	private final ObservableList<Tag> allTags = FXCollections.observableArrayList();

	public TagsAccessor() {
		for (int index = 0; index < 5; ++index) {
			Tag tag = this.createNewTag();
			tag.setName("Tag " + index);
		}
	}
	
	public void addTagsListListener(ListChangeListener<Tag> listener) {
		this.allTags.addListener(listener);
	}
	
	public ObservableList<Tag> getAllTags() {
		return this.allTags;
	}
	
	public Tag createNewTag() {
		long newIdentifier = this.firstAvailableTagIdentifier();
		Tag newTag = new Tag(newIdentifier);
		newTag.setName("New tag");
		this.allTags.add(newTag);
		return newTag;
	}
	
	public void deleteTag(Tag tag) {
		this.allTags.remove(tag);
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