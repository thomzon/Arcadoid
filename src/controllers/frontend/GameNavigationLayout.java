package controllers.frontend;

import java.util.List;

import data.model.BaseItem;

/**
 * Defines what an object must respond to in order to present a specific layout of the game catalog.
 * @author Thomas Debouverie
 *
 */
public interface GameNavigationLayout {

	public void prepareForParentPaneDisappearance();
	public void setupSettingsAccess();
	public void reloadWithDisplayedItems(List<BaseItem> items);
	public void reloadItem(BaseItem item);
	public void navigateToSiblingInItems(BaseItem item, List<BaseItem> items);
	public void navigateToChildren(List<BaseItem> childrenItems);
	public void navigateToParentWithSiblings(BaseItem parent, List<BaseItem> siblings);
	public void startingGame();
	public void stoppingGame();
	
}
