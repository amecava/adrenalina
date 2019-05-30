package it.polimi.ingsw.server.model.players;

import it.polimi.ingsw.server.model.ammo.AmmoCube;
import it.polimi.ingsw.server.model.ammo.AmmoTile;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class Player implements Target {

    private String playerId;
    private boolean connected = true;
    private boolean activePlayer = false;

    private Square oldPosition;
    private Square currentPosition;

    private Bridge bridge;
    private PointStructure pointStructure;

    private List<AmmoCube> ammoCubesList = new ArrayList<>();

    private List<WeaponCard> weaponCardList = new ArrayList<>();
    private List<PowerUpCard> powerUpsList = new ArrayList<>();

    public Player(String playerId, Color playerColor) {

        this.playerId = playerId;

        this.bridge = new Bridge(playerColor);
        this.pointStructure = new PointStructure(this);

        this.ammoCubesList.add(new AmmoCube(Color.RED, false));
        this.ammoCubesList.add(new AmmoCube(Color.BLUE, false));
        this.ammoCubesList.add(new AmmoCube(Color.YELLOW, false));

        this.ammoCubesList.add(new AmmoCube(Color.RED, true));
        this.ammoCubesList.add(new AmmoCube(Color.BLUE, true));
        this.ammoCubesList.add(new AmmoCube(Color.YELLOW, true));

        this.ammoCubesList.add(new AmmoCube(Color.RED, true));
        this.ammoCubesList.add(new AmmoCube(Color.BLUE, true));
        this.ammoCubesList.add(new AmmoCube(Color.YELLOW, true));
    }

    @Override
    public TargetType getTargetType() {

        return TargetType.PLAYER;
    }

    @Override
    public Square getCurrentPosition() {

        return this.currentPosition;
    }

    @Override
    public List<Player> getPlayers() {

        return Arrays.asList(this);
    }

    public String getPlayerId() {

        return this.playerId;
    }

    public Player setConnected(boolean connected) {

        this.connected = connected;

        return this;
    }

    public boolean isActivePlayer() {

        return this.activePlayer;
    }

    public void setActivePlayer(boolean activePlayer) {

        this.activePlayer = activePlayer;
    }

    public Square getOldPosition() {

        return this.oldPosition;
    }

    public Bridge getBridge() {

        return this.bridge;
    }

    public Color getColor() {

        return this.bridge.getColor();
    }

    public int getPoints() {

        return this.bridge.getPoints();
    }

    public void setPoints(int points) {

        this.bridge.setPoints(points);
    }

    public void addKill() {

        this.bridge.addKill();
    }

    public void setFrenzy() {

        this.bridge.setFrenzy();
        this.bridge.setKillStreakCount();
    }

    public void setPointsUsed() {

        this.bridge.setPointsUsed();
    }

    public List<Color> getShots() {

        return this.bridge.getShots();
    }

    public List<Color> getMarks() {

        return this.bridge.getMarks();
    }

    public void damagePlayer(Color color, boolean checkMarks) {

        this.bridge.appendShot(color, checkMarks);
    }

    public void markPlayer(Color color) {

        this.bridge.appendMark(color);
    }

    public void movePlayer(Square destination) {

        if (this.currentPosition != null) {

            this.oldPosition = this.currentPosition;
            this.oldPosition.removePlayer(this);
        }

        this.currentPosition = destination;
        this.currentPosition.addPlayer(this);
    }

    public boolean isDead() {

        return this.bridge.isDead();
    }

    public Adrenalin getAdrenalin() {

        return this.bridge.getAdrenalin();
    }

    public void setAdrenalin(Adrenalin adrenalin) {

        this.bridge.setAdrenalin(adrenalin);
    }

    public void checkAdrenalin() {

        if (!this.bridge.isFrenzyActions()) {
            this.bridge.setAdrenalin(this.bridge.getAdrenalin());
        }
    }

    private ActionStructure getCurrentAction() {

        return this.bridge.getCurrentAction();
    }

    public boolean isFirstPlayer() {

        return this.bridge.isFirstPlayer();
    }

    public void setFirstPlayer(boolean firstPlayer) {

        this.bridge.setFirstPlayer(firstPlayer);
    }

    public void setFrenzyActions(boolean frenzyActions) {

        this.bridge.setFrenzyActions(frenzyActions);
    }

    public boolean isRespawn() {

        return this.bridge.isRespawn();
    }

    public void setRespawn(boolean respawn) {

        this.bridge.setRespawn(respawn);

    }

    public int getRemainingActions() {

        return this.bridge.getRemainingActions();
    }

    public void setRemainingActions(int remainingActions) {

        this.bridge.setRemainingActions(remainingActions);
    }

    public List<ActionStructure> getActions() {

        return this.bridge.getActions();
    }

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

    public void endAction() {

        this.bridge.endAction();
    }

    public PointStructure createPointStructure(List<Color> shots) {

        return this.pointStructure.createPointStructure(shots);
    }

    public List<AmmoCube> getAmmoCubesList() {

        return this.ammoCubesList;
    }

    public List<PowerUpCard> getPowerUpsList() {

        return this.powerUpsList;
    }

    public List<WeaponCard> getWeaponCardList() {

        return this.weaponCardList;
    }

    public void addWeaponCard(WeaponCard weaponCard) {

        weaponCard.setOwner(this);
        this.weaponCardList.add(weaponCard);
    }

    public void addPowerUp(PowerUpCard powerUp) {

        powerUp.setOwner(this);
        this.powerUpsList.add(powerUp);
    }

    public PowerUpCard findPowerUp(String name, Color color) throws CardNotFoundException {

        return this.powerUpsList.stream()
                .filter(x -> x.getName().equals(name) && x.getColor().equals(color))
                .findAny()
                .orElseThrow(() -> new CardNotFoundException("Non hai in mano il powerup che hai selezionato."));
    }

    public PowerUpCard removePowerUp(String name, Color color) throws CardNotFoundException {

        PowerUpCard powerUpCard = this.powerUpsList.stream()
                .filter(x -> x.getName().equals(name) && x.getColor().equals(color))
                .findAny()
                .orElseThrow(() -> new CardNotFoundException("You don't have that power up card!"));

        powerUpCard.setOwner(null);

        return this.powerUpsList.remove(this.powerUpsList.indexOf(powerUpCard));
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

            throw new IllegalActionException("Non puoi raccogliere adesso, seleziona l'azione giusta.");
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

    public void collect(int cardId) throws CardException, IllegalActionException {

        if (this.getCurrentAction() == null || this.getCurrentAction().isCollect() == null
                || !this.getCurrentAction().isCollect()) {

            throw new IllegalActionException("Non puoi raccogliere adesso, seleziona l'azione giusta.");
        }

        if (!this.currentPosition.isSpawn()) {

            throw new SquareException("Sei in un quadrato di rigenerazione, non c'è nessuna carta da raccogliere.");
        }

        if (this.weaponCardList.size() == 3) {

            throw new FullHandException("Hai già tre carte in mano, devi selezionare l'id della carta che vui scartare.");
        }

        this.addWeaponCard((WeaponCard) this.currentPosition.collectWeaponCard(cardId));

        this.getCurrentAction().endAction(2, false);
    }

    public void collect(int playerCardId, int squareCardId)
            throws CardException, IllegalActionException {

        if (this.getCurrentAction() == null || this.getCurrentAction().isCollect() == null
                || !this.getCurrentAction().isCollect()) {

            throw new IllegalActionException("Non puoi raccogliere adesso, seleziona l'azione giusta.");
        }

        if (!this.currentPosition.isSpawn()) {

            throw new SquareException("Non sei in un quadrato di rigenerazione, non devi scartare nessuna carta.");
        }

        if (this.weaponCardList.size() != 3) {

            throw new FullHandException("Non puoi scartare una carta per raccoglierne una se non ne hai già tre in mano");
        }

        this.addWeaponCard((WeaponCard) this.currentPosition.collectWeaponCard(
                this.weaponCardList.stream()
                        .filter(x -> x.getId() == playerCardId)
                        .findAny()
                        .orElseThrow(() -> new CardNotFoundException("Non hai la carta selezionata.")),
                squareCardId));

        this.getCurrentAction().endAction(2, false);
    }

    public void reload(int cardId, List<PowerUpCard> powerUpCardList)
            throws IllegalActionException, CardException {

        if (this.getCurrentAction() == null || this.getCurrentAction().isReload() == null
                || !this.getCurrentAction().isReload()) {

            throw new IllegalActionException("Prima seleziona quale azione vuoi usare (se vuoi ricaricare, seleziona la numero 4).");
        }

        this.weaponCardList.stream()
                .filter(x -> x.getId() == cardId)
                .findAny()
                .orElseThrow(() -> new CardNotFoundException("Hai chiesto di ricaricare un'arma che non hai, scegline una valida."))
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
                .orElseThrow(() -> new CardNotFoundException("Non hai la carta selezionata, selezionane una valida."))
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