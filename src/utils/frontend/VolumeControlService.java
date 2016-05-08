package utils.frontend;

import java.io.File;
import java.io.IOException;

import utils.global.GlobalUtils;

public class VolumeControlService {

	private static VolumeControlService sharedInstance = null;
	
	private VolumeControlService() {
	}

	public static VolumeControlService sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new VolumeControlService();
		}
		return sharedInstance;
	}
	
	public void lowerVolume() {
		this.changeVolume(-2000);
	}
	
	public void raiseVolume() {
		this.changeVolume(2000);
	}
	
	private void changeVolume(int amount) {
		String nircmdExecutablePath = new File("nircmd", "nircmd.exe").getPath();
		try {
			Runtime.getRuntime().exec(nircmdExecutablePath + " changesysvolume " + amount, null, null);
		} catch (IOException e) {
			GlobalUtils.simpleErrorAlertForKeys("error.header.volumeControl", "error.body.nircmdNotFound");
		}
	}
	
	
	
}