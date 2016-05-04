package controllers.editor;

import static java.util.Arrays.asList;

import java.io.File;

import data.settings.Messages;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Static utilities for artwork image files selection.
 * @author Thomas Debouverie
 *
 */
public class ArtworkPathSelection {
	
	/**
	 * Open a file selection dialog to select an artwork (image) file, starting in the artwork folder path defined in current settings.
	 * @param window The window in which the dialog must be opened. Can be null.
	 * @return The File object associated with the selected image.
	 */
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
