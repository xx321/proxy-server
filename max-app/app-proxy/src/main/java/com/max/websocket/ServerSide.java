package com.max.websocket;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.springframework.stereotype.Component;

import com.max.config.ClientSocketConfig;

@Component
@ClientEndpoint(configurator = ClientSocketConfig.class)
public class ServerSide {
	
	private final Object serverLock = new Object();
	private final Object clientLock = new Object();
	
	private boolean mainSocket = false;
	
	private Session serverSide;
	private Session clientSide;

	public boolean isMainSocket() {
		return mainSocket;
	}
	public void setMainSocket(boolean mainSocket) {
		this.mainSocket = mainSocket;
	}
	
	public Session getServerSide() {
		return serverSide;
	}
	public void setServerSide(Session serverSide) {
		this.serverSide = serverSide;
	}
	public Session getClientSide() {
		return clientSide;
	}
	public void setClientSide(Session clientSide) {
		this.clientSide = clientSide;
	}

	@OnOpen
    public void onOpen(Session session) {
        this.serverSide = session;
    }

    @OnMessage
    public void onMessage(String text) throws IOException {
    	
    	text = "server 端 - " + serverSide.getId() + text;
    	System.out.println(text);

    	if (!isMainSocket())
    		return;
    	
    	if (clientSide != null)
    		synchronized (clientLock) {
    			if (clientSide != null && clientSide.isOpen())
    				clientSide.getBasicRemote().sendText(text);
			}
    }

    @OnError
    public void onError(Throwable throwable) {
    	throwable.printStackTrace();
    	this.onClosing();
    }

    @OnClose
    public void onClosing() {
    	if (clientSide != null)
    		synchronized (clientLock) {
    			if (clientSide != null && clientSide.isOpen())
	    			try {
	    				clientSide.close();
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			} finally {
	    				clientSide = null;
	    			}
			}
    	
    	if (serverSide != null)
    		synchronized (serverLock) {
    	    	if (serverSide != null && serverSide.isOpen())
	    			try {
	    				serverSide.close();
	    				System.out.println("server 端 - " + serverSide.getId() + " close ...");
	    			} catch (IOException e) {
	    				e.printStackTrace();
	    			} finally {
	    				serverSide = null;
	    			}
    		}
    }
    
    public void sendMessage(String text) throws IOException {
    	if (serverSide != null)
    		synchronized (serverLock) {
    			if (serverSide != null && serverSide.isOpen())
    				serverSide.getBasicRemote().sendText(text);
    		}
    }
    
    public static ServerSide connect(String url) throws Exception {
        WebSocketContainer wsc = ContainerProvider.getWebSocketContainer();
        ServerSide serverSide = new ServerSide();
        wsc.connectToServer(serverSide, new URI(url));
        return serverSide;
    }
    
}
