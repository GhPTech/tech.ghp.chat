package tech.ghp.chat.application;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.eventadminserversentevents.capabilities.RequireEventAdminServerSentEventsWebResource;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

import tech.ghp.chat.api.Chat;
import tech.ghp.chat.api.Message;

@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@RequireEventAdminServerSentEventsWebResource
@Component(
		name="tech.ghp.chat.provider"
		)
public class ChatApplication implements REST {

	/*
	public String getUpper(String string) {
		return string.toUpperCase();
	}
	*/
	
	public static String MESSAGE_TOPIC = "tech/ghp/chat/message";
	public static String USERS_TOPIC = "tech/ghp/chat/users";
	
	@Reference
	EventAdmin  eventAdmin;
	
	private String localUser;
	
	private void usersChanged(String userName) {
	    Event event = new Event(USERS_TOPIC, Collections.singletonMap(Chat.USER_NAME, userName));
	    eventAdmin.postEvent(event);
	  }
	
	private final ConcurrentHashMap<String, Chat> users = new ConcurrentHashMap<>();
	  
	  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	  void addChat(Chat chat, Map<String, Object> map) {
	    String userName = (String) map.get(Chat.USER_NAME);
	    if (users.put(userName, chat) == null) {
	      if ( isLocal(map))
	        localUser = userName;
	      usersChanged(userName);
	    }
	  }
	  
	  void removeChat(Chat chat, Map<String, Object> map) {
	    String userName = (String) map.get(Chat.USER_NAME);
	    Chat remove = users.remove(userName);
	    if (remove != null)
	      usersChanged(userName);
	  }
	  
	  private boolean isLocal(Map<String,Object> map) {
		 return map.containsKey("service.exported.interfaces");
	  }
	  
	  public Collection<String> getUsers() {
	    return users.keySet();
	  }
	  
	  public boolean putMessage( Message message ) throws Exception {
	    Chat c = users.get(message.to);
	    if ( c == null)
	      return false;
	    message.from = localUser;
	    return c.send(message);
	  }

}
