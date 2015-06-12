package com.wesquel.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Created by dmitriy on 6/8/15.
 */
public class LockableList<E> extends ArrayList<E>
{
	private boolean locked = false;

	public void lock()
	{
		locked = true;
	}

	@Override
	public E set(int index, E element)
	{
		if (locked)
		{
			return null;
		}

		return super.set(index, element);
	}

	@Override
	public boolean add(E e)
	{
		if (locked)
		{
			return false;
		}

		return super.add(e);
	}

	@Override
	public void add(int index, E element)
	{
		if (!locked)
		{
			super.add(index, element);
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		if (locked)
		{
			return false;
		}

		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c)
	{
		if (locked)
		{
			return false;
		}

		return super.addAll(index, c);
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex)
	{
		if (!locked)
		{
			super.removeRange(fromIndex, toIndex);
		}
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		if (locked)
		{
			return false;
		}

		return super.removeAll(c);
	}

	@Override
	public E remove(int index)
	{
		if (locked)
		{
			return null;
		}

		return super.remove(index);
	}

	@Override
	public void clear()
	{
		if (!locked)
		{
			super.clear();
		}
	}

	@Override
	public boolean remove(Object o)
	{
		if (locked)
		{
			return false;
		}

		return super.remove(o);
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		if (locked)
		{
			return false;
		}

		return super.retainAll(c);
	}

	@Override
	public boolean removeIf(Predicate<? super E> filter)
	{
		if (locked)
		{
			return false;
		}

		return super.removeIf(filter);
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator)
	{
		if (!locked)
		{
			super.replaceAll(operator);
		}
	}

	@Override
	public void sort(Comparator<? super E> c)
	{
		if (!locked)
		{
			super.sort(c);
		}
	}

	public LockableList(int initialCapacity)
	{
		super(initialCapacity);
	}

	public LockableList()
	{
		super();
	}

	public boolean isLocked()
	{
		return locked;
	}
}
