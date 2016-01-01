package data.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationCenter {

	private static NotificationCenter sharedInstance = null;
	
	private Map<String, ArrayList<NotificationObserver>> nameToObserver = new HashMap<String, ArrayList<NotificationObserver>>();
	
	private NotificationCenter() {
	}
	
	public static NotificationCenter sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new NotificationCenter();
		}
		return sharedInstance;
	}
	
	public void addObserver(String notificationName, Object observer, String methodName) {
		if (notificationName == null || observer == null || methodName == null) return;
		ArrayList<NotificationObserver> observersForName = this.nameToObserver.get(notificationName);
		if (observersForName == null) {
			observersForName = new ArrayList<NotificationObserver>();
			this.nameToObserver.put(notificationName, observersForName);
		}
		NotificationObserver notificationObserver = new NotificationObserver(observer, methodName);
		observersForName.add(notificationObserver);
	}
	
	public void postNotification(String notificationName, Object argument) {
		ArrayList<NotificationObserver> observersForName = this.nameToObserver.get(notificationName);
		if (observersForName != null) {
			for (NotificationObserver notificationObserver : observersForName) {
				notificationObserver.call(argument);
			}
		}
	}
	
	public void removeObserver(Object observer) {
		for (ArrayList<NotificationObserver> observersForName : this.nameToObserver.values()) {
			for (int index = observersForName.size() - 1; index >= 0; ++index) {
				NotificationObserver notificationObserver = observersForName.get(index);
				if (notificationObserver.observer == observer) {
					observersForName.remove(index);
				}
			}
		}
	}
	
	private class NotificationObserver {
		
		private Object observer;
		private String methodName;
		
		private NotificationObserver(Object observer, String methodName) {
			this.observer = observer;
			this.methodName = methodName;
		}
		
		private void call(Object argument) {
			try {
				this.observer.getClass().getMethod(this.methodName, argument.getClass()).invoke(this.observer, argument);
				return;
			} catch (Exception e) {
			}
			try {
				this.observer.getClass().getMethod(this.methodName).invoke(this.observer);
			} catch (Exception e) {
			}
		}
		
	}

}