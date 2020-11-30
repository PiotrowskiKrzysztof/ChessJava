package chess.gui;

import chess.engine.board.Board;
import chess.engine.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

import static chess.gui.Table.*;

public class GameHistoryPanel extends JPanel { // klasa reprezentująca panel boczny ekranu rozgrywki

    private final DataModel model; // przechowuje dane historii ruchów
    private final JScrollPane scrollPane; // scroll panelu
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400); // rozmiar panelu

    GameHistoryPanel()
    {
        this.setLayout(new BorderLayout()); // odpowiada za ułożenie elementów w oknie
        this.model = new DataModel(); // inicjalizacja kontenera do przechowywania danych
        final JTable table = new JTable(model); // stworzenie tabeli z danymi
        table.setRowHeight(15); // ustawienie wysokości wiersza w tabeli
        this.scrollPane = new JScrollPane(table); // stworzenie scrolla w panelu
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION); // scroll się pojawi po przekroczeniu preferowanego rozmiaru
        this.add(scrollPane, BorderLayout.CENTER); // dodanie scrolla do panelu
        this.setVisible(true); // ustawienie widocznosci na true
    }

    // metoda do aktualizacji historii ruchów
    void redo(Board board, final MoveLog moveHistory)
    {
        int currentRow = 0;
        this.model.clear();
        for(final Move move : moveHistory.getMoves())
        {
            final String moveText = move.toString();
            // jeżeli ruch został wykonany przez białą figurę ...
            if(move.getMovedPiece().getPieceAlliance().isWhite())
            {
                this.model.setValueAt(moveText, currentRow, 0); // ... dodaj wykonany ruch do historii w kolumnie 0 (białe)
            }
            else if(move.getMovedPiece().getPieceAlliance().isBlack())
            {
                this.model.setValueAt(moveText, currentRow, 1); // ... dodaj wykonany ruch do historii w kolumnie 1 (czarne)
                currentRow++; // obecny wiersz zwiększa się dopiero tutaj, ponieważ gracz czarny wykonuje ruch jako drugi i dopiero po nim przechodzimy do kolejnego wiersza
            }
        }

        if(moveHistory.getMoves().size() > 0)
        {
            final Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
            final String moveText = lastMove.toString();

            // jeżeli ostatni ruch był gracza białego ...
            if(lastMove.getMovedPiece().getPieceAlliance().isWhite())
            {
                // ... przypisujemy wartość w kolumnie 0 (białe)
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
            }
            // jeżeli jednak, ostatni ruch był gracza czarnego ...
            else if(lastMove.getMovedPiece().getPieceAlliance().isBlack())
            {
                // ... przypisujemy wartość w kolumnie 1 (czarne)
                this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
            }
        }
        final JScrollBar vertical = scrollPane.getVerticalScrollBar(); // tworzenie scrolla
        vertical.setValue(vertical.getMaximum());
    }

    private String calculateCheckAndCheckMateHash(final Board board) {
        // jeżeli był szach-mat
        if(board.currentPlayer().isInCheckMate())
        {
            return "#";
        }
        // jeżeli król jest w szachu
        else if(board.currentPlayer().isInCheck())
        {
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel
    {
        private final ArrayList<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel()
        {
            this.values = new ArrayList<>();
        }

        // wyczyszczenie danych panelu z hostorią ruchów
        public void clear()
        {
            this.values.clear();
            setRowCount(0);
        }

        // nadpisanie metody z DefaultTableModel, zwraca ilość wierszy
        @Override
        public int getRowCount() {
            if(this.values == null)
            {
                return 0;
            }
            return this.values.size();
        }

        // nadpisanie metody z DefaultTableModel, zwraca ilość kolumn
        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        // nadpisanie metody z DefaultTableModel, pobiera określoną wartość
        @Override
        public Object getValueAt(final int row, final int column) {
            final Row currentRow = this.values.get(row);
            if(column == 0)
            {
                return currentRow.getWhiteMove();
            }
            else if(column == 1)
            {
                return currentRow.getBlackMove();
            }
            return null;
        }

        // nadpisanie metody z DefaultTableModel, przypisuje określoną wartość
        @Override
        public void setValueAt(final Object aValue, final int row, final int column) {
            final Row currentRow;
            // jeżeli ilość danych jest mniejsza lub równa ilości wierszy, dodajemy kolejne dane
            if(this.values.size() <= row)
            {
                currentRow = new Row();
                this.values.add(currentRow);
            }
            else // w innym przypadku przypisujemy wartości określonemu wierszu
            {
                currentRow = this.values.get(row);
            }

            if(column == 0)// kolumna białych ruchów
            {
                currentRow.setWhiteMove((String)aValue);
                fireTableRowsInserted(row, row); // metoda do wstawienia wiersza (zakres od do) z DefaultTableModel
            }
            else if(column == 1) // kolumna czarnych ruchów
            {
                currentRow.setBlackMove((String)aValue);
                fireTableCellUpdated(row, column); // metoda do zaktualizowania danych z DefaultTableModel
            }
        }

        // parametryzacja, Class<?> może być dowolnego typu
        @Override
        public Class<?> getColumnClass(final int column) {
            return Move.class;
        }

        @Override
        public String getColumnName(final int column) {
            return NAMES[column];
        }
    }

    // klasa odpowiadająca wierszowi danych wykonanych ruchów (jednej kolejce ruchów obu graczy)
    private static class Row
    {
        private String whiteMove; // ruch białego gracza
        private String blackMove; // ruch czarnego gracza

        Row()
        {

        }

        public String getWhiteMove()
        {
            return this.whiteMove;
        }
        public String getBlackMove()
        {
            return this.blackMove;
        }
        public void setWhiteMove(final String move)
        {
            this.whiteMove = move;
        }
        public void setBlackMove(final String move)
        {
            this.blackMove = move;
        }
    }
}
