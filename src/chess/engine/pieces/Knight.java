package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece{ // klasa implementująca metody poruszania się figury

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17}; // pozycje planszy na których może pojawić się figura

    Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {

        int candidateDestinationCoordinate;
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidate : CANDIDATE_MOVE_COORDINATES) // pętla po możliwych położeniach figury na planszy
        {
            candidateDestinationCoordinate = this.piecePosition + currentCandidate; // do aktualnej pozycji dodawane jest przesunięcie

            if(true /* isValidTileCoordinate */) // jeśli dane położenie na planszy jest dozwolone...
            {
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                if(!candidateDestinationTile.isTileOccupied()) // jeśli dane miejsce nie jest zajęte...
                {
                    legalMoves.add(new Move()); // ...dodajemy ten ruch do listy możliwych ruchów (nieatakujący)
                }
                else // jeśli jednak dane miejsce jest zajęte...
                {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if(this.pieceAlliance != pieceAlliance)
                    {
                        legalMoves.add(new Move()); // ...dodajemy ten ruch do listy możliwych ruchów (atakujących)
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves); // jako wynik zwracamy listę dozwolonych ruchów
    }
}
