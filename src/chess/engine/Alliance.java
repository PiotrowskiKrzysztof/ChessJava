package chess.engine;

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
        public boolean isWhite() {
            return true;  // w tym miejscu jawnie wskazujemy że figura znajduje się na polu białym, analogicznie w przypadku koloru BLACK
        }

        @Override
        public boolean isBlack() {
            return false;
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
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true; // wartość true potwierdza że figura stoi na polu czarnym
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };
    // metoda do pobierania koloru figury
    public abstract int getDirection();
    // metody służące do sprawdzenia koloru pola na którym aktualnie ma stać/stoi zadana figura
    public abstract boolean isWhite();
    public abstract boolean isBlack();

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
