
/*
Layout for the kalah game board 

	12	11	10	9	8	7

13							6

	0	1	2	3	4	5

Board class
- Contains the board configuration.
- Holds the game information of the board state.
- Contains functions that deal with the board.
- Contains the utility value of the state (heuristic function).
- Calculates move which resulted in the current state
  (alpha-beta search function).
 */
public class Board {

	private int[] board;
	
	private int nextPlayer; 
	private int assumedPlayer; 
	private static int bestMove = -1; 
	
	private static final int STONE_NUM = 6; // number of stones to start in each bowel 
	private static final int BOWL_NUM = 14; // total bowel number 
	private static final int BOWL_START_ONE = 0; // start index for Player #1's bowls
	private static final int BOWL_END_ONE = 5; // end index for Player #1's bowls
	private static final int BOWL_START_TWO = 7;// start index for Player #2's bowls
	private static final int BOWL_END_TWO = 12;// end index for Player #2's bowls
	private static final int KALAH_ONE = 6; // bowel ID for Player #1's kalah
	private static final int KALAH_TWO = 13;// bowel ID for Player #2's kalah
	
	private static final int PLAYER_ONE = 1;
	private static final int PLAYER_TWO = 2; 
	
	private static final int BOUND_HEURISTIC = Integer.MAX_VALUE; 
	private static final int CUTOFF_DEPTH = 15; // set search cutoff depth 

	
	private boolean hasExtraTurn = false; // if one player got an extra turn 
	private boolean isGameOver = false; // if game is over 
	
	/***************************************************************************
	 * Board Constructor 
	 **************************************************************************/
	public Board(int player){
		// initialize starting player 
		nextPlayer = PLAYER_ONE; 
		assumedPlayer = player; 

		// initialize Board 
		board = new int[BOWL_NUM]; 
		
		for(int i = 0; i < BOWL_NUM; i ++){
			if(i == KALAH_ONE || i == KALAH_TWO){
				board[i] = 0; 
			}else{
				board[i] = STONE_NUM; 
			}
		}
	}

	/***************************************************************************
	 * Display the state of the Board
	 **************************************************************************/
	public void showBoard() {
		int index;
		System.out.println();
		for (index = BOWL_END_TWO; index >= BOWL_START_TWO; index--) {
			System.out.print("\t" + board[index]);
		}
		System.out.print("\n\n" + board[KALAH_TWO]);
		System.out.println("\t\t\t\t\t\t\t" + board[KALAH_ONE] + "\n");
		for (index = BOWL_START_ONE; index <= BOWL_END_ONE; index++) {
			System.out.print("\t" + board[index]);
		}
		System.out.println("\n");
	}
	
	/***************************************************************************
	 * Return the number of stones at position bowl
	 **************************************************************************/
	public int getStones(int bowl){
		return board[bowl]; 
	}
	
	/***************************************************************************
	 * Set up the moves for player one or two, based on the Kalah Rules
	 **************************************************************************/
	public int move(int bowl){
		
		if(board[bowl] == 0){
			System.out.println("! No stone left at bowl #" + bowl); 
			return -1; 
		}
		
		if (!(((nextPlayer == PLAYER_ONE) && (bowl >= BOWL_START_ONE) && (bowl <= BOWL_END_ONE)) || 
				((nextPlayer == PLAYER_TWO) && (bowl >= BOWL_START_TWO) && (bowl <= BOWL_END_TWO))))
		{
			System.out.println("! Incorrect bowl ID for Player #" + nextPlayer);
			return -1; 
		}
		
		hasExtraTurn = false; 
		int seeds = board[bowl] + bowl;
		board[bowl] = 0;
 		switch(nextPlayer){
		case PLAYER_ONE: 
			for(int i = bowl + 1; i<= seeds; i++){	
				if((i%BOWL_NUM) != KALAH_TWO){ // player don't drop stone in opponent's kalah
					if(i == seeds && i%BOWL_NUM >= BOWL_START_ONE 
							&& i%BOWL_NUM <= BOWL_END_ONE && board[i%BOWL_NUM] == 0){
						// player drops last stone in his bowl, which was previously empty
						board[i%BOWL_NUM] = 0; 
						board[KALAH_ONE] ++; 
						board[KALAH_ONE] += board[12 - i%BOWL_NUM]; 
						board[12 - i%BOWL_NUM] = 0; 
					}else board[i%BOWL_NUM]++;
				}else{
					seeds ++; 
				}
			}
			if(seeds%BOWL_NUM == KALAH_ONE){
				// got extra turn if drop last stone in his kalah
				hasExtraTurn = true;
			}else{
				nextPlayer = PLAYER_TWO; 
			}
			break; 
			
		case PLAYER_TWO: 
			for(int i = bowl + 1; i<= seeds; i++){	
				if((i%BOWL_NUM) != KALAH_ONE){
					if(i == seeds && i%BOWL_NUM >= BOWL_START_TWO 
							&& i%BOWL_NUM <= BOWL_END_TWO 
							&& board[i%BOWL_NUM] == 0){
						board[i%BOWL_NUM] = 0; 
						board[KALAH_TWO] ++; 
						board[KALAH_TWO] += board[12 - i%BOWL_NUM]; 
						board[12 - i%BOWL_NUM] = 0; 
					}else board[i%BOWL_NUM]++;
				} else{
					seeds ++; 
				}
			}
			if(seeds%BOWL_NUM == KALAH_TWO){
				// got extra turn
				hasExtraTurn = true;
			}else{
				nextPlayer = PLAYER_ONE;  
			}
			break; 
		}
 		
 		// if player one has no stones in his small bowls
 		// then all the stones that remain in the other player's small bowls, 
 		// are placed in his kalah -- game over
 		if(isBowlsEmpty(BOWL_START_ONE, BOWL_END_ONE)){
 			
 			int remainStones = 0; 
 			for (int i = BOWL_START_TWO; i <= BOWL_END_TWO; i ++) {
 				remainStones += board[i];
 				board[i] = 0; 
 			}
 			board[KALAH_TWO] += remainStones; 
 			 
 			isGameOver = true; 
 		}else if(isBowlsEmpty(BOWL_START_TWO, BOWL_END_TWO)){
		// if player two has no stones in his/her small bowls
 			int remainStones = 0; 
 			for (int i = BOWL_START_ONE; i <= BOWL_END_ONE; i ++) {
 				remainStones += board[i];
 				board[i] = 0; 
 			}
 			board[KALAH_ONE] += remainStones; 
 			
 			isGameOver = true; 
		}
		return 0; 
	}
  
	/***************************************************************************
	 * Alpha-beta search implementation 
	 * This function searches the space and finds the best move
	 **************************************************************************/
	public int alphaBetaSearch(Board board){
		if(board.assumedPlayer != board.nextPlayer){
			return -1; 
		}
		bestMove = -1; 
		
		maxValue(board, -BOUND_HEURISTIC, BOUND_HEURISTIC, CUTOFF_DEPTH); 
		 
		return bestMove; 
	}
 
	/***************************************************************************
	 * Function that computes Max-Value
	 * Args: Board board, the current state of the board; 
	 * int alpha, the utility for the best choice found so far for MAX; 
	 * int beta, the utility for the best choice found so far for MIN.
	 * Returns: heuristic function value of each state 
	 * when the player makes the move 
	 * 
	 * Purpose:	The current state of the board will be expanded if possible
	 * by calling expandMaxState function.
	 * The successor states will then be analyzed, and the maximum value
	 * from the successors is propagated back to the parent state.
	 **************************************************************************/
	private int maxValue(Board board, int alpha, int beta, int depth){
		if(depth == 0 || board.isGameOver){
			// cutoff test 
			return board.heuristicEvaluation(); 
		}
		
		int v = -BOUND_HEURISTIC; 
		if(board.nextPlayer == PLAYER_ONE){
			// expand all possible moves of current state of the board 
			for(int i = BOWL_START_ONE; i <= BOWL_END_ONE; i ++){
				if(board.board[i] != 0){
					
					v = expandMaxState(board, i, alpha, beta, depth, v); 
					if(v >= beta) return v; 
					alpha = Math.max(alpha, v); 
				}
				
			}
		}else if(board.nextPlayer == PLAYER_TWO){
			for(int i = BOWL_START_TWO; i <= BOWL_END_TWO; i ++){
				if(board.board[i] != 0){
					v = expandMaxState(board, i, alpha, beta, depth, v); 
					if(v >= beta) return v; 
					alpha = Math.max(alpha, v); 
				}
			}
		}
		 
		return v; 
	}
 
	/***************************************************************************
	 * Function that computes Min-Value
	 * Returns: int, heuristic function value of each state
	 * when the opponent makes the move 
	 * Purpose:	the current state of the board will be expanded if possible
	 * by calling expandMinState function.
	 * The successor states will then be analyzed, and the minimum value
     * from the successors is propagated back to the parent state.
	 **************************************************************************/
	private int minValue(Board board, int alpha, int beta, int depth){
		if(depth == 0 || board.isGameOver){
			// cutoff test 
			return board.heuristicEvaluation(); 
		}
		
		int v = BOUND_HEURISTIC; 
		if(board.nextPlayer == PLAYER_ONE){
			for(int i = BOWL_START_ONE; i <= BOWL_END_ONE; i ++){
				if(board.board[i] != 0){
					v = expandMinState(board, i, alpha, beta, depth, v); 
					if(v <= alpha) return v; 
					beta = Math.min(beta, v); 
				}
			}
		}else if(board.nextPlayer == PLAYER_TWO){
			for(int i = BOWL_START_TWO; i <= BOWL_END_TWO; i ++){
				if(board.board[i] != 0){
					v = expandMinState(board, i, alpha, beta, depth, v); 
					if(v <= alpha) return v; 
					beta = Math.min(beta, v); 
				}
			}
		}
		return v; 
	}
	
	int expandMaxState(Board board, int index, 
			int alpha, int beta, int depth, int v)
	{
		// create copyBoard which is a copy of board.board
		Board copyBoard = board.clone();  
		// make a move
		copyBoard.move(index);
		// look at evaluation functions of expanded states
		int utility = evaluate(copyBoard, alpha, beta, depth);
 
			if (depth == CUTOFF_DEPTH){
				if (utility > v){
					bestMove = index;
				}
				System.out.println("* Possible move: " + index + "\tcomputed value: " + utility);
			}
			// if the utility value is greater than current, change it
			v = Math.max(utility, v);
		 
		return v;
	}
	int  expandMinState(Board board, int index, 
			int alpha, int beta, int depth, int v )
	{
		// create copyBoard which is a copy of board.board
		Board copyBoard = board.clone();  
		// make a move
		copyBoard.move(index);
		// look at evaluation functions of expanded states
		int utility = evaluate(copyBoard, alpha, beta, depth);
		// if the utility value is less than current, change it
		v = (utility < v)? utility : v;
		return v;
	}	
 
	/***************************************************************************
	 * Evaluate the successor states
	 **************************************************************************/
	private int evaluate(Board board, int alpha, int beta, int depth){
		if (board.nextPlayer != board.assumedPlayer){
			// evaluate as if the next player is going
			return minValue(board, alpha, beta, depth - 1);
		}
		else if (board.nextPlayer == board.assumedPlayer){
			// evaluation as if player was going again
			return maxValue(board, alpha, beta, depth - 1);
		} 
		return 0;
	}

	/***************************************************************************
	 * Heuristic evaluation function I  
	 * Defined as the number of stones in the player's kalah minus 
	 * the number of stones in the opponent's kalah. 
	 **************************************************************************/
	/*
	private int heuristicEvaluation(){
		return (assumedPlayer == PLAYER_ONE) ? 
		board[KALAH_ONE] - board[KALAH_TWO] : 
		board[KALAH_TWO] - board[KALAH_ONE];
	}
	*/
 
	/***************************************************************************
	 * Heuristic evaluation function II  
	 * Defined as the number of stones in the player's kalah - 
	 * the number of stones in the opponent's kalah + 
	 * the number of playable stones in the player's bowl - 
	 * the number of playable stones in the opponent's bowl. 
	 **************************************************************************/
	private int heuristicEvaluation(){

		int stones1 = 0, stones2 = 0; 
		for(int i = BOWL_START_ONE; i <= BOWL_END_ONE; i ++){
			stones1 += board[i]; 
		}
 
		for(int i = BOWL_START_TWO; i <= BOWL_END_TWO; i ++){
			stones2 += board[i]; 
		}

		return (assumedPlayer == PLAYER_ONE) ? 
		board[KALAH_ONE] - board[KALAH_TWO] + stones1 - stones2 : 
		board[KALAH_TWO] - board[KALAH_ONE] + stones2 - stones1 ;
		 
	}	

	/***************************************************************************
	 * Return a copy of the current Board state
	 **************************************************************************/
	public Board clone(){
		
		Board copy = new Board(this.assumedPlayer); 
		for(int i = 0; i < BOWL_NUM; i ++){
			copy.board[i] = this.board[i]; 
		}
		
		copy.nextPlayer = this.nextPlayer;
		 
		copy.assumedPlayer = this.assumedPlayer; 
		copy.isGameOver = false; 
		copy.hasExtraTurn = false; 
	 
		return copy; 
	}
	
	/***************************************************************************
	 * Check if player one/two has any stones in his/her small bowls
	 **************************************************************************/
	private boolean isBowlsEmpty(int start, int end){
		for(int i = start; i <= end; i++){
			if(board[i] != 0) return false; 
		}
		return true; 
	}
	
	/***************************************************************************
	 * Return if game is over
	 **************************************************************************/
	public boolean isGameOver(){
		return isGameOver; 
	}

	/***************************************************************************
	 * Return the difference between the stones in two players' kalah
	 **************************************************************************/
	public int getKalah(){
		return board[KALAH_ONE] - board[KALAH_TWO]; 
	}
	
	/***************************************************************************
	 * Return if the current player has extra turn
	 **************************************************************************/
	public boolean hasExtraTurn(){
		return hasExtraTurn; 
	}
	
	public int getNextPlayer(){
		return nextPlayer; 
	}
 
}
