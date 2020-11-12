package chess.engine.board;

import chess.engine.Alliance;
import chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

public class Board {

    private final List<Tile> gameBoard;

    //konstruktor szachownicy
    private Board(Builder builder) {
        this.gameBoard = createGameBoard(builder);
    }

    //geter kwadracika szachownicy
    public Tile getTile(final int TileCoordinate)
    {
        return null;
    }

    //tworzenie szachownicy
    private static List<Tile> createGameBoard(final Builder builder) {
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for(int i = 0; i< BoardUtils.NUM_TILES; i++) {
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }

    //stworzenie szachownicy z ustawionymi figurami
    public static Board createStandardBoard() {

    }

    // klasa odpowiedzialna za budowanie szachownicy
    public static class Builder {

        //mapa przechowująca id kwadracika oraz figurę
        Map<Integer, Piece> boardConfig;
        //odpowiada za określenie kolejności wykonywania ruchu
        Alliance nextMoveMaker;

        public Builder() {

        }

        //ustawienie figury
        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        //ustalanie ruchów graczy
        public Builder setMoveMaker(final Alliance nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        //budowanie szachownicy
        public Board build() {
            return new Board(this);
        }

    }

}
