package com.blz.employeepayroll;

public class EmployeePayrollException extends Exception {
	enum ExceptionType {
		DATABASE_EXCEPTION, No_SUCH_CLASS, CONNECTION_FAILED, RESOURCES_NOT_CLOSED_EXCEPTION, COMMIT_FAILED
	}

	public ExceptionType type;

	public EmployeePayrollException(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}

}
