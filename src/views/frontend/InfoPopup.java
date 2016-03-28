package views.frontend;

import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import utils.frontend.UIUtils;

/**
 * Simple message popup, with or without close button
 * @author Thomas
 *
 */
public class InfoPopup extends FrontendPopup {

	/**
	 * Information text
	 */
	private Text text;
	/**
	 * Close button
	 */
	private Button closeButton;
	
	/**
	 * Creates a message popup
	 * @param width Popup width
	 * @param height Popup height
	 * @param message Message to display
	 * @param withCloseButton If true, an "Ok" button allows user to close the popup
	 */
	public InfoPopup(double width, double height, String message, boolean withCloseButton) {
		super(width, height);
		this.text = new Text(message);
		this.text.setWrappingWidth(width - UIUtils.POPUP_TEXT_MARGIN);
		this.text.setTextAlignment(TextAlignment.CENTER);
		UIUtils.assignStyleClassToNodes("popup-text", this.text);
		this.getChildren().add(this.text);
		if (withCloseButton) {
			this.addCloseButton();
		}
	}
	
	/**
	 * Update displayed message.
	 * @param newMessage New message to display
	 */
	public void setMessage(String newMessage) {
		this.text.setText(newMessage);
	}
	
	private void addCloseButton() {
		this.closeButton = UIUtils.createButton("infoPopup.ok", true);
		this.closeButton.setOnAction(event -> {
			discard();
		});
		this.getChildren().add(this.closeButton);
	}
	
	@Override
	public void makeAppearAfterStandardDelay() {
		super.makeAppearAfterStandardDelay();
		this.text.setLayoutX(getWidth()/2 - this.text.getLayoutBounds().getWidth()/2);
		this.text.setLayoutY(this.text.getLayoutBounds().getHeight() + UIUtils.POPUP_TEXT_MARGIN);
		if (this.closeButton != null) {
			this.closeButton.setLayoutY(this.getHeight() - this.closeButton.getHeight() - UIUtils.POPUP_TEXT_MARGIN);
			this.closeButton.setLayoutX(this.getWidth()/2 - this.closeButton.getWidth()/2);
		}
	}
	
	/**
	 * @return Y coordinate at which children can start layout out components, so it is below popup title
	 */
	protected double getStartYForChildren() {
		return this.text.getLayoutBounds().getHeight() + UIUtils.POPUP_TEXT_MARGIN*2;
	}

}
