package org.apiphany.client.http;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apiphany.json.JsonBuilder;

/**
 * Apache HTTP Client 5 properties.
 *
 * @author Radu Sebastian LAZIN
 */
public class ApacheHC5Properties {

	/**
	 * The root property prefix for Apache HTTP Client 5 configuration.
	 */
	public static final String ROOT = "http-client-5";

	/**
	 * Specific Apache HTTP Client 5 request properties.
	 */
	private Request request = new Request();

	/**
	 * @see #toString()
	 */
	@Override
	public String toString() {
		return JsonBuilder.toJson(this);
	}

	/**
	 * Returns the request properties.
	 *
	 * @return the request properties
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * Sets the request properties.
	 *
	 * @param request properties to set
	 */
	public void setRequest(final Request request) {
		this.request = request;
	}

	/**
	 * Apache HTTP Client 5 Request properties, the defaults will be populated from {@link RequestConfig#DEFAULT}.
	 *
	 * @author Radu Sebastian LAZIN
	 */
	public static class Request {

		private boolean protocolUpgradeEnabled = RequestConfig.DEFAULT.isProtocolUpgradeEnabled();

		private String protocolVersion = HttpVersion.DEFAULT.getProtocol();

		@Override
		public String toString() {
			return JsonBuilder.toJson(this);
		}

		public boolean isProtocolUpgradeEnabled() {
			return protocolUpgradeEnabled;
		}

		public void setProtocolUpgradeEnabled(final boolean protocolUpgradeEnabled) {
			this.protocolUpgradeEnabled = protocolUpgradeEnabled;
		}

		public String getProtocolVersion() {
			return protocolVersion;
		}

		public ProtocolVersion getHttpProtocolVersion() {
			try {
				return ProtocolVersion.parse(protocolVersion);
			} catch (ParseException pe) {
				return HttpVersion.DEFAULT;
			}
		}

		public void setProtocolVersion(final String protocolVersion) {
			this.protocolVersion = protocolVersion;
		}

	}

}
