package controllers.frontend;

import java.util.List;

import data.model.BaseItem;

public interface GameNavigationLayout {

	public void prepareForParentPaneDisappearance();
	public void setupSettingsAccess();
	public void reloadWithDisplayedItems(List<BaseItem> items);
	public void navigateToSiblingInItems(BaseItem item, List<BaseItem> items);
	public void navigateToChildren(List<BaseItem> childrenItems);
	public void navigateToParentWithSiblings(BaseItem parent, List<BaseItem> siblings);
	
}
