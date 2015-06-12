package com.wesquel.data.domain.sql;

import com.wesquel.utils.Constants;
import com.wesquel.utils.KeyValuePair;

import java.sql.JDBCType;
import java.sql.SQLType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dmitriy on 6/8/15.
 */


public class JsonSqlValueMap
{
	private String tableName;

	private int rowInitialSize;

	private List<SqlTableRow> rows;

	private List<KeyValuePair<String, SQLType>> fields;

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

		this.tableName = tableName;
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
		if (fields == null && row != null && row.isLocked())
		{
			fields = row.getValueList().stream().map(n -> {
				return new KeyValuePair<>(n.getName(),
						Constants.jsonTokenToSQLTypeMap.getOrDefault(n.getValueType(),
								JDBCType.VARCHAR));
			}).collect(Collectors.toList());
		}
	}

	public List<KeyValuePair<String, SQLType>> getFields()
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

			for (KeyValuePair<String, SQLType> field : fields)
			{
				if (!first)
				{
					s.append(", ");
				}
				else
				{
					first = false;
				}

				s.append(field);
			}
		}

		s.append("\n}\n}");

		return s.toString();
	}
}
