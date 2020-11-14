package chess.engine.player;

public enum MoveStatus {//klasa do określania możliwego/niemożliwego do wykonania ruchu ruchu
    DONE{
        @Override
        boolean isDone() {
            return true;
        }
    };

    abstract boolean isDone();
}
