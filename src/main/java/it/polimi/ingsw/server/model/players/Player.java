package it.polimi.ingsw.server.model.players;

import it.polimi.ingsw.server.model.ammo.AmmoCube;
import it.polimi.ingsw.server.model.ammo.AmmoTile;
import it.polimi.ingsw.server.model.ammo.Cost;
import it.polimi.ingsw.server.model.board.rooms.Square;
import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.PowerUpCard;
import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.EffectHandler;
import it.polimi.ingsw.server.model.cards.effects.EffectArgument;
import it.polimi.ingsw.server.model.cards.effects.TargetType;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.exceptions.jacop.IllegalActionException;
import it.polimi.ingsw.server.model.exceptions.cards.CardException;
import it.polimi.ingsw.server.model.exceptions.cards.CardNotFoundException;
import it.polimi.ingsw.server.model.exceptions.cards.EmptySquareException;
import it.polimi.ingsw.server.model.exceptions.effects.EffectException;
import it.polimi.ingsw.server.model.exceptions.properties.PropertiesException;
import it.polimi.ingsw.server.model.players.bridges.ActionStructure;
import it.polimi.ingsw.server.model.players.bridges.Adrenalin;
import it.polimi.ingsw.server.model.exceptions.cards.FullHandException;
import it.polimi.ingsw.server.model.exceptions.cards.SquareException;
import it.polimi.ingsw.server.model.players.bridges.Bridge;
import it.polimi.ingsw.server.model.points.PointStructure;
import it.polimi.ingsw.server.model.cards.Target;
import java.io.Serializable;
import it.polimi.ingsw.common.JsonUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 * The class representing a player of the match.
 */
public class Player implements Target, Serializable {

    /**
     * The id of the player.
     */
    private String playerId;

    /**
     * It says if this player is still connected.
     */
    private boolean connected = true;

    /**
     * It says if this player is the activePlayer.
     */
    private boolean activePlayer = false;

    /**
     * The last position of this player.
     */
    private Square oldPosition;

    /**
     * The current position of the player.
     */
    private Square currentPosition;

    /**
     * The Bridge of this player.
     */
    private Bridge bridge;

    /**
     * The point structure that counts the points of this player.
     */
    private PointStructure pointStructure;

    /**
     * The list AmmoCube of the player.
     */
    private List<AmmoCube> ammoCubesList = new ArrayList<>();

    /**
     * The list WeaponCard of the player.
     */
    private List<WeaponCard> weaponCardList = new ArrayList<>();

    /**
     * The list PowerUpCard of the player.
     */
    private List<PowerUpCard> powerUpsList = new ArrayList<>();

    /**
     * Creates the player based on the parameters.
     *
     * @param playerId The id of the player.
     * @param playerColor The color of the player.
     */
    public Player(String playerId, Color playerColor) {

        this.playerId = playerId;

        this.bridge = new Bridge(playerColor);
        this.pointStructure = new PointStructure(this);

        this.ammoCubesList.add(new AmmoCube(Color.ROSSO, false));
        this.ammoCubesList.add(new AmmoCube(Color.ROSSO, true));
        this.ammoCubesList.add(new AmmoCube(Color.ROSSO, true));

        this.ammoCubesList.add(new AmmoCube(Color.BLU, false));
        this.ammoCubesList.add(new AmmoCube(Color.BLU, true));
        this.ammoCubesList.add(new AmmoCube(Color.BLU, true));

        this.ammoCubesList.add(new AmmoCube(Color.GIALLO, false));
        this.ammoCubesList.add(new AmmoCube(Color.GIALLO, true));
        this.ammoCubesList.add(new AmmoCube(Color.GIALLO, true));
    }

    /**
     * Gets the TargetType corresponding to Player.
     *
     * @return The TargetType.
     */
    @Override
    public TargetType getTargetType() {

        return TargetType.PLAYER;
    }

    /**
     * Gets the current position of the player.
     *
     * @return The Square which is the current position of the player.
     */
    @Override
    public Square getCurrentPosition() {

        return this.currentPosition;
    }

    /**
     * Gets a list containing only this player.
     *
     * @return A list containing only this player.
     */
    @Override
    public List<Player> getPlayers() {

        return Arrays.asList(this);
    }

    /**
     * Gets the id of the player.
     *
     * @return The id of the player.
     */
    public String getPlayerId() {

        return this.playerId;
    }

    /**
     * Sets the connection status of the player.
     *
     * @param connected The connection status of the player.
     * @return This player.
     */
    public Player setConnected(boolean connected) {

        this.connected = connected;

        return this;
    }

    /**
     * Checks if this is the current active player.
     *
     * @return a boolean that says if this is the current active player.
     */
    public boolean isActivePlayer() {

        return this.activePlayer;
    }

    /**
     * Sets the activePlayer property.
     *
     * @param activePlayer The new state.
     */
    public void setActivePlayer(boolean activePlayer) {

        this.activePlayer = activePlayer;
    }

    /**
     * Gets the last position of the player.
     *
     * @return The Square corresponding to the last position of the player.
     */
    public Square getOldPosition() {

        return this.oldPosition;
    }

    /**
     * Gets the bridge of the player.
     *
     * @return The
     */
    public Bridge getBridge() {

        return this.bridge;
    }

    /**
     * Gets the color of the player.
     *
     * @return The Color of the player.
     */
    public Color getColor() {

        return this.bridge.getColor();
    }

    /**
     * Gets the points of the player.
     *
     * @return The integer value of the points.
     */
    public int getPoints() {

        return this.bridge.getPoints();
    }

    /**
     * Sets the points of the player.
     *
     * @param points The integer value of the points of the player.
     */
    public void setPoints(int points) {

        this.bridge.setPoints(points);
    }

    /**
     * Adds a kill to the bridge of the player.
     */
    public void addKill() {

        this.bridge.addKill();
    }

    /**
     * Sets the frenzy mode to the player.
     */
    public void setFrenzy() {

        this.bridge.setFrenzy();
        this.bridge.setKillStreakCount();
    }

    /**
     * Sets a point used to the bridge of the player.
     */
    public void setPointsUsed() {

        this.bridge.setPointsUsed();
    }

    /**
     * Gets the damages of the player.
     *
     * @return The list of Color of the damages.
     */
    public List<Color> getShots() {

        return this.bridge.getShots();
    }

    /**
     * Gets the marks of the player.
     *
     * @return The list of Color of the marks of the player.
     */
    public List<Color> getMarks() {

        return this.bridge.getMarks();
    }

    /**
     * Adds a mark to the bridge of the player.
     *
     * @param color The Color of the damage that will be added.
     * @param checkMarks A boolean that says if this damage implies adding the marks.
     */
    public void damagePlayer(Color color, boolean checkMarks) {

        this.bridge.appendShot(color, checkMarks);
    }

    /**
     * Adds a mark to the bridge of the player.
     *
     * @param color The Color of the mark that will be added.
     */
    public void markPlayer(Color color) {

        this.bridge.appendMark(color);
    }

    /**
     * Performs the "move" action. Moves the player to the square "destination".
     *
     * @param destination The Square which is the final destination of the movement.
     */
    public void movePlayer(Square destination) {

        if (this.currentPosition != null) {

            this.oldPosition = this.currentPosition;
            this.oldPosition.removePlayer(this);
        }

        this.currentPosition = destination;
        this.currentPosition.addPlayer(this);
    }

    /**
     * Checks if the player is dead.
     *
     * @return A boolean that says if the player is dead.
     */
    public boolean isDead() {

        return this.bridge.isDead();
    }

    /**
     * Gets the level of adrenalin of the player.
     *
     * @return The Adrenalin enum value.
     */
    public Adrenalin getAdrenalin() {

        return this.bridge.getAdrenalin();
    }

    /**
     * Sets the adrenalin level of the player.
     *
     * @param adrenalin The new adrenalin level of the player.
     */
    public void setAdrenalin(Adrenalin adrenalin) {

        this.bridge.setAdrenalin(adrenalin);
    }

    /**
     * Checks the adrenalin level of the player.
     */
    public void checkAdrenalin() {

        if (!this.bridge.isFrenzyActions()) {
            this.bridge.setAdrenalin(this.bridge.getAdrenalin());
        }
    }

    /**
     * Gets the action of the player currently activated.
     *
     * @return The ActionStructure of the action.
     */
    public ActionStructure getCurrentAction() {

        return this.bridge.getCurrentAction();
    }

    /**
     * Gets the weaponCard currently activated by the user.
     *
     * @return The WeaponCard currently activated by the user.
     */
    public WeaponCard getCurrentWeaponCard() {

        return this.bridge.getCurrentWeaponCard();
    }

    /**
     * Checks if the player is shooting.
     *
     * @return A boolean that says if the player is shooting.
     */
    public boolean isShooting() {

        return this.bridge.isShooting();
    }

    /**
     * Checks if the player is the first one of the turn.
     *
     * @return A boolean that says if the first one of the turn.
     */
    public boolean isFirstPlayer() {

        return this.bridge.isFirstPlayer();
    }

    /**
     * Sets the firstPlayer property according to the boolean parameter.
     *
     * @param firstPlayer The boolean value of the firstPlayer property. True if this is the first player.
     */
    public void setFirstPlayer(boolean firstPlayer) {

        this.bridge.setFirstPlayer(firstPlayer);
    }

    /**
     * Sets the frenzyActions property according to the boolean parameter.
     *
     * @param frenzyActions The boolean value of the frenzyAction property. True if the player is in
     * a state in which he has frenzy actions.
     */
    public void setFrenzyActions(boolean frenzyActions) {

        this.bridge.setFrenzyActions(frenzyActions);
    }

    /**
     * Checks if the player needs to respawn.
     * @return The boolean that says if the player needs to respawn.
     */
    public boolean isRespawn() {

        return this.bridge.isRespawn();
    }

    /**
     * Sets the respawn boolean.
     * @param respawn The boolean value that the player needs.
     */
    public void setRespawn(boolean respawn) {

        this.bridge.setRespawn(respawn);

    }

    /**
     * Gets the remaining actions of the player.
     * @return The integer corresponding to the number of remaining actions of the player.
     */
    public int getRemainingActions() {

        return this.bridge.getRemainingActions();
    }

    /**
     * Sets the number of remaining actions of the player.
     * @param remainingActions The integer of the remaining actions of the player.
     */
    public void setRemainingActions(int remainingActions) {

        this.bridge.setRemainingActions(remainingActions);
    }

    /**
     * Gets the actions of the player.
     * @return The list of ActionStructure of the player.
     */
    public List<ActionStructure> getActions() {

        return this.bridge.getActions();
    }

    /**
     * Selects the action that the user wants to perform.
     * @param actionId The number of the action that the user wants to perform.
     * @throws IllegalActionException If the action cannot be performed at the moment.
     */
    public void selectAction(int actionId) throws IllegalActionException {

        if ((this.getRemainingActions() == -1) || !this.activePlayer) {

            throw new IllegalActionException(
                    "Per selezionare un'azione aspetta che sia il tuo turno.");
        }
        if (this.currentPosition == null) {

            throw new IllegalActionException(
                    "Per selezionare un'azione devi prima rigenerarti in un quadrato.");
        }
        if ((this.getRemainingActions() == 0
                && actionId != 4)) {

            throw new IllegalActionException(
                    "Hai finito le azioni disponibili, puoi solo ricaricare.");
        }

        this.bridge.selectAction(actionId - 1);

        this.setRemainingActions(actionId == 4 ? 0 : this.getRemainingActions() - 1);
    }

    /**
     * End the action the player is currently performing.
     */
    public void endAction() {

        this.bridge.endAction();
    }

    /**
     * Creates a PointStructure for the shots list of color.
     * @param shots The List of Color that will be added to the structure.
     * @return The PointStructure.
     */
    public PointStructure createPointStructure(List<Color> shots) {

        return this.pointStructure.createPointStructure(shots);
    }

    /**
     * Gets the list of cubes of the player.
     * @return The List of AmmoCube.
     */
    public List<AmmoCube> getAmmoCubesList() {

        return this.ammoCubesList;
    }

    /**
     * Gets the list of power ups of the player.
     * @return The List of AmmoCube.
     */
    public List<PowerUpCard> getPowerUpsList() {

        return this.powerUpsList;
    }

    /**
     * Gets the list of weapons of the player.
     * @return The List of AmmoCube.
     */
    public List<WeaponCard> getWeaponCardList() {

        return this.weaponCardList;
    }

    /**
     * Adds a weaponCard to the hand of the player.
     * @param weaponCard The WeaponCard to be added.
     */
    void addWeaponCard(WeaponCard weaponCard) {

        weaponCard.setOwner(this);
        this.weaponCardList.add(weaponCard);
    }

    /**
     * Adds a powerUp to the hand of the player.
     * @param powerUp The powerUp to be added.
     */
    public void addPowerUp(PowerUpCard powerUp) {

        powerUp.setOwner(this);
        this.powerUpsList.add(powerUp);
    }

    /**
     * Searches a power up based on its name and color.
     * @param name The name of the power up.
     * @param color The color o the power up.
     * @return The PowerUpCard object.
     * @throws CardNotFoundException If the searched power up is not present in player's hande.
     */
    public PowerUpCard findPowerUp(String name, Color color) throws CardNotFoundException {

        return this.powerUpsList.stream()
                .filter(x -> JsonUtility.levenshteinDistance(name, x.getName()) <= 3 && x.getColor()
                        .equals(color))
                .findAny()
                .orElseThrow(() -> new CardNotFoundException(
                        "Non hai in mano il powerup che hai selezionato."));
    }

    /**
     * Removes a power up from player's
     * @param name
     * @param color
     * @return
     * @throws CardNotFoundException
     */
    private PowerUpCard removePowerUp(String name, Color color) throws CardNotFoundException {

        PowerUpCard powerUpCard = this.powerUpsList.stream()
                .filter(x -> x.getName().equals(name) && x.getColor().equals(color))
                .findAny()
                .orElseThrow(() -> new CardNotFoundException("You don't have that power up card!"));

        powerUpCard.setOwner(null);

        return this.powerUpsList.remove(this.powerUpsList.indexOf(powerUpCard));
    }

    public PowerUpCard removePowerUp(PowerUpCard card) {

        card.setOwner(null);

        return this.powerUpsList.remove(this.powerUpsList.indexOf(card));
    }

    public void removePlayerFromBoard() {

        this.currentPosition.removePlayer(this);

        this.currentPosition = null;
        this.oldPosition = null;

        this.setRespawn(true);
    }

    public void move(EffectArgument effectTarget, EffectHandler effectHandler)
            throws PropertiesException, EffectException, IllegalActionException {

        if (this.getCurrentAction() == null || this.getCurrentAction().getMove() == null || !this
                .getCurrentAction().getMove()) {

            throw new IllegalActionException("Non puoi muoverti adesso.");
        }

        effectHandler.useEffect(this.getCurrentAction().getEffect(), effectTarget);

        this.getCurrentAction().setEffectAsUsed();
        this.getCurrentAction().endAction(1, false);
    }

    public AmmoTile collect()
            throws SquareException, EmptySquareException, IllegalActionException {

        if (this.getCurrentAction() == null || this.getCurrentAction().isCollect() == null || !this
                .getCurrentAction().isCollect()) {

            throw new IllegalActionException(
                    "Non puoi raccogliere adesso, seleziona un'altra azione.");
        }

        if (this.currentPosition.isSpawn()) {

            throw new SquareException(
                    "Sei in uno square di rigenerazione, seleziona l'id della carta da raccogliere\ned eventualmente l'id della carta da scartare.");
        }

        AmmoTile tmpTile = this.currentPosition.collectAmmoTile();

        tmpTile.getAmmoCubesList().forEach(x ->
                this.ammoCubesList.stream().filter(AmmoCube::isUsed)
                        .filter(y -> y.getColor().equals(x))
                        .findFirst().ifPresent(z -> z.setUsed(false)));

        if (tmpTile.hasPowerUpCard() && this.powerUpsList.size() < 3) {

            this.addPowerUp(tmpTile.getPowerUpCard());
        }

        this.getCurrentAction().endAction(2, false);

        return tmpTile;
    }

    public void collect(int cardId, List<PowerUpCard> powerUpCards)
            throws CardException, IllegalActionException {

        if (this.getCurrentAction() == null || this.getCurrentAction().isCollect() == null
                || !this.getCurrentAction().isCollect()) {

            throw new IllegalActionException(
                    "Non puoi raccogliere adesso, seleziona l'azione giusta.");
        }

        if (!this.currentPosition.isSpawn()) {

            throw new SquareException(
                    "Sei in un quadrato di rigenerazione, non c'è nessuna carta da raccogliere.");
        }

        if (this.weaponCardList.size() == 3) {

            throw new FullHandException(
                    "Hai già tre carte in mano, devi selezionare l'id della carta che vui scartare.");
        }

        List<Color> costCopy = new ArrayList<>(this.currentPosition.getCostOfCard(cardId));

        costCopy.remove(0);

        Cost.checkCost(this, costCopy, powerUpCards);

        costCopy.forEach(x ->
                this.getAmmoCubesList().stream()
                        .filter(y -> y.getColor().equals(x) && !y.isUsed())
                        .findFirst().get()
                        .setUsed(true)
        );

        this.addWeaponCard((WeaponCard) this.currentPosition.collectWeaponCard(cardId));

        this.getCurrentAction().endAction(2, false);
    }

    public void collect(int squareCardId, int playerCardId, List<PowerUpCard> powerUpCards)
            throws CardException, IllegalActionException {

        if (this.getCurrentAction() == null || this.getCurrentAction().isCollect() == null
                || !this.getCurrentAction().isCollect()) {

            throw new IllegalActionException(
                    "Non puoi raccogliere adesso, seleziona l'azione giusta.");
        }

        if (!this.currentPosition.isSpawn()) {

            throw new SquareException(
                    "Non sei in un quadrato di rigenerazione, non devi scartare nessuna carta.");
        }

        if (this.weaponCardList.size() != 3) {

            throw new FullHandException(
                    "Non puoi scartare una carta per raccoglierne una se non ne hai già tre in mano");
        }

        WeaponCard card = this.weaponCardList.stream()
                .filter(x -> x.getId() == playerCardId)
                .findAny()
                .orElseThrow(() -> new CardNotFoundException("Non hai la carta selezionata."));

        this.addWeaponCard((WeaponCard) this.currentPosition.collectWeaponCard(
                card,
                squareCardId));

        this.weaponCardList.remove(card);
        card.setOwner(null);

        this.getCurrentAction().endAction(2, false);
    }

    public void reload(int cardId, List<PowerUpCard> powerUpCardList)
            throws IllegalActionException, CardException {

        if (this.getCurrentAction() == null || this.getCurrentAction().isReload() == null
                || !this.getCurrentAction().isReload()) {

            throw new IllegalActionException(
                    "Prima seleziona quale azione vuoi usare (se vuoi ricaricare, seleziona la numero 4).");
        }

        this.weaponCardList.stream()
                .filter(x -> x.getId() == cardId)
                .findAny()
                .orElseThrow(() -> new CardNotFoundException(
                        "Hai chiesto di ricaricare un'arma che non hai, scegline una valida."))
                .reloadWeapon(powerUpCardList);

        this.getCurrentAction().endAction(2, false);

    }

    public void activateCard(int cardId) throws CardException, IllegalActionException {

        if (this.getCurrentAction() == null || this.getCurrentAction().isShoot() == null || !this
                .getCurrentAction().isShoot()) {

            throw new IllegalActionException("Non puoi sparare adesso.");
        }

        this.bridge.setCurrentWeaponCard(this.weaponCardList.stream()
                .filter(x -> x.getId() == cardId)
                .findAny()
                .orElseThrow(() -> new CardNotFoundException(
                        "Non hai la carta selezionata, selezionane una valida."))
                .activateCard());

        this.getCurrentAction().endAction(4, false);
    }

    public void useCard(EffectType effectType, EffectArgument effectTarget,
            List<PowerUpCard> powerUpCardList)
            throws PropertiesException, EffectException, CardException, IllegalActionException {

        if (this.bridge.getCurrentWeaponCard() == null) {

            throw new IllegalActionException("Prima scrivi quale carta vuoi utilizzare.");
        }

        this.bridge.getCurrentWeaponCard().useCard(effectType, effectTarget, powerUpCardList);
    }

    public PowerUpCard spawn(String name, Color color)
            throws IllegalActionException, CardNotFoundException {

        if ((this.isActivePlayer() && this.currentPosition == null)
                || this.isRespawn()) {

            PowerUpCard powerUpCard = this.removePowerUp(name, color);

            this.setRespawn(false);

            return powerUpCard;

        } else {

            throw new IllegalActionException("You can't respawn!");
        }
    }

    public JsonObject toJsonObject() {

        JsonArrayBuilder weaponsBuilder = Json.createArrayBuilder();
        JsonArrayBuilder powerUpsBuilder = Json.createArrayBuilder();
        JsonArrayBuilder cubesBuilder = Json.createArrayBuilder();

        this.weaponCardList.stream().map(Card::toJsonObject).forEach(weaponsBuilder::add);
        this.powerUpsList.stream().map(Card::toJsonObject).forEach(powerUpsBuilder::add);
        this.ammoCubesList.stream()
                .filter(x -> !x.isUsed())
                .map(AmmoCube::getColor)
                .map(Color::toString)
                .forEach(cubesBuilder::add);

        return Json.createObjectBuilder()
                .add("playerId", this.playerId)
                .add("character", this.getColor().getCharacter())
                .add("points", this.getPoints())
                .add("isActivePlayer", this.activePlayer)
                .add("bridge", this.bridge.toJsonObject())
                .add("weapons", weaponsBuilder.build())
                .add("powerUps", powerUpsBuilder.build())
                .add("ammoCubes", cubesBuilder.build())
                .add("connected", this.connected)
                .build();
    }
}