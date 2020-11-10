package chess.engine;

public enum Alliance {  // Używamy enum'a do deklaracji kolorów
    // określa kolor do którego będzie należeć dana figura
    WHITE {
        @Override
        public int getDirection() {
            return -1;
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return 1;
        }
    };
    // metoda do pobierania koloru figury
    public abstract int getDirection();
}
