package org.apiphany.client.http;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.util.Timeout;
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
	 * Specific Apache HTTP Client 5 connection properties.
	 */
	private Connection connection = new Connection();

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
	 * Returns the connection properties.
	 *
	 * @return the connection properties
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Sets the connection properties.
	 *
	 * @param connection properties to set
	 */
	public void setConnection(final Connection connection) {
		this.connection = connection;
	}

	/**
	 * Client connection and pooling properties.
	 *
	 * @author Radu Sebastian LAZIN
	 */
	public static class Connection {

		/**
		 * Default time to live: 30 seconds.
		 */
		public static final Timeout DEFAULT_TIME_TO_LIVE = Timeout.ofSeconds(30);

		/**
		 * Default maximum total connections per pool.
		 */
		public static final int DEFAULT_MAX_TOTAL = 100;

		/**
		 * Default maximum per route connections.
		 */
		public static final int DEFAULT_MAX_PER_ROUTE = 100;

		/**
		 * Time to live.
		 */
		private Timeout timeToLive = DEFAULT_TIME_TO_LIVE;

		/**
		 * Maximum total connections.
		 */
		private int maxTotal = DEFAULT_MAX_TOTAL;

		/**
		 * Maximum per route connections.
		 */
		private int maxPerRoute = DEFAULT_MAX_PER_ROUTE;

		/**
		 * Default constructor.
		 */
		protected Connection() {
			// empty
		}

		/**
		 * Returns a JSON representation of this {@link Connection} object.
		 *
		 * @return a JSON string representing this object.
		 */
		@Override
		public String toString() {
			return JsonBuilder.toJson(this);
		}

		/**
		 * Returns the maximum per route connections.
		 *
		 * @return the maximum per route connections.
		 */
		public int getMaxPerRoute() {
			return maxPerRoute;
		}

		/**
		 * Sets the maximum per route connections.
		 *
		 * @param maxPerRoute the maximum per route connections to set.
		 */
		public void setMaxPerRoute(final int maxPerRoute) {
			this.maxPerRoute = maxPerRoute;
		}

		/**
		 * Returns the maximum total connections.
		 *
		 * @return the maximum total connections.
		 */
		public int getMaxTotal() {
			return maxTotal;
		}

		/**
		 * Sets the maximum total connections.
		 *
		 * @param maxTotal the maximum total connections to set.
		 */
		public void setMaxTotal(final int maxTotal) {
			this.maxTotal = maxTotal;
		}

		/**
		 * Returns the time to live timeout.
		 *
		 * @return the time to live timeout.
		 */
		public Timeout getTimeToLive() {
			return timeToLive;
		}

		/**
		 * Sets the time to live timeout.
		 *
		 * @param timeToLive time to live to set.
		 */
		public void setTimeToLive(Timeout timeToLive) {
			this.timeToLive = timeToLive;
		}
	}

	/**
	 * Apache HTTP Client 5 Request properties, the defaults will be populated from {@link RequestConfig#DEFAULT}.
	 *
	 * @author Radu Sebastian LAZIN
	 */
	public static class Request {

		/**
		 * Flag indicating whether protocol upgrade is enabled. Defaults to the value from {@link RequestConfig#DEFAULT}.
		 */
		private boolean protocolUpgradeEnabled = RequestConfig.DEFAULT.isProtocolUpgradeEnabled();

		/**
		 * The HTTP protocol version to use. Defaults to the value from {@link HttpVersion#DEFAULT}.
		 */
		private String protocolVersion = HttpVersion.DEFAULT.getProtocol();

		/**
		 * Returns a JSON representation of this Request object.
		 *
		 * @return JSON string representation of this object
		 */
		@Override
		public String toString() {
			return JsonBuilder.toJson(this);
		}

		/**
		 * Gets whether protocol upgrade is enabled.
		 *
		 * @return true if protocol upgrade is enabled, false otherwise
		 */
		public boolean isProtocolUpgradeEnabled() {
			return protocolUpgradeEnabled;
		}

		/**
		 * Sets whether protocol upgrade should be enabled.
		 *
		 * @param protocolUpgradeEnabled true to enable protocol upgrade, false to disable
		 */
		public void setProtocolUpgradeEnabled(final boolean protocolUpgradeEnabled) {
			this.protocolUpgradeEnabled = protocolUpgradeEnabled;
		}

		/**
		 * Gets the protocol version as a string.
		 *
		 * @return the protocol version string
		 */
		public String getProtocolVersion() {
			return protocolVersion;
		}

		/**
		 * Gets the protocol version as a {@link ProtocolVersion} object. If parsing fails, returns the default HTTP version.
		 *
		 * @return the protocol version as ProtocolVersion object
		 */
		public ProtocolVersion getHttpProtocolVersion() {
			try {
				return ProtocolVersion.parse(protocolVersion);
			} catch (ParseException pe) {
				return HttpVersion.DEFAULT;
			}
		}

		/**
		 * Sets the protocol version.
		 *
		 * @param protocolVersion the protocol version string to set
		 */
		public void setProtocolVersion(final String protocolVersion) {
			this.protocolVersion = protocolVersion;
		}
	}
}
