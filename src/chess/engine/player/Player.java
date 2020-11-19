package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.pieces.King;
import chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = legalMoves;
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty(); // przekazujemy aktualną pozycję King'a
    }

    //getter do pobierania King'a
    public King getPlayerKing()
    {
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves()
    {
        return this.legalMoves;
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>(); // przekazując listę sprawdzamy, czy zostanie podjęty atak (odnosi się to do zmiennej isInCheck, ustawianej w konstruktorze Player'a)
        for(final Move move : moves)
        {
            if(piecePosition == move.getDestinationCoordinate()) // sprawdzamy, czy położenie przeciwnika przecina się z pozycją King'a
            {
                attackMoves.add(move); // jeśli tak, to jest to ruch atakujący
            }
        }
        return ImmutableList.copyOf(attackMoves);
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

    // sprawsza czy król jest w szachu
    public boolean isInCheck() {
        return this.isInCheck;
    }

    // poniższe metody będzie trzeba dokończyć !
    // sprawdza czy jest szach-mat
    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    // sprawdza czy wystąpił pat (nie ma szach-matu, a król przegrywającego gracza nie ma możliwości ruchu)
    public boolean isInStaleMate() {
        return !this.isInCheck() && !hasEscapeMoves();
    }

    // sprawdzamy czy dana figura ma drogę ucieczki (w tym przypadku figurą będzie King)
    protected boolean hasEscapeMoves() {
        for(final Move move : this.legalMoves)
        {
            final MoveTransition transition = makeMove(move);
            if(transition.getMoveStatus().isDone())  // if sprawdza, czy jesteśmy w stanie wykonać ruch (czy nie jesteśmy w sytuacji bez wyjścia)
            {
                return true;
            }
        }
        return false;
    }

    // sprawdza czy jest możliwa roszada (roszada - przesunięcie króla oraz wieży w jednym ruchu)
    public boolean isCastled() {
        return false;
    }

    // po wykonaniu ruchu dostajesz informację o przejściu
    public MoveTransition makeMove(final Move move) {
        if(!isMoveLegal(move))
        {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE); // jeśli ruch nie jest dozwolony, zwracamy tego samego boarda - nic się nie dzieje
        }
        // jeśli jednak ruch okaże się możliwy do wykonania...
        final Board transitionBoard = move.execute(); // ...po prostu wykonujemy go ;)
        // w tym miejscu sprawdzamy ataki oponenta aktualnego gracza
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(), transitionBoard.currentPlayer().getLegalMoves());
        // jeśli jest jakikolwiek atak King'a
        if(!kingAttacks.isEmpty())
        {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    // przechowuje Kolekcję z możliwymi roszadami graczy
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals);

}
