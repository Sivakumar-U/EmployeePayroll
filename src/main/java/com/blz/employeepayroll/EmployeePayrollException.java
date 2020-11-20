package com.blz.employeepayroll;

public class EmployeePayrollException extends Exception {
	enum ExceptionType {
		DATABASE_EXCEPTION, No_SUCH_CLASS
	}

	public ExceptionType type;

	public EmployeePayrollException(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}

}
