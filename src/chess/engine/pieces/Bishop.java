package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.engine.board.Move.*;

public class Bishop extends Piece { // KLASA REPREZENTUJĄCA FIGURĘ GOŃCA

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -7, 7, 9}; // pozycje planszy na których może pojawić się figura

    public Bishop(Alliance pieceAlliance, int piecePosition) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for(final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES)
        {
            int candidateDestinationCoordinate = this.piecePosition;
            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                if(isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset) || isEightColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset))
                {
                    break;
                }
                candidateDestinationCoordinate += candidateCoordinateOffset;
                if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
                {
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
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return BoardUtils.FIRST_COLUMN[currentPosition] && candidateOffset == -9 || candidateOffset == 7;
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && candidateOffset == -7 || candidateOffset == 9;
    }
}
