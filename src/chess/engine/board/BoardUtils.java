package chess.engine.board;

public class BoardUtils { //klasa pomocnicza do planszy, ma zawierać tylko statyczne metody

    //tablice do ograniczania ruchów figór w kolumnach granicznych
    public static final boolean[] FIRST_COLUMN = null;
    public static final boolean[] SECOND_COLUMN = null;
    public static final boolean[] SEVENTH_COLUMN = null;
    public static final boolean[] EIGHTH_COLUMN = null;

    private BoardUtils() {
        throw new RuntimeException("You cannot instantiate me!");
    }

    //walidacja koordynatów
    public static boolean isValidTileCoordinate(int coordinate) {
        return coordinate >= 0 && coordinate < 64;
    }
}
