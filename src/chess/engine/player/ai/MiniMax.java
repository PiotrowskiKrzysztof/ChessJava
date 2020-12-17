package chess.engine.player.ai;

import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.player.MoveTransition;

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
    public Move execute(Board board, int depth) { // metoda zwracająca najlepszy możliwy ruch na planszy
        return null;
    }

    // funkcje pomocnicze dla execute() - min i max
    // TODO: rekurencja! pilnować warunku stopu
    public int min(final Board board, final int depth) // symulacja procesu minimalizacji
    {
        if(depth == 0 /* lub game over*/)
        {
            return this.boardEvaluator.evaluate(board, depth);
        }

        int lowestSeenValue = Integer.MAX_VALUE; // nadajemy najwyższą możliwą wartość (nieosiągalną dla Board'a)
        for(final Move move : board.currentPlayer().getLegalMoves()) // poruszamy się pętlą po wszystkich dozwolonych ruchach
        {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move); // symulacja wykonywania ruchu celem wyliczenia
            if(moveTransition.getMoveStatus().isDone())
            {
                final int currentValue = max(moveTransition.getTransitionBoard(), depth - 1);
                if(currentValue <= lowestSeenValue) // porównanie wartości w drzewie ruchów
                {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue; // zwracamy najniższą możliwą wartość na zadanym poziomie drzewa
    }

    public int max(final Board board, final int depth)
    {
        if(depth == 0 /* lub game over*/)
        {
            return this.boardEvaluator.evaluate(board, depth);
        }

        int highestSeenValue = Integer.MIN_VALUE; // nadajemy najniższą możliwą wartość (nieosiągalną dla Board'a)
        for(final Move move : board.currentPlayer().getLegalMoves()) // poruszamy się pętlą po wszystkich dozwolonych ruchach
        {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move); // symulacja wykonywania ruchu celem wyliczenia
            if(moveTransition.getMoveStatus().isDone())
            {
                final int currentValue = max(moveTransition.getTransitionBoard(), depth - 1);
                if(currentValue >= highestSeenValue) // porównanie wartości w drzewie ruchów
                {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue; // zwracamy najniższą możliwą wartość na zadanym poziomie drzewa
    }
}
