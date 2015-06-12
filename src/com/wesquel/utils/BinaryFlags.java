package com.wesquel.utils;

/**
 * Created by dmitriy on 5/28/15.
 */
public class BinaryFlags
{
	private long value;

	private void setFlag(int index, boolean enabled)
	{
		if (enabled)
		{
			this.value = this.value | (1 << index);
		}
		else
		{
			this.value = this.value & ~(1 << index);
		}
	}

	private void setFlag(int index)
	{
		setFlag(index, true);
	}

	private void resetFlag(int index)
	{
		setFlag(index, false);
	}

	public void setValue(long value)
	{
		this.value = value;
	}

	public long getValue()
	{
		return value;
	}

	public boolean isFlagSet(int index)
	{
		return ((value & (1 << index)) == value);
	}
}
