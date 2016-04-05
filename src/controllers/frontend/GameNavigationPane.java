package controllers.frontend;

import controllers.frontend.GameNavigationLayoutFactory.GameNavigationLayoutType;
import views.frontend.FrontendPane;

public class GameNavigationPane extends FrontendPane {

	private GameNavigationLayout layout;
	
	@Override
	public void doLayout() {
		this.layout = new GameNavigationLayoutFactory().createLayoutForTypeInParentPane(GameNavigationLayoutType.COVERFLOW, this);
		this.layout.setupSettingsAccess();
	}
	
	@Override
	public void prepareForDisappearance() {
		super.prepareForDisappearance();
		this.layout.prepareForParentPaneDisappearance();
	}
	
}