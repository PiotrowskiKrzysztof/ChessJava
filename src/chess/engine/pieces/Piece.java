package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {
    protected final int piecePosition; // pozycja figury
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove; // zmienna używana do określenia czy ruch wykonujemy jako pierwszy z serii ruchów

    Piece(final int piecePosition, final Alliance pieceAlliance)
    {
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        this.isFirstMove = false;
    }

     public int getPiecePosition() {
        return this.piecePosition;
     }

    public Alliance getPieceAlliance()
    {
        return this.pieceAlliance;
    }

    public boolean isFirstMove()
    {
        return this.isFirstMove;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board); // zwracamy zbiór możliwych (prawidłowych) ruchów elementu

    public enum PieceType // enum, wyliczamy figury po ich nazwach w celu ulepszenia wyświetlania metodą toString
    {
        PAWN("P"), // każdej z figur przyznajemy "nazwę"
        KNIGHT("N"),
        BISHOP("B"),
        ROOK("R"),
        QUEEN("Q"),
        KING("K");

        private String pieceName;

        PieceType(final String pieceName)
        {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName; // w metodzie toString zwracamy "nazwę" figury
        }
    }

}
