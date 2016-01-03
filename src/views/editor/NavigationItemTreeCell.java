package views.editor;

import data.model.NavigationItem;
import javafx.scene.control.TreeCell;

public class NavigationItemTreeCell extends TreeCell<NavigationItem> {

	@Override
	protected void updateItem(NavigationItem item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty && item != null) {
			this.setText(item.getName());
		} else {
			this.setText("");
		}
	}
	
}
