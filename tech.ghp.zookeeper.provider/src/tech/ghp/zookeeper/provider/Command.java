package tech.ghp.zookeeper.provider;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.ZooKeeper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Component;

import osgi.enroute.debug.api.Debug;

@Component(
  property = { 
    Debug.COMMAND_SCOPE + "=zk",  
    Debug.COMMAND_FUNCTION + "=zk", 
    Debug.COMMAND_FUNCTION + "=ls",
    Debug.COMMAND_FUNCTION + "=data" 
  }, 
  service = Command.class
)
public class Command {

  private ZooKeeper zk;

  public String zk() {
    return
      "zk              help\n"
    + "ls <path>       list children\n"
    + "data <path>     show data of node\n";
  }

  @Activate void activate() throws IOException {
    this.zk = new ZooKeeper("localhost:6789", 10000, null);
  }
		
  @Deactivate void deactivate() throws Exception {
    this.zk.close();;
  }
	
  public List<String> ls(String path) throws Exception {
    return zk.getChildren(path, false);
  }

  public String data(String path) throws Exception {
    byte[] data = zk.getData(path, false, null);
    return new String(data, "UTF-8");
  }
}