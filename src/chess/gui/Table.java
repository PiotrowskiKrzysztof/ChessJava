package chess.gui;

import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.engine.player.MoveTransition;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {

    private final JFrame gameFrame; // okno gry
    private final BoardPanel boardPanel; // panel gry
    private Board chessBoard; // szachownica

    // zmienne potrzebne do zaznaczania i odznaczania figur na kwadraciku
    private Tile sourceTile; // aktualnie kliknięty kwadracik
    private Tile destinationTile; // kwadracik na który chcemy podążać
    private Piece humanMovedPiece; // figura, którą gracz wykonał ruch
    private BoardDirection boardDirection; // kierunek planszy

    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600); // zmienna z rozmiarem okna
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350); // zmienna z rozmiarem szachownicy
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10); // zmienna z rozmiarem kwadracika
    private static String defaultPieceImagesPath = "art/pieces/"; // początek sieżki do figur

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    public Table() {
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout()); // odpowiada za ułożenie elementów w oknie
        final JMenuBar tableMenuBar = createTableMenuBar(); // stworzenie menu okna
        this.gameFrame.setJMenuBar(tableMenuBar); // przypisanie stworzonego menu do okna
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION); // przekazanie zmiennej z rozmiarem do Jframe
        this.chessBoard = Board.createStandardBoard(); // tworzenie szachownicy
        this.boardPanel = new BoardPanel(); // dodanie panelu gry do okna gry
        this.boardDirection = BoardDirection.NORMAL; // ustawiamy kierunek ustawienia planszy na normalny
        this.highlightLegalMoves = false; // nadajemy false, tak by bazowo nie wyświetlały się podpowiedzi ruchów
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER); // wycentrowanie panelu gry
        this.gameFrame.setVisible(true); // ustawiasz widoczność
    }

    // metoda wywołująca menu
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    // metoda do tworzenia menu
    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File"); // zmienna przechowująca wszystkie elementy menu
        final JMenuItem openPGN = new JMenu("Load PGN File"); // PGN (Portable Game Notation) - format zapisu partii szachowych
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("open up that pgn file");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit"); // dodajemy element menu - wyjśćie z gry poprzez ActionListener
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    // menu preferencji
    private JMenu createPreferencesMenu()
    {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // po wybraniu opcji z listy...
                boardDirection = boardDirection.opposite(); // ...pobieramy aktualny kierunek szachownicy i zwracamy jego odwrotność
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight legal moves", false);

        legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
            }
        });

        preferencesMenu.add(legalMoveHighlighterCheckbox);

        return preferencesMenu;
    }

    // enum określający, w którą stronę zwrócona jest plansza z figurami
    public enum BoardDirection
    {
        NORMAL
                {
                    @Override
                    List<TilePanel> traverse(final List<TilePanel> boardTiles)
                    {
                        return boardTiles;
                    }

                    @Override
                    BoardDirection opposite() // metoda opposite dla każdego enum'a będzie zwracała odwrotną stronę szachownicy (zamiana stronami tak, by wygodnie grać)
                    {
                        return FLIPPED;
                    }
                },
        FLIPPED
                {
                    @Override
                    List<TilePanel> traverse(final List<TilePanel> boardTiles)
                    {
                        return Lists.reverse(boardTiles);
                    }

                    @Override
                    BoardDirection opposite()
                    {
                        return NORMAL;
                    }
                };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    // klasa do szachownicy
    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8,8)); // dziedziczenie po JPanel oraz tworzenie szablonu 8x8
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel); // dodanie kwadracika do listy
                add(tilePanel); // dodanie kwadracika do JPanel
            }
            setPreferredSize(BOARD_PANEL_DIMENSION); // ustalenie rozmiaru szachownicy
            validate(); // metoda JPanel weryfikuje ten kontener i wszystkie jego składniki podrzędne.
        }
        // metoda do rysowania planszy
        public void drawBoard (final Board board)
        {
            removeAll(); // zdejmujemy wszystkie umieszczone komponenty
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)) // for przechodzi po planszy odpowiednio do aktualnego kierunku planszy
            {
                tilePanel.drawTile(board); // dla każdego elementu rysujemy kwadracik (Tile'a) na boardzie
                add(tilePanel); // dodajemy element do boarda
            }
            validate();
            repaint();
        }
    }

    // klasa do wyświetlania logów wykonanych ruchów
    public static class MoveLog {

        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index) {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }

    private class TilePanel extends JPanel {
    // extends JPanel - każdemu Tile'owi przypisany panel = 64 JPanel'e
        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridLayout()); // dziedziczenie po JPanel oraz tworzenie szablonu 1x1
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION); // utalenie rozmiaru kwadracika
            assignTileColor(); // ustalenie koloru kwadracika
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() { // nasłuchiwanie zdarzeń myszki
                @Override
                public void mouseClicked(final MouseEvent e) {

                    if(isRightMouseButton(e)) { // kliknięcie prawym przyciskiem myszki powoduje odznaczenie figury (imitacja cofnięcia kliknięcia)
                        sourceTile = null; // przypisujemy każdemu wartość null - brak zaznaczenia
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if(isLeftMouseButton(e)) { // kliknięcie lewym przyciskiem myszki powoduje zaznaczenie figury
                        if(sourceTile == null) {
                            // przy pierwszym kliknięciu ...
                            sourceTile = chessBoard.getTile(tileId); // ... zaznaczamy kliknięty kwadracik
                            humanMovedPiece = sourceTile.getPiece(); // ... pobieramy figurę na klikniętym kwadraciku
                            if(humanMovedPiece == null) { // ... jeżeli gracz nie wykonał ruchu ...
                                sourceTile = null;        // ... to przypisujemy zaznaczonemu kwadracikowi początkową wartość null
                            }
                        } else {
                            // przy drugim kliknięciu ...
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone())
                            {
                                chessBoard = transition.getTransitionBoard();
                                // TODO: trzeba będzie pomyśleć nad utworzeniem dziennika logów - zapisaywać ruchy w jakiś sposób (?)
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        // "odświeżenie" GUI
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                boardPanel.drawBoard(chessBoard); // przerysowanie planszy przy zadanym chessBoard
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            validate(); // metoda JPanel weryfikuje ten kontener i wszystkie jego składniki podrzędne.
        }

        // metoda do rysowania Tile'a
        public void drawTile(final Board board)
        {
            assignTileColor(); // nadajemy kwadracikowi kolor
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();

        }

        private void assignTilePieceIcon(final Board board)
        {
            this.removeAll(); // zdejmujemy wszystko co zostało uprzednio umieszczone na panelu (JPanel)
            if(board.getTile(this.tileId).isTileOccupied()) // jeśli pobrany element boarda jest zajęty (jeśli na Tile'u stoi Piece) to rysujemy ten element
            {
                try { // pobieranie obrazka umieszczamy w bloku try-catch
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0,1) +
                            board.getTile(this.tileId).getPiece().toString() + ".gif")); // pobieranie obrazka zapisanego za pomocą tego wzorca, np. "WB.gif" - white bishop
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // "podświetlamy" możliwe ruchy na planszy
        private void highlightLegals(final Board board)
        {
            if(highlightLegalMoves)  // podświetlenie opcjonalne, jeśli zostanie wybrane w menu jako aktywne...
            {
                for(final Move move : pieceLegalMoves(board)) // pobieramy wybrany przez użytkownika element (a co za tym idzie, sprawdzamy jego możliwe ruchy)
                {
                    if(move.getDestinationCoordinate() == this.tileId) // jeśli pobrana lokalizacja jest dostępna dla figury
                    {
                        try
                        {   // nadajemy label w postaci obrazka
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/highlights/green_dot.png")))));
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board)
        {   // jeśli został wybrany element oraz jeśli aliance jest równy aktualnemu graczowi
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance())
            { // przelicz możliwe ruchy elementu planszy
                return humanMovedPiece.calculateLegalMoves(board);
            } // w p. p. zwróc pustą listę
            return Collections.emptyList();
        }

        private void assignTileColor() { // nadajemy polom nieparzystym i parzystym odpowiadający kolor
            if(BoardUtils.EIGHTH_RANK[this.tileId] || BoardUtils.SIXTH_RANK[this.tileId] || BoardUtils.FOURTH_RANK[this.tileId] || BoardUtils.SECOND_RANK[this.tileId])
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            else if(BoardUtils.SEVENTH_RANK[this.tileId] || BoardUtils.FIFTH_RANK[this.tileId] || BoardUtils.THIRD_RANK[this.tileId] || BoardUtils.FIRST_RANK[this.tileId])
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
        }

    }
}
