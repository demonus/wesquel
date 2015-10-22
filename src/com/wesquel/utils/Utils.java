package com.wesquel.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by dmitriy on 6/29/15.
 */
public class Utils
{

	public static void close(Object... items) throws SQLException
	{
		for (Object obj : items)
		{
			if (obj != null)
			{
				if (obj instanceof Connection)
				{
					((Connection) obj).close();
				}

				if (obj instanceof Statement)
				{
					((Statement) obj).close();
				}

				if (obj instanceof ResultSet)
				{
					((ResultSet) obj).close();
				}
			}
		}
	}
}
