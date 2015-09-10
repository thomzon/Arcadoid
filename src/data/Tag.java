package data;

import java.util.ArrayList;

public class Tag {

	private int identifier;
	private String name;
	private String thumbnailArtworkPath;
	private String backgroundArtworkPath;
	private Tag parentTag;
	private ArrayList<Tag> tags;

	public Tag() {
		this.tags = new ArrayList<Tag>();
	}
	
	public int getIdentifier() {
		return identifier;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThumbnailArtworkPath() {
		return thumbnailArtworkPath;
	}

	public void setThumbnailArtworkPath(String thumbnailArtworkPath) {
		this.thumbnailArtworkPath = thumbnailArtworkPath;
	}

	public String getBackgroundArtworkPath() {
		return backgroundArtworkPath;
	}

	public void setBackgroundArtworkPath(String backgroundArtworkPath) {
		this.backgroundArtworkPath = backgroundArtworkPath;
	}

	public Tag getParentTag() {
		return parentTag;
	}

	public void setParentTag(Tag parentTag) {
		this.parentTag = parentTag;
	}
	
	public void addTag(Tag tag) {
		this.tags.add(tag);
	}
	
	public void removeTag(Tag tag) {
		this.tags.remove(tag);
	}
	
	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		} else if (anObject instanceof Tag) {
			Tag otherTag = (Tag)anObject;
			return otherTag.getIdentifier() == this.getIdentifier();
		} else {
			return false;
		}
	}
	
}