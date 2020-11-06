package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;

import java.util.List;

public abstract class Piece {
    protected final int piecePosition; // pozycja elementu
    protected final Alliance pieceAlliance;

    Piece(final int piecePosition, final Alliance pieceAlliance)
    {
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
    }

    public Alliance getPieceAlliance()
    {
        return this.pieceAlliance;
    }

    public abstract List<Move> calculateLegalMoves(final Board board); // zwracamy zbiór możliwych (prawidłowych) ruchów elementu

}
