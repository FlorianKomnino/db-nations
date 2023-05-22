package org.java.nations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.TimeZone;

public class Main {
	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/db_nations";
		String user = "root";
		String password = "code";
		
		try (Scanner sc = new Scanner(System.in);
				Connection con = DriverManager.getConnection(url, user, password)) {
			
			
			System.out.print("Ricerca nazioni per nome o parte del nome: ");
			String userResearch = sc.nextLine();
			
			String sql = "SELECT c.name, c.country_id , r.name, c2.name FROM countries c "
					+ "JOIN regions r "
					+ "ON c.region_id = r.region_id "
					+ "JOIN continents c2 "
					+ "ON r.continent_id = c2.continent_id "
					+ "WHERE c.name LIKE \"%" + userResearch + "%\" "
					+ "ORDER BY c.name";

			
			try (PreparedStatement ps = con.prepareStatement(sql)) {

				try (ResultSet rs = ps.executeQuery()) {
					
					while(rs.next()) {
						
						final String nationName = rs.getString(1);
						final int nationId = rs.getInt(2);
						final String regionName = rs.getString(3);
						final String continentName = rs.getString(4);
						
							
						System.out.println(nationName + " - " + nationId + " - " 
								+ regionName + " - " + continentName);
					}
				}				
			} catch (SQLException ex) {
				System.err.println("Query not well formed");
			}
			
			System.out.print("Inserisci l'id della nazione di cui vuoi conoscere i dettagli: ");
			String userAdvancedResearch = sc.nextLine();
			int userInputId = Integer.valueOf(userAdvancedResearch);
			
			String sqlAdvanced = "SELECT c.name, l.`language`, cs.`year` AS lastYear, cs.population, cs.gdp "
					+ "FROM countries c "
					+ "JOIN country_languages cl "
					+ "ON c.country_id = cl.country_id "
					+ "JOIN languages l "
					+ "ON cl.language_id = l.language_id JOIN country_stats cs "
					+ "ON c.country_id = cs.country_id "
					+ "WHERE c.country_id = " + userInputId
					+ " HAVING lastYear = ("
					+ "SELECT max(cs2.`year`) "
					+ "FROM country_stats cs2 "
					+ "JOIN countries c2 "
					+ "ON cs2.country_id = c2.country_id "
					+ "WHERE c2.country_id = " + userInputId
					+ " ORDER BY cs2.`year` "
					+ "DESC "
					+ "LIMIT 1)";

			
			try (PreparedStatement ps = con.prepareStatement(sqlAdvanced)) {

				try (ResultSet rs = ps.executeQuery()) {
					int i = 0;
					
					while(rs.next()) {
						if (i == 0) {
							final int lastYear = rs.getInt(3);
							final int nationPopulation = rs.getInt(4);
							final BigDecimal gdp = rs.getBigDecimal(5);
							
							System.out.println("Ultimo anno di statistiche: " + lastYear);
							System.out.println("Popolazione della nazione: " + nationPopulation);
							System.out.println("GDP della nazione: " + gdp);
							System.out.println("Le lingue parlate nello stato scelto sono:");
							i++;
						}
						final String nationLanguage = rs.getString(2);
						
						System.out.println(nationLanguage);
						
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
