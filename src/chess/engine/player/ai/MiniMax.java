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
        final long startTime = System.currentTimeMillis(); // mierzymy czas w milisekundach

        Move bestMove = null; // TODO: na razie null, do określenia czym jest najlepszy ruch

        int highestSeenValue = Integer.MIN_VALUE; // przypisujemy do zmiennej największą możliwą liczbę ujemną
        int lowestSeenValue = Integer.MAX_VALUE; // przypisujemy do zmiennej największą możliwą liczbę dodatnią
        int currentValue;

        System.out.println(board.currentPlayer() + " zastanawia się nad ruchem na głębokości " + depth);

        int numMoves = board.currentPlayer().getLegalMoves().size();

        for(final Move move : board.currentPlayer().getLegalMoves())
        {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone())
            {
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(), depth - 1) : max(moveTransition.getTransitionBoard(), depth - 1);

                if(board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue)
                {
                    highestSeenValue = currentValue;
                    bestMove = move;
                }
                else if(board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue)
                {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;
        return bestMove;
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
                final int currentValue = min(moveTransition.getTransitionBoard(), depth - 1);
                if(currentValue >= highestSeenValue) // porównanie wartości w drzewie ruchów
                {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue; // zwracamy najniższą możliwą wartość na zadanym poziomie drzewa
    }
}
