package com.wesquel.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

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
				"jdbc:mysql://127.0.0.1:3306/wesquel?user=root&password=root&zeroDateTimeBehavior=convertToNull" +
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

	public Connection getConnection() throws SQLException
	{
		return pool.getConnection();
	}

	public int getMaxIdleTime()
	{
		return pool.getMaxIdleTime();
	}

	public boolean isAutoCommitOnClose()
	{
		return pool.isAutoCommitOnClose();
	}

	public void setAutoCommitOnClose(boolean autoCommitOnClose)
	{
		pool.setAutoCommitOnClose(autoCommitOnClose);
	}

	public int getMaxStatements()
	{
		return pool.getMaxStatements();
	}

	public int getMaxPoolSize()
	{
		return pool.getMaxPoolSize();
	}

	public int getMinPoolSize()
	{
		return pool.getMinPoolSize();
	}

	public int getMaxConnectionAge()
	{
		return pool.getMaxConnectionAge();
	}

	public int getNumConnections() throws SQLException
	{
		return pool.getNumConnections();
	}

	public int getNumBusyConnections() throws SQLException
	{
		return pool.getNumBusyConnections();
	}

	public int getNumIdleConnections() throws SQLException
	{
		return pool.getNumIdleConnections();
	}

	public int getThreadPoolSize() throws SQLException
	{
		return pool.getThreadPoolSize();
	}

	public int getThreadPoolNumActiveThreads() throws SQLException
	{
		return pool.getThreadPoolNumActiveThreads();
	}

	public int getThreadPoolNumIdleThreads() throws SQLException
	{
		return pool.getThreadPoolNumIdleThreads();
	}

	public int getThreadPoolNumTasksPending() throws SQLException
	{
		return pool.getThreadPoolNumTasksPending();
	}

	public void close()
	{
		pool.close();
	}
}
