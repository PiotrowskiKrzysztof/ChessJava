package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.engine.board.Move.*;

public class WhitePlayer extends Player{
    public WhitePlayer(final Board board, final Collection<Move> whiteStandardLegalMoves, final Collection<Move> blackStandardLegalMoves) {

        //argumenty: szachownica, dozwolone ruchy twoich figur, dozwolone ruchy figur przeciwnika
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    // metoda do roszad (specjalny ruch, zamiana króla oraz wieży)
    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals) {

        final List<Move> kingCastles = new ArrayList<>(); // Lista do przechowywania roszad

        // jeżeli jest to pierwszy ruch oraz król nie jest w szachu
        if(this.playerKing.isFirstMove() && !this.isInCheck()) {
            // jeżeli pola na prawo od białego króla są wolne
            if(!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()) {

                final Tile rookTile = this.board.getTile(63); // pozycja wieży po prawej stronie

                // jeżeli prawa wieża jest na swojej pozycji oraz jest to pierwszy ruch
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // sprawdza czy przeciwnik nie ma ruchów atakujących na pozycje 61 i 62 oraz czy figura na pozycji wieży jest wieżą
                    if(Player.calculateAttacksOnTile(61, opponentsLegals).isEmpty() &&
                       Player.calculateAttacksOnTile(62, opponentsLegals).isEmpty() &&
                       rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 62, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 61));
                    }
                }
            }
            // jeżeli pola na lewo od białego króla są wolne
            if(!this.board.getTile(59).isTileOccupied() &&
               !this.board.getTile(58).isTileOccupied() &&
               !this.board.getTile(57).isTileOccupied()) {

                final Tile rookTile = this.board.getTile(56); // pozycja wieży po lewej stronie

                // jeżeli lewa wieża jest na swojej pozycja oraz jest to pierwszy ruch
                if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()
                        && Player.calculateAttacksOnTile(58, opponentsLegals).isEmpty()
                        && Player.calculateAttacksOnTile(59, opponentsLegals).isEmpty()
                        && rookTile.getPiece().getPieceType().isRook()) {
                    kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 58, (Rook)rookTile.getPiece(), rookTile.getTileCoordinate(), 59));
                }
            }
        }

        return ImmutableList.copyOf(kingCastles); //ImmutableList zwraca listę, której nie można zmienić (biblioteka guava)
    }
}
