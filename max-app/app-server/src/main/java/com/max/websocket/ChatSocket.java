package com.max.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.max.config.ServerSideProperties;
import com.max.config.ServerSocketConfig;

@Component
@ServerEndpoint(value = "/socket/chat", configurator = ServerSocketConfig.class)
public class ChatSocket {
	
    private final Object stateLock = new Object();
	
    private static List<Session> sessions = Collections.synchronizedList(new LinkedList<Session>());

    private static ServerSideProperties properties;
    
    @Autowired
	public void setProperties(ServerSideProperties properties) {
		ChatSocket.properties = properties;
	}

	@OnOpen
    public void onOpen(Session session) {     
    	sessions.add(session);
    	
    	System.out.println("proxy 端 - " + session.getId() + " open ...");
    	sessions.forEach(s -> {
    		System.out.println("在線 proxy 端  ： " + s.getId());
    	});
    }
    
    @OnMessage
    public void onMessage(Session sender, String message) throws IOException {
    	
    	System.out.println("proxy 端 - " + sender.getId() + " 傳來了消息 ： " + message);
    	
    	synchronized (stateLock) {
	    	for (Session recipient : sessions) {
	    		String text = properties.getServerName() + "傳來了消息 ： " + message;
    	    	if (recipient.isOpen())
    	    		recipient.getBasicRemote().sendText(text);
	    	}
    	}
    }   
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        synchronized (stateLock) {
        	sessions.remove(session);
        	
        	System.out.println("proxy 端 - " + session.getId() + " close ...");
    		sessions.forEach(s -> {
    			System.out.println("在線 proxy 端  ： " + s.getId());
    		});
        }
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
    	throwable.printStackTrace();
		CloseReason reason = new CloseReason(CloseCodes.NORMAL_CLOSURE, "");
		onClose(session, reason);
    }
    
    @Override
    protected void finalize() throws Throwable {
    }
}
