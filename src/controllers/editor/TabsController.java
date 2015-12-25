package controllers.editor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.ArcadoidEditor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

public class TabsController implements Initializable {

	@FXML
	private Tab gamesTab;
	@FXML
	private Tab navigationTab;
	@FXML
	private Tab tagsTab;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.setupTabForController(this.gamesTab, "GamesViewController");
		this.setupTabForController(this.navigationTab, "NavigationViewController");
		this.setupTabForController(this.tagsTab, "TagsViewController");
	}
	
	private void setupTabForController(Tab tab, String controllerName) {
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ArcadoidEditor.class.getResource("/views/editor/" + controllerName + ".fxml"));
            AnchorPane tagsPane = (AnchorPane) loader.load();
            tab.setContent(tagsPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
}