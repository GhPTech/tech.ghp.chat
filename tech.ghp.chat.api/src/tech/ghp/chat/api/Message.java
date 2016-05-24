package tech.ghp.chat.api;
import org.osgi.dto.DTO;

public class Message extends DTO {
	public String from;
	public String to;
	public String text;
}