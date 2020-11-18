package chess.engine.board;

import chess.engine.Alliance;
import chess.engine.pieces.*;
import chess.engine.player.BlackPlayer;
import chess.engine.player.Player;
import chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class Board {

    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces; // kolekcje służące do śledzenia ruchu figur w rozbiciu na poszczególne kolory
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;

    private final Player currentPlayer;

    //konstruktor szachownicy
    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);

        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    @Override
    public String toString() { // nadpisujemy metodę toString do wyświetlania Board
        final StringBuilder builder = new StringBuilder();
        for(int i = 0; i < BoardUtils.NUM_TILES; i++)
        {
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if((i+1) % BoardUtils.NUM_TILES_PER_ROW == 0)
            {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    //zwraca białego gracza
    public Player whitePlayer() {
        return this.whitePlayer;
    }

    //zwraca czarnego gracza
    public Player blackPlayer() {
        return this.blackPlayer;
    }

    //zwraca aktualnego gracza
    public Player currentPlayer()
    {
        return this.currentPlayer;
    }

    //zwraca czarne figury
    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    //zwraca białe figury
    public  Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) { // sprawdzamy legalMoves dla kolekcji elementów
        final List<Move> legalMoves = new ArrayList<>();
        for(final Piece piece : pieces)
        {
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard, final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();
        for(final Tile tile : gameBoard)
        {
            if(tile.isTileOccupied()) // jeśli dane pole jest zajęte...
            {
                final Piece piece = tile.getPiece(); // ...pobieramy element stojący na tym polu
                if(piece.getPieceAlliance() == alliance) // jeśli pobrany element jest równy temu który został przekazany jako parametr w calculateActivePieces
                {
                    activePieces.add(piece); // dodajemy elemeny do listy aktywnych elementów
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    //geter kwadracika szachownicy
    public Tile getTile(final int tileCoordinate)
    {
        return gameBoard.get(tileCoordinate);
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
        final Builder builder = new Builder();
        // Czarne figury
        builder.setPiece(new Rook(Alliance.BLACK,0)); // ustawiamy wieżę czarnego koloru na pozycji o id = 0, itd.
        builder.setPiece(new Knight(Alliance.BLACK,1));
        builder.setPiece(new Bishop(Alliance.BLACK,2));
        builder.setPiece(new Queen(Alliance.BLACK,3));
        builder.setPiece(new King(Alliance.BLACK,4));
        builder.setPiece(new Bishop(Alliance.BLACK,5));
        builder.setPiece(new Knight(Alliance.BLACK,6));
        builder.setPiece(new Rook(Alliance.BLACK,7));
        builder.setPiece(new Pawn(Alliance.BLACK,8));
        builder.setPiece(new Pawn(Alliance.BLACK,9));
        builder.setPiece(new Pawn(Alliance.BLACK,10));
        builder.setPiece(new Pawn(Alliance.BLACK,11));
        builder.setPiece(new Pawn(Alliance.BLACK,12));
        builder.setPiece(new Pawn(Alliance.BLACK,13));
        builder.setPiece(new Pawn(Alliance.BLACK,14));
        builder.setPiece(new Pawn(Alliance.BLACK,15));
        // Białe figury
        builder.setPiece(new Pawn(Alliance.WHITE,48));
        builder.setPiece(new Pawn(Alliance.WHITE,49));
        builder.setPiece(new Pawn(Alliance.WHITE,50));
        builder.setPiece(new Pawn(Alliance.WHITE,51));
        builder.setPiece(new Pawn(Alliance.WHITE,52));
        builder.setPiece(new Pawn(Alliance.WHITE,53));
        builder.setPiece(new Pawn(Alliance.WHITE,54));
        builder.setPiece(new Pawn(Alliance.WHITE,55));
        builder.setPiece(new Rook(Alliance.WHITE,56));
        builder.setPiece(new Knight(Alliance.WHITE,57));
        builder.setPiece(new Bishop(Alliance.WHITE,58));
        builder.setPiece(new Queen(Alliance.WHITE,59));
        builder.setPiece(new King(Alliance.WHITE,60));
        builder.setPiece(new Bishop(Alliance.WHITE,61));
        builder.setPiece(new Knight(Alliance.WHITE,62));
        builder.setPiece(new Rook(Alliance.WHITE,63));
        // ruch białych figur
        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();

    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    // klasa odpowiedzialna za budowanie szachownicy
    public static class Builder {

        //mapa przechowująca id kwadracika oraz figurę
        Map<Integer, Piece> boardConfig;
        //odpowiada za określenie kolejności wykonywania ruchu
        Alliance nextMoveMaker;

        public Builder() {
            this.boardConfig = new HashMap<>();

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
