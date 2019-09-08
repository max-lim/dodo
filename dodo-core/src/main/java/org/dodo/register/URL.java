package org.dodo.register;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dodo.common.utils.StringUtils;
/**
 * url
 * @author maxlim
 *
 */
public class URL {
	private String protocol;
	private String host;
	private int port;
	private String path;
	private Map<String, String> params;
	private String uniq;
	private final static Pattern PATTERN_URL = Pattern.compile("^([A-Za-z_\\-]+)://([A-Za-z0-9_\\-\\.]+):([1-9][0-9]+)/([A-Za-z0-9_\\.]+)\\??(.+)?");

	public URL() {

	}
	public URL(String protocol, String host, int port, String path, Map<String, String> params) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.path = path;
		this.params = params;
		this.uniq = this.path+"/"+this.host+":"+this.port;
	}

	/**
	 * 根据完整url字符串进行构建URL对象
	 * @param urlstring
	 * @return
	 */
	public static URL build(String urlstring) {
		URL url;
		Matcher matcher = PATTERN_URL.matcher(urlstring);
		if(matcher.find()) {
			String protocol = matcher.group(1);
			String host = matcher.group(2);
			int port = Integer.valueOf(matcher.group(3));
			String path = matcher.group(4);
			Map<String, String> params = null;
			if(matcher.groupCount() > 4 && StringUtils.isNotEmpty(matcher.group(5))) {				
				params = querystring2map(matcher.group(5));
			}
			url = new URL(protocol, host, port, path, params);
		}
		else {
			throw new IllegalArgumentException("urlstring:" + urlstring + " is not matcher pattern " + PATTERN_URL.toString());
		}
		return url;
	}
	private static Map<String, String> querystring2map(String querystring) {
		Map<String, String> map = new HashMap<>();
		String kvs[] = querystring.split("&");
		for(String kvstr: kvs) {
			String kv[] = kvstr.split("=");
			map.put(kv[0], kv[1]);
		}
		return map;
	}
	public String querystring() {
		if(params != null && ! params.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			params.forEach((k, v) -> {
				if(v != null) {					
					sb.append(k).append("=").append(v).append("&");
				}
			});
			return sb.substring(0, sb.length() - 1);
		}
		return "";
	}
	public String address() {
		return host+":"+port;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(protocol).append("://").append(host).append(":").append(port).append("/")
			.append(path).append("?").append(querystring());
		return sb.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + port;
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URL other = (URL) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (port != other.port)
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		return true;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public String getUniq() {
		return uniq;
	}
	public void setUniq(String uniq) {
		this.uniq = uniq;
	}
	
}
