package com.wesquel.data.domain;

import com.wesquel.db.ResultTable;
import com.wesquel.exceptions.DuplicateKeyException;
import com.wesquel.exceptions.NotFoundException;
import com.wesquel.exceptions.ValidationException;
import com.wesquel.http.client.HttpClient;
import com.wesquel.utils.BinaryFlags;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dmitriy on 5/28/15.
 */
public class WebService
{
	private String name;

	private String url;

	private String username;

	private String password;

	BinaryFlags flags;

	Map<String, WSParameter> parameters;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public BinaryFlags getFlags()
	{
		return flags;
	}

	public void setFlags(long flags)
	{
		this.flags.setValue(flags);
	}

	public static final int FLAG_HTTP_POST = 1 << 0;

	public WebService(String name, String url, String username, String password, long flags)
	{
		this.name = name;
		this.url = url;
		this.username = username;
		this.password = password;

		this.flags = new BinaryFlags();
		this.parameters = new ConcurrentHashMap<>();
	}

	public WebService(String name, String url, String username, String password)
	{
		this(name, url, username, password, 0);
	}

	public WebService(String name, String url)
	{
		this(name, url, null, null, 0);
	}

	public void addParameter(WSParameter parameter) throws ValidationException
	{
		if (parameter == null)
		{
			throw new ValidationException("Parameter is null");
		}

		if (parameters.containsKey(parameter.getAlias()))
		{
			throw new DuplicateKeyException("Parameter with alias \"" + parameter.getAlias() + "\" already exists");
		}

		parameters.put(parameter.getAlias(), parameter);
	}

	public void setParameter(WSParameter parameter) throws ValidationException
	{
		if (parameter == null)
		{
			throw new ValidationException("Parameter is null");
		}

		if (!parameters.containsKey(parameter.getAlias()))
		{
			throw new NotFoundException("Parameter with alias \"" + parameter.getAlias() + "\" not found");
		}

		parameters.put(parameter.getAlias(), parameter);
	}

	public WSParameter getParameter(String alias) throws ValidationException
	{
		if (alias == null)
		{
			throw new ValidationException("Alias is null");
		}

		if (!parameters.containsKey(alias))
		{
			throw new NotFoundException("Parameter with alias \"" + alias + "\" not found");
		}

		return parameters.get(alias);
	}

	public HttpResponse execute(Map<String, String> values) throws ValidationException, IOException
	{
		validateValues(values);

		HttpClient httpClient = new HttpClient();

		HttpRequestBase method;

		if (flags.isFlagSet(FLAG_HTTP_POST))
		{
			method = new HttpGet(this.getUrl());
		}
		else
		{
			method = new HttpPost(this.getUrl());
		}

		if (values != null)
		{
			List<NameValuePair> params = new ArrayList<>(values.size());

			Set<WSParameter> setParams = new HashSet<>();

			for (Map.Entry<String, String> value : values.entrySet())
			{
				WSParameter parameter = parameters.get(value.getKey());

				if (parameter != null && parameter.getName() != null)
				{
					params.add(new BasicNameValuePair(parameter.getName(), value.getValue()));

					setParams.add(parameter);
				}
			}

			for (Map.Entry<String, WSParameter> entry : parameters.entrySet())
			{
				WSParameter parameter = entry.getValue();

				if (!setParams.contains(parameter) && parameter.getDefaultValue() != null)
				{
					params.add(new BasicNameValuePair(parameter.getName(), parameter.getDefaultValue()));
				}
			}

			if (method instanceof HttpGet)
			{
				String queryString = URLEncodedUtils.format(params, "UTF-8");

				method.setURI(URI.create(this.getUrl() + "?" + queryString));
			}
			else
			{
				((HttpPost) method).setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			}

		}

		return httpClient.execute(method);
	}

	public void validateValues(Map<String, String> values) throws ValidationException
	{
		for (Map.Entry<String, WSParameter> entry : parameters.entrySet())
		{
			if (entry.getValue().getFlags().isFlagSet(WSParameter.Flags.REQUIRED.getValue()) &&
					!values.containsKey(entry.getKey()) && entry.getValue().getDefaultValue() == null)
			{
				throw new ValidationException("Missing required parameter: " + entry.getKey());
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		WebService facebook = new WebService("Facebook", "https://graph.facebook.com/v2.3/me/feed");

		facebook.addParameter(new WSParameter("access_token", "access_token", "CAACEdEose0cBAMvhyHe3yJu7wbCqHboXYZB29VMLWOK1syT58BD98S5tRZCSEM4sidRfcbi1x52NocZAxfZCRVfkps46DaoRTeUuj0M5QZBUvbDHAfmFJwivbI5fpLq1GPZBrsy5UXEH4jMRoWQUgKigU2AZAEVTEOJhxZCVPvz9f9ySbvVWxeqgleBZAlWpgfXH2x1TjlC7apKgdgzl6drzh9JceCGCdLY4ZD"));

		Map<String, String> values = new HashMap<>();

		WebService weather = new WebService("weather", "http://api.openweathermap.org/data/2.5/weather");

		weather.addParameter(new WSParameter("city", "q", "New York, NY"));

		values.put("city", "94566");

		HttpResponse response = facebook.execute(values);

		String json = IOUtils.toString(response.getEntity().getContent());

		ResultTable resultTable = new ResultTable();

		resultTable.analyzeJSON("facebook", json);
	}
}
