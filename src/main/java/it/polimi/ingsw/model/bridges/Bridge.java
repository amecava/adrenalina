package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.Color;

public class Bridge {

    private DamageBridge damageBridge;
    private ActionBridge actionBridge = new ActionBridge();

    public Bridge(Color playerColor) {

        this.damageBridge = new DamageBridge(playerColor);
    }

    public DamageBridge getDamageBridge() {

        return this.damageBridge;
    }

    public ActionBridge getActionBridge() {

        return this.actionBridge;
    }

    public void setDamage(Color color, int quantity) {

        this.damageBridge.addDamage(color, quantity);
    }

    public void setMark(Color color, int quantity) {

        this.damageBridge.addMarker(color, quantity);
    }
}
