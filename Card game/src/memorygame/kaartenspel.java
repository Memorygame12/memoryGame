package memorygame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class kaartenspel {

	private static Boolean isRunning = true;
	private static Boolean isGameOver = false;

	public static String[][] board = new String[5][5];
	public static String[][] cards = new String[5][5];
	public static Scanner scanner = new Scanner(System.in);
	private static int userId;
	private static final String UPDATE_USER_SCORE = " Update users set Score = ? where UserID = ? ";
	private static final String SELECT_USER_BY_ID = landingpage.sqlcom.concat(" where UserID = ? ");
	public static Integer incorrectCount = 0;
	public static Integer correctPairCount = 0;
	public static Integer turnsCount = 1;
	public static Integer maxAmountOfTurns = 10;

	public kaartenspel() {
	}

	//creating playing board
	public static void printBoard() {

		System.out.println(String.format("\nTurn: %s  |  Correct: %s  |  Incorrect: %s", turnsCount, correctPairCount,
				incorrectCount));

		for (int i = 0; i < 4; i++) {
			System.out.print("|");
			for (int j = 0; j < 5; j++) {
				System.out.print(board[i][j]);
				System.out.print("|");
			}
			System.out.println();

		}
	}

	//creating cards shuffling
	public static void shuffleCards() {
		Random random = new Random();
		ArrayList<String> letters = new ArrayList<String>();
		letters.add(" a ");
		letters.add(" b ");
		letters.add(" c ");
		letters.add(" d ");
		letters.add(" e ");
		letters.add(" f ");
		letters.add(" g ");
		letters.add(" h ");
		letters.add(" i ");
		letters.add(" j ");
		letters.add(" a ");
		letters.add(" b ");
		letters.add(" c ");
		letters.add(" d ");
		letters.add(" e ");
		letters.add(" f ");
		letters.add(" g ");
		letters.add(" h ");
		letters.add(" i ");
		letters.add(" j ");

		int index;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 5; j++) {
				index = random.nextInt(letters.size());
				cards[i][j] = letters.get(index);
				letters.remove(index);

			}
		}
	}

	//checking user input
	public static int getIntegerInput(String msg, int limit) {
		int input;
		while (true) {
			try {
				System.out.println(msg);
				input = Integer.parseInt(scanner.nextLine());

				if (input >= 1 && input <= limit) {
					return input;
				}

				System.out.println("Kies een getal dat ligt van 1 tot " + limit);

			} catch (NumberFormatException error) {
				System.out.println("Moet een getal zijn");
			}
		}
	}

	//processing user input
	private static int[] pickCard(int cardNumber) {
		int pickedCard[] = new int[2];

		int cardRow = getIntegerInput("Card " + cardNumber + " - Row (1-4)", 4);

		int cardColumn = getIntegerInput("Card " + cardNumber + " - Column (1-5)", 5);


		if (board[cardRow - 1][cardColumn - 1].equals(" _ ")) {
			board[cardRow - 1][cardColumn - 1] = cards[cardRow - 1][cardColumn - 1];
			printBoard();

			pickedCard[0] = cardRow;
			pickedCard[1] = cardColumn;
			return pickedCard;
		}

		Boolean pairPicked = true;
		while (pairPicked) {
			System.out.println("Al getoetst");
			printBoard();

			System.out.println("Card " + cardNumber + " - Row (1-4)");
			cardRow = scanner.nextInt();

			System.out.println("Card " + cardNumber + " - Column (1-5)");
			cardColumn = scanner.nextInt();

			if (board[cardRow - 1][cardColumn - 1].equals(" _ ")) {
				board[cardRow - 1][cardColumn - 1] = cards[cardRow - 1][cardColumn - 1];
				printBoard();

				pickedCard[0] = cardRow;
				pickedCard[1] = cardColumn;
				pairPicked = false;
			}
		}
		return pickedCard;
	}

	//updating the score in the database
	private static void updateScore(Integer gameScore) {
		Connection myConn;
		try {
			myConn = DriverManager.getConnection(landingpage.DB_URL, landingpage.USER, landingpage.PASS);
			PreparedStatement myStmt;

			myStmt = myConn.prepareStatement(SELECT_USER_BY_ID);

			myStmt.setString(1, String.valueOf(userId));
			ResultSet rs = myStmt.executeQuery();
			if (rs.next()) {
				Integer scoreInDatabase = rs.getInt("Score");
				Integer newScore = scoreInDatabase + gameScore;

				myStmt = myConn.prepareStatement(UPDATE_USER_SCORE);
				myStmt.setString(1, String.valueOf(newScore));
				myStmt.setString(2, String.valueOf(userId));

			
				int updatedRows = myStmt.executeUpdate();
				if (updatedRows > 0) {
					System.out.println("Score has been updated to " + newScore);
				} else {
					
					for (int incorrectCount = 0; incorrectCount < 2; incorrectCount++) {
						System.out.print("Game over");
					}
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	
	public boolean gameOver() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (board[i][j].equals(" _ ")) {
					return false;
				}
			}
		}
		return false;
	}

	public static void game(int id) throws Exception {
		userId = id;
		isRunning = true;
		// Main loop
		while (isRunning) {

			Scanner input = new Scanner(System.in);

			System.out.println("Toets n voor een nieuw spel, q voor het afsluiten en i voor de instructies");
			String nqi = input.nextLine();
			if (nqi.equals("q")) {
				System.out.println("exiting");
				isRunning = true;
				break;

			}

			else if (nqi.equals("i")) {
				System.out.println();
				System.out.println("Hieronder de instructies:");
				System.out.println("Kies voor elk kaart een rij en kolom nummer.");
				System.out.println("Bij een correcte paar van de 2 kaarten zullen er 2 punten toegekend worden.");
				System.out.println("Bij een incorrecte paar van de 2 kaarten zullen er geen punten toegekend worden.");
				System.out.println("Als alle paren (10) gevonden zijn zonder mislukkingen, krijgt de speler 3 bonuspunten");
				System.out.println("");
			}

			else if (nqi.equals("n")) {
				turnsCount = 1;
				correctPairCount = 0;
				incorrectCount = 0;
				isGameOver = false;
				shuffleCards();

				for (int i = 0; i < 4; i++) {
					for (int j = 0; j < 5; j++) {
						board[i][j] = " _ ";
					}
				}

				// Game loop
				while (!isGameOver) {
					printBoard();
					int card1[] = new int[2];
					card1 = pickCard(1);

					int card2[] = new int[2];
					card2 = pickCard(2);

					// Check if the pairs are equal/correct
					if (board[card1[0] - 1][card1[1] - 1].equals(board[card2[0] - 1][card2[1] - 1])) {
						System.out.println("\n== Correct ==");
						correctPairCount += 1;
					} else {
						System.out.println("\n== Incorrect ==");
						incorrectCount += 1;
					}

					if (turnsCount == maxAmountOfTurns) {

						// correct pairs without faults
						if (correctPairCount == 10) {
							Integer score = 20 + 3;
							updateScore(score);
							System.out.println("\nCONGRATULATIONS!!! YOU HAVE REACHED THE END SUCCESSFULLY\n");
							isGameOver = true;
						}

						// only mismatched pairs
						else if (incorrectCount == 10) {
							Integer score = 0;
							updateScore(score);
							System.out.println("\nYOU LOST. BETTER LUCK NEXT TIME!\n");
							isGameOver = true;
						}

						// correct pairs with faults
						else {
							Integer score = (10 - incorrectCount) * 2;
							updateScore(score);
							System.out.println("\nGAME OVER!!! YOU HAVE REACHED MAXIMUM NUMBER OF TURNS\n");
							isGameOver = true;
						}
					}

					turnsCount += 1;

				}
			}
		}
	}
}