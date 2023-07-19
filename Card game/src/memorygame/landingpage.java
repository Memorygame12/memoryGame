

//	Groepsleden					Studentennummer
//
// 1. Atmosoerodjo Joshua		SE/1121/064
// 2. Mahabier	Dipak			SNE/1121/009
// 3. Matabadal Vishaant		SNE/1121/025
// 4. Soeropawiro Farhen		SNE/1121/020
// 5. Xiao Sally 				SE/1121/060




package memorygame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class landingpage {

	public static final String DB_URL = "jdbc:mysql://localhost/users";
	public static final String USER = "root";
	public static final String PASS = "";
	public static final String sqlcom = "select * from users ";
	
	//input for registering
	public static void register() throws Exception {
		Scanner keyboard1 = new Scanner(System.in);

		System.out.print("Enter your username: ");
		String username = keyboard1.nextLine();

		System.out.print("Enter your first password: ");
		String password = keyboard1.nextLine();

		System.out.print("Enter your email: ");
		String email = keyboard1.nextLine();

		System.out.print("Enter your telefoonnummer: ");
		String telefoonnummer = keyboard1.nextLine();

		// 1. Get a connection to database
		Connection myConn;
		myConn = DriverManager.getConnection(DB_URL, USER, PASS);
		PreparedStatement myStmt;

		// 2. Create a statement
		String sql = "insert into users " + " (Username, Password, Email,Telefoonnummer,Score )"
				+ " values (?, ?, ?, ?, 0)";

		myStmt = myConn.prepareStatement(sql);

		myStmt.setString(1, username);
		myStmt.setString(2, password);
		myStmt.setString(3, email);
		myStmt.setString(4, telefoonnummer);

		// 3. Execute SQL query
		myStmt.executeUpdate();

		System.out.println("Insert complete.");

	}

	//creating main menu
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		
		while (true) {
			System.out.println("");
			System.out.println("Welcome bij de memory game. ");
			System.out.println("Druk op 1 om te registreren. ");
			System.out.println("Druk op 2 om in te loggen. ");
			System.out.println("Druk op 3 voor de top 10 spelers.");

			int choice = keyboard.nextInt();

			switch (choice) {
			case 1:
				try {
					register();
				} catch (Exception error) {
					System.out.println("Kan niet registreren");
				}
				System.out.println("Log in voor verdere toegang");

				try {
					int userId = login();
					kaartenspel kaartenspel = new kaartenspel();
					kaartenspel.game(userId);
				} catch (Exception error) {
					System.out.println("Kan niet inloggen");
				}
				break;

			case 2:
				try {
					int userId = login();
					kaartenspel kaartenspel = new kaartenspel();
					kaartenspel.game(userId);
				} catch (Exception error) {
					System.out.println("Kan niet inloggen");
				}
				break;

			case 3:
				try {
					top10();
				} catch (Exception error) {
					System.out.println("Kan de top 10 scores niet verkrijgen");
				}
				break;

			}
		}
	}

	//creating login page
	public static int login() {
		int userId = 0;
		try {

			Scanner sc = new Scanner(System.in);

			System.out.println();
			System.out.print("Enter your username: ");
			String username1 = sc.nextLine();

			System.out.print("Enter your password: ");
			String password1 = sc.nextLine();

			Connection Conn;
			Conn = DriverManager.getConnection("jdbc:mysql://localhost/users", "root", "");
			Statement st;

			st = Conn.createStatement();

			String sqlverf = sqlcom.concat(" where Username ='" + username1 + "' and Password ='" + password1 + "'");

			ResultSet rs = st.executeQuery(sqlverf);

			if (rs.next()) {

				userId = rs.getInt("UserId");
				System.out.println("Login was successful");
				System.out.println();

			} else {
				System.err.println("Invalid username or password!!!!!!!!");
				retry();

			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
		}
		return userId;
	}

	public static void retry() throws Exception {
		System.out.println("Please try again");

	}

	//requesting top 10 best scores
	public static void top10() throws Exception {
		Connection Conn;
		Conn = DriverManager.getConnection("jdbc:mysql://localhost/users", "root", "");
		ResultSet dataset;

		String sqltop10 = "select username, score from users group by score DESC limit 10;";
		PreparedStatement pr = Conn.prepareStatement(sqltop10);
		dataset = pr.executeQuery();
		while (dataset.next()) {
			System.out.println(dataset.getString(1) + " " + dataset.getString(2));
		}

	}
}