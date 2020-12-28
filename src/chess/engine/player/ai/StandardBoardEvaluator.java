package chess.engine.player.ai;

import chess.engine.board.Board;
import chess.engine.pieces.Piece;
import chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator { // klasa do obliczenia punktów szachownicy

    private static final int CHECK_BONUS = 50; // pynkty, które się zdobywa za ruch ustalający szach
    private static final int CHECK_MATE_BONUS = 10000; // pynkty, które się zdobywa za szach mat
    private static final int DEPTH_BONUS = 100; // zmienna do obliczania punktów za głębokość
    private static final int CASTLE_BONUS = 60; // punkty za wykonanie roszady

    // funkcja obliczająca
    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board, board.blackPlayer(), depth);
    }

    // obliczanie punktów gracza
    private int scorePlayer(final Board board, final Player player, final int depth) {
        return pieceValue(player) + mobility(player) + check(player) + checkmate(player, depth) + castled(player);
    }

    // jeżeli zagrano ruch roszady dodaje dodatkowe punkty, w innym przypadku zwraca 0
    private static int castled(final Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    // jeżeli był szach mat, dodaje dodatkowe punkty, w innym przypadku zwraca 0
    private static int checkmate(final Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : 0;
    }

    // określa głębokość
    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    // jeżeli wystąpił szach, dodaje dodatkowe punkty, w innym przypadku zwraca 0
    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    // określa ilość możliwych ruchów do wykonania (czym więcej możliwości ma gracz, tym więcej punktów zdobywa)
    private static int mobility(final Player player) {
        return player.getLegalMoves().size();
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
