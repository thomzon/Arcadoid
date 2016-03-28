package data.input;

public interface PlayerInputObserver {

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
