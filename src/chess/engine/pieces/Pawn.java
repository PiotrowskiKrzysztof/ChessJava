package chess.engine.pieces;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.engine.board.Move.*;

public class Pawn extends Piece { // KLASA REPREZENTUJĄCA FIGURĘ PIONU

    private final static int[] CANDIDATE_MOVE_COORDINATE = {8, 16, 7, 9}; // 8 pól w szachownicy to jedno miejsce wyżej nad pionkiem

    public Pawn(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    public Pawn(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            // this.getPieceAlliance().getDirection() - określa kolor (w którą stronę musi iść figura) dla białych -1, czarnych 1
            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset); // miejsce gdzie figura może wykonać ruch

            // walidacja koordynatów
            if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            // jeżeli pionek jest przesunięty o 8 pól oraz kwadracik szachownicy (na który chcemy iść) NIE jest zajęty (ruch nieatakujący)
            if(currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
            }
            // odtworzenie "skoku" pionu z pozycji startowej, sprawdzamy czy jest to pierwszy wykonywany ruch na planszy, czy figury w odpowiednich kolorach znajdują się w odpowiadających im rzędach (ruch nieatakujący)
            else if(currentCandidateOffset == 16 && this.isFirstMove() && ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.getPieceAlliance().isBlack()) || (BoardUtils.SECOND_RANK[this.piecePosition] && this.getPieceAlliance().isWhite())))
            {
                // sprawdzamy czy kwadracik będący nad polem nad którym figura wykonuje skok jest pusty
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if(!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() && !board.getTile(candidateDestinationCoordinate).isTileOccupied())
                { // po sprawdzeniu że pole jest puste, możemy dodać ruch do listy prawidłowych ruchów po planszy
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }
            // pierwsze dwa ruchy atakujące: ruch atakujący figury po diagonali
            // warunek dotyczy niemożliwego wyjścia poza planszę gdy figura znajduje się na brzegu planszy i użytkownik chciałby (niechcący lub celowo) wykonać ruch poza nią
            else if(currentCandidateOffset == 7 && !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() || (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))))
            {
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied())
                {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                    {
                        legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                    }
                 // jeżeli tablica posiada pionek, na którym może być wykonany ruch en passant (taki, który wykonał podwójny skok)
                } else if(board.getEnPassantPawn() != null) {
                    // jeżeli pozycja pionka na którym może być wykonany ruch en passant równa się pozycji zaznaczonej figury + odwrotnemu kierunkowi figury
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        // jeżeli zaznaczona figura jest innego koloru niż figura, którą chcemy zbić ...
                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            // ... dodajemy atak en passant do legalnych ruchów
                            legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
            else if(currentCandidateOffset == 9 && !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() || (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))))
            {
                if(board.getTile(candidateDestinationCoordinate).isTileOccupied())
                {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance())
                    {
                        legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                    }
                // jeżeli tablica posiada pionek, na którym może być wykonany ruch en passant (taki, który wykonał podwójny skok)
                } else if(board.getEnPassantPawn() != null) {
                    // jeżeli pozycja pionka na którym może być wykonany ruch en passant równa się pozycji zaznaczonej figury - odwrotnemu kierunkowi figury
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        // jeżeli zaznaczona figura jest innego koloru niż figura, którą chcemy zbić ...
                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            // ... dodajemy atak en passant do legalnych ruchów
                            legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    // pobiera ruch figury i tworzy nową figurę na przesuniętym miejscu
    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }
}
