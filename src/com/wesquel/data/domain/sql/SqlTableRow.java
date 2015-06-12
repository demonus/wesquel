package com.wesquel.data.domain.sql;

import com.google.gson.stream.JsonToken;
import com.wesquel.exceptions.InvalidActionException;
import com.wesquel.utils.KeyValuePair;
import com.wesquel.utils.LockableList;

import java.util.List;

/**
 * Created by dmitriy on 6/8/15.
 */
public class SqlTableRow
{
	private List<FieldData> valueList;

	private boolean locked = false;

	public SqlTableRow(int size)
	{
		valueList = new LockableList<>(size);
	}

	public SqlTableRow()
	{
		this(0);
	}

	public void addValue(String fieldName, Object value, JsonToken type) throws InvalidActionException
	{
		if (locked)
		{
			throw new InvalidActionException("Cannot perform action on a locked row");
		}

		valueList.add(new FieldData(fieldName, new KeyValuePair<>(value, type)));
	}

	public void deleteRow(int index)
	{
		valueList.remove(index);
	}

	public List<FieldData> getValueList()
	{
		return valueList;
	}


	public void complete()
	{
		locked = true;

		((LockableList) valueList).lock();
	}

	public boolean isLocked()
	{
		return locked;
	}

	public int size()
	{
		return valueList.size();
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("SqlTableRow: \n{");

		boolean first = true;

		for (FieldData fieldData : valueList)
		{
			if (!first)
			{
				s.append(", ");
			}
			else
			{
				first = false;
			}

			s.append("{").append(fieldData).append("}");
		}

		s.append("\n}");

		return s.toString();
	}
}

class FieldData
{
	String name;

	KeyValuePair<Object, JsonToken> value;

	public FieldData(String name, Object value, JsonToken valueType)
	{
		this(name, new KeyValuePair<Object, JsonToken>(value, valueType));
	}

	public FieldData(String name, KeyValuePair<Object, JsonToken> value)
	{
		this.name = name;

		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public KeyValuePair<Object, JsonToken> getValue()
	{
		return value;
	}

	public void setValue(KeyValuePair<Object, JsonToken> value)
	{
		this.value = value;
	}

	public void setValue(Object value, JsonToken valueType)
	{
		setValue(new KeyValuePair<Object, JsonToken>(value, valueType));
	}

	public Object getValueData()
	{
		return value.getKey();
	}

	public JsonToken getValueType()
	{
		return value.getValue();
	}

	@Override
	public String toString()
	{
		return "FieldData{" +
				"name='" + name + '\'' +
				", value=" + value +
				'}';
	}
}