package com.wesquel.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;

/**
 * Created by root on 6/7/15.
 */
public class ConnectionPool
{
	private ComboPooledDataSource pool;

	private void connect() throws PropertyVetoException
	{
		pool = new ComboPooledDataSource();

		pool.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver

		pool.setJdbcUrl(
				"jdbc:mysql://127.0.0.1:3306/guest?user=root&password=root&zeroDateTimeBehavior=convertToNull" +
						"&autoReconnect=true" +
						"&useUnicode=true&characterEncoding=utf8&jdbcCompliantTruncation=false&useServerPrepStmts" +
						"=true" +
						"&allowMultiQueries=true&rollbackOnPooledClose=false");

		pool.setMinPoolSize(5);
		pool.setMaxPoolSize(50);
		pool.setAcquireIncrement(1);

		pool.setPreferredTestQuery("select 0");
		pool.setIdleConnectionTestPeriod(60);
	}


	public ConnectionPool() throws PropertyVetoException
	{
		connect();
	}

}
