package controllers.frontend;

import views.frontend.FrontendPane;

public class GameNavigationLayoutFactory {

	public enum GameNavigationLayoutType {
		COVERFLOW,
	}
	
	public GameNavigationLayout createLayoutForTypeInParentPane(GameNavigationLayoutType layoutType, FrontendPane parentPane) {
		switch (layoutType) {
		case COVERFLOW:
			return new CoverflowLayout(parentPane);
		default:
			return new CoverflowLayout(parentPane);
		}
	}
	
}
