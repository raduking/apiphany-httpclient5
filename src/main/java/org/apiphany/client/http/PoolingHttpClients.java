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

		ClientProperties.Timeout timeout = clientProperties.getTimeout();
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(Timeout.ofMilliseconds(timeout.getConnectionRequestTimeout()))
				.build();
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
						.setSoTimeout(Timeout.ofMilliseconds(timeout.getSocketTimeout()))
						.build())
				.setDefaultConnectionConfig(ConnectionConfig.custom()
						.setConnectTimeout(Timeout.ofMilliseconds(timeout.getConnectTimeout()))
						.setSocketTimeout(Timeout.ofMilliseconds(timeout.getSocketTimeout()))
						.build())
				.setMaxConnPerRoute(connections.getMaxPerRoute())
				.setMaxConnTotal(connections.getMaxTotal());
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
