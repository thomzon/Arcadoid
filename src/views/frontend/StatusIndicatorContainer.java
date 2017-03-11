package views.frontend;

import java.io.File;

import data.access.FrontendData;
import data.model.BaseItem;
import data.model.Game;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

/**
 * Handles display of status indicator for an item.
 * If it is not a game, it will not display anything.
 * If it is a game, it can display the "New" or "Favorite" indicator.
 * @author Thomas Debouverie
 *
 */
public class StatusIndicatorContainer extends Region {

	private static final double STATUS_INDICATOR_SIZE = 30;

	private ImageView statusIndicatorImageView = new ImageView();
	
	public StatusIndicatorContainer() {
		this.setPrefWidth(STATUS_INDICATOR_SIZE);
		this.setPrefHeight(STATUS_INDICATOR_SIZE);
		this.getChildren().addAll(this.statusIndicatorImageView);
	}
	
	public void updateStatusIndicatorForItem(BaseItem item) {
		if (item instanceof Game) {
			Game game = (Game)item;
			if (FrontendData.sharedInstance().isFavorite(game)) {
				File favoriteIconFile = new File("images", "favorite_indicator.png");
				Image image = new Image(favoriteIconFile.toURI().toString(), false);
				this.statusIndicatorImageView.setImage(image);
			} else if (!FrontendData.sharedInstance().gameIsSeen(game)) {
				File newGameIconFile = new File("images", "new_indicator.png");
				Image image = new Image(newGameIconFile.toURI().toString(), false);
				this.statusIndicatorImageView.setImage(image);
			} else {
				this.statusIndicatorImageView.setImage(null);
			}
		} else {
			this.statusIndicatorImageView.setImage(null);
		}
	}
	
}
