package data.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Offers a simple interface to register and post notifications based on a notification name.
 * @author Thomas Debouverie
 *
 */
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
	
	/**
	 * Adds given observer as object that will be notified when a notification with given name is posted.
	 * The NotificationCenter will try to call the given method name on the observer.
	 * @param notificationName Name of the notification to observe.
	 * @param observer Object that must be notified.
	 * @param methodName Name of the method to call on the observer.
	 */
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
	
	/**
	 * Posts a notification with the given name and notifies all observers registered for that notification name.
	 * @param notificationName Name of the notification to post.
	 * @param argument Optional argument associated with the notification.
	 */
	public void postNotification(String notificationName, Object argument) {
		ArrayList<NotificationObserver> observersForName = this.nameToObserver.get(notificationName);
		if (observersForName != null) {
			for (NotificationObserver notificationObserver : observersForName) {
				notificationObserver.call(argument);
			}
		}
	}
	
	/**
	 * Removes given observer from all notification listening.
	 * @param observer Object to remove.
	 */
	public void removeObserver(Object observer) {
		for (ArrayList<NotificationObserver> observersForName : this.nameToObserver.values()) {
			for (int index = observersForName.size() - 1; index >= 0 && !observersForName.isEmpty(); ++index) {
				NotificationObserver notificationObserver = observersForName.get(index);
				if (notificationObserver.observer == observer) {
					observersForName.remove(index);
				}
			}
		}
	}
	
	/**
	 * Wrapper for an observer object and the method that must be called when notified.
	 * @author Thomas Debouverie
	 *
	 */
	private class NotificationObserver {
		
		private Object observer;
		private String methodName;
		
		private NotificationObserver(Object observer, String methodName) {
			this.observer = observer;
			this.methodName = methodName;
		}
		
		/**
		 * Tries to call the observer method with the given argument.
		 * If the argument is null or the observer method does not support an argument of the argument's type,
		 * the observer method is called without any argument.
		 * @param argument
		 */
		private void call(Object argument) {
			if (argument == null || !this.callWithArgument(argument)) {
				this.callWithoutArgument();
			}
		}
		
		/**
		 * Tries to call the observer method with the given argument.
		 * @param argument Argument to add to the observer method call.
		 * @return True if the call with the argument succeeded, otherwise false.
		 */
		private boolean callWithArgument(Object argument) {
			try {
				this.observer.getClass().getMethod(this.methodName, argument.getClass()).invoke(this.observer, argument);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		
		/**
		 * Tries to call the observer method without any argument.
		 * @return True if the call without argument succeeded, otherwise false.
		 */
		private boolean callWithoutArgument() {
			try {
				this.observer.getClass().getMethod(this.methodName).invoke(this.observer);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		
	}

}