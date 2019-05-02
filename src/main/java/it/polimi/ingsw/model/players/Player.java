package it.polimi.ingsw.model.players;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.ammo.AmmoCube;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.rooms.Square;
import it.polimi.ingsw.model.cards.PowerUpCard;
import it.polimi.ingsw.model.cards.WeaponCard;
import it.polimi.ingsw.model.cards.effects.EffectHandler;
import it.polimi.ingsw.model.cards.effects.EffectArgument;
import it.polimi.ingsw.model.cards.effects.TargetType;
import it.polimi.ingsw.model.cards.effects.EffectType;
import it.polimi.ingsw.model.exceptions.IllegalActionException;
import it.polimi.ingsw.model.exceptions.cards.CardException;
import it.polimi.ingsw.model.exceptions.cards.CardNotFoundException;
import it.polimi.ingsw.model.exceptions.effects.EffectException;
import it.polimi.ingsw.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.model.players.bridges.ActionStructure;
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
import java.util.Arrays;
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
    private WeaponCard currentWeaponCard;

    private List<Card> weaponHand = new ArrayList<>();
    private List<AmmoCube> ammoCubesList = new ArrayList<>();
    private List<PowerUpCard> powerUpsList = new ArrayList<>();

    public Player(String playerId, Color playerColor) {

        this.playerId = playerId;
        this.playerColor = playerColor;
        this.bridge = new Bridge(playerColor);
        this.pointStructure = new PointStructure(this);
        this.endOfGame = false;
        this.firstPlayer = false;

        this.ammoCubesList.add(new AmmoCube(Color.RED, false));
        this.ammoCubesList.add(new AmmoCube(Color.BLUE, false));
        this.ammoCubesList.add(new AmmoCube(Color.YELLOW, false));

        for (int i = 0; i < 2; i++) {

            this.ammoCubesList.add(new AmmoCube(Color.RED, true));
            this.ammoCubesList.add(new AmmoCube(Color.BLUE, true));
            this.ammoCubesList.add(new AmmoCube(Color.YELLOW, true));
        }
    }

    @Override
    public TargetType getTargetType() {

        return TargetType.PLAYER;
    }

    @Override
    public List<Player> getPlayers() {

        return Arrays.asList(this);
    }

    public String getPlayerId() {

        return this.playerId;
    }

    public Color getColor() {

        return this.playerColor;
    }

    public Square getOldPosition() {

        return this.oldPosition;
    }

    @Override
    public Square getCurrentPosition() {

        return this.currentPosition;
    }

    public boolean isFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(boolean firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    //useful for tests
    public void addPowerUp(PowerUpCard powerUp) {

        powerUp.setOwner(this);
        this.powerUpsList.add(powerUp);
    }

    public List<PowerUpCard> getPowerUpsList() {

        return this.powerUpsList;
    }

    public Bridge getBridge() {

        return this.bridge;
    }

    public PointStructure getPointStructure() {

        return this.pointStructure;
    }

    public List<Card> getWeaponHand() {

        return this.weaponHand;
    }

    public void setWeaponHand(List<Card> weaponHand) {
        this.weaponHand = weaponHand;
    }

    public List<AmmoCube> getAmmoCubesList() {

        return this.ammoCubesList;
    }

    public void clearHand() {

        this.weaponHand.clear();
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

    public Adrenalin getAdrenalin() {

        return this.bridge.checkAdrenalin();
    }

    public void movePlayer(Square destination) {

        if (this.currentPosition != null) {

            this.oldPosition = this.currentPosition;
            this.oldPosition.removePlayer(this);
        }

        this.currentPosition = destination;
        this.currentPosition.addPlayer(this);
    }

    public void damagePlayer(Color color, boolean checkMarks) {

        this.bridge.appendShot(color, checkMarks);
    }

    public void markPlayer(Color color) {

        this.bridge.appendMark(color);
    }

    public void controlAdrenalin() {

        if (!endOfGame) {
            this.setAdrenalin(this.bridge.checkAdrenalin());
        }
    }

    public void setAdrenalin(Adrenalin adrenalin) {
        this.bridge.setAdrenalin(adrenalin);
    }


    public void frenzyActions() {
        this.endOfGame = true;
    }

    // this method returns an AmmoTile because the Square.collectAmmoTile method removes the
    // item collected from the square, and at the end of the turn the board must be refreshed,
    // so the AmmoTile collected has to be placed in the AmmoTilesDeck by the presenter!!


    private void checkIfCollect() throws IllegalActionException {
        if (this.getCurrentAction() == null || this.getCurrentAction().isCollect() == null
                || !this.getCurrentAction().isCollect()) {

            throw new IllegalActionException(
                    " you can't collect from square in this  chosen action!!!");
        }
    }

    //TODO: copy (and fix) the next comment after calling this method
    /*

    if (tmpTile.getAmmoCubesList().stream().count()  == 2) {
                player.addPowerUp(board.getPowerUp());
            }

     */
    public AmmoTile collect()
            throws SquareTypeException, EmptySquareException, IllegalActionException {

        this.checkIfCollect();

        if (!this.currentPosition.isSpawn()) {

            AmmoTile tmpTile = this.currentPosition.collectAmmoTile();

            // set used player's ammo cubes to not used
            tmpTile.getAmmoCubesList().forEach(x ->
                    this.ammoCubesList.stream().filter(AmmoCube::isUsed)
                            .filter(y -> y.getColor().equals(x))
                            .findFirst().ifPresent(z -> z.setUsed(false)));

            this.getCurrentAction().endAction(2, false);

            return tmpTile;
        }
        throw new SquareTypeException("You're in a spawn square, wrong method call");
    }

    public void collect(int cardId)
            throws IllegalActionException, CardException {

        this.checkIfCollect();

        if (!this.currentPosition.isSpawn()) {

            throw new SquareTypeException("You're not in a spawn square, wrong method call");
        }

        if (this.weaponHand.size() == 3) {

            throw new FullHandException("You already have three cards, wrong method call");
        }

        this.weaponHand.add((this.currentPosition.collectWeaponCard(cardId)));

        ((WeaponCard) this.weaponHand.get(this.weaponHand.size() - 1)).setOwner(this);

        this.getCurrentAction().endAction(2, false);
    }


    // this method needs to be called only after a FullHand exception gets thrown, or after checking
    // player's cards - it assumes that the player already discarded playerCard
    //good code
    //setting the owner for the new card and deleting the owner from discard card
    /////are done in the square method collect(int, int)
    public void collect(int playerCardId, int squareCardId)
            throws CardException, IllegalActionException {

        this.checkIfCollect();

        if (!this.currentPosition.isSpawn()) {

            throw new SquareTypeException("You're not in a spawn square, wrong method call");
        }

        if (this.weaponHand.size() != 3) {

            throw new FullHandException("You already have three cards, wrong method call");
        }

        this.weaponHand.add(this.currentPosition.collectWeaponCard(
                this.weaponHand.stream()
                        .map(x -> (WeaponCard) x)
                        .filter(x -> x.getId() == playerCardId)
                        .findAny()
                        .orElseThrow(() -> new CardNotFoundException("You don't have that card!")),
                squareCardId));

        ((WeaponCard) this.weaponHand.get(this.weaponHand.size() - 1)).setOwner(this);

        this.getCurrentAction().endAction(2, false);
    }

    // all these methods are for action bridge moves
    public void selectAction(int actionId) throws IllegalActionException {

        this.bridge.getActionBridge().selectAction(actionId);
    }

    public void activateCard(int cardId) throws CardException, IllegalActionException {

        if (this.getCurrentAction() == null) {

            throw new IllegalActionException(" please select the action you would like to use!!!");
        }

        if (this.getCurrentAction().isShoot() == null || !this.getCurrentAction().isShoot()) {

            throw new IllegalActionException(" you can't shoot in this action");
        }

        WeaponCard weaponCard = this.weaponHand.stream()
                .map(x -> (WeaponCard) x)
                .filter(x -> x.getId() == cardId)
                .findAny()
                .orElseThrow(() -> new CardNotFoundException("You don't have that card!"));

        weaponCard.activateCard();

        this.currentWeaponCard = weaponCard;

        this.getCurrentAction().endAction(4, false);
    }

    public void useCard(EffectType effectType, EffectArgument effectTarget)
            throws PropertiesException, EffectException, IllegalActionException {

        if (this.getCurrentAction() == null) {

            throw new IllegalActionException(" please select the action you would like to use!!!");
        }

        if (this.currentWeaponCard == null) {

            throw new IllegalActionException(
                    "please activate the card before using it by writing activate card with the cardId!!");
        }

        this.currentWeaponCard.useCard(effectType, effectTarget);
    }

    public void reload(int cardId, List<PowerUpCard> ammoList)
            throws IllegalActionException, CardException {

        if (this.getCurrentAction() == null) {

            throw new IllegalActionException(" please select the action you would like to use!!!");
        }

        WeaponCard tempWeaponCard = this.weaponHand.stream()
                .map(x -> (WeaponCard) x)
                .filter(x -> x.getId() == cardId)
                .findAny()
                .orElseThrow(() -> new CardNotFoundException("You don't have that card!"));

        if (this.getCurrentAction().isReload() == null || !this.getCurrentAction().isReload()) {

            throw new IllegalActionException(" you can't reload in this action!!");
        } else {

            tempWeaponCard.reloadWeapon(ammoList);

            this.getCurrentAction().endAction(2, false);
        }
    }

    public void move(EffectArgument effectTarget, EffectHandler effectHandler)
            throws IllegalActionException, EffectException, PropertiesException {

        if (this.getCurrentAction() == null) {

            throw new IllegalActionException(" please select the action you would like to use!!!");
        }

        if (this.getCurrentAction().getMove() == null || !this.getCurrentAction().getMove()) {

            throw new IllegalActionException(" you can't move yourself right now!!!");
        }

        effectHandler.useEffect(this.getCurrentAction().getEffect(), effectTarget);

        this.getCurrentAction().setEffectAsUsed();
        this.getCurrentAction().endAction(1, false);
    }

    public void endAction() {

        this.currentWeaponCard = null;
        this.bridge.getActionBridge().endAction();
    }

    public ActionStructure getCurrentAction() {

        return this.bridge.getActionBridge().getCurrentAction();
    }
}