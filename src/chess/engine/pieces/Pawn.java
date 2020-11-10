package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATE = {8}; // 8 pól w szachownicy to jedno miejsce wyżej nad pionkiem

    Pawn(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            // this.getPieceAlliance().getDirection() - określa kolor (w którą stronę musi iść figura) dla białych -1, czarnych 1
            int candidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * currentCandidateOffset); // miejsce gdzie figura może wykonać ruch

            // walidacja koordynatów
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            // jeżeli pionek jest przesunięty o 8 pól oraz kwadracik szachownicy (na który chcemy iść) NIE jest zajęty
            if(currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                // tutaj będzie trzeba zrobić specjalny ruch dla pionka
                legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
            }
        }

        return legalMoves;
    }
}
