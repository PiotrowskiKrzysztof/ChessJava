package chess.engine.board;

import chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

//element szachownicy, kwadracik
public abstract class Tile {

    protected final int tileCoordinate; //koordynaty kwadracika
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPosibleEmptyTyiles(); //mapa przechowująca id oraz pusty kwadracik

    //przypisuje do mapy id oraz pusty element
    private static Map<Integer, EmptyTile> createAllPosibleEmptyTyiles() {

        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }
        //immutableMap (z biblioteki guava od google) powoduje, że mapa nigdy się nie zmieni
        return ImmutableMap.copyOf(emptyTileMap);
    }

    //tworzymy kwadracik
    public static Tile createTile(final int tileCoordinate, final Piece piece) {
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }

    //konstruktor kwadracika szachownicy
    private Tile(final int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }

    public abstract boolean isTileOccupied(); //sprawdza czy kwadracik zajęty
    public abstract Piece getPiece(); //otrzymuje figure
    public int getTileCoordinate()
    {
        return this.tileCoordinate;
    }

    //klasa do pustych kwadracikow
    public static final class EmptyTile extends Tile {

        //konstruktor pustego kwadracika szachownicy
        private EmptyTile(final int coordinate) {
            super(coordinate);
        }

        //metoda toString
        @Override
        public String toString()
        {
            return "-"; // dla pustego miejsca zwracamy "-"
        }

        //ustawia wartość fals na pustym kwadraciku
        @Override
        public boolean isTileOccupied() {
            return false;
        }

        //na pustym kwadracie nie moze być figury, więc zwraca null
        @Override
        public Piece getPiece() {
            return null;
        }
    }

    //kwadracik zajęty
    public static final class OccupiedTile extends Tile {

        //figura na kwadraciku
        private final Piece pieceOnTile;

        //konstruktor zajętego elementu przez figure
        private OccupiedTile(int tileCoordinate, final Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        //mteoda toString
        @Override
        public String toString()
        {
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() :
                    getPiece().toString();
        }

        //ustawia wartość true na zajętym kwadraciku
        @Override
        public boolean isTileOccupied() {
            return true;
        }

        //pobiera figurę z kwadracika
        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        }

    }

}
