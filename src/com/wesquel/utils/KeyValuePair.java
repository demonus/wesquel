package com.wesquel.utils;

/**
 * Created by dmitriy on 6/8/15.
 */
public class KeyValuePair<K, V>
{
	private K key;

	private V value;

	public KeyValuePair()
	{
	}

	public KeyValuePair(K key, V value)
	{
		this.key = key;

		this.value = value;
	}

	public K getKey()
	{
		return key;
	}

	public void setKey(K key)
	{
		this.key = key;
	}

	public V getValue()
	{
		return value;
	}

	public void setValue(V value)
	{
		this.value = value;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		KeyValuePair<?, ?> that = (KeyValuePair<?, ?>) o;

		if (key != null ? !key.equals(that.key) : that.key != null)
		{
			return false;
		}
		return !(value != null ? !value.equals(that.value) : that.value != null);

	}

	@Override
	public int hashCode()
	{
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "KeyValuePair{" +
				"key=" + key +
				", value=" + value +
				'}';
	}
}
