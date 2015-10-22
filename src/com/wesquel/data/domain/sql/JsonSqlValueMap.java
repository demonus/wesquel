package com.wesquel.data.domain.sql;

import com.google.gson.stream.JsonToken;
import com.wesquel.exceptions.InvalidActionException;
import com.wesquel.utils.Constants;
import com.wesquel.utils.KeyValuePair;
import com.wesquel.utils.Utils;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmitriy on 6/8/15.
 */


public class JsonSqlValueMap
{
	private String tableName;

	private int rowInitialSize;

	private List<SqlTableRow> rows;

	private Map<String, KeyValuePair<String, SQLType>> fields;

	private KeyValuePair<String, String> idField = null;


	public JsonSqlValueMap(String tableName)
	{
		this(0, 0, tableName);
	}

	public JsonSqlValueMap(int rowInitialSize, String tableName)
	{
		this(rowInitialSize, 0, tableName);
	}

	public JsonSqlValueMap(int rowInitialSize, int rowInitialCount, String tableName)
	{
		this.rowInitialSize = rowInitialSize;

		this.rows = new ArrayList<>(rowInitialCount);

		this.tableName = tableName;// + "_" + StringUtils.replace(UUID.randomUUID().toString(), "-", "");
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public void addRow(SqlTableRow row)
	{
		this.rows.add(row);
	}

	public SqlTableRow getActiveRow()
	{
		if (this.rows.size() == 0)
		{
			return newRow();
		}
		else
		{
			SqlTableRow row = this.rows.get(this.rows.size() - 1);

			if (row.isLocked())
			{
				return newRow();
			}
			else
			{
				return row;
			}
		}
	}

	public void completeActiveRow()
	{
		if (this.rows.size() > 0)
		{
			SqlTableRow row = rows.get(rows.size() - 1);

			if (!row.isLocked())
			{
				if (idField != null)
				{
					try
					{
						row.addValue(idField.getKey(), idField.getValue(), JsonToken.STRING);
					}
					catch (InvalidActionException e)
					{
						e.printStackTrace();
					}
				}

				row.complete();

				generateFieldsList(row);
			}
		}
	}

	public SqlTableRow newRow()
	{
		completeActiveRow();

		SqlTableRow row = new SqlTableRow(this.rowInitialSize);

		this.rows.add(row);

		return row;
	}

	public List<SqlTableRow> getRows()
	{
		return rows;
	}

	private void generateFieldsList(SqlTableRow row)
	{
		if (row != null && row.isLocked())
		{
			if (fields == null)
			{
				fields = new LinkedHashMap<>(row.size());
			}

			for (FieldData fieldData : row.getValueList())
			{
				String fieldName = fieldData.getName();

				if (fieldName != null && !fields.containsKey(fieldName))
				{
					KeyValuePair<String, SQLType> newField = new KeyValuePair<>(fieldName,
							getFieldType(fieldData.getValueType(), fieldData.getValueData()));

					fields.put(fieldData.getName(), newField);
				}
			}
		}
	}

	public Map<String, KeyValuePair<String, SQLType>> getFields()
	{
		if (fields == null)
		{
			int size = rows.size();

			if (size > 0)
			{
				generateFieldsList(rows.get(size - 1));
			}
		}

		return fields;
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("JsonSqlValueMap{tableName='").append(tableName).append("', \nrows: \n{");

		boolean first = true;

		for (SqlTableRow row : rows)
		{
			if (!first)
			{
				s.append(", ");
			}
			else
			{
				first = false;
			}

			s.append(row);
		}

		s.append("\n},\n fields:\n{");

		if (fields == null)
		{
			s.append("<empty>");
		}
		else
		{
			first = true;

			for (Map.Entry<String, KeyValuePair<String, SQLType>> field : fields.entrySet())
			{
				if (!first)
				{
					s.append(", ");
				}
				else
				{
					first = false;
				}

				s.append(field.getValue());
			}
		}

		s.append("\n}\n}");

		return s.toString();
	}


	public String getCreateStatement()
	{
		if (fields != null)
		{
			StringBuilder sql = (new StringBuilder("create table ")).append(tableName).append(" (");

			boolean first = true;

			for (Map.Entry<String, KeyValuePair<String, SQLType>> field : fields.entrySet())
			{
				if (!first)
				{
					sql.append(", ");
				}
				else
				{
					first = false;
				}

				sql.append(field.getKey()).append(" ").append(field.getValue().getValue().getName());

				if (field.getValue().getValue() == JDBCType.VARCHAR)
				{
					sql.append("(1000)");
				}
			}

			sql.append(")");

			return sql.toString();
		}
		else
		{
			return null;
		}
	}

	public void executeSql(Connection connection) throws SQLException
	{
		if (fields != null)
		{
			PreparedStatement pstInsert = null;

			PreparedStatement pstCreate = null;

			try
			{
				pstCreate = connection.prepareStatement(getCreateStatement());

				pstCreate.executeUpdate();
			}
			finally
			{
				Utils.close(pstCreate);
			}

			for (SqlTableRow row : rows)
			{

				StringBuilder insert = (new StringBuilder("insert into ")).append(tableName).append("(");

				StringBuilder values = new StringBuilder();

				boolean first = true;

				for (FieldData fieldData : row.getValueList())
				{
					if (!first)
					{
						insert.append(", ");

						values.append(", ");
					}
					else
					{
						first = false;
					}

					insert.append(fieldData.getName());

					values.append("?");
				}

				insert.append(") values (").append(values).append(")");

				try
				{
					pstInsert = connection.prepareStatement(insert.toString());

					for (int i = 0; i < row.getValueList().size(); i++)
					{
						FieldData fieldData = row.getValueList().get(i);

						KeyValuePair<String, SQLType> fieldDefinition = getFields().get(fieldData.getName());

						pstInsert.setObject(i + 1, fieldData.getValueData(),
								fieldDefinition.getValue().getVendorTypeNumber());
					}

					pstInsert.executeUpdate();
				}
				finally
				{
					Utils.close(pstInsert);
				}
			}
		}
	}

	public void setIdField(String idFieldName, String idFieldValue)
	{
		this.idField = new KeyValuePair<>(idFieldName, idFieldValue);
	}

	public String getIdFieldName()
	{
		if (idField != null)
		{
			return idField.getKey();
		}
		else
		{
			return null;
		}
	}

	public String getIdFieldValue()
	{
		if (idField != null)
		{
			return idField.getValue();
		}
		else
		{
			return null;
		}
	}

	public KeyValuePair<String, String> getIdField()
	{
		return idField;
	}


	public void merge(JsonSqlValueMap source) throws InvalidActionException
	{
		if (source == null)
		{
			throw new NullPointerException("Source object is NULL!");
		}
		else
		{
			if (source.getRows().size() > 0)
			{
				SqlTableRow row = source.getRows().get(source.getRows().size() - 1);

				if (!row.isLocked())
				{
					throw new InvalidActionException("Source is not completed!");
				}

				if (rows.size() > 0 && !rows.get(rows.size() - 1).isLocked())
				{
					throw new InvalidActionException("Target is not completed!");
				}

				if (fields == null)
				{
					generateFieldsList(row);
				}
				else
				{
					for (FieldData fieldData : row.getValueList())
					{
						if (!fields.containsKey(fieldData.getName()))
						{
							KeyValuePair<String, SQLType> newField = new KeyValuePair<>(fieldData.getName(),
									getFieldType(fieldData.getValueType(), fieldData.getValueData()));

							fields.put(fieldData.getName(), newField);
						}
						else
						{
							SQLType newFieldType = getFieldType(fieldData.getValueType(), fieldData.getValueData());

							KeyValuePair<String, SQLType> existingField = fields.get(fieldData.getName());

							if (!existingField.getValue().equals(newFieldType))
							{
								SQLType targetType = JDBCType.VARCHAR;

								if (existingField.getValue().equals(JDBCType.INTEGER) &&
										(newFieldType.equals(JDBCType.DOUBLE)))
								{
									targetType = newFieldType;
								}
								else if (existingField.getValue().equals(JDBCType.NULL))
								{
									targetType = newFieldType;
								}

								existingField.setValue(targetType);
							}
						}
					}
				}

				rows.addAll(source.rows);
			}
		}
	}

	private SQLType getFieldType(JsonToken jsonToken, Object fieldData)
	{
		return (jsonToken == JsonToken.NUMBER &&
				((Double) fieldData) % 1 !=
						0) ? JDBCType.DOUBLE : Constants.jsonTokenToSQLTypeMap
				.getOrDefault(jsonToken,
						JDBCType.VARCHAR);
	}
}
