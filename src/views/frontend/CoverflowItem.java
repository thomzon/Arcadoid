package views.frontend;

import java.io.File;

import data.model.BaseItem;
import data.settings.Settings;
import data.settings.Settings.PropertyId;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * Handles display of one catalog item on screen, coverflow style.
 * @author Thomas Debouverie
 *
 */
public class CoverflowItem extends Parent {

	private static final double REFLECTION_SIZE = 0.4;
	public static final double WIDTH = 200;
	public static final double HEIGHT = WIDTH + (WIDTH*REFLECTION_SIZE);
	private static final double RADIUS_H = WIDTH / 2;
	private static final double BACK = WIDTH / 10;
	
	private PerspectiveTransform transform = new PerspectiveTransform();
	private Label itemNameLabel = new Label();
	private Reflection reflectionEffect = new Reflection();
	private ImageView imageView = new ImageView();

	/**
	 * Angle determines rotation on its Y axis.
	 */
	private final DoubleProperty angle = new SimpleDoubleProperty(45) {
        @Override protected void invalidated() {
            // When angle changes calculate new transform
        	updateAngleTransform();
        }
    };
    
    /**
     * Reflection determines the amount of itself that is reflected vertically.
     */
    private final DoubleProperty reflection = new SimpleDoubleProperty(0.5) {
    	@Override protected void invalidated() {
            // When reflection changes calculate new reflectionEffect
            updateReflection();
        }
    };
    
	public CoverflowItem() {
		this.setDefaultImage();
		this.reflectionEffect.setFraction(REFLECTION_SIZE);
		this.reflectionEffect.setTopOpacity(this.reflection.get());
		this.imageView.setEffect(this.reflectionEffect);
		this.imageView.setFitHeight(WIDTH);
		this.imageView.setFitWidth(WIDTH);
		this.imageView.setPreserveRatio(true);
		this.setEffect(this.transform);
		this.getChildren().addAll(this.imageView);
		
		this.itemNameLabel.setTextFill(Color.WHITE);
		this.itemNameLabel.setTextAlignment(TextAlignment.CENTER);
		this.itemNameLabel.setAlignment(Pos.CENTER);
		this.itemNameLabel.setPrefWidth(200);
		this.itemNameLabel.setMinWidth(200);
		this.itemNameLabel.setMaxWidth(200);
		this.itemNameLabel.setLayoutY(150);
		this.getChildren().addAll(this.itemNameLabel);
	}
	
	public void setBaseItem(BaseItem item) {
		this.itemNameLabel.setText(item.getName());
		if (item.getThumbnailArtworkPath() != null && item.getThumbnailArtworkPath().length() > 0) {
			File artworkFile = new File(Settings.getSetting(PropertyId.ARTWORKS_FOLDER_PATH), item.getThumbnailArtworkPath());
			if (artworkFile.exists()) {
				Image image = new Image(artworkFile.toURI().toString(), false);
				this.itemNameLabel.setVisible(false);
				this.setImage(image);
			} else {
				this.setDefaultImage();
			}
		} else {
			this.setDefaultImage();
		}
	}
	
	private void setDefaultImage() {
		File coverFile = new File("images", "default_thumbnail.jpg");
		Image image = new Image(coverFile.toURI().toString(), false);
		this.itemNameLabel.setVisible(true);
		this.setImage(image);
	}
	
	private void setImage(Image image) {
		this.imageView.setImage(image);
		double totalHeight = this.imageView.getBoundsInParent().getHeight();
		totalHeight = totalHeight / (REFLECTION_SIZE + 1);
		if (totalHeight < WIDTH) {
			this.setLayoutY(WIDTH - totalHeight);
		} else {
			this.setLayoutY(0);
		}
	}
	
	public final double getAngle() {
		return this.angle.getValue();
	}

	public final void setAngle(double value) {
		this.angle.setValue(value);
	}

	public final DoubleProperty angleModel() {
		return this.angle;
	}
	
	public final double getReflection() {
		return this.reflection.get();
	}
	
	public final void setReflection(double value) {
		this.reflection.setValue(value);
	}
	
	public final DoubleProperty reflectionModel() {
		return this.reflection;
	}
	
	private void updateReflection() {
		this.reflectionEffect.setTopOpacity(this.reflection.get());
	}
	
	private void updateAngleTransform() {
		 double lx = (RADIUS_H - Math.sin(Math.toRadians(this.angle.get())) * RADIUS_H - 1);
         double rx = (RADIUS_H + Math.sin(Math.toRadians(this.angle.get())) * RADIUS_H + 1);
         double uly = (-Math.cos(Math.toRadians(this.angle.get())) * BACK);
         double ury = -uly;
         transform.setUlx(lx);
         transform.setUly(uly);
         transform.setUrx(rx);
         transform.setUry(ury);
         transform.setLrx(rx);
         transform.setLry(this.imageView.getBoundsInParent().getHeight() + uly);
         transform.setLlx(lx);
         transform.setLly(this.imageView.getBoundsInParent().getHeight() + ury);
	}
	
}
