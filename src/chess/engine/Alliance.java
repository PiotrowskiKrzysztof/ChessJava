package chess.engine;

import chess.engine.board.BoardUtils;
import chess.engine.player.BlackPlayer;
import chess.engine.player.Player;
import chess.engine.player.WhitePlayer;

public enum Alliance {  // Używamy enum'a do deklaracji kolorów
    // określa kolor do którego będzie należeć dana figura
    WHITE {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return true;  // w tym miejscu jawnie wskazujemy że figura znajduje się na polu białym, analogicznie w przypadku koloru BLACK
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.EIGHTH_RANK[position];
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true; // wartość true potwierdza że figura stoi na polu czarnym
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.FIRST_RANK[position];
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    public abstract int getDirection(); // metoda do pobierania koloru figury
    public abstract int getOppositeDirection(); // metoda do ruchu en passant, pobiera przeciwny kierunek figury (1 dla białych, -1 dla czarnych)
    // metody służące do sprawdzenia koloru pola na którym aktualnie ma stać/stoi zadana figura
    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract boolean isPawnPromotionSquare(int position);

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
