package utils.frontend;

import java.io.File;

import javafx.animation.Transition;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Handles audio playback for musics and sounds
 * @author Thomas Debouverie
 *
 */
public class AudioPlayer {

	private static AudioPlayer sharedInstance = null;
	
	private MediaPlayer mediaPlayer;
	private String currentPlayedFilePath;
	
	private AudioPlayer() {
	}

	/**
	 * Singleton access.
	 * @return Unique instance of the AudioPlayer class
	 */
	public static AudioPlayer sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new AudioPlayer();
		}
		return sharedInstance;
	}
	
	/**
	 * Plays given audio file
	 * @param path Path to the audio file
	 * @param withFade If true, sound will smoothly fade in
	 */
	public void playAudioFile(String path, boolean withFade) {
		if (this.currentPlayedFilePath != null && path.equals(this.currentPlayedFilePath)) return;
		Media media = new Media(new File(path).toURI().toString());
	    this.mediaPlayer = new MediaPlayer(media);
	    if (withFade) {
	    	this.startCurrentWithFade();
	    } else {
	    	this.mediaPlayer.play();
	    }
	    this.currentPlayedFilePath = path;
	}
	
	/**
	 * Stops current audio
	 * @param withFade If true, sound will smoothly fade out
	 */
	public void stopCurrentPlayback(boolean withFade) {
		if (withFade) {
			this.stopCurrentWithFade();
		} else {
			this.currentPlayedFilePath = null;
			this.mediaPlayer.stop();
		}
	}
	
	private void startCurrentWithFade() {
		this.mediaPlayer.setVolume(0);
	    this.mediaPlayer.setOnReady(new Runnable() {
	        @Override
	        public void run() {
	          mediaPlayer.play();
	          new Transition() {
	            {
	              setCycleDuration(Duration.millis(UIUtils.MUSIC_FADE_DURATION));
	            }
	            @Override
	            protected void interpolate(double frac) {
	              mediaPlayer.setVolume(frac);
	            }
	          }.play();
	        }
	      });
	}
	
	private void stopCurrentWithFade() {
		Transition fade = new Transition() {
			{
				setCycleDuration(Duration.millis(UIUtils.MUSIC_FADE_DURATION));
			}
			@Override
			protected void interpolate(double frac) {
				mediaPlayer.setVolume(1 - frac);
			}
		};
		fade.setOnFinished((event) -> {
			stopCurrentPlayback(false);
		});
		fade.play();
	}
	
}
