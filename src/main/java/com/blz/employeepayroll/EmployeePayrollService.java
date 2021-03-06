package com.blz.employeepayroll;

import java.time.LocalDate;
import java.util.List;

public class EmployeePayrollService {
	public enum IOService {
		DB_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private static EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) throws EmployeePayrollException {
		if (ioService.equals(IOService.DB_IO))
			return this.employeePayrollList = employeePayrollDBService.readData(null, null);
		return this.employeePayrollList;
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService, String start, String end)
			throws EmployeePayrollException {
		try {
			LocalDate startLocalDate = LocalDate.parse(start);
			LocalDate endLocalDate = LocalDate.parse(end);
			if (ioService.equals(IOService.DB_IO))
				return employeePayrollDBService.readData(startLocalDate, endLocalDate);
			return this.employeePayrollList;
		} catch (EmployeePayrollException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
	}

	public void updateRecord(String name, double salary) throws EmployeePayrollException {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.salary = salary;
	}

	public boolean checkUpdatedRecordSyncWithDatabase(String name) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollData = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollData.get(0).equals(getEmployeePayrollData(name));
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.endsWith(name)).findFirst()
				.orElse(null);
	}

	public int readEmployeePayrollData(String function, String gender) throws EmployeePayrollException {
		return employeePayrollDBService.readDataSumPayroll(function, gender);
	}

	public int readEmployeePayrollAvgData(String function, String gender) throws EmployeePayrollException {
		return employeePayrollDBService.readDataAvgPayroll(function, gender);
	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate start, String gender)
			throws EmployeePayrollException {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayrollDetails(name, salary, start, gender));
	}

	public void addEmployeeToDatabse(String name, double salary, LocalDate start, String gender, String department,
			boolean active) throws EmployeePayrollException {
		employeePayrollList
				.add(employeePayrollDBService.addEmployeeToDatabase(name, salary, start, gender, department, active));
	}

	public void deleteEmployeeToPayroll(String name) throws EmployeePayrollException {
		this.employeePayrollList = this.employeePayrollDBService.deleteEmployeeFromDatabase(name);
	}

	public boolean checkedRecordDeletedFromDatabase(String name) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollData = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollData.size() == 0 && getEmployeePayrollData(name) == null;
	}

}
