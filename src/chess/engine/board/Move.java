package chess.engine.board;

import chess.engine.board.Board.Builder;
import chess.engine.pieces.Pawn;
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

    // tworzymy hashCode ruchu zawierający informacje o wykonanym ruchu
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.destinationCoordinate;
        result = prime * result  + this.movedPiece.hashCode();

        return  result;
    }

    //porównywanie ruchów - potrzebne do wykonania ataku, który zadamy
    //figury posiadają całą Kolekcję ruchów i będziemy sprawdzać, czy ruch, ktory chcemy wykonać istnieje w Kolekcji
    @Override
    public boolean equals(final Object other) {
        if(this == other) {
            return true;
        }
        if(!(other instanceof Move)) { //instanceof porównuje typy, sprawdza czy other jest klasą Move
            return false;
        }
        final Move otherMove = (Move) other;
        return  getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getMovedPiece().equals(otherMove.getMovedPiece());

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

    // sprawdza czy można atakować
    public boolean isAttack() {
        return false;
    }

    // sprawdza czy to ruch roszadowy (castling)
    public boolean isCastlingMove() {
        return false;
    }

    // getter atakowanej figury
    public Piece getAttackedPiece() {
        return null;
    }

    // Zwraca nową tablicę z wykonanym ruchem
    public Board execute() {

        final Builder builder = new Builder();

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

        // tworzymy hashCode ataku zawierający informacje o wykonanym ataku
        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        //porównywanie ruchów - potrzebne do wykonania ataku, który zadamy
        //figury posiadają całą Kolekcję ruchów i będziemy sprawdzać, czy ruch, ktory chcemy wykonać istnieje w Kolekcji
        @Override
        public boolean equals(final Object other) {
            if(this == other) {
                return true;
            }
            if(!(other instanceof AttackMove)) { //instanceof porównuje typy, sprawdza czy other jest klasą Move
                return false;
            }
            final  AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        // METODA DO UZUPEŁNIENIA! Wykonanie ruchu jako zwrócenie board'a
        @Override
        public Board execute() {
            return null;
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece () {
            return this.attackedPiece;
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

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            // przypisanie pozycji figur gracza
            for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            // przypisanie pozycji figur przeciwnika
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn)this.movedPiece.movePiece(this); // pobiera nowe koordynaty i tworzy na ich miejscu figurę
            builder.setPiece(movedPiece); // przypisanie pozycji po wykonanym ruchu
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
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
