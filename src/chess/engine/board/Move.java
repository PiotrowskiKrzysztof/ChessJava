package chess.engine.board;

import chess.engine.pieces.Piece;

public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final int destinationCoordinate;

    // klasa ruchu
    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
    }

    // getter zwracający współrzędną miejsca docelowego
    public int getDestinationCoordinate()
    {
        return this.destinationCoordinate;
    }

    // klasa zwyczajnego ruchu
    public static final class MajorMove extends Move {

        public MajorMove(final Board board, final Piece movedPiece, final int destimationCoordinate) {
            super(board, movedPiece, destimationCoordinate);
        }
    }

    // klasa ruchu atakującego
    public static final class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece movedPiece, final int destimationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destimationCoordinate);
            this.attackedPiece = attackedPiece;
        }
    }

}
