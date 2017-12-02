package client.ai;
import common.game.*;

public class AI {
	
	public static int[] makeMove(Match match, int AIid) {
		Board board = new Board(11,11,match.getBoard().getTiles().clone());
		int move[] = {-1, -1, -1, -1};
		move = negamaxab(board, 3, AIid, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return move;
	}
	
	private static double attackHeuristic(Board board) {
		Tile[][] tiles = board.getTiles();
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
	private static double defendHeuristic(Board board) {
		Tile[][] tiles = board.getTiles();
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
	private static int[] negamaxab(Board board, int depth, int AIid, int a, int b) {
		int bestMove[] = {3, 5, 2, 5};
		return bestMove;
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
		
		/*def negamaxIDSab(game, depthLimit):
	    for depth in range(depthLimit+1):
	        result, moves = negamaxab(game, depth)
	        if result == game.getWinningValue():
	            return result, moves
		*/
		public static void main(String[] args) {
			Match match = new Match(0,1);
			Board board = match.getBoard();
			System.out.println(board);
			System.out.println("Attack: " + attackHeuristic(board));
			System.out.println("Defense: " + defendHeuristic(board));
		}
}

