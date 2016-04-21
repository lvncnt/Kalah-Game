Implement agent to play game of Kalah
=============

- The program reads one command-line argument (1 or 2), indicating whether the program should assume the role of Player #1 or Player #2.

- The program reacts to commands "move", "opponent <move>" and "quit", and prints a representation of the game state each time it changes

- Error message will be displayed if 
 1) a wrong command is entered; 
 2) an ID for bowl that does not belong to the player is entered; 
 3) the bowl ID entered has no stones left. 
 
- A message will be displayed if  
1) a player gets an extra move. 
 
- The rules for moving stones is implemented based on 'the Rules of the game Kalah' 

- The alpha-beta search is implemented by following the Alpha-beta algorithm on p. 170 in the textbook. The following modifications are made,  
1) the terminal test is replaced with a cutoff test. 
2) a heuristic evaluation function instead of the utility function is used. 
