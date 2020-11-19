package com.blz.employeepayroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String username = "root";
		String password = "Amigos@1";
		Connection connection;
		System.out.println("Connecting to database:" + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, username, password);
		System.out.println("Connection is successful:" + connection);
		return connection;
	}

	public List<EmployeePayrollData> readData() {
		String query = "SELECT * FROM employee_payroll; ";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query);
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate start = result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, start));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return employeePayrollList;
	}

}
