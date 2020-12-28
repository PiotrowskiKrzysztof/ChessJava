package chess.engine.player.ai;

import chess.engine.board.Board;
import chess.engine.pieces.Piece;
import chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator { // klasa do obliczenia punktów szachownicy

    // funkcja obliczająca
    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board, board.blackPlayer(), depth);
    }

    // obliczanie punktów gracza
    private int scorePlayer(final Board board, final Player player, final int depth) {
        return pieceValue(player);
    }

    // obliczanie punktów wszystkich aktywnych figór
    private static int pieceValue(final Player player) {
        int pieceValueScore = 0;
        for(final Piece piece : player.getActivePieces()) {
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }
}
