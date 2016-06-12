package data.input;

/**
 * Defines an object capable of reacting to player input.
 * @author Thomas Debouverie
 *
 */
public interface PlayerInputObserver {

	default public void anyInputEntered() {}
	default public void navigateUp() {}
	default public void navigateDown() {}
	default public void navigateLeft() {}
	default public void navigateRight() {}
	default public void confirm() {}
	default public void raiseVolume() {}
	default public void lowerVolume() {}
	default public void addFavorite() {}
	default public void quitGame() {}
	
}
