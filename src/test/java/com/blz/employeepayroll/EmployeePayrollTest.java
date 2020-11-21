package com.blz.employeepayroll;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blz.employeepayroll.EmployeePayrollService.IOService;

public class EmployeePayrollTest {

	private static EmployeePayrollService employeePayrollService;

	@BeforeClass
	public static void createcensusAnalyser() {
		employeePayrollService = new EmployeePayrollService();
	}

	@Test
	public void givenEmployeePayroll_WhenRetrieved_ShouldMatchEmployeeCount() throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(4, employeePayrollData.size());
	}

	@Test
	public void givenEmployeePayroll_WhenUpdate_ShouldSyncWithDB() throws EmployeePayrollException, SQLException {
		employeePayrollService.updateRecord("Terisa", 3000000);
		boolean result = employeePayrollService.checkUpdatedRecordSyncWithDatabase("Terisa");
		Assert.assertTrue(result);
	}

	@Test
	public void givenEmployeePayroll_WhenRetrieved_ShouldMatchEmployeeCountInGivenRange()
			throws EmployeePayrollException, SQLException {
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO,
				"2018-01-03", "2020-05-21");
		Assert.assertEquals(3, employeePayrollData.size());
	}

	@Test
	public void givenEmployeePayrollData_ShouldReturnTotalOfMaleEmployeeSalary() throws EmployeePayrollException {
		Assert.assertEquals(8000000, employeePayrollService.readEmployeePayrollData("Sum", "M"));
	}

	@Test
	public void givenEmployeePayrollData_ShouldReturnTotalOfFemaleEmployeeSalary() throws EmployeePayrollException {
		Assert.assertEquals(3000000, employeePayrollService.readEmployeePayrollData("Sum", "F"));
	}

	@Test
	public void givenEmployeePayrollData_ShouldReturnAvgOfMaleEmployeeSalary() throws EmployeePayrollException {
		Assert.assertEquals(4000000, employeePayrollService.readEmployeePayrollAvgData("Avg", "M"));
	}

	@Test
	public void givenEmployeePayrollData_ShouldReturnAvgOfFemaleEmployeeSalary() throws EmployeePayrollException {
		Assert.assertEquals(3000000, employeePayrollService.readEmployeePayrollAvgData("Avg", "F"));
	}

	@Test
	public void givenEmployeePayroll_WhenAddNewRecord_ShouldSyncWithDB() throws EmployeePayrollException, SQLException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Allen", 9000000.00, LocalDate.now(), "M");
		boolean result = employeePayrollService.checkUpdatedRecordSyncWithDatabase("Allen");
		Assert.assertTrue(result);
	}
}
