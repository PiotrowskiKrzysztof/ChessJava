package chess.engine.player;

import chess.engine.board.Board;
import chess.engine.board.Move;

public class MoveTransition { //klasa do przechowywania informacji o przejściach

    private final Board transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board transitionBoard, final Move move, final MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus()
    {
        return this.moveStatus;
    }

    public Board getTransitionBoard()
    {
        return this.transitionBoard;
    }
}
