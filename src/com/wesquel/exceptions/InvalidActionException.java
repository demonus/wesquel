package com.wesquel.exceptions;

/**
 * Created by dmitriy on 6/8/15.
 */
public class InvalidActionException extends Exception
{
	public InvalidActionException()
	{
		super();
	}

	public InvalidActionException(String message)
	{
		super(message);
	}

	public InvalidActionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
