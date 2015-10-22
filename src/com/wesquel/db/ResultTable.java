package com.wesquel.db;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.wesquel.data.domain.sql.JsonSqlValueMap;
import com.wesquel.data.domain.sql.SqlTableRow;
import com.wesquel.exceptions.InvalidActionException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by root on 6/3/15.
 */
public class ResultTable
{

	public void analyzeJSON(String alias, String json, Connection connection)
			throws IOException, InvalidActionException, SQLException
	{
		JsonReader reader = new JsonReader(new StringReader(json));

		Map<String, JsonSqlValueMap> tables = new LinkedHashMap<>();

		reader.beginObject();

		JsonSqlValueMap jsonObject = new JsonSqlValueMap(alias);

		tables.put(alias, jsonObject);

		readJSON(alias, reader, jsonObject, false, tables, "");

		jsonObject.completeActiveRow();

		for (Map.Entry<String, JsonSqlValueMap> tableEntry : tables.entrySet())
		{
			JsonSqlValueMap table = tableEntry.getValue();

			System.out.println("\n" + table.getCreateStatement() + "\n");

			table.executeSql(connection);
		}
	}

	private void readJSON(String alias, JsonReader reader, JsonSqlValueMap valueMap, boolean isArray,
						  Map<String, JsonSqlValueMap> tables, String prefix) throws
			IOException, InvalidActionException
	{
		while (reader.hasNext())
		{
			String name;

			try
			{
				name = reader.nextName();
			}
			catch (IllegalStateException ex)
			{
				name = null;
			}

			JsonToken token = reader.peek();

			if (token == JsonToken.BEGIN_OBJECT)
			{
				boolean isUnnamedObject = (name == null);

				if (isUnnamedObject)
				{
					name = alias;
				}

				reader.beginObject();

				String newPrefix;

				if (isArray && isUnnamedObject)
				{
					newPrefix = prefix;
				}
				else
				{
					newPrefix = prefix + name + "$";
				}

				readJSON(alias, reader, valueMap, false, tables, newPrefix);

				reader.endObject();

				if (isArray && isUnnamedObject)
				{
					valueMap.completeActiveRow();
				}

				readJSON(alias, reader, valueMap, isArray, tables, prefix);
			}
			else if (token == JsonToken.BEGIN_ARRAY)
			{
				reader.beginArray();

				String tableName = alias + "$" + prefix + name;

				JsonSqlValueMap newTable = new JsonSqlValueMap(tableName);

				String idFieldName = tableName + "$id";

				String idFieldValue = StringUtils.replace(UUID.randomUUID().toString(), "-", "");

				newTable.setIdField(idFieldName, idFieldValue);

				valueMap.getActiveRow().addValue(idFieldName, idFieldValue, JsonToken.STRING);

				readJSON(alias, reader, newTable, true, tables, prefix + name + "$");

				reader.endArray();

				if (tables.containsKey(tableName))
				{
					JsonSqlValueMap existingTable = tables.get(tableName);

					existingTable.merge(newTable);
				}
				else
				{
					tables.put(tableName, newTable);
				}

				readJSON(alias, reader, valueMap, isArray, tables, prefix);
			}
			else
			{
				if (valueMap != null && name != null)
				{
					SqlTableRow row = valueMap.getActiveRow();

					Object obj;

					switch (token)
					{
					case BOOLEAN:
						obj = reader.nextBoolean();
						break;

					case NUMBER:
						obj = reader.nextDouble();

						break;

					case NULL:
						obj = null;
						break;

					default:
						obj = reader.nextString();
					}

					row.addValue(prefix + name, obj, token);
				}
				else
				{
					System.out.println("valueMap = " + valueMap + "; name = " + name);
				}
			}
		}
	}


	public static void main(String[] args) throws IOException, InvalidActionException
	{

	}
}
