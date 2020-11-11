package chess.engine;

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
    };
    // metoda do pobierania koloru figury
    public abstract int getDirection();
    // metody służące do sprawdzenia koloru pola na którym aktualnie ma stać/stoi zadana figura
    public abstract boolean isWhite();
    public abstract boolean isBlack();
}
