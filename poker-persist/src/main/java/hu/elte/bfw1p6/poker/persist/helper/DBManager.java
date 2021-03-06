package hu.elte.bfw1p6.poker.persist.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import hu.elte.bfw1p6.poker.properties.PokerDataBaseProperties;

/**
 * Az adatbázishoz való csatlakozást biztosító osztály.
 * @author feher
 *
 */
public final class DBManager {

	private Connection con = null;
	
	private PokerDataBaseProperties properties;
	
	private static String IP;
	private static String DATABASE_NAME;
	private static String USERNAME;
	private static String PASSWORD;
	
	private static String strCon;

	public DBManager() {
		properties = PokerDataBaseProperties.getInstance();
		IP = properties.getProperty("ip");
		DATABASE_NAME = properties.getProperty("dbname");
		USERNAME = properties.getProperty("username");
		PASSWORD = properties.getProperty("password");
		
		strCon = "jdbc:mysql://" + IP + "/" + DATABASE_NAME + "?user=" + USERNAME + "&password=" + PASSWORD;
		
		try {
			con = DriverManager.getConnection(strCon);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return con;
	}
}