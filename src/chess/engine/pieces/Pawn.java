package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.engine.board.Move.*;

public class Pawn extends Piece { // KLASA REPREZENTUJĄCA FIGURĘ PIONU

    private final static int[] CANDIDATE_MOVE_COORDINATE = {8, 16}; // 8 pól w szachownicy to jedno miejsce wyżej nad pionkiem

    Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            // this.getPieceAlliance().getDirection() - określa kolor (w którą stronę musi iść figura) dla białych -1, czarnych 1
            final int candidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * currentCandidateOffset); // miejsce gdzie figura może wykonać ruch

            // walidacja koordynatów
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            // jeżeli pionek jest przesunięty o 8 pól oraz kwadracik szachownicy (na który chcemy iść) NIE jest zajęty
            if(currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                // tutaj będzie trzeba zrobić specjalny ruch dla pionka
                legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
            }
            // odtworzenie "skoku" pionu z pozycji startowej, sprawdzamy czy jest to pierwszy wykonywany ruch na planszy, czy figury w odpowiednich kolorach znajdują się w odpowiadających im rzędach
            else if(currentCandidateOffset == 16 && this.isFirstMove() && (BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isBlack()) || (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.getPieceAlliance().isWhite()))
            {
                // sprawdzamy czy kwadracik będący nad polem nad którym figura wykonuje skok jest pusty
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() && !board.getTile(candidateDestinationCoordinate).isTileOccupied())
                { // po sprawdzeniu że pole jest puste, możemy dodać ruch do listy prawidłowych ruchów po planszy
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                }
            }
        }

        return legalMoves;
    }
}
