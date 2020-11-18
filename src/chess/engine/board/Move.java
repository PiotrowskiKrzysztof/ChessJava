package chess.engine.board;

import chess.engine.pieces.Piece;

public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final int destinationCoordinate;

    public static final Move NULL_MOVE = new NullMove();

    // klasa ruchu
    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
    }

    // getter aktualnego położenia
    public int getCurrentCoordinate()
    {
        return this.getMovedPiece().getPiecePosition();
    }

    // getter zwracający współrzędną miejsca docelowego
    public int getDestinationCoordinate()
    {
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece() {
        return this.movedPiece;
    }

    // Zwraca nową tablicę z wykonanym ruchem
    public Board execute() {

        final Board.Builder builder = new Board.Builder();

        // zwraca położenie figur gracza
        for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
            // DO ZROBIENIA! trzeba dodać hashcode
            if(!this.movedPiece.equals(piece)) {
                builder.setPiece(piece);
            }
        }

        // zwraca położenie figur przeciwnika
        for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }

        // ruch przesuniętej figury
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());

        return builder.build();
    }

    // klasa zwyczajnego ruchu
    public static final class MajorMove extends Move {

        public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }

    // klasa ruchu atakującego
    public static class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }
        // METODA DO UZUPEŁNIENIA! Wykonanie ruchu jako zwrócenie board'a
        @Override
        public Board execute() {
            return null;
        }
    }

    // klasa zwyczajnego ruchu figury Pawn
    public static final class PawnMove extends Move {

        public PawnMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }
    // TODO: DO IMPLEMENTACJI CIAŁA KLAS WYKONYWANIA POSZCZEGÓLNYCH RUCHÓW
    // ruch atakujący figury Pawn
    public static class PawnAttackMove extends AttackMove {

        public PawnAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
    }

    // atak en passant figury - specyficzne dla niej
    public static final class PawnEnPassantAttackMove extends PawnAttackMove {

        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
    }

    // skok figury Pawn - specyficzny ruch dla figury
    public static final class PawnJump extends Move {

        public PawnJump(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }

    static abstract class CastleMove extends Move {

        public CastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }

    public static final class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }

    public static final class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
    }

    // ruch pusty
    public static final class NullMove extends Move {

        public NullMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Nie da się wykonać ruchu pustego!");
        }
    }

    public static class MoveFactory
    {
        private MoveFactory()
        {
            throw new RuntimeException("Nie da się utworzyć instancji!");
        }

        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate)
        {
            for(final Move move: board.getAllLegalMoves())
            {
                if(move.getCurrentCoordinate() == currentCoordinate && move.getDestinationCoordinate() == destinationCoordinate)
                {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}
