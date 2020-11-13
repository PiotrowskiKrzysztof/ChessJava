package chess.engine.board;

public class BoardUtils { //klasa pomocnicza do planszy, ma zawierać tylko statyczne metody

    //tablice do ograniczania ruchów figór w kolumnach granicznych
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    //inicjalizacja rzędów planszy
    public static final boolean[] SECOND_ROW = initRow(8);  // liczby wskazują na id pola od którego zaczyna się dany rząd
    public static final boolean[] SEVENTH_ROW = initRow(48);

    public static final int NUM_TILES = 64; //ilość kwadracików na szachownicy
    public static final int NUM_TILES_PER_ROW = 8; //ilośc kwadracików w kolumnie/wierszu

    //inicjalizacja kolumny
    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[NUM_TILES]; //tablica rozmiaru 64, ponieważ plansza jest 8x8
        do {
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW; //w każdej kolumnie jest 8 kratek
        } while (columnNumber < NUM_TILES);
        return column;
    }

    //inicjalizacja rzędu
    private static boolean[] initRow(int rowNumber)
    {
        final boolean[] row = new boolean[NUM_TILES];
        do {
            row[rowNumber] = true;
            rowNumber++;
        } while (rowNumber % NUM_TILES_PER_ROW != 0);
        return row;
    }

    private BoardUtils() {
        throw new RuntimeException("You cannot instantiate me!");
    }

    //walidacja koordynatów
    public static boolean isValidTileCoordinate(final int coordinate) {
        return coordinate >= 0 && coordinate < NUM_TILES;
    }
}
