import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 ****************************************************************************** 
 * @author LVNCNT
 * @date Apr 07, 2016
 * 
 ***************************  Description of the Code **************************
 * 
 *       1. The program reads one command-line argument (1 or 2), indicating
 *       whether the program should assume the role of Player #1 or Player #2.
 *       
 *       2. The program reacts to commands "move", "opponent <move>" and "quit",
 *       and prints a representation of the game state each time it changes
 *		 (the state display is commented out right now to work with the server). 
 *       
 *       3. Error message will be displayed if 
 *		 (the message display is commented out right now to work with the server)
 *       1) a wrong command is entered; 
 *       2) an ID for bowl that does not belong to the player is entered; 
 *       3) the bowl ID entered has no stones left. 
 *       
 *       4. A message will be displayed if 
 *		 (the message display is commented out right now to work with the server)
 *       1) a player gets an extra move. 
 *       
 *       5. The rules for moving stones is implemented 
 *       based on 'the Rules of the game Kalah' 
 *       
 *       6. The alpha-beta search is implemented by following the Alpha-beta 
 *		 algorithm on p. 170 in the textbook. The following modifications are 
 *		 made,  
 *		 1) the terminal test is replaced with a cutoff test. 
 *		 2) a heuristic evaluation function instead of the utility function is 
 *  	 used. 
 * 
 ****************************************************************************** 
 */

public class Main {

	private static final String QUIT = "quit";
	private static final String MOVE = "move";
	private static final String OPPONENT = "opponent";
 
	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("! usage: java Main <Player ID>");
			return;
		}

		int player = Integer.parseInt(args[0]);
		if (player != 1 && player != 2) {
			System.out.println("! Invalid Player ID, choose 1 or 2");
			return;
		}

		Board board = new Board(player);
		board.showBoard();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
 		int move, result = 0;
		String moveStr;
		String command = "";

		while (true) {

			try {
				command = in.readLine();
				command = command.trim().replaceAll("\n ", "");
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (command.equals(QUIT)) {
				return;
			} else if (command.equals(MOVE)) {
				  			
				// compute move by alpha-beta search
				move = board.alphaBetaSearch(board); 
				System.out.println("* Best move: " + move);
				moveStr = String.valueOf(move + "\n");
				try {
					System.out.write(moveStr.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				result = board.move(move); 
				
			} else if (command.startsWith(OPPONENT) && command.split(" ").length > 1) {
				move = Integer.parseInt(command.split(" ")[1]);

				result = board.move(move);
			} else {
				System.out.println("! Invalid command: choose move, opponent <move>, or quit");
			}
			
			if (result != -1) {
				board.showBoard();
			}
			
			if(board.isGameOver()){
				
				System.out.println("* Game over");
				if(board.getKalah() > 0){
					System.out.println("* Player #1 wins");
				}else if(board.getKalah() < 0){
					System.out.println("* Player #2 wins");
				}else{
					System.out.println("* It's a tie");
				}
				return; 
			}
				
			if(board.hasExtraTurn()){
				System.out.println("* Got extra turn for Player #" + board.getNextPlayer());
			}
		}
	}
}
