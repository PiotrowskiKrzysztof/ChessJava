package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import chess.engine.board.Move.MajorMove;
import chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.engine.board.Move.*;

public class Knight extends Piece{ // KLASA REPREZENTUJĄCA FIGURĘ SKOCZKA

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17}; // pozycje planszy na których może pojawić się figura

    Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) // pętla po wszystkich możliwych położeniach figury na planszy
        {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset; // do aktualnej pozycji dodawane jest przesunięcie

            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) // jeśli dane położenie na planszy jest dozwolone...
            {
                // jeżeli figura znajdzie się w granicznych kolumnach, to ograniczy możliwość ruchów do wykonania
                if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                    isSecondColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                    isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                    isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                        continue;
                }

                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                if(!candidateDestinationTile.isTileOccupied()) // jeśli dane miejsce nie jest zajęte...
                {
                    // ...dodajemy ten ruch do listy możliwych ruchów (nieatakujący)
                    // tworzymy nowy ruch, który przyjmuje w argumentach szachownice, własną figurę oraz docelowe koordynaty
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                }
                else // jeśli jednak dane miejsce jest zajęte...
                {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if(this.pieceAlliance != pieceAlliance)
                    {
                        // ...dodajemy ten ruch do listy możliwych ruchów (atakujących)
                        // tworzymy nowy ruch atakujący, który przujmuje w argumentach szachownice, własną firugę, docelowe koordynaty oraz atakowaną figurę stojącą na docelowych koordynatach
                        legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves); // jako wynik zwracamy listę dozwolonych ruchów
    }

    //Ograniczenia ruchów figury na granicznych kolumnach
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        // zwraca dozwolone ruchy dla pierwszej kolumny
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -17 || candidateOffset == -10 || candidateOffset == 6 || candidateOffset == 15);
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
        // zwraca dozwolone ruchy dla drugiej kolumny
        return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset == -10 || candidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
        // zwraca dozwolone ruchy dla siódmej kolumny
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (candidateOffset == -6 || candidateOffset == 10);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        // zwraca dozwolone ruchy dla ósmej kolumny
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -15 || candidateOffset == -6 || candidateOffset == 10 || candidateOffset == 17);
    }

}
