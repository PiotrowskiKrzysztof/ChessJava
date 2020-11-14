package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.pieces.King;
import chess.engine.pieces.Piece;

import java.util.Collection;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;

    Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = legalMoves;
    }

    // ustala czy figura jest Królem
    protected King establishKing() {
        for(final Piece piece : getActivePieces()) {
            if(piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Tablica nie zostsała zwalidowana! Nie powinien tutaj sięgnąć!");
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    // poniższe metody będzie trzeba dokończyć !
    // sprawsza czy król jest w szachu
    public boolean isInCheck() {
        return false;
    }

    // sprawdza czy jest szach-mat
    public boolean isInCheckMate() {
        return false;
    }

    // sprawdza czy wystąpił pat (nie ma szach-matu, a król przegrywającego gracza nie ma możliwości ruchu)
    public boolean isInStaleMate() {
        return false;
    }

    // sprawdza czy jest możliwa roszada (roszada - przesunięcie króla oraz wieży w jednym ruchu)
    public boolean isCastled() {
        return false;
    }

    // po wykonaniu ruchu dostajesz informację o przejściu
    public MoveTransition makeMove(final Move move) {
        return null;
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();

}
