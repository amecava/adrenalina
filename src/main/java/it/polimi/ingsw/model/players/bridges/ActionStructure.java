package it.polimi.ingsw.model.players.bridges;

import it.polimi.ingsw.model.cards.effects.Effect;

public class ActionStructure {
    private Boolean move;
    private Boolean collect;
    private Boolean reload;
    private Boolean shoot;
    private  Effect effect;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getMove() {
        return move;
    }

    public void setMove(Boolean move) {
        this.move = move;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public Boolean isCollect() {
        return collect;
    }

    public void setCollect(Boolean collect) {
        this.collect = collect;
    }

    public Boolean isReload() {
        return reload;
    }

    public void setReload(Boolean reload) {
        this.reload = reload;
    }

    public Boolean isShoot() {
        return shoot;
    }

    public void setShoot(Boolean shoot) {
        this.shoot = shoot;
    }
    public void setAsUsed(int limit){

    }
    public void setEffectAsUsed(){
        this.effect.setUsed(false);
    }
    public void endAction(int limit, boolean change){
        int i=0;
        if (this.getMove()!=null )
            this.move =change;
        i++;
        if  (i==limit)
            return;
        if ( this.isCollect()!=null )
            this.collect=change;
        i++;
        if (i==limit)
            return;
        if (this.isReload()!=null )
            this.reload=change;
        i++;
        if (i==limit)
            return;
        if ( this.isShoot()!=null)
            this.shoot=change;
    }

}
