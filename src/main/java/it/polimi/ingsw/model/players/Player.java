package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.ammo.AmmoCube;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.players.bridges.Adrenalin;
import it.polimi.ingsw.model.exceptions.cards.EmptySquareException;
import it.polimi.ingsw.model.exceptions.cards.FullHandException;
import it.polimi.ingsw.model.exceptions.cards.SquareTypeException;
import it.polimi.ingsw.model.players.bridges.Bridge;
import it.polimi.ingsw.model.players.bridges.Shots;
import it.polimi.ingsw.model.points.PointStructure;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Target;
import java.util.ArrayList;
import java.util.List;

public class Player implements Target {
    private boolean endOfGame;
    private boolean firstPlayer;
    private String playerId;
    private Color playerColor;
    private Square oldPosition;
    private Square currentPosition;

    private Bridge bridge;
    private PointStructure pointStructure;

    private List<AmmoCube> ammoCubesList = new ArrayList<>();

    private List<Card> weaponHand = new ArrayList<>();

    public Player(String playerId, Color playerColor, EffectHandler effectHandler) {

        this.playerId = playerId;
        this.playerColor = playerColor;
        this.bridge = new Bridge(playerColor, effectHandler);
        this.pointStructure = new PointStructure(this);
        this.endOfGame=false;

        for (int i = 0; i < 3; i++) {

            if (i == 0) {

                this.ammoCubesList.add(new AmmoCube(Color.RED, false));
                this.ammoCubesList.add(new AmmoCube(Color.BLUE, false));
                this.ammoCubesList.add(new AmmoCube(Color.YELLOW, false));
            } else {

                this.ammoCubesList.add(new AmmoCube(Color.RED, true));
                this.ammoCubesList.add(new AmmoCube(Color.BLUE, true));
                this.ammoCubesList.add(new AmmoCube(Color.YELLOW, true));
            }
        }
    }

    public String getPlayerId() {

        return this.playerId;
    }

    public Color getPlayerColor() {

        return this.playerColor;
    }

    public Square getOldPosition() {

        return this.oldPosition;
    }

    @Override
    public Square getCurrentPosition() {

        return this.currentPosition;
    }

    public Bridge getBridge() {

        return this.bridge;
    }


    // useful for tests
    public List<AmmoCube> getAmmoCubesList() {

        return this.ammoCubesList;
    }

    //useful for tests
    public List<Card> getWeaponHand() {
        return this.weaponHand;
    }

    //useful for tests
    public void clearHand() {

        this.weaponHand.clear();
    }


    public PointStructure getPointStructure() {

        return this.pointStructure;
    }

    public Card getWeaponCard(int index) {

        return this.weaponHand.get(index);
    }

    public void setWeaponHand(List<Card> weaponHand) {

        this.weaponHand = weaponHand;
    }

    public int getPoints() {

        return this.bridge.getPoints();
    }

    public void setPoints(int points) {

        this.bridge.setPoints(points);
    }

    public PointStructure createPointStructure(List<Shots> shots) {

        return this.pointStructure.createPointStructure(shots);
    }

    public void setFrenzy() {

        this.bridge.setFrenzy();
        this.bridge.setKillStreakCount();
    }

    public List<Shots> getShots() {

        return this.bridge.getShots();
    }

    public List<Shots> getMarks() {

        return this.bridge.getMarks();
    }

    public boolean isDead() {

        return this.bridge.isDead();
    }

    public void movePlayer(Square destination) {

        if (this.currentPosition != null) {

            this.oldPosition = this.currentPosition;
            this.oldPosition.removePlayer(this);
        }

        this.currentPosition = destination;
        this.currentPosition.addPlayer(this);
    }

    public void damagePlayer(Color color) {
        this.bridge.appendShot(color);
        if (!endOfGame)
            this.bridge.setAdrenalin(this.bridge.checkAdrenalin());
    }
    public void frenzyActions(){
        this.bridge.setAdrenalin(Adrenalin.FIRSTFRENZY);
        this.endOfGame=true;

    }

    public void markPlayer(Color color) {

        this.bridge.appendMark(color);
    }

    // this method returns an AmmoTile because the Square.collectAmmoTile method removes the
    // item collected from the square, and at the end of the turn the board must be refreshed,
    // so the AmmoTile collected has to be placed in the AmmoTilesDeck by the gameHandler
    public AmmoTile collect() throws SquareTypeException, EmptySquareException {

        Card tmpTile;

        if (!this.currentPosition.isSpawn()) {

            tmpTile = this.currentPosition.collectAmmoTile();

            if (tmpTile != null) {

                // set used player's ammo cubes to not used
                ((AmmoTile) tmpTile).getAmmoCubesList().forEach(x ->
                        this.ammoCubesList.stream().filter(AmmoCube::isUsed)
                                .filter(y -> y.getColor().equals(x))
                                .findFirst().ifPresent(z -> z.setUsed(false)));
            } else {
                throw new EmptySquareException("You already collected everything in this square!");
            }

            //TODO add the powerUp to player's hand if the tile allows it

        } else {
            throw new SquareTypeException("You're in a spawn square, wrong method call");
        }

        return (AmmoTile) tmpTile;
    }

    public void collect(int cardId)
            throws SquareTypeException, FullHandException, EmptySquareException {

        if (!this.currentPosition.isSpawn()) {

            throw new SquareTypeException("You're not in a spawn square, wrong method call");

        } else if (this.weaponHand.size() == 3) {

            throw new FullHandException("You already have three cards, wrong method call");

        } else {

            this.weaponHand.add(this.currentPosition.collectWeaponCard(cardId));
        }
    }

    public void collect(Card playerCard, int squareCardId)
            throws SquareTypeException, EmptySquareException {

        if (!this.currentPosition.isSpawn()) {

            throw new SquareTypeException("You're not in a spawn square, wrong method call");

        } else if (this.weaponHand.size() == 3) {

            this.weaponHand.add(this.currentPosition.collectWeaponCard(playerCard, squareCardId));

        } else {

            throw new SquareTypeException(
                    "You're in a spawn square, but your hand is not full, wrong method call");
        }
    }

}
