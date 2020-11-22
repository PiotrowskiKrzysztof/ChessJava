package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.board.Move.KingSideCastleMove;
import chess.engine.board.Move.QueenSideCastleMove;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player{
    public BlackPlayer(final Board board, final Collection<Move> whiteStandardLegalMoves, final Collection<Move> blackStandardLegalMoves) {

        //argumenty: szachownica, dozwolone ruchy twoich figur, dozwolone ruchy figur przeciwnika
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);

    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals) {
        final List<Move> kingCastles = new ArrayList<>(); // Lista do przechowywania roszad

        // jeżeli jest to pierwszy ruch oraz król nie jest w szachu
        if(this.playerKing.isFirstMove() && !this.isInCheck()) {
            // jeżeli pola na prawo od czarnego króla są wolne
            if(!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()) {

                final Tile rookTile = this.board.getTile(7); // pozycja wieży po prawej stronie

                // jeżeli prawa wieża jest na swojej pozycji oraz jest to pierwszy ruch
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // sprawdza czy przeciwnik nie ma ruchów atakujących na pozycje 5 i 6 oraz czy figura na pozycji wieży jest wieżą
                    if(Player.calculateAttacksOnTile(5, opponentsLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(6, opponentsLegals).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 6, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 5));
                    }
                }
            }
            // jeżeli pola na lewo od czarnego króla są wolne
            if(!this.board.getTile(1).isTileOccupied() &&
                    !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(3).isTileOccupied()) {

                final Tile rookTile = this.board.getTile(0); // pozycja wieży po lewej stronie

                // jeżeli lewa wieża jest na swojej pozycja oraz jest to pierwszy ruch
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()
                        && Player.calculateAttacksOnTile(2, opponentsLegals).isEmpty()
                        && Player.calculateAttacksOnTile(3, opponentsLegals).isEmpty()
                        && rookTile.getPiece().getPieceType().isRook()) {
                    kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 2, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
                }
            }
        }

        return ImmutableList.copyOf(kingCastles); //ImmutableList zwraca listę, której nie można zmienić (biblioteka guava)
    }
}
