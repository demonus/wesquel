package com.wesquel.exceptions;

/**
 * Created by dmitriy on 5/28/15.
 */
public class DuplicateKeyException extends ValidationException
{
	public DuplicateKeyException(String message)
	{
		super(message);
	}

	public DuplicateKeyException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DuplicateKeyException(String message, Throwable cause, boolean enableSuppression, boolean
			writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
