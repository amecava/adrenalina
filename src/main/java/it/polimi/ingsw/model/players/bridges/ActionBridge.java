package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.board.rooms.Square;

class ActionBridge {

    private Square oldPosition;
    private Square currentPosition;

    ActionBridge() {
    }

    Square getOldPosition() {

        return this.oldPosition;
    }

    void setOldPosition(Square oldPosition) {

        this.oldPosition = oldPosition;
    }

    Square getCurrentPosition() {

        return this.currentPosition;
    }

    void setCurrentPosition(Square currentPosition) {

        this.oldPosition = this.currentPosition;
        this.currentPosition = currentPosition;
    }
}
