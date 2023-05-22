package org.java.nations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class Main {
	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/db_nations";
		String user = "root";
		String password = "code";
		
		try (Connection con = DriverManager.getConnection(url, user, password)) {
			
			String sql = "SELECT * FROM countries c ORDER BY c.name";
			
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				
				try (ResultSet rs = ps.executeQuery()) {
					
					while(rs.next()) {
						
						final int id = rs.getInt(1);
						final String name = rs.getString(2);
						final int area = rs.getInt(3);
						
							
						System.out.println(id + " - " + name + " - " 
								+ area);
					}
				}				
			} catch (SQLException ex) {
				System.err.println("Query not well formed");
			}
		} catch (SQLException ex) {
			System.err.println("Error during connection to db");
		}
	}
	
	public static LocalDateTime getLocalDateTime(Timestamp time) {
		
		return LocalDateTime
				.ofInstant(Instant.ofEpochMilli(time.getTime()), 
						TimeZone.getDefault().toZoneId());  
	}
}
