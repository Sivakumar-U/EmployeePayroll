package com.blz.employeepayroll;

import static org.junit.Assert.*;

import java.sql.SQLException;
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
		Assert.assertEquals(3, employeePayrollData.size());
	}

	@Test
	public void givenEmployeePayroll_WhenUpdate_ShouldSyncWithDB() throws EmployeePayrollException, SQLException {
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateRecord("Terisa", 3000000);
		boolean result = employeePayrollService.checkUpdatedRecordSyncWithDatabase("Terisa");
		Assert.assertTrue(result);
	}
}
