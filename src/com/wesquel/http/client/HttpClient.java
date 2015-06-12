package com.wesquel.http.client;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by dmitriy on 5/29/15.
 */
public class HttpClient
{
	private static org.apache.http.client.HttpClient client;

	private HttpContext context;

	static
	{
		PoolingHttpClientConnectionManager poolingClientConnectionManager = new PoolingHttpClientConnectionManager();

		poolingClientConnectionManager.setMaxTotal(500);

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();

		clientBuilder.setConnectionManager(poolingClientConnectionManager);

		client = clientBuilder.build();
	}

	public HttpClient()
	{
		context = new BasicHttpContext();
	}

	public HttpResponse execute(HttpUriRequest httpUriRequest) throws IOException, ClientProtocolException
	{
		return client.execute(httpUriRequest, context);
	}

	public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest)
			throws IOException, ClientProtocolException
	{
		return client.execute(httpHost, httpRequest, context);
	}

	public <T> T execute(HttpUriRequest httpUriRequest,
						 ResponseHandler<? extends T> responseHandler)
			throws IOException, ClientProtocolException
	{
		return client.execute(httpUriRequest, responseHandler, context);
	}

	public <T> T execute(HttpHost httpHost, HttpRequest httpRequest,
						 ResponseHandler<? extends T> responseHandler)
			throws IOException, ClientProtocolException
	{
		return client.execute(httpHost, httpRequest, responseHandler, context);
	}
}
