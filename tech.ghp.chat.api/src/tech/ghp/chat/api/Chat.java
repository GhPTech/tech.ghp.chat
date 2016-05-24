package tech.ghp.chat.api;

/**
 * 
 */

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface Chat {
	
	/**
	 * 
	 */
	String USER_NAME = "user.name";

	boolean send(Message message) throws Exception;
	
}
