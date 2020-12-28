package chess.gui;

import chess.engine.board.Move;
import chess.engine.pieces.Piece;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static chess.gui.Table.MoveLog;

// klasa z panelem zbitych figur
public class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final Color PANEL_COLOR = Color.decode("0xFDFE6");
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);

    public TakenPiecesPanel() {
        super(new BorderLayout());
        setBackground(PANEL_COLOR); // ustawienie koloru okna
        setBorder((PANEL_BORDER)); // ustawienie ramki okna
        this.northPanel = new JPanel(new GridLayout(8, 2)); // ustawienie wierszy oraz kolumn górnemu panelowi
        this.southPanel = new JPanel(new GridLayout(8, 2)); // ustawienie wierszy oraz kolumn dolnemu panelowi
        this.northPanel.setBackground(PANEL_COLOR); // przypisanie koloru
        this.southPanel.setBackground(PANEL_COLOR); // przypisanie koloru
        add(this.northPanel, BorderLayout.NORTH); // dodanie gornego panelu do okna
        add(this.southPanel, BorderLayout.SOUTH); // dodanie dolnego panelu do okna
        setPreferredSize(TAKEN_PIECES_DIMENSION); // ustawienie rozmiaru
    }

    public void redo(final MoveLog moveLog) { // MoveLog z klasy Table

        northPanel.removeAll();
        southPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for(final Move move : moveLog.getMoves()) { // iteracja po wszystkich ruchach w logach
            if(move.isAttack()) { // jeżeli ruch był atakiem
                final Piece takenPiece = move.getAttackedPiece(); // pobiera atakowaną figurę
                if(takenPiece.getPieceAlliance().isWhite()) { // jeżeli atakowana figura jest biała ...
                    whiteTakenPieces.add(takenPiece); // ... dodaj ją do listy z białymi figurami
                } else if (takenPiece.getPieceAlliance().isBlack()) { // jeżeli atakowana figura jest czarna ...
                    blackTakenPieces.add(takenPiece); // ... dodaj ją do listy z czarnymi figurami
                } else {
                    throw new RuntimeException("Przechwycony wyjątek z logów - if(move.isAttack)");
                }
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        Collections.sort(blackTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        for(final Piece takenPiece : whiteTakenPieces) {
            try {
                // scieżka do obrazka z figurą
                final BufferedImage image = ImageIO.read(new File("art/pieces/" + takenPiece.getPieceAlliance().toString().substring(0, 1) + "" + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage()
                        .getScaledInstance(icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.northPanel.add(imageLabel);
            } catch(final IOException e) { // IOException to wyjątek do przechwytywania nieudanej próby odczytu
                e.printStackTrace();
            }
        }

        for (final Piece takenPiece : blackTakenPieces) {
            try {
                final BufferedImage image = ImageIO.read(new File("art/pieces/" + takenPiece.getPieceAlliance().toString().substring(0, 1) + "" + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage()
                        .getScaledInstance(icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
                this.southPanel.add(imageLabel);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        validate();

    }

}
