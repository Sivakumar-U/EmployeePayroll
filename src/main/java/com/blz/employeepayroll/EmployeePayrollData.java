package com.blz.employeepayroll;

import java.time.LocalDate;

public class EmployeePayrollData {

	public int id;
	public String name;
	public double salary;
	public LocalDate startDate;

	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
		super();
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.startDate = startDate;
	}

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", name=" + name + ", salary=" + salary + ", start=" + startDate + "]";
	}

}
