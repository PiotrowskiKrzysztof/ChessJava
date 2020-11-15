package chess.engine.player;

public enum MoveStatus {//enum do określania możliwego/niemożliwego do wykonania ruchu ruchu
    DONE{ // Ruch wykonany
        @Override
        boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE{ // Ruch niedozowolony
        @Override
        boolean isDone() {
            return false; // zwracamy false dla nielegalnego ruchu
        }
    },

    LEAVES_PLAYER_IN_CHECK{ // Ruch pozostawiający gracza w szachu
        @Override
        boolean isDone() {
            return false;
        }
    };

    abstract boolean isDone();
}
