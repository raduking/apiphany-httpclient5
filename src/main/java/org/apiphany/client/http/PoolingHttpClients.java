package org.apiphany.client.http;

import java.util.function.Consumer;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.apiphany.client.ClientProperties;
import org.morphix.lang.function.Consumers;

/**
 * Interface containing utility methods for creating/configuring pooling HTTP clients.
 *
 * @author Radu Sebastian LAZIN
 */
public interface PoolingHttpClients {

	/**
	 * Returns a configured HTTP client based on the given client properties. The caller is responsible for closing the
	 * returned HTTP client.
	 *
	 * @param clientProperties HTTP client properties
	 * @return HTTP client
	 */
	static CloseableHttpClient createClient(final ClientProperties clientProperties) {
		return createClient(clientProperties, noCustomizer());
	}

	/**
	 * Returns a configured HTTP client based on the given client properties. The caller is responsible for closing the
	 * returned HTTP client.
	 *
	 * @param clientProperties HTTP client properties
	 * @param connectionManagerBuilderCustomizer builder customizer
	 * @return HTTP client
	 */
	static CloseableHttpClient createClient(
			final ClientProperties clientProperties,
			final Consumer<PoolingHttpClientConnectionManagerBuilder> connectionManagerBuilderCustomizer) {
		return createClient(clientProperties, connectionManagerBuilderCustomizer, noCustomizer(), noCustomizer());
	}

	/**
	 * Returns a configured HTTP client based on the given client properties. The caller is responsible for closing the
	 * returned HTTP client.
	 *
	 * @param clientProperties HTTP client properties
	 * @param connectionManagerBuilderCustomizer builder customizer
	 * @param connectionManagerCustomizer connection manager customizer
	 * @return HTTP client
	 */
	@SuppressWarnings("resource")
	static CloseableHttpClient createClient(
			final ClientProperties clientProperties,
			final Consumer<PoolingHttpClientConnectionManagerBuilder> connectionManagerBuilderCustomizer,
			final Consumer<PoolingHttpClientConnectionManager> connectionManagerCustomizer,
			final Consumer<HttpClientBuilder> httpClientBuilderCustomizer) {
		PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = createConnectionManagerBuilder(clientProperties);
		connectionManagerBuilderCustomizer.accept(connectionManagerBuilder);

		PoolingHttpClientConnectionManager connectionManager = connectionManagerBuilder.build();
		connectionManagerCustomizer.accept(connectionManager);

		RequestConfig requestConfig = createRequestConfig(clientProperties);

		HttpClientBuilder httpClientBuilder = HttpClients.custom()
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig);
		httpClientBuilderCustomizer.accept(httpClientBuilder);

		return httpClientBuilder.build();
	}

	/**
	 * Returns a pulling HTTP client connection manager builder configured with the given client properties.
	 *
	 * @param clientProperties HTTP client properties
	 * @return a pulling HTTP client connection manager
	 */
	static PoolingHttpClientConnectionManagerBuilder createConnectionManagerBuilder(final ClientProperties clientProperties) {
		ClientProperties.Timeout timeout = clientProperties.getTimeout();
		ClientProperties.Connection connections = clientProperties.getConnection();

		return PoolingHttpClientConnectionManagerBuilder.create()
				.setDefaultSocketConfig(SocketConfig.custom()
						.setSoTimeout(Timeout.of(timeout.getSocket()))
						.build())
				.setDefaultConnectionConfig(ConnectionConfig.custom()
						.setConnectTimeout(Timeout.of(timeout.getConnect()))
						.setSocketTimeout(Timeout.of(timeout.getSocket()))
						.build())
				.setMaxConnPerRoute(connections.getMaxPerRoute())
				.setMaxConnTotal(connections.getMaxTotal());
	}

	/**
	 * Creates the {@link RequestConfig} based on the client properties and the specified Apache HTTP Client 5 properties.
	 *
	 * @param clientProperties generic client properties
	 * @return returns the request configuration object
	 */
	static RequestConfig createRequestConfig(final ClientProperties clientProperties) {
		ClientProperties.Timeout timeout = clientProperties.getTimeout();

		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.of(timeout.getConnectionRequest()));

		ApacheHC5Properties apacheHC5Properties = clientProperties.getCustomProperties(ApacheHC5Properties.class);
		if (null == apacheHC5Properties) {
			return requestConfigBuilder.build();
		}
		ApacheHC5Properties.Request request = apacheHC5Properties.getRequest();

		return requestConfigBuilder
				.setProtocolUpgradeEnabled(request.isProtocolUpgradeEnabled())
				.build();
	}

	/**
	 * To be used in conjunction with {@link #createClient(ClientProperties, Consumer, Consumer, Consumer)} when no
	 * customization is necessary for one of the parameters.
	 *
	 * @param <T> customizer type
	 *
	 * @return empty customizer
	 */
	static <T> Consumer<T> noCustomizer() {
		return Consumers.noConsumer();
	}
}
