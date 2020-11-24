package chess.engine.player;

public enum MoveStatus {//enum do określania możliwego/niemożliwego do wykonania ruchu ruchu
    DONE{ // Ruch wykonany
        @Override
        public boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE{ // Ruch niedozowolony
        @Override
        public boolean isDone() {
            return false; // zwracamy false dla nielegalnego ruchu
        }
    },

    LEAVES_PLAYER_IN_CHECK{ // Ruch pozostawiający gracza w szachu
        @Override
        public boolean isDone() {
            return false;
        }
    };

    public abstract boolean isDone();
}
