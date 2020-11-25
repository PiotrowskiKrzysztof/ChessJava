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
    private final int cachedHashCode;

    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove)
    {
        this.pieceType = pieceType;
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    // metoda slużąca do przeliczania hashcode'u (mogłaby być też jako override poniżej)
    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    // nadpisana metoda equals() - porównujemy obiekty, bazowy equals porównuje referencje
    @Override
    public boolean equals(final Object other) {
        if(this == other)
        {
            return true;
        }
        if(!(other instanceof Piece))
        {
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() && pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
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

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K") {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
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

        public abstract boolean isRook();
    }

}
