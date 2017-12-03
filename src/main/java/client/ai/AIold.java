package client.ai;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import common.game.*;

public class AIold {
	
	public static int[] makeMove(Match match, int AIid, boolean isDefender) {
		//Copy the match's board so we can explore states without modifying the match
		Board board = new Board(11,11,match.getBoard().getTiles().clone());
		Tile[][] tiles = board.getTiles();
		//Sort pieces into lists for quicker move exploration
		ArrayList<Tile> attackPieces = new ArrayList<Tile>();
		ArrayList<Tile> defensePieces = new ArrayList<Tile>();
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				if (tiles[i][j].hasPiece()) {
					if (tiles[i][j].getPiece().getUser() == AIid) {
						if (isDefender) {
							defensePieces.add(tiles[i][j]);
						}
						else {
							attackPieces.add(tiles[i][j]);
						}
					}
					else {
						if (isDefender) {
							attackPieces.add(tiles[i][j]);
						}
						else {
							defensePieces.add(tiles[i][j]);
						}
					}
				}
			}
		}
		//Find the best move
		double move[] = new double[5];
		move = negamaxab(tiles, 3, AIid, isDefender, attackPieces, defensePieces, Double.MIN_VALUE, Double.MAX_VALUE);
		int bestMove[] = new int[4];
		for (int i = 0; i < 4; i++) {
			bestMove[i] = (int) move[i];
		}
		return bestMove;
	}
	
	private static double[] negamaxab(Tile[][] tiles, int depth, int AIid, boolean isDefender, ArrayList<Tile> attackPieces, ArrayList<Tile> defensePieces, double a, double b) {
		if (depth == 0) {
			double value;
			if (isDefender) {
				value = defendHeuristic(tiles);
			}
			else {
				value = attackHeuristic(tiles);
			}
			double[] stateValue = {-1, -1, -1, -1, value};
			return stateValue;
		}
		double bestMove[] = {-1, -1, -1, -1, Double.MIN_VALUE};
		if (isDefender) {
			for (int i = 0; i < defensePieces.size(); i++) {
				Tile tile = defensePieces.get(i);
				ArrayList<Tile> moves = getAvailableMoves(tiles, tile);
				for (int j = 0; j < moves.size(); j++) {
					//Copy board state so we can safely modify it
					Tile[][] moveTiles = tiles.clone();
					//Make the move
					moves.get(j).setPiece(tile.getPiece());
					tile.removePiece();
					HashSet<Tile> captured = capture(moveTiles, moves.get(j));
					ArrayList<Tile> newAttack = new ArrayList<Tile>();
					for (int k = 0; k < attackPieces.size(); k++) {
						if (!(captured.contains(attackPieces.get(k)))) newAttack.add(attackPieces.get(k));
					}
					//Explore the new move from other perspective
					double[] move = negamaxab(moveTiles, depth-1, AIid, !(isDefender), newAttack, defensePieces, -b, -a);
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
			for (int i = 0; i < attackPieces.size(); i++) {
				Tile tile = attackPieces.get(i);
				ArrayList<Tile> moves = getAvailableMoves(tiles, tile);
				for (int j = 0; j < moves.size(); j++) {
					//Copy board state so we can safely modify it
					Tile[][] moveTiles = tiles.clone();
					//Make the move
					moves.get(j).setPiece(tile.getPiece());
					tile.removePiece();
					HashSet<Tile> captured = capture(moveTiles, moves.get(j));
					ArrayList<Tile> newDefense = new ArrayList<Tile>();
					for (int k = 0; k < defensePieces.size(); k++) {
						if (!(captured.contains(defensePieces.get(k)))) newDefense.add(defensePieces.get(k));
					}
					//Explore the new move from other perspective
					double[] move = negamaxab(moveTiles, depth-1, AIid, !(isDefender), attackPieces, newDefense, -b, -a);
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
	
	private static ArrayList<Tile> getAvailableMoves(Tile[][] tiles, Tile tile) {
        ArrayList<Tile> availableMoves = new ArrayList<Tile>();
        //Confirm tile has a piece on it
        if (tile.hasPiece()) {
            int x = tile.getX();
            int y = tile.getY();
            //Check areas left of tile
            if (x != 0) {
                for (int i = x - 1; i >= 0; i--) {
                    //Check if the tile has no piece on it and is not the throne
                    if (!(tiles[i][y].hasPiece()) && !(tiles[i][y].getType().equals(TileType.THRONE)))
                        availableMoves.add(tiles[i][y]);
                    else
                        break;
                }
            }
            //Check areas right of tile
            if (x != 10) {
                for (int i = x + 1; i <= 10; i++) {
                	//Check if the tile has no piece on it and is not the throne
                    if (!(tiles[i][y].hasPiece()) && !(tiles[i][y].getType().equals(TileType.THRONE)))
                        availableMoves.add(tiles[i][y]);
                    else
                        break;
                }
            }
            //Check above tile
            if (y != 0) {
                for (int i = y - 1; i >= 0; i--) {
                	//Check if the tile has no piece on it and is not the throne
                    if (!(tiles[x][i].hasPiece()) && !(tiles[x][i].getType().equals(TileType.THRONE)))
                        availableMoves.add(tiles[x][i]);
                    else
                        break;
                }
            }
            //Check below tile
            if (y != 10) {
                for (int i = y + 1; i <= 10; i++) {
                	//Check if the tile has no piece on it and is not the throne
                    if (!(tiles[x][i].hasPiece()) && !(tiles[x][i].getType().equals(TileType.THRONE)))
                        availableMoves.add(tiles[x][i]);
                    else
                        break;
                }
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
	
	private static double attackHeuristic(Tile[][] tiles) {
		int aPieces = 0;
		int dPieces = 0;
		double aDistances = 0;
		//Count number of attacking and defending pieces.
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				if (tiles[i][j].hasPiece()) {
					if (tiles[i][j].getPiece().getColor().equals(Color.BLACK)) {
						aPieces++;
						//Add Manhattan distance of this piece from the center to aDistances
						aDistances += Math.abs(5 - i) + Math.abs(5 - j);
					}
					else {
						dPieces++;
					}
				}
			}
		}
		//Divide distances by attacking pieces to get average distance from the center
		aDistances /= aPieces;
		//Divide aDistances by 10 so it becomes a decimal value, subtract from 1 so shorter distances are worth more.
		aDistances = 1 - (aDistances / 10);
		//Generate heuristic. Values before decimal place are how many more pieces attacker has on the board, while 
		//the values decimal place are larger the closer the atttacker's pieces are to the center on average.
		return (aPieces - dPieces) + aDistances;
	}
	
	private static double defendHeuristic(Tile[][] tiles) {
		int aPieces = 0;
		int dPieces = 0;
		double dDistances = 0;
		//Count number of attacking and defending pieces.
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				if (tiles[i][j].hasPiece()) {
					if (tiles[i][j].getPiece().getColor().equals(Color.BLACK)) {
						aPieces++;
					}
					else {
						dPieces++;
						//Add Manhattan distance of this piece from the center to aDistances
						dDistances += Math.abs(5 - i) + Math.abs(5 - j);
					}
				}
			}
		}
		//Divide distances by defending pieces to get average distance from the center
		dDistances /= dPieces;
		//Divide aDistances by 10 so it becomes a decimal value.
		dDistances /= 10;
		//Generate heuristic. Values before decimal place are how many more pieces attacker has on the board, while 
		//the values decimal place are larger the closer the atttacker's pieces are to the center on average.
		return (dPieces - aPieces) + dDistances;
	}
	private static HashSet<Tile> capture(Tile[][] tiles, Tile capturerTile) {
        HashSet<Tile> capturedTiles = new HashSet<Tile>();
        int x = capturerTile.getX();
        int y = capturerTile.getY();
        //Capture top piece if capturable
        if (y > 1) {
           if (aboveCapturable(tiles, capturerTile)) {
        	   //If piece to be captured is a king
               if (tiles[x][y - 1].getPiece().isKing()) {
            	   //kingCapture attempts to capture the king.
            	   if (kingCapture(tiles, tiles[x][y - 1])) {
            		   //If king was successfully captured
                       capturedTiles.add(tiles[x][y - 1]);
                   }
               }
               //Capture the piece
               else {
            	   capturedTiles.add(tiles[x][y - 1]);
                   tiles[x][y - 1].removePiece();
               }
           }
        }
        //Capture bottom piece if capturable
        if (y < 9) {
            if (belowCapturable(tiles, capturerTile)) {
            	//If piece to be captured is a king
                if (tiles[x][y + 1].getPiece().isKing()) {
                	//kingCaptured attempts to capture the king.
                    if (kingCapture(tiles, tiles[x][y + 1])) {
                    	//If king was successfully captured
                        capturedTiles.add(tiles[x][y + 1]);
                    }
                }
                //Capture the piece
                else {
                	capturedTiles.add(tiles[x][y + 1]);
                    tiles[x][y + 1].removePiece();
                }
            }
        }
        //Capture left piece if capturable
        if (x > 1) {
            if (leftCapturable(tiles, capturerTile)) {
                //If piece to be captured is a king
                if (tiles[x - 1][y].getPiece().isKing()) {
                    //kingCaptured attempts to capture the king.
                    if (kingCapture(tiles, tiles[x - 1][y])) {
                        //If king was successfully captured
                        capturedTiles.add(tiles[x - 1][y]);
                    }
                }
                //Capture the piece
                else {
                    capturedTiles.add(tiles[x - 1][y]);
                    tiles[x - 1][y].removePiece();
                }
            }
        }
        //Capture right piece if capturable
        if (x < 9) {
            if(rightCapturable(tiles, capturerTile)) {
                //If piece to be captured is a king
                if (tiles[x + 1][y].getPiece().isKing()) {
                	//kingCaptured attempts to capture the king.
                    if (kingCapture(tiles, tiles[x + 1][y])) {
                    	//If king was successfully captured
                        capturedTiles.add(tiles[x + 1][y]);
                    }
                }
                //Capture the piece
                else {
                	capturedTiles.add(tiles[x + 1][y]);
                	tiles[x + 1][y].removePiece();
                }
            }
        }
        return capturedTiles;
    }
    private static boolean aboveCapturable(Tile[][] tiles, Tile capturerTile) {
    	int x = capturerTile.getX();
    	int y = capturerTile.getY();
    	if (tiles[x][y - 1].hasPiece()) {
            //Check if top piece belongs to the enemy
            if (!(capturerTile.getPiece().getUser() == tiles[x][y - 1].getPiece().getUser())) {
                //Check if there a piece on the other side of that piece
                if (tiles[x][y - 2].hasPiece()) {
                    //Check if that piece belongs to the capturer
                    if (capturerTile.getPiece().getUser() == tiles[x][y - 2].getPiece().getUser()) {
                        //Make sure that piece isn't a King
                        if (!(tiles[x][y - 2].getPiece().isKing())) {
                        	return true;
                        }
                    }
                }
            }
    	}
        return false;
    }
    private static boolean belowCapturable(Tile[][] tiles, Tile capturerTile) {
    	int x = capturerTile.getX();
    	int y = capturerTile.getY();
    	if (tiles[x][y + 1].hasPiece()) {
            //Check if top piece belongs to the enemy
            if (!(capturerTile.getPiece().getUser() == tiles[x][y + 1].getPiece().getUser())) {
                //Check if there a piece on the other side of that piece
                if (tiles[x][y + 2].hasPiece()) {
                    //Check if that piece belongs to the capturer
                    if (capturerTile.getPiece().getUser() == tiles[x][y + 2].getPiece().getUser()) {
                        //Make sure that piece isn't a King
                        if (!(tiles[x][y + 2].getPiece().isKing())) {
                        	return true;
                        }
                    }
                }
            }
    	}
        return false;
    }
    private static boolean leftCapturable(Tile[][] tiles, Tile capturerTile) {
    	int x = capturerTile.getX();
    	int y = capturerTile.getY();
    	if (tiles[x - 1][y].hasPiece()) {
            //Check if top piece belongs to the enemy
            if (!(capturerTile.getPiece().getUser() == tiles[x - 1][y].getPiece().getUser())) {
                //Check if there a piece on the other side of that piece
                if (tiles[x - 2][y].hasPiece()) {
                    //Check if that piece belongs to the capturer
                    if (capturerTile.getPiece().getUser() == tiles[x - 2][y].getPiece().getUser()) {
                        //Make sure that piece isn't a King
                        if (!(tiles[x - 2][y].getPiece().isKing())) {
                        	return true;
                        }
                    }
                }
            }
    	}
    	return false;
    }
    private static boolean rightCapturable(Tile[][] tiles, Tile capturerTile) {
    	int x = capturerTile.getX();
    	int y = capturerTile.getY();
    	if (tiles[x + 1][y].hasPiece()) {
            //Check if top piece belongs to the enemy
            if (!(capturerTile.getPiece().getUser() == tiles[x + 1][y].getPiece().getUser())) {
                //Check if there a piece on the other side of that piece
                if (tiles[x + 2][y].hasPiece()) {
                    //Check if that piece belongs to the capturer
                    if (capturerTile.getPiece().getUser() == tiles[x + 2][y].getPiece().getUser()) {
                        //Make sure that piece isn't a King
                        if (!(tiles[x + 2][y].getPiece().isKing())) {
                        	return true;
                        }
                    }
                }
            }
    	}
    	return false;
    }

    //Captures the king if surrounded.  Returns true is king was captured, false otherwise.
    private static boolean kingCapture(Tile[][] tiles, Tile kingTile) {
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
            kingTile.removePiece();
            return true;
        }
        return false;
    }
    private static boolean kingCheckAbove(Tile[][] tiles, Tile kingTile) {
    	int x = kingTile.getX();
    	int y = kingTile.getY();
    	//If it's the throne
        if (tiles[x][y - 1].getType().equals(TileType.THRONE)) {
            return true;
        }
        //Otherwise if a piece is there
        else if (tiles[x][y - 1].hasPiece()) {
            //Check if piece on tile belongs to the enemy
            if (!(kingTile.getPiece().getUser() == tiles[x][y - 1].getPiece().getUser())) {
                return true;
            }
        }
        return false;
    }
    private static boolean kingCheckBelow(Tile[][] tiles, Tile kingTile) {
    	int x = kingTile.getX();
    	int y = kingTile.getY();
    	//If it's the throne
        if (tiles[x][y + 1].getType().equals(TileType.THRONE)) {
            return true;
        }
        //Otherwise if a piece is there
        else if (tiles[x][y + 1].hasPiece()) {
            //Check if piece on tile belongs to the enemy
            if (!(kingTile.getPiece().getUser() == tiles[x][y + 1].getPiece().getUser())) {
                return true;
            }
        }
        return false;
    }
    private static boolean kingCheckLeft(Tile[][] tiles, Tile kingTile) {
    	int x = kingTile.getX();
    	int y = kingTile.getY();
    	//If it's the throne
        if (tiles[x - 1][y].getType().equals(TileType.THRONE)) {
            return true;
        }
        //Otherwise if a piece is there
        else if (tiles[x - 1][y].hasPiece()) {
            //Check if piece on tile belongs to the enemy
            if (!(kingTile.getPiece().getUser() == tiles[x - 1][y].getPiece().getUser())) {
                return true;
            }
        }
        return false;
    }
    private static boolean kingCheckRight(Tile[][] tiles, Tile kingTile) {
    	int x = kingTile.getX();
    	int y = kingTile.getY();
    	//If it's the throne
        if (tiles[x + 1][y].getType().equals(TileType.THRONE)) {
            return true;
        }
        //Otherwise if a piece is there
        else if (tiles[x + 1][y].hasPiece()) {
            //Check if piece on tile belongs to the enemy
            if (!(kingTile.getPiece().getUser() == tiles[x + 1][y].getPiece().getUser())) {
                return true;
            }
        }
        return false;
    }
}

