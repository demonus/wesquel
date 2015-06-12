package com.wesquel.exceptions;

/**
 * Created by dmitriy on 5/28/15.
 */
public class NotFoundException extends ValidationException
{
	public NotFoundException(String message)
	{
		super(message);
	}

	public NotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
