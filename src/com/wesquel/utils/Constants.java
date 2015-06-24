package com.wesquel.utils;

import com.google.gson.stream.JsonToken;

import java.sql.JDBCType;
import java.sql.SQLType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitriy on 6/8/15.
 */
public class Constants
{

	public static Map<JsonToken, SQLType> jsonTokenToSQLTypeMap = new HashMap<JsonToken, SQLType>()
	{{
			this.put(JsonToken.STRING, JDBCType.VARCHAR);
			this.put(JsonToken.NUMBER, JDBCType.INTEGER);
			this.put(JsonToken.BOOLEAN, JDBCType.BOOLEAN);
			this.put(JsonToken.NULL, JDBCType.NULL);
		}};


}
