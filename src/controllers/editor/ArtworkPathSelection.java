package controllers.editor;

import static java.util.Arrays.asList;

import java.io.File;

import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class ArtworkPathSelection {
	
	public static File selectArtworkFile(Window window) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(Messages.get("title.selectImage"));
		chooser.setInitialDirectory(new File(Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH)));
		chooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("Images", asList("*.jpg", "*.png", "*.jpeg", "*.gif"))
        );
		return chooser.showOpenDialog(window);
	}

}
