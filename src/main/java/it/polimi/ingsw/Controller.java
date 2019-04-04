package it.polimi.ingsw;

import it.polimi.ingsw.View.View;
import java.util.List;

public class Controller {

    private Model model;
    private View view;

    public Controller() {
        this.model = new Model();
        this.view = new View();
    }

    public void buildBoard(){
        this.model.buildBoard();
    }

    public void initCardHandler(){
        this.model.initCardHandler();
    }

    public void setPlayersList(List<Player> list){
        this.model.setPlayerList(list);
    }

    public void displayPlayers(){
        this.model.displayPlayers();
    }
}
