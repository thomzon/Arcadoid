package controllers.frontend;

import controllers.frontend.GameNavigationLayoutFactory.GameNavigationLayoutType;
import views.frontend.FrontendPane;

public class GameNavigationPane extends FrontendPane {

	private GameNavigationLayout layout;
	
	@Override
	public void setupPane() {
		super.setupPane();
		this.layout = new GameNavigationLayoutFactory().createLayoutForTypeInParentPane(GameNavigationLayoutType.COVERFLOW, this);
		this.makeChildrenVisible(false);
	}
	
	@Override
	public void doLayout() {
		this.layout.setupSettingsAccess();
	}
	
	@Override
	public void prepareForDisappearance() {
		super.prepareForDisappearance();
		this.layout.prepareForParentPaneDisappearance();
	}
	
}