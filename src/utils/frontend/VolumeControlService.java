package utils.frontend;

import java.io.File;
import java.io.IOException;

import javafx.animation.Timeline;
import utils.global.GlobalUtils;

/**
 * Handles changing the system volume and giving adequate feedback of the new volume level.
 * @author Thomas Debouverie
 *
 */
public class VolumeControlService {

	private static VolumeControlService sharedInstance = null;
	
	private Timeline stopAudioFeedbackTimeline;

	private VolumeControlService() {
	}

	/**
	 * Singleton access.
	 * @return Unique instance of the VolumeControlService class
	 */
	public static VolumeControlService sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new VolumeControlService();
		}
		return sharedInstance;
	}
	
	/**
	 * Lowers the volume of a fixed amount.
	 */
	public void lowerVolume() {
		this.changeVolume(-500);
	}
	
	/**
	 * Raises the volume of a fixed amount.
	 */
	public void raiseVolume() {
		this.changeVolume(500);
	}
	
	private void changeVolume(int amount) {
		String nircmdExecutablePath = new File("nircmd", "nircmd.exe").getPath();
		try {
			this.playAudioFeedback();
			Runtime.getRuntime().exec(nircmdExecutablePath + " changesysvolume " + amount, null, null);
		} catch (IOException e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.volumeControl", "error.body.nircmdNotFound");
		}
	}
	
	private void playAudioFeedback() {
		this.cancelStopFeedbackCall();
		File audioFile = new File("sounds", "volume_feedback.mp3");
		AudioPlayer.sharedInstance().playAudioFile(audioFile.getPath(), true);
		this.stopAudioFeedbackTimeline = UIUtils.callMethodAfterTime(this, "stopAudioFeedback", UIUtils.AUDIO_FEEDBACK_AUTOSTOP_DELAY);
	}
	
	private void cancelStopFeedbackCall() {
		if (this.stopAudioFeedbackTimeline != null) {
			this.stopAudioFeedbackTimeline.stop();
			this.stopAudioFeedbackTimeline = null;
		}
	}
	
	public void stopAudioFeedback() {
		this.cancelStopFeedbackCall();
		AudioPlayer.sharedInstance().stopCurrentPlayback(true);
	}
	
}