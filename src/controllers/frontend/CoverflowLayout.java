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
		this.settingsButton = UIUtils.createButton("", false);
		this.settingsButton.setOnAction((event) -> {
			UIService.getInstance().displaySettings(true);
		});
		this.settingsButton.setId("settings-button");
		this.parentPane.getChildren().add(this.settingsButton);
		
		this.mouseBehaviour.setupInPane(this.parentPane, new Node[]{this.settingsButton});
		this.mouseBehaviour.startBehaviour();
	}

}
