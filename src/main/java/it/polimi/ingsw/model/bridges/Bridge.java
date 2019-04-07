package it.polimi.ingsw.model.bridges;

import it.polimi.ingsw.model.Color;

public class Bridge {
    private DamageBridge damageBridge;
    private ActionBridge actionBridge;


    public void setDamageBridge(DamageBridge damageBridge) {
        this.damageBridge = damageBridge;
    }

    public void setActionBridge(ActionBridge actionBridge) {
        this.actionBridge = actionBridge;
    }

    public DamageBridge getDamageBridge() {
        return damageBridge;
    }

    public ActionBridge getActionBridge() {
        return actionBridge;
    }


    public void setDamage(Color color, int quantity ){
        this.damageBridge.addDamage(color, quantity );
    }
    public void setMarker (Color color , int quantity){
        this.damageBridge.addMarker(color ,quantity);
    }
}
