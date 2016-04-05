package controllers.frontend;

import javafx.scene.Node;
import javafx.scene.control.Button;
import utils.frontend.MouseAutohideBehaviour;
import utils.frontend.UIUtils;
import views.frontend.FrontendPane;

public class CoverflowLayout implements GameNavigationLayout {

	private FrontendPane parentPane;
	private Button settingsButton;
	private MouseAutohideBehaviour mouseBehaviour = new MouseAutohideBehaviour();
	
	public CoverflowLayout(FrontendPane parentPane) {
		this.parentPane = parentPane;
	}

	@Override
	public void prepareForParentPaneDisappearance() {
		this.mouseBehaviour.stopBehaviour();
	}
	
	@Override
	public void setupSettingsAccess() {
		this.settingsButton = UIUtils.createButton("title.settings", true);
		this.settingsButton.setOnAction((event) -> {
			UIService.getInstance().displaySettings(true);
		});
		this.parentPane.getChildren().add(this.settingsButton);
		this.settingsButton.setLayoutX(UIUtils.BORDER_NODE_MARGIN);
		this.settingsButton.setLayoutY(UIUtils.BORDER_NODE_MARGIN);
		this.mouseBehaviour.setupInPane(this.parentPane, new Node[]{this.settingsButton});
		this.mouseBehaviour.startBehaviour();
	}

}
