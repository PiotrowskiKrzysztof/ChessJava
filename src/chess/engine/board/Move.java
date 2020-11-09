package chess.engine.board;

import chess.engine.pieces.Piece;

public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final int destimationCoordinate;

    // klasa ruchu
    private Move(final Board board, final Piece movedPiece, final int destimationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destimationCoordinate = destimationCoordinate;
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
