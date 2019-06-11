package com.max.config;

import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig.Configurator;
import javax.websocket.HandshakeResponse;

public class ClientSocketConfig extends Configurator {
	
    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
//		System.out.println("app-proxy ClientSocketConfig"); 
//        System.out.println(headers);
    }

    @Override
    public void afterResponse(HandshakeResponse handshakeResponse) {
//		System.out.println("app-proxy ClientSocketConfig");
//        System.out.println(handshakeResponse);
    }
}
