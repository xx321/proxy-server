package com.max.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.max.config.ProxySideProperties;
import com.max.config.ServerSocketConfig;

@Component
@ServerEndpoint(value = "/websocket/socket/{uri}", configurator = ServerSocketConfig.class)
public class ClientSide {
	
    private static Map<Session, List<ServerSide>> serverSideMap = new ConcurrentHashMap<>();
    
    private static ProxySideProperties properties;
    
    @Autowired
    public void setProperties(ProxySideProperties properties) {
		ClientSide.properties = properties;
	}

	@OnOpen
    public void onOpen(Session session, @PathParam("uri") String uri) {     
		String mainSocket = properties.getMainSocket() + "socket/" + uri;
    	
		List<ServerSide> serverSides = serverSideMap.get(session);
		if (serverSides == null) {
			serverSides = Collections.synchronizedList(new LinkedList<ServerSide>());
			serverSideMap.put(session, serverSides);
		}
		
		
		for(String serverUrl : properties.getSocketUrls().split(",")) {
			String url = serverUrl + "socket/" + uri;
			try {
				ServerSide serverSide = ServerSide.connect(url);
				serverSide.setMainSocket(url.equals(mainSocket));
				serverSide.setClientSide(session);
				serverSides.add(serverSide);
			} catch (Exception e) {
				onError(session, e);
				break;
			}
		}
		
		System.out.println("client 端 - " + session.getId() + " open ...");
		serverSideMap.forEach((k,v)-> {
			System.out.println("在線 用戶 " + k.getId());
			v.forEach(s -> {
				System.out.println("在線 server 端 ： " + s.getServerSide().getId());
			});
		});
    }
    
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
    	System.out.println("client 端 - " + session.getId() + " 傳來了消息 ： " + message);
    	
    	List<ServerSide> serverSides = serverSideMap.get(session);
    	if (serverSides == null) return;
    	for (ServerSide serverSide : serverSides)
    		serverSide.sendMessage(message);
    }   
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    	List<ServerSide> serverSides = serverSideMap.remove(session);
    	
    	System.out.println("client 端 - " + session.getId() + " close ...");
    	if (serverSides == null) return;
    	for (ServerSide serverSide : serverSides)
    		serverSide.onClosing();
    	
		serverSideMap.forEach((k,v)-> {
			System.out.println("在線 用戶 " + k.getId());
			v.forEach(s -> {
				System.out.println("在線 server 端 ： " + s.getServerSide().getId());
			});
		});
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
    	throwable.printStackTrace();
		CloseReason reason = new CloseReason(CloseCodes.TRY_AGAIN_LATER, "");
		onClose(session, reason);
    }
    
    @Override
    protected void finalize() throws Throwable {
    }
}
