package it.polimi.ingsw.server.presenter;

import it.polimi.ingsw.server.model.cards.WeaponCard;
import it.polimi.ingsw.server.model.cards.effects.EffectType;
import it.polimi.ingsw.server.model.players.Player;
import it.polimi.ingsw.server.model.players.bridges.ActionStructure;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

class StateHandler {

    /**
     * Creates the StateHandler.
     */
    private StateHandler() {

        //
    }

    /**
     * Creates the ActivePlayerState based on what the player should be able to do, adding the
     * possible actions that the player can perform in the moment of the game this method is
     * called.
     *
     * @param player The Player that needs his State to be updated.
     * @param object The JsonObject with the information of the state of the player.
     * @return The JsonObject with the possible actions that the player can perform.
     */
    static JsonObject createActivePlayerState(Player player, JsonObject object) {

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        objectBuilder.add("state", "activePlayerState");
        objectBuilder.add("info", object.getJsonArray("info"));
        objectBuilder.add("methods", object.getJsonArray("methods"));

        objectBuilder.add("bridge", player.getBridge().toJsonObject());

        return objectBuilder.build();
    }

    /**
     * Creates the ActionState based on what the player should be able to do, adding the possible
     * actions that the player can perform in the moment of the game this method is called.
     *
     * @param player The Player that needs his State to be updated.
     * @param object The JsonObject with the information of the state of the player.
     * @return The JsonObject with the possible actions that the player can perform.
     */
    static JsonObject createActionState(Player player, JsonObject object) {

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json
                .createArrayBuilder(object.getJsonArray("methods"));

        objectBuilder.add("state", "actionState");
        objectBuilder.add("info", object.getJsonArray("info"));

        ActionStructure action = player.getCurrentAction();

        if (action.getMove() != null && action.getMove()) {

            arrayBuilder.add("moveAction");
        }

        if (action.isCollect() != null && action.isCollect()) {

            arrayBuilder.add("askCollect");
        }

        if (action.isShoot() != null && action.isShoot()) {

            arrayBuilder.add("askActivateWeapon");
        }

        if (action.isReload() != null && action.isReload()) {

            arrayBuilder.add("askReload");
        }

        objectBuilder.add("methods", arrayBuilder.build());

        return objectBuilder.build();
    }

    /**
     * Creates the ShootState based on what the player should be able to do, adding the
     * effects that the player can execute in the moment of the game this method is called.
     *
     * @param player The Player that needs his State to be updated.
     * @param object The JsonObject with the information of the state of the player.
     * @return The JsonObject with the possible actions that the player can perform.
     */
    static JsonObject createShootState(Player player, JsonObject object) {

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json
                .createArrayBuilder(object.getJsonArray("methods"));

        objectBuilder.add("state", "shootState");
        objectBuilder.add("info", object.getJsonArray("info"));

        WeaponCard weaponCard = player.getCurrentWeaponCard();

        if (weaponCard.isLoaded()) {

            if (weaponCard.getMap().get(EffectType.PRIMARY) != null && !weaponCard.getMap()
                    .get(EffectType.PRIMARY).isUsed()) {

                arrayBuilder.add("askUsePrimary");
                objectBuilder
                        .add("primaryArgs", weaponCard.getMap().get(EffectType.PRIMARY).getArgs());
                objectBuilder.add("primaryCost",
                        !weaponCard.getMap().get(EffectType.PRIMARY).getCost().isEmpty());
                objectBuilder.add("primaryTargetType",
                        weaponCard.getMap().get(EffectType.PRIMARY).getTargetType().toString());
            }

            if (weaponCard.getMap().get(EffectType.ALTERNATIVE) != null && !weaponCard.getMap()
                    .get(EffectType.ALTERNATIVE).isUsed()) {

                arrayBuilder.add("askUseAlternative");
                objectBuilder.add("alternativeArgs",
                        weaponCard.getMap().get(EffectType.ALTERNATIVE).getArgs());
                objectBuilder.add("alternativeCost",
                        !weaponCard.getMap().get(EffectType.ALTERNATIVE).getCost().isEmpty());
                objectBuilder.add("alternativeTargetType",
                        weaponCard.getMap().get(EffectType.ALTERNATIVE).getTargetType().toString());
            }

            if (weaponCard.getMap().get(EffectType.OPTIONAL_1) != null && !weaponCard.getMap()
                    .get(EffectType.OPTIONAL_1).isUsed() && (weaponCard.getMap()
                    .get(EffectType.OPTIONAL_1).getActivated() == null || weaponCard.getMap()
                    .get(EffectType.OPTIONAL_1).getActivated())) {

                arrayBuilder.add("askUseOptional1");
                objectBuilder.add("optional1Args",
                        weaponCard.getMap().get(EffectType.OPTIONAL_1).getArgs());
                objectBuilder.add("optional1Cost",
                        !weaponCard.getMap().get(EffectType.OPTIONAL_1).getCost().isEmpty());
                objectBuilder.add("optional1TargetType",
                        weaponCard.getMap().get(EffectType.OPTIONAL_1).getTargetType().toString());
            }

            if (weaponCard.getMap().get(EffectType.OPTIONAL_2) != null && !weaponCard.getMap()
                    .get(EffectType.OPTIONAL_2).isUsed() && (weaponCard.getMap()
                    .get(EffectType.OPTIONAL_2).getActivated() == null || weaponCard.getMap()
                    .get(EffectType.OPTIONAL_2).getActivated())) {

                arrayBuilder.add("askUseOptional2");
                objectBuilder.add("optional2Args",
                        weaponCard.getMap().get(EffectType.OPTIONAL_2).getArgs());
                objectBuilder.add("optional2Cost",
                        !weaponCard.getMap().get(EffectType.OPTIONAL_2).getCost().isEmpty());
                objectBuilder.add("optional2TargetType",
                        weaponCard.getMap().get(EffectType.OPTIONAL_2).getTargetType().toString());
            }
        }

        objectBuilder.add("methods", arrayBuilder.build());

        return objectBuilder.build();
    }
}
