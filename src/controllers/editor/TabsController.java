package controllers.editor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import applications.ArcadoidEditor;
import data.access.NotificationCenter;
import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import utils.global.GlobalUtils;

/**
 * View controller in charge of the main tabs for the Arcadoid Editor application.
 * @author Thomas Debouverie
 *
 */
public class TabsController implements Initializable {

	@FXML
	private Tab settingsTab, gamesTab, navigationTab, tagsTab;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setupTabForController(this.settingsTab, "SettingsViewController");
		this.setupTabForController(this.gamesTab, "GamesViewController");
		this.setupTabForController(this.navigationTab, "NavigationViewController");
		this.setupTabForController(this.tagsTab, "TagsViewController");
		NotificationCenter.sharedInstance().addObserver(Settings.SETTINGS_VALIDITY_CHANGED_NOTIFICATION, this, "updateTabsState");
		this.updateTabsState();
	}
	
	public void updateTabsState() {
		boolean settingsValid = Settings.getSettingAsBoolean(PropertyId.EDITOR_SETTINGS_VALID);
		this.gamesTab.setDisable(!settingsValid);
		this.navigationTab.setDisable(!settingsValid);
		this.tagsTab.setDisable(!settingsValid);
	}
	
	private void setupTabForController(Tab tab, String controllerName) {
		try {
            FXMLLoader loader = new FXMLLoader();
            Messages.setupLoader(loader);
            loader.setLocation(ArcadoidEditor.class.getResource("/views/editor/" + controllerName + ".fxml"));
            AnchorPane tagsPane = (AnchorPane) loader.load();
            tab.setContent(tagsPane);
        } catch (IOException e) {
        	GlobalUtils.simpleErrorAlertForKeys("error.header.fxmlLoad", "error.body.fxmlLoad");
        }
	}
	
}