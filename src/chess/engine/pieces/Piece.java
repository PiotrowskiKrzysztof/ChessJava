package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {

    protected  final PieceType pieceType; // typ figury
    protected final int piecePosition; // pozycja figury
    protected final Alliance pieceAlliance; // określa kolor figury
    protected final boolean isFirstMove; // zmienna używana do określenia czy ruch wykonujemy jako pierwszy z serii ruchów

    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance)
    {
        this.pieceType = pieceType;
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

    public PieceType getPieceType() { return this.pieceType; }

    public abstract Collection<Move> calculateLegalMoves(final Board board); // zwracamy zbiór możliwych (prawidłowych) ruchów elementu

    public abstract Piece movePiece(Move move); // // pobiera ruch figury i tworzy nową figurę na przesuniętym miejscu

    public enum PieceType // enum, wyliczamy figury po ich nazwach w celu ulepszenia wyświetlania metodą toString
    {
        // każdej z figur przyznajemy "nazwę"
        PAWN("P") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        KNIGHT("N") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        BISHOP("B") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        ROOK("R") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        QUEEN("Q") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        KING("K") {
            @Override
            public boolean isKing() {
                return true;
            }
        };

        private String pieceName;

        PieceType(final String pieceName)
        {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName; // w metodzie toString zwracamy "nazwę" figury
        }

        public abstract boolean isKing();
    }

}
