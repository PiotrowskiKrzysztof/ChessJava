package chess.engine.player.ai;

import chess.engine.board.Board;

public interface BoardEvaluator {
    int evaluate(Board board, int depth);  // im większa liczba dodatnia - wygrywa biały kolor, im mniejsza (w tym ujemna) liczba - wygrywa czarny kolor
}
