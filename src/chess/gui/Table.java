package chess.gui;

import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import chess.engine.pieces.Piece;
import chess.engine.player.MoveTransition;
import chess.engine.player.ai.MiniMax;
import chess.engine.player.ai.MoveStrategy;
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
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

// dziedziczy z klasy Observable, klasa będzie obserwowana,
// gdzie obserwator (observer został dodany poniżej) aktualizuje swój stan na podstawie zmiany obserwowanego elementu
public class Table extends Observable {

    private final JFrame gameFrame; // okno gry
    private final GameHistoryPanel gameHistoryPanel; // panel z historią gry
    private final TakenPiecesPanel takenPiecesPanel; // panel ze zbitymi figurami
    private final BoardPanel boardPanel; // panel z szachownicą
    private final MoveLog moveLog; // logi wykonanych ruchów
    private final GameSetup gameSetup;

    private Board chessBoard; // szachownica

    // zmienne potrzebne do zaznaczania i odznaczania figur na kwadraciku
    private Tile sourceTile; // aktualnie kliknięty kwadracik
    private Tile destinationTile; // kwadracik na który chcemy podążać
    private Piece humanMovedPiece; // figura, którą gracz wykonał ruch
    private BoardDirection boardDirection; // kierunek planszy

    private Move computerMove;

    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600); // zmienna z rozmiarem okna
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350); // zmienna z rozmiarem szachownicy
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10); // zmienna z rozmiarem kwadracika
    private static String defaultPieceImagesPath = "art/pieces/"; // początek sieżki do figur

    private final Color lightTileColor = Color.decode("#F5F4ED");
    private final Color darkTileColor = Color.decode("#A1A09A");

    private static final Table INSTANCE = new Table();

    private Table() {
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout()); // odpowiada za ułożenie elementów w oknie
        final JMenuBar tableMenuBar = createTableMenuBar(); // stworzenie menu okna
        this.gameFrame.setJMenuBar(tableMenuBar); // przypisanie stworzonego menu do okna
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION); // przekazanie zmiennej z rozmiarem do Jframe
        this.chessBoard = Board.createStandardBoard(); // tworzenie szachownicy
        this.gameHistoryPanel = new GameHistoryPanel(); // stworzenie panelu z historią gier
        this.takenPiecesPanel = new TakenPiecesPanel(); // stworzenie panelu ze zbitymi figurami
        this.boardPanel = new BoardPanel(); // dodanie panelu gry do okna gry
        this.moveLog = new MoveLog(); // ruchy
        this.addObserver(new TableGameAIWatcher()); // dodanie obserwatora (AI)
        this.gameSetup = new GameSetup(this.gameFrame, true); // dodanie do panelu opcji z rozpoczęciem gry
        this.boardDirection = BoardDirection.NORMAL; // ustawiamy kierunek ustawienia planszy na normalny
        this.highlightLegalMoves = false; // nadajemy false, tak by bazowo nie wyświetlały się podpowiedzi ruchów
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST); // ustawienie panelu ze zbitymi figurami po lewej stronie
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER); // wycentrowanie panelu z szachownicą
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST); // ustawienie panelu ze zbitymi figurami po prawej stronie
        this.gameFrame.setVisible(true); // ustawiasz widoczność
    }

    public static Table get() {
        return INSTANCE;
    }

    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }

    // metoda wywołująca menu
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
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

    private JMenu createOptionsMenu() {

        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem setupGameMenuItem = new JMenuItem("SetupGame");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }

    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    // obserwator AI aktualizuje swój stan na podstawie zmiany obserwowanego elementu
    private static class TableGameAIWatcher implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
               !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
               !Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }
            // jeżeli wystąpił szach mat
            if(Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
                System.out.println("przegrałeś, " + Table.get().getGameBoard().currentPlayer() + " jesteś w szachu");
            }
            // jeżeli wystąpił pat (gracz nie może wykonać żadnego ruchu zgodnego z zasadami)
            if(Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                System.out.println("przegrałeś, " + Table.get().getGameBoard().currentPlayer() + " wystąpił pat, nie możesz wykonać żadnego ruchu zgodnego z zasadami");
            }
        }
    }

    public void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    public void updateComputerMove(final Move move) {
        this.computerMove = move;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private void moveMadeUpdate (final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    //SwingWorker to narzędzia z biblioteki swing, służy do prawidłowego użycia wątku wywołującego zdarzenia
    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank() {

        }

        // zwraca najlepszy ruch do wykonania
        @Override
        protected Move doInBackground() throws Exception {
            final MoveStrategy miniMax = new MiniMax(4);
            final Move bestMove = miniMax.execute(Table.get().getGameBoard());
            return bestMove;
        }

        // wykonanie ruchu przez komputer
        @Override
        public void done() {
            try {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().moveLog);
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
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

    // klasa do przechowywania logów wykonanych ruchów
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

    enum PlayerType {
        HUMAN,
        COMPUTER
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
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate()); // tworzymy nowy ruch
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move); // odnosimy sie do obecnej tablicy
                            // jezeli ruch zostal wykonany
                            if(transition.getMoveStatus().isDone())
                            {
                                chessBoard = transition.getTransitionBoard(); // pobieramy zmieniona tablice
                                moveLog.addMove(move); // dodajemy ruch do logow
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        // "odświeżenie" GUI
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog); // aktualizacja panelu z historią ruchów
                                takenPiecesPanel.redo(moveLog); // aktualizacja panelu ze zbitymi figurami
                                // jeżeli gracz jest komputerem ...
                                if(gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN); // ... to obserwuje gracza i zaktualizuje sie po jego ruchu
                                }
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
            assignTilePieceIcon(board); // przydziela kwadracikowi ikonę figury
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
