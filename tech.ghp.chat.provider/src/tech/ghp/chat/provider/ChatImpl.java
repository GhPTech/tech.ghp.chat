package tech.ghp.chat.provider;

import java.util.Map;

//import org.osgi.service.cm.Configuration;
//import org.osgi.service.cm.Configuration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import osgi.enroute.dto.api.DTOs;

import tech.ghp.chat.api.Chat;
import tech.ghp.chat.api.Message;

/**
 * 
 */

//@ObjectClassDefinition
//@interface Configuration {
//  String user_name() default "osgi";
//}

@Designate(ocd=Configuration.class, factory=false)
@Component(
		name = "tech.ghp.chat", 
		property = {
			"user.name=osgi",
			"service.exported.interfaces=*"
		}
)
public class ChatImpl implements Chat{
	
	@Reference
	EventAdmin eventAdmin;
	
	@Reference
	DTOs dtos;
	
	@Override
	public boolean send(Message message) throws Exception {
		System.out.printf("%s: %s%n", message.from, message.text);
		
		Map<String,Object> map = dtos.asMap(message);
		
		Event event = new Event("tech/ghp/chat/message", map);
		eventAdmin.postEvent(event);
		
		return true;
	}


}
