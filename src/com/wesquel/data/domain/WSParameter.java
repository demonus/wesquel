package com.wesquel.data.domain;

import com.wesquel.utils.BinaryFlags;

/**
 * Created by dmitriy on 5/28/15.
 */

public class WSParameter
{
	private String alias;

	private String name;

	private String defaultValue;

	private BinaryFlags flags;

	public enum Flags
	{
		REQUIRED(1 << 0);

		int value;

		Flags(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	public BinaryFlags getFlags()
	{
		return flags;
	}

	public void setFlags(long flags)
	{
		this.flags.setValue(flags);
	}

	public WSParameter(String alias, String name, String defaultValue, long flags)
	{
		this.alias = alias;
		this.name = name;
		this.defaultValue = defaultValue;
		this.flags = new BinaryFlags();
	}

	public WSParameter(String alias, String name, String defaultValue)
	{
		this(alias, name, defaultValue, 0);
	}

	public WSParameter(String alias, String name)
	{
		this(alias, name, null, 0);
	}
}
