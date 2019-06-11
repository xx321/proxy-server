
package com.max.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @param
 * @author liugen.wen sixvision
 * @version V1.0    1、request克隆  2、提供增加header裡面放置對象的方法
 * @ClassName:
 * @Description:
 * @date 2018/6/26 14:03
 */
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    private Map<String,String> headerMap;

    private String oriRemoteAddr;
    private String oriRemoteHost;
    private String oriServerName;
    private String oriServerPort;
    private String oriProtocol;
    private String oriContentLength;
    
    private Boolean isProxy;
    
    private boolean hasProxy(HttpServletRequest request) {
    	if (isProxy != null) return isProxy;
    	synchronized (this) {
    		if (isProxy != null) return isProxy;
            Enumeration enumeration = request.getHeaderNames();
            while(enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                if (ReqUtil.IS_PROXY.equalsIgnoreCase(name)) {
                	isProxy = Boolean.valueOf(request.getHeader(name));
                	return isProxy;
                }  
            }
            isProxy = false;
		}
        return isProxy;
    }
    
    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        headerMap = new HashMap<String,String>();
        // 提取原本的HEADER内容 -- 這裡可以指定需要留的header 參數
        if (!hasProxy(request)) {
            Enumeration enumeration = request.getHeaderNames();
            headerMap = new HashMap<String,String>();
            while(enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                headerMap.put(name, request.getHeader(name));
            }
        } else {
        	ReqUtil.setAddress(request, this);
        }
        body = HttpHelper.getBodyString(request).getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }


    public void addHeader(String name,String value) {
        headerMap.put(name, value);
    }


    @Override
    public String getHeader(String name) {
        return headerMap.get(name);
    }

    @Override
    public Enumeration getHeaderNames() {
        Set<String> set = new HashSet<String>(headerMap.keySet());
        if (!isProxy) {
            Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
            while (e.hasMoreElements()) {
                String n = e.nextElement();
                set.add(n);
            }
        }
        // create an enumeration from the set and return
        return Collections.enumeration(set);
    }

    public Map getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map headerMap) {
        this.headerMap = headerMap;
    }

	public void setRemoteAddr(String remoteAddr) {
		this.oriRemoteAddr = remoteAddr;
	}

	public void setRemoteHost(String remoteHost) {
		this.oriRemoteHost = remoteHost;
	}

	public void setServerName(String serverName) {
		this.oriServerName = serverName;
	}

	public void setServerPort(String serverPort) {
		this.oriServerPort = serverPort;
	}

	public void setProtocol(String protocol) {
		this.oriProtocol = protocol;
	}

	public void setContentLength(String contentLength) {
		this.oriContentLength = contentLength;
	}

	@Override
	public int getContentLength() {
		try {
			return isProxy ? Integer.parseInt(this.oriContentLength) : super.getContentLength();
		} catch (Exception e) {
			return super.getContentLength();
		}
	}

	@Override
	public String getProtocol() {
		return isProxy ? this.oriProtocol : super.getProtocol();
	}

	@Override
	public String getServerName() {
		return isProxy ? this.oriServerName : super.getServerName();
	}

	@Override
	public int getServerPort() {
		try {
			return isProxy ? Integer.parseInt(this.oriServerPort) : super.getServerPort();
		} catch (Exception e) {
			return super.getContentLength();
		}
	}

	@Override
	public String getRemoteHost() {
		return isProxy ? this.oriRemoteHost : super.getRemoteHost();
	}

	@Override
	public String getRemoteAddr() {
		return isProxy ? this.oriRemoteAddr : super.getRemoteAddr();
	}

}
