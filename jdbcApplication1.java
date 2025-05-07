package com.Jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class jdbcApplication1 {

	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws SQLException {
		int choice = 0;
		do {
			System.out.println("=================================");
			System.out.println("1.Adduser\n2.Updateuser\n3.Deleteuser\n4.Viewuser\n5.Exit");
			System.out.println("Take your action here: ");
			try {
				choice = sc.nextInt();
				switch (choice) {
				case 1:
					Adduser();
					break;
				case 2:
					Updateuser();
					break;
				case 3:
					Deleteuser();
					break;
				case 4:
					Viewuser();
					break;
				case 5:
					Exituser();
					break;
				default:
					System.out.println("Invalid choice! Please select 1-5");
				}
			} catch (Exception e) {
				System.out.println("Please enter a valid number (1-5)");
				sc.nextLine(); // Clear invalid input
			}
		} while (choice != 5);
	}

	public static Connection getConnection() {
		Connection connection = null;
		try {
			String url = "jdbc:mysql://localhost:3306/credo";
			String uname = "root";
			String pass = "root";
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, uname, pass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static void Adduser() throws SQLException {
		Connection connection = null;
		try {
			System.out.println("Enter the patient_id");
			int patient_id = sc.nextInt();
			sc.nextLine(); // Clear buffer
			System.out.println("Enter the patient_name");
			String patient_name = sc.nextLine();
			System.out.println("Enter the patient_disease");
			String patient_disease = sc.nextLine();
			System.out.println("Enter the patient_age");
			int patient_age = sc.nextInt();
			sc.nextLine(); // Clear buffer
			System.out.println("Enter the patient_insurance");
			String patient_insurance = sc.nextLine();

			String query = "insert into patient values(?,?,?,?,?)";
			connection = JdbcApplication.getConnection();
			PreparedStatement psmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			psmt.setInt(1, patient_id);
			psmt.setString(2, patient_name);
			psmt.setString(3, patient_disease);
			psmt.setInt(4, patient_age);
			psmt.setString(5, patient_insurance);
			int count = psmt.executeUpdate();
			if (count > 0) {
				System.out.println("Inserted Successfully");
			} else {
				System.out.println("Failed to Insert");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void Updateuser() throws SQLException {
		Connection connection = null;
		try {
			System.out.println("Enter patient_id to update");
			int patient_id = sc.nextInt();

			connection = getConnection();
			String checkQuery = "SELECT * FROM patient WHERE patient_id = ?";
			PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
			checkStmt.setInt(1, patient_id);
			ResultSet rs = checkStmt.executeQuery();

			if (!rs.next()) {
				System.out.println("Patient with ID " + patient_id + " not found!");
				return;
			}

			sc.nextLine(); // Clear buffer
			System.out.println("Enter new patient_name (press enter to skip)");
			String patient_name = sc.nextLine();
			System.out.println("Enter new patient_disease (press enter to skip)");
			String patient_disease = sc.nextLine();
			System.out.println("Enter new patient_age (enter 0 to skip)");
			int patient_age = sc.nextInt();
			sc.nextLine(); // Clear buffer
			System.out.println("Enter new patient_insurance (press enter to skip)");
			String patient_insurance = sc.nextLine();

			String query = "UPDATE patient SET " + (patient_name.isEmpty() ? "" : "patient_name = ?, ")
					+ (patient_disease.isEmpty() ? "" : "patient_disease = ?, ")
					+ (patient_age == 0 ? "" : "patient_age = ?, ")
					+ (patient_insurance.isEmpty() ? "" : "patient_insurance = ?, ")
					+ "patient_id = patient_id WHERE patient_id = ?";

			query = query.replaceAll(", $", " ");
			PreparedStatement psmt = connection.prepareStatement(query);
			int paramIndex = 1;

			if (!patient_name.isEmpty())
				psmt.setString(paramIndex++, patient_name);
			if (!patient_disease.isEmpty())
				psmt.setString(paramIndex++, patient_disease);
			if (patient_age != 0)
				psmt.setInt(paramIndex++, patient_age);
			if (!patient_insurance.isEmpty())
				psmt.setString(paramIndex++, patient_insurance);
			psmt.setInt(paramIndex, patient_id);

			int count = psmt.executeUpdate();
			if (count > 0) {
				System.out.println("Updated Successfully");
			} else {
				System.out.println("Failed to Update");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void Deleteuser() throws SQLException {
		Connection connection = null;
		try {
			System.out.println("Enter patient_id to delete");
			int patient_id = sc.nextInt();

			connection = getConnection();
			String query = "DELETE FROM patient WHERE patient_id = ?";
			PreparedStatement psmt = connection.prepareStatement(query);
			psmt.setInt(1, patient_id);

			int count = psmt.executeUpdate();
			if (count > 0) {
				System.out.println("Deleted Successfully");
			} else {
				System.out.println("Patient not found or Failed to Delete");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void Viewuser() throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			String query = "SELECT * FROM patient";
			PreparedStatement psmt = connection.prepareStatement(query);
			ResultSet rs = psmt.executeQuery();

			boolean hasRecords = false;
			while (rs.next()) {
				hasRecords = true;
				System.out.println("Patient ID: " + rs.getInt("patient_id"));
				System.out.println("Name: " + rs.getString("patient_name"));
				System.out.println("Disease: " + rs.getString("patient_disease"));
				System.out.println("Age: " + rs.getInt("patient_age"));
				System.out.println("Insurance: " + rs.getString("patient_insurance"));
				System.out.println("------------------------");
			}

			if (!hasRecords) {
				System.out.println("No patients found in the database");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void Exituser() {
		System.out.println("=================================");
		System.out.println("Thank you for using our Patient Management System!");
		System.out.println("Application closing...");
		System.out.println("Goodbye!");
		System.out.println("=================================");

		// Clean up resources
		try {
			if (sc != null) {
				sc.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Exit the application
		System.exit(0);
	}
}
