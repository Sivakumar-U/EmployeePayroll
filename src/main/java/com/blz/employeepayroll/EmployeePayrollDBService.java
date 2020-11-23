package com.blz.employeepayroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeePayrollDBService {

	private PreparedStatement employeePayrollPreparedStatement;
	private static EmployeePayrollDBService employeePayrollDBService;

	private EmployeePayrollDBService() {

	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

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

	public List<EmployeePayrollData> readData(LocalDate start, LocalDate end) throws EmployeePayrollException {
		String query = null;
		if (start != null)
			query = String.format("select * from employee_payroll WHERE Start between '%s' and '%s';", start, end);
		if (start == null)
			query = "select * from employee_payroll";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query);
			employeePayrollList = this.getEmployeePayrollData(result);
		} catch (SQLException ex) {
			throw new EmployeePayrollException(ex.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return employeePayrollList;
	}

	public int updateEmployeeData(String name, double salary) throws EmployeePayrollException {
		return this.updateEmployeeDataUsingStatement(name, salary);
	}

	private int updateEmployeeDataUsingStatement(String name, double salary) throws EmployeePayrollException {
		String query = String.format("update employee_payroll set salary= %.2f where name='%s';", salary, name);
		try (Connection connection = this.getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			return preparedStatement.executeUpdate(query);
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
		return 0;
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollData = null;
		if (this.employeePayrollPreparedStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollPreparedStatement.setString(1, name);
			ResultSet resultSet = employeePayrollPreparedStatement.executeQuery();
			employeePayrollData = this.getEmployeePayrollData(resultSet);
		} catch (SQLException ex) {
			throw new EmployeePayrollException(ex.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return employeePayrollData;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollData = new ArrayList();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("ID");
				String name = resultSet.getString("Name");
				double salary = resultSet.getDouble("Salary");
				LocalDate startDate = resultSet.getDate("Start").toLocalDate();
				employeePayrollData.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		} catch (SQLException ex) {
			throw new EmployeePayrollException(ex.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return employeePayrollData;
	}

	private void prepareStatementForEmployeeData() throws EmployeePayrollException {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_Payroll WHERE Name = ?";
			employeePayrollPreparedStatement = connection.prepareStatement(sql);
		} catch (SQLException ex) {
			throw new EmployeePayrollException(ex.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
	}

	public int readDataSumPayroll(String total, String gender) throws EmployeePayrollException {
		int sum = 0;
		String query = String.format("select %s(Salary) from employee_payroll where gender = '%s' group by gender;",
				total, gender);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.next();
			sum = resultSet.getInt(1);
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return sum;
	}

	public int readDataAvgPayroll(String avg, String gender) throws EmployeePayrollException {
		int avgTotal = 0;
		String query = String.format("select %s(Salary) from employee_payroll where gender = '%s' group by gender;",
				avg, gender);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.next();
			avgTotal = resultSet.getInt(1);
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return avgTotal;
	}

	public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate start, String gender)
			throws EmployeePayrollException {
		int id = -1;
		EmployeePayrollData employeePayrollData = null;
		String query = String.format(
				"insert into employee_payroll(Name, Salary, Start, Gender) values ('%s', '%s', '%s', '%s')", name,
				salary, start, gender);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(query, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					id = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(id, name, salary, start);
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return employeePayrollData;
	}

	public EmployeePayrollData addEmployeeToPayrollDetails(String name, double salary, LocalDate start, String gender)
			throws EmployeePayrollException {
		int id = -1;
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException sqlException) {
			throw new EmployeePayrollException(sqlException.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		try (Statement statement = connection.createStatement()) {
			String query = String.format(
					"insert into employee_payroll(Name, Salary, Start, Gender) values ('%s', '%s', '%s', '%s')", name,
					salary, start, gender);
			int rowAffected = statement.executeUpdate(query, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					id = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(id, name, salary, start);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
				throw new EmployeePayrollException(ex.getMessage(),
						EmployeePayrollException.ExceptionType.CONNECTION_FAILED);
			}
		}
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String query = String.format(
					"insert into payrolldetails(Emp_ID, Basic_Pay, Deductions, Taxable_Pay, Tax, Net_Pay) values ('%s', '%s', '%s', '%s', '%s', '%s')",
					id, salary, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(query);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayrollData(id, name, salary, start);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
				throw new EmployeePayrollException(ex.getMessage(),
						EmployeePayrollException.ExceptionType.CONNECTION_FAILED);
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(), EmployeePayrollException.ExceptionType.COMMIT_FAILED);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					throw new EmployeePayrollException(e.getMessage(),
							EmployeePayrollException.ExceptionType.RESOURCES_NOT_CLOSED_EXCEPTION);
				}
		}
		return employeePayrollData;
	}

	public EmployeePayrollData addEmployeeToDatabase(String name, double salary, LocalDate start, String gender,
			String department, boolean is_active) throws EmployeePayrollException {
		int id = -1;
		Connection connection = null;
		EmployeePayrollData employeePayrollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException sqlException) {
			throw new EmployeePayrollException(sqlException.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		try (Statement statement = connection.createStatement()) {
			String query = String.format(
					"insert into employee_payroll(Name, Salary, Start, Gender, Department) values ('%s', '%s', '%s', '%s', '%s')",
					name, salary, start, gender, department);
			int rowAffected = statement.executeUpdate(query, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					id = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(id, name, salary, start, department);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EmployeePayrollException(e1.getMessage(),
						EmployeePayrollException.ExceptionType.CONNECTION_FAILED);
			}
		}
		try (Statement statement = connection.createStatement()) {
			List<String> departmentList = new ArrayList<>(Arrays.asList("sales", "marketing"));
			String query = String.format("insert into department(emp_id,dept_name) values ('%s', '%s')", id,
					department);
			int rowAffected = statement.executeUpdate(query, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					id = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(id, name, salary, start, department);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EmployeePayrollException(e1.getMessage(),
						EmployeePayrollException.ExceptionType.CONNECTION_FAILED);
			}
		}
		try (Statement statement = connection.createStatement()) {
			List<String> departmentList = new ArrayList<>(Arrays.asList("sales", "marketing"));
			String query = String.format("insert into department(emp_id,dept_name) values ('%s', '%s')", id,
					department);
			int rowAffected = statement.executeUpdate(query, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					id = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(id, name, salary, start, department);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EmployeePayrollException(e1.getMessage(),
						EmployeePayrollException.ExceptionType.CONNECTION_FAILED);
			}
		}
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String query = String.format(
					"insert into payrolldetails(Emp_ID, Basic_Pay, Deductions, Taxable_Pay, Tax, Net_Pay) values ('%s', '%s', '%s', '%s', '%s', '%s')",
					id, salary, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(query);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayrollData(id, name, salary, start, department);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EmployeePayrollException(e1.getMessage(),
						EmployeePayrollException.ExceptionType.CONNECTION_FAILED);
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(), EmployeePayrollException.ExceptionType.COMMIT_FAILED);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					throw new EmployeePayrollException(e.getMessage(),
							EmployeePayrollException.ExceptionType.RESOURCES_NOT_CLOSED_EXCEPTION);
				}
		}
		return employeePayrollData;
	}

	public List<EmployeePayrollData> deleteEmployeeFromDatabase(String name) throws EmployeePayrollException {
		String query = String.format("update employee_payroll set is_active = false where name = '%s';", name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
			return this.readData();
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.CONNECTION_FAILED);
		}
	}

	private List<EmployeePayrollData> readData() throws EmployeePayrollException {
		String query;
		query = "select * from employee_payroll";
		return getEmployeePayrollDB(query);
	}

	private List<EmployeePayrollData> getEmployeePayrollDB(String query) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollData;
		try {
			Connection connection = this.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			employeePayrollData = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return employeePayrollData;
	}
}
