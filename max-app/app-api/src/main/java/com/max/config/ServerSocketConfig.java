package com.max.config;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

public class ServerSocketConfig extends Configurator {
	
	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		
//		System.out.println("app-server");
//		System.out.println(request.getHeaders().toString());
//		System.out.println(request.getParameterMap().toString());
		
	}
}
