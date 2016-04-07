package controllers.frontend;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import utils.frontend.MouseAutohideBehaviour;
import utils.frontend.UIUtils;
import views.frontend.FrontendPane;

public class CoverflowLayout implements GameNavigationLayout {

	private FrontendPane parentPane;
	private Button settingsButton, syncButton;
	private MouseAutohideBehaviour mouseBehaviour = new MouseAutohideBehaviour();
	
	public CoverflowLayout(FrontendPane parentPane) {
		this.parentPane = parentPane;
		this.createAllNodes();
	}

	@Override
	public void prepareForParentPaneDisappearance() {
		this.mouseBehaviour.stopBehaviour();
	}
	
	@Override
	public void setupSettingsAccess() {
		this.layoutAllNode();
		this.mouseBehaviour.setupInPane(this.parentPane, new Node[]{this.settingsButton, this.syncButton});
		this.mouseBehaviour.startBehaviour();
	}
	
	private void createAllNodes() {
		this.settingsButton = UIUtils.createButton("", false);
		this.settingsButton.setOnAction((event) -> {
			UIService.getInstance().displaySettings(true);
		});
		this.settingsButton.setId("settings-button");
		
		this.syncButton = UIUtils.createButton("", false);
		this.syncButton.setOnAction((event) -> {
			UIService.getInstance().startCatalogSync();
		});
		this.syncButton.setId("sync-button");
		
		this.parentPane.getChildren().addAll(this.settingsButton, this.syncButton);
	}
	
	private void layoutAllNode() {
		Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		this.syncButton.setLayoutX(screenBounds.getWidth() - this.syncButton.getWidth());
	}

}
