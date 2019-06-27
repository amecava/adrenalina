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

    private StateHandler() {

        //
    }

    static JsonObject createActivePlayerState(Player player, JsonObject object) {

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        objectBuilder.add("state", "activePlayerState");
        objectBuilder.add("info", object.getJsonArray("info"));
        objectBuilder.add("methods", object.getJsonArray("methods"));

        objectBuilder.add("bridge", player.getBridge().toJsonObject());

        return objectBuilder.build();
    }

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
                objectBuilder.add("askUsePrimary", weaponCard.getMap().get(EffectType.PRIMARY).getArgs());
            }

            if (weaponCard.getMap().get(EffectType.ALTERNATIVE) != null && !weaponCard.getMap()
                    .get(EffectType.ALTERNATIVE).isUsed()) {

                arrayBuilder.add("askUseAlternative");
                objectBuilder.add("askUseAlternative", weaponCard.getMap().get(EffectType.ALTERNATIVE).getArgs());
            }

            if (weaponCard.getMap().get(EffectType.OPTIONAL_1) != null && !weaponCard.getMap()
                    .get(EffectType.OPTIONAL_1).isUsed() && (weaponCard.getMap()
                    .get(EffectType.OPTIONAL_1).getActivated() == null || weaponCard.getMap()
                    .get(EffectType.OPTIONAL_1).getActivated())) {

                arrayBuilder.add("askUseOptional1");
                objectBuilder.add("askUseOptional1", weaponCard.getMap().get(EffectType.OPTIONAL_1).getArgs());
            }

            if (weaponCard.getMap().get(EffectType.OPTIONAL_2) != null && !weaponCard.getMap()
                    .get(EffectType.OPTIONAL_2).isUsed() && (weaponCard.getMap()
                    .get(EffectType.OPTIONAL_2).getActivated() == null || weaponCard.getMap()
                    .get(EffectType.OPTIONAL_2).getActivated())) {

                arrayBuilder.add("askUseOptional2");
                objectBuilder.add("askUseOptional2", weaponCard.getMap().get(EffectType.OPTIONAL_2).getArgs());
            }
        }

        objectBuilder.add("methods", arrayBuilder.build());

        return objectBuilder.build();
    }
}
