package chess.engine.player.ai;

import chess.engine.board.Board;
import chess.engine.board.Move;

public class MiniMax implements MoveStrategy { // klasa implementująca algorytm minimax

    private final BoardEvaluator boardEvaluator;

    public MiniMax()
    {
        this.boardEvaluator = null; // TODO: ustawiam na null dla zarysu! Do poprawki
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    @Override
    public Move execute(Board board, int depth) {
        return null;
    }
}
