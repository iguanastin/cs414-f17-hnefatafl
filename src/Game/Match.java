package Game;

public class Match {
	private Board board;
	private User attacker;
	private User defender;
	
	public Match(User attacker, User defender) {
		this.attacker = attacker;
		this.defender = defender;
		board = new Board(11, 11);
		board.initialize(attacker, defender);
	}
}
