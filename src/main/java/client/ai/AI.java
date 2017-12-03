package client.ai;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import common.game.*;

public class AI {
	private Match match;
	private int AIid;
	private boolean isDefender;
	private char[][] aiBoard;
	private ArrayList<Coordinate> attackTiles;
	private ArrayList<Coordinate> defendTiles;
	
	public AI(Match match, int AIid, boolean isDefender) {
		this.match = match;
		this.AIid = AIid;
		this.isDefender = isDefender;
		attackTiles = new ArrayList<Coordinate>();
		defendTiles = new ArrayList<Coordinate>();
		aiBoard = new char[11][11];
		//Populate AI Board
		Tile[][] tiles = match.getBoard().getTiles();
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				if (tiles[i][j].hasPiece()) {
					if (tiles[i][j].getPiece().isKing()) {
						aiBoard[i][j] = 'K';
						defendTiles.add(new Coordinate(i, j));
					}
					else if (tiles[i][j].getPiece().getUser() == AIid) {
						if (isDefender) {
							aiBoard[i][j] = 'W';
							defendTiles.add(new Coordinate(i, j));
						}
						else {
							aiBoard[i][j] = 'B';
							attackTiles.add(new Coordinate(i, j));
						}
					}
					else {
						if (isDefender) {
							aiBoard[i][j] = 'B';
							attackTiles.add(new Coordinate(i, j));
						}
						else {
							aiBoard[i][j] = 'W';
							defendTiles.add(new Coordinate(i, j));
						}
					}
				}
				else {
					aiBoard[i][j] = ' ';
				}
			}
		}
	}
	
	public int[] makeMove() {
		System.out.println("1");
		//Copy the match's board so we can explore states without modifying the match
		char[][] tiles = aiBoard.clone();
		ArrayList<Coordinate> attackTiles = new ArrayList<Coordinate>(this.attackTiles);
		ArrayList<Coordinate> defendTiles = new ArrayList<Coordinate>(this.defendTiles);
		//Find the best move
		double move[] = new double[5];
		move = negamaxab(tiles, 4, isDefender, attackTiles, defendTiles, -9999999, 9999999);
		int bestMove[] = new int[4];
		for (int i = 0; i < 4; i++) {
			bestMove[i] = (int) move[i];
		}
		System.out.println("2");
		return bestMove;
	}

	private double heuristic(char[][] tiles){
		double attackScore = 0;
		double defendScore = 0;
		for (int i = 0; i < tiles.length; i++){
			for (int j = 0; j < tiles[i].length; j++){
				if (tiles[i][j] == 'K'){
					if ((i == 0 || i == 10) || (j == 0 || j == 10)){
						defendScore += 1000;
					}
					if (i != 0 && tiles[i-1][j] == 'B'){
						defendScore -= 25;
					}
					if (i != 10 && tiles[i+1][j] == 'B'){
						defendScore -= 25;
					}
					if (j != 0 && tiles[i][j-1] == 'B'){
						defendScore -= 25;
					}
					if (j != 10 && tiles[i][j+1] == 'B'){
						defendScore -= 25;
					}
				}
				else if (tiles[i][j] == 'W'){
					defendScore += 5;
					defendScore += Math.abs(5 - i) + Math.abs(5 - j);
				}
				else if (tiles[i][j] == 'B'){
					attackScore += 5;
					attackScore -= Math.abs(5 - i) + Math.abs(5 - j);
				}
			}
		}
		return defendScore - attackScore;
	}
	
	private double[] negamaxab(char[][] tiles, int depth, boolean isDefender, ArrayList<Coordinate> attackTiles, ArrayList<Coordinate> defendTiles, double a, double b) {
		if (depth == 0) {
			double value;
			if(isDefender){
				value = heuristic(tiles);
			}
			else{
				value = -heuristic(tiles);
			}
			double[] stateValue = {-1, -1, -1, -1, value};
			return stateValue;
		}
		double bestMove[] = {-1, -1, -1, -1, -9999999};
		if (isDefender) {
			for (int i = 0; i < defendTiles.size(); i++) {
				Coordinate tile = defendTiles.get(i);
				ArrayList<Coordinate> moves = getAvailableMoves(tiles, tile);
				for (int j = 0; j < moves.size(); j++) {
					//Copy board state so we can safely modify it
					char[][] moveTiles = new char[11][11];
					for (int k = 0; k < tiles.length; k++){
						for (int l = 0; l < tiles[k].length; l++){
							moveTiles[k][l] = tiles[k][l];
						}
					}
					//Make the move
					moveTiles[moves.get(j).getX()][moves.get(j).getY()] = moveTiles[tile.getX()][tile.getY()];
					moveTiles[tile.getX()][tile.getY()] = ' ';
					HashSet<Coordinate> captured = capture(moveTiles, moves.get(j));
					ArrayList<Coordinate> newAttack = new ArrayList<Coordinate>();
					for (int k = 0; k < attackTiles.size(); k++) {
						if (!(captured.contains(attackTiles.get(k)))) newAttack.add(attackTiles.get(k));
					}
					//Explore the new move from other perspective
					double[] move = negamaxab(moveTiles, depth-1, !(isDefender), newAttack, defendTiles, -b, -a);
					//Populate move data
					move[0] = tile.getX();
					move[1] = tile.getY();
					move[2] = moves.get(j).getX();
					move[3] = moves.get(j).getY();
					//Invert the opponent's value
					move[4] = -move[4];
					if (move[4] > bestMove[4]) {
						bestMove = move;
					}
					if (move[4] > a) {
						a = move[4];
					}
					if (a >= b) break;
				}
			}
		}
		else {
			for (int i = 0; i < attackTiles.size(); i++) {
				Coordinate tile = attackTiles.get(i);
				ArrayList<Coordinate> moves = getAvailableMoves(tiles, tile);
				for (int j = 0; j < moves.size(); j++) {
					//Copy board state so we can safely modify it
					char[][] moveTiles = new char[11][11];
					for (int k = 0; k < tiles.length; k++){
						for (int l = 0; l < tiles[k].length; l++){
							moveTiles[k][l] = tiles[k][l];
						}
					}
					//Make the move
					moveTiles[moves.get(j).getX()][moves.get(j).getY()] = moveTiles[tile.getX()][tile.getY()];
					moveTiles[tile.getX()][tile.getY()] = ' ';
					HashSet<Coordinate> captured = capture(moveTiles, moves.get(j));
					ArrayList<Coordinate> newDefend = new ArrayList<Coordinate>();
					for (int k = 0; k < defendTiles.size(); k++) {
						if (!(captured.contains(defendTiles.get(k)))) newDefend.add(defendTiles.get(k));
					}
					//Explore the new move from other perspective
					double[] move = negamaxab(moveTiles, depth-1, !(isDefender), attackTiles, newDefend, -b, -a);
					//Populate move data
					move[0] = tile.getX();
					move[1] = tile.getY();
					move[2] = moves.get(j).getX();
					move[3] = moves.get(j).getY();
					//Invert the opponent's value
					move[4] = -move[4];
					if (move[4] > bestMove[4]) {
						bestMove = move;
					}
					if (move[4] > a) {
						a = move[4];
					}
					if (a >= b) break;
				}
			}
		}
		return bestMove;
	}
	
	private static ArrayList<Coordinate> getAvailableMoves(char[][] tiles, Coordinate tile) {
        ArrayList<Coordinate> availableMoves = new ArrayList<Coordinate>();
            int x = tile.getX();
            int y = tile.getY();
            //Check areas left of tile
            if (x != 0) {
                for (int i = x - 1; i >= 0; i--) {
                    //Check if the tile has no piece on it and is not the throne
                    if (!(tiles[i][y] == 'K' || tiles[i][y] == 'W' || tiles[i][y] == 'B') && !(i == 5 && y == 5))
                        availableMoves.add(new Coordinate(i, y));
                    else
                        break;
                }
            }
            //Check areas right of tile
            if (x != 10) {
                for (int i = x + 1; i <= 10; i++) {
                	//Check if the tile has no piece on it and is not the throne
                	if (!(tiles[i][y] == 'K' || tiles[i][y] == 'W' || tiles[i][y] == 'B') && !(i == 5 && y == 5))
                        availableMoves.add(new Coordinate(i, y));
                    else
                        break;
                }
            }
            //Check above tile
            if (y != 0) {
                for (int i = y - 1; i >= 0; i--) {
                	//Check if the tile has no piece on it and is not the throne
                	if (!(tiles[x][i] == 'K' || tiles[x][i] == 'W' || tiles[x][i] == 'B') && !(x == 5 && i == 5))
                        availableMoves.add(new Coordinate(x, i));
                    else
                        break;
                }
            }
            //Check below tile
            if (y != 10) {
                for (int i = y + 1; i <= 10; i++) {
                	//Check if the tile has no piece on it and is not the throne
                	if (!(tiles[x][i] == 'K' || tiles[x][i] == 'W' || tiles[x][i] == 'B') && !(x == 5 && i == 5))
                        availableMoves.add(new Coordinate(x, i));
                    else
                        break;
                }
            }
        
        return availableMoves;
    }
		/*def negamaxab(game, depthLeft, a=-(sys.maxsize), b=sys.maxsize):
	    if(depthLeft > game.depthExplored):
	        game.depthExplored = depthLeft
	    # If at terminal state or depth limit, return utility value and move None
	    if game.isOver() or depthLeft == 0:
	        return game.getUtility(), None # call to negamax knows the move
	    # Find best move and its value from current state
	    bestValue, bestMove = None, None
	    for move in game.getMoves():
	        # Apply a move to current state
	        game.makeMove(move)
	        # Use depth-first search to find eventual utility value and back it up.
	        #  Negate it because it will come back in context of next player
	        value, _ = negamaxab(game, depthLeft-1, -b, -a)
	        # Remove the move from current state, to prepare for trying a different move
	        game.unmakeMove(move)
	        if value is None:
	            continue
	        value = - value
	        if bestValue is None or value > bestValue:
	            # Value for this move is better than moves tried so far from this state.
	            bestValue, bestMove = value, move
	        if value > a:
	            a = value
	        if a >= b:
	            break
	    return bestValue, bestMove
		*/

	private HashSet<Coordinate> capture(char[][] tiles, Coordinate capturerTile) {
        HashSet<Coordinate> capturedTiles = new HashSet<Coordinate>();
        int x = capturerTile.getX();
        int y = capturerTile.getY();
        //Capture top piece if capturable
        if (y > 1) {
           if (aboveCapturable(tiles, capturerTile)) {
        	   //If piece to be captured is a king
               if (tiles[x][y - 1] == 'K') {
            	   //kingCapture attempts to capture the king.
            	   if (kingCapture(tiles, new Coordinate(x, y - 1))) {
            		   //If king was successfully captured
                       capturedTiles.add(new Coordinate(x, y - 1));
                   }
               }
               //Capture the piece
               else {
            	   capturedTiles.add(new Coordinate(x, y - 1));
                   tiles[x][y - 1] = ' ';
               }
           }
        }
        //Capture bottom piece if capturable
        if (y < 9) {
            if (belowCapturable(tiles, capturerTile)) {
            	//If piece to be captured is a king
                if (tiles[x][y + 1] == 'K') {
             	   //kingCapture attempts to capture the king.
             	   if (kingCapture(tiles, new Coordinate(x, y + 1))) {
             		   //If king was successfully captured
                        capturedTiles.add(new Coordinate(x, y + 1));
                    }
                }
                //Capture the piece
                else {
             	   capturedTiles.add(new Coordinate(x, y + 1));
                    tiles[x][y + 1] = ' ';
                }
            }
        }
        //Capture left piece if capturable
        if (x > 1) {
            if (leftCapturable(tiles, capturerTile)) {
            	//If piece to be captured is a king
                if (tiles[x - 1][y] == 'K') {
             	   //kingCapture attempts to capture the king.
             	   if (kingCapture(tiles, new Coordinate(x - 1, y))) {
             		   //If king was successfully captured
                        capturedTiles.add(new Coordinate(x - 1, y));
                    }
                }
                //Capture the piece
                else {
             	   capturedTiles.add(new Coordinate(x - 1, y));
                    tiles[x - 1][y] = ' ';
                }
            }
        }
        //Capture right piece if capturable
        if (x < 9) {
            if(rightCapturable(tiles, capturerTile)) {
            	//If piece to be captured is a king
                if (tiles[x + 1][y] == 'K') {
             	   //kingCapture attempts to capture the king.
             	   if (kingCapture(tiles, new Coordinate(x + 1, y))) {
             		   //If king was successfully captured
                        capturedTiles.add(new Coordinate(x + 1, y));
                    }
                }
                //Capture the piece
                else {
             	   capturedTiles.add(new Coordinate(x + 1, y));
                    tiles[x + 1][y] = ' ';
                }
            }
        }
        return capturedTiles;
    }
    private boolean aboveCapturable(char[][] tiles, Coordinate capturerTile) {
    	int x = capturerTile.getX();
    	int y = capturerTile.getY();
    	//Check if there is a piece here, and if it belongs to the enemy.
    	if (tiles[x][y - 1] != tiles[x][y] && tiles[x][y - 1] != ' ') {
    		//Check if the piece on the other side belongs to this team and isn't the king
    		if (tiles[x][y - 2] == tiles[x][y]) {
    			return true;
    		}
    	}
        return false;
    }
    private boolean belowCapturable(char[][] tiles, Coordinate capturerTile) {
    	int x = capturerTile.getX();
    	int y = capturerTile.getY();
    	//Check if there is a piece here, and if it belongs to the enemy.
    	if (tiles[x][y + 1] != tiles[x][y] && tiles[x][y + 1] != ' ') {
    		//Check if the piece on the other side belongs to this team and isn't the king
    		if (tiles[x][y + 2] == tiles[x][y]) {
    			return true;
    		}
    	}
        return false;
    }
    private boolean leftCapturable(char[][] tiles, Coordinate capturerTile) {
    	int x = capturerTile.getX();
    	int y = capturerTile.getY();
    	//Check if there is a piece here, and if it belongs to the enemy.
    	if (tiles[x - 1][y] != tiles[x][y] && tiles[x - 1][y] != ' ') {
    		//Check if the piece on the other side belongs to this team and isn't the king
    		if (tiles[x - 2][y] == tiles[x][y]) {
    			return true;
    		}
    	}
        return false;
    }
    private boolean rightCapturable(char[][] tiles, Coordinate capturerTile) {
    	int x = capturerTile.getX();
    	int y = capturerTile.getY();
    	//Check if there is a piece here, and if it belongs to the enemy.
    	if (tiles[x + 1][y] != tiles[x][y] && tiles[x + 1][y] != ' ') {
    		//Check if the piece on the other side belongs to this team and isn't the king
    		if (tiles[x + 2][y] == tiles[x][y]) {
    			return true;
    		}
    	}
        return false;
    }

    //Captures the king if surrounded.  Returns true is king was captured, false otherwise.
    private boolean kingCapture(char[][] tiles, Coordinate kingTile) {
        //If above, below, left, and right are all true, the king is surrounded and captured.
        boolean above = false;
        boolean below = false;
        boolean left = false;
        boolean right = false;
        int x = kingTile.getX();
        int y = kingTile.getY();
        //Check the tiles above, below, left and right
        if (y != 0) above = kingCheckAbove(tiles, kingTile);
        if (y != 10) below = kingCheckBelow(tiles, kingTile);
        if (x != 0)  left = kingCheckLeft(tiles, kingTile);
        if (x != 10) right = kingCheckRight(tiles, kingTile);
        if (above && below && left && right) {
            tiles[x][y] = ' ';
            return true;
        }
        return false;
    }
    private boolean kingCheckAbove(char[][] tiles, Coordinate kingTile) {
    	int x = kingTile.getX();
    	int y = kingTile.getY();
    	//If it's the throne
        if (x == 5 && y == 5) {
            return true;
        }
        //Otherwise if a piece is there
        else if (tiles[x][y - 1] == 'B') {
            return true;
        }
        return false;
    }
    private boolean kingCheckBelow(char[][] tiles, Coordinate kingTile) {
    	int x = kingTile.getX();
    	int y = kingTile.getY();
    	//If it's the throne
    	if (x == 5 && y == 5) {
            return true;
        }
    	//Otherwise if a piece is there
        else if (tiles[x][y + 1] == 'B') {
            return true;
        }
        return false;
    }
    private static boolean kingCheckLeft(char[][] tiles, Coordinate kingTile) {
    	int x = kingTile.getX();
    	int y = kingTile.getY();
    	//If it's the throne
    	if (x == 5 && y == 5) {
            return true;
        }
    	 //Otherwise if a piece is there
        else if (tiles[x - 1][y] == 'B') {
            return true;
        }
        return false;
    }
    private static boolean kingCheckRight(char[][] tiles, Coordinate kingTile) {
    	int x = kingTile.getX();
    	int y = kingTile.getY();
    	//If it's the throne
    	if (x == 5 && y == 5) {
            return true;
        }
    	 //Otherwise if a piece is there
        else if (tiles[x + 1][y] == 'B') {
            return true;
        }
        return false;
    }
}

