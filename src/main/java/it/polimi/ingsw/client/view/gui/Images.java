package it.polimi.ingsw.client.view.gui;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javafx.application.Preloader.ProgressNotification;
import javafx.scene.image.Image;

class Images {

    static final Map<String, Entry<Image, Image>> boardsMap = new HashMap<>();

    static final Map<Integer, Image> weaponsMap = new HashMap<>();
    static final Map<String, Image> powerUpsMap = new HashMap<>();
    static final Map<String, Image> ammoTilesMap = new HashMap<>();

    static final Map<String, Image> playersMap = new HashMap<>();
    static final Map<String, Image> bridgesMap = new HashMap<>();
    static final Map<String, Image> possibleActionsMap = new HashMap<>();


    static final Map<String, Image> dropsMap = new HashMap<>();
    static final Map<String, Image> cubesMap = new HashMap<>();
    static final Map<String, Image> imagesMap = new HashMap<>();

    private Images() {

        //
    }

    static void loadImages(GUIView guiView) {

        guiView.notifyPreloader(new ProgressNotification(0));
        boardsMap.put("board1",
                new SimpleEntry<>(
                        new Image("images/boards/0 - UPUP.png"),
                        new Image("images/boards/0 - UPUP - select.png")));
        boardsMap.put("board2",
                new SimpleEntry<>(
                        new Image("images/boards/1 - DOWNDOWN.png"),
                        new Image("images/boards/1 - DOWNDOWN - select.png")));
        boardsMap.put("board3",
                new SimpleEntry<>(
                        new Image("images/boards/2 - UPDOWN.png"),
                        new Image("images/boards/2 - UPDOWN - select.png")));
        boardsMap.put("board4",
                new SimpleEntry<>(
                        new Image("images/boards/3 - DOWNUP.png"),
                        new Image("images/boards/3 - DOWNUP - select.png")));

        guiView.notifyPreloader(new ProgressNotification(0.05));

        weaponsMap.put(0, new Image("images/cards/AD_weapons_IT_0225.png"));
        weaponsMap.put(1, new Image("images/cards/1.png"));
        weaponsMap.put(2, new Image("images/cards/2.png"));
        weaponsMap.put(3, new Image("images/cards/3.png"));
        weaponsMap.put(4, new Image("images/cards/4.png"));
        weaponsMap.put(5, new Image("images/cards/5.png"));
        weaponsMap.put(6, new Image("images/cards/6.png"));
        guiView.notifyPreloader(new ProgressNotification(0.1));
        weaponsMap.put(7, new Image("images/cards/7.png"));
        weaponsMap.put(8, new Image("images/cards/8.png"));
        weaponsMap.put(9, new Image("images/cards/9.png"));
        weaponsMap.put(10, new Image("images/cards/10.png"));
        weaponsMap.put(11, new Image("images/cards/11.png"));
        weaponsMap.put(12, new Image("images/cards/12.png"));
        weaponsMap.put(13, new Image("images/cards/13.png"));
        guiView.notifyPreloader(new ProgressNotification(0.15));
        weaponsMap.put(14, new Image("images/cards/14.png"));
        weaponsMap.put(15, new Image("images/cards/15.png"));
        weaponsMap.put(16, new Image("images/cards/16.png"));
        weaponsMap.put(17, new Image("images/cards/17.png"));
        weaponsMap.put(18, new Image("images/cards/18.png"));
        weaponsMap.put(19, new Image("images/cards/19.png"));
        weaponsMap.put(20, new Image("images/cards/20.png"));
        guiView.notifyPreloader(new ProgressNotification(0.2));
        weaponsMap.put(21, new Image("images/cards/21.png"));

        powerUpsMap.put("GRANATAVENOM BLU",
                new Image("images/cards/GRANATAVENOMBLUE.png"));
        powerUpsMap.put("GRANATAVENOM ROSSO",
                new Image("images/cards/GRANATAVENOMRED.png"));
        powerUpsMap.put("GRANATAVENOM GIALLO",
                new Image("images/cards/GRANATAVENOMYELLOW.png"));
        powerUpsMap.put("TELETRASPORTO BLU",
                new Image("images/cards/TELETRASPORTOBLUE.png"));
        powerUpsMap.put("TELETRASPORTO ROSSO",
                new Image("images/cards/TELETRASPORTORED.png"));
        powerUpsMap.put("TELETRASPORTO GIALLO",
                new Image("images/cards/TELETRASPORTOYELLOW.png"));
        guiView.notifyPreloader(new ProgressNotification(0.25));
        powerUpsMap.put("MIRINO BLU",
                new Image("images/cards/MIRINOBLUE.png"));
        powerUpsMap.put("MIRINO ROSSO",
                new Image("images/cards/MIRINORED.png"));
        powerUpsMap.put("MIRINO GIALLO",
                new Image("images/cards/MIRINOYELLOW.png"));
        powerUpsMap.put("RAGGIOCINETICO BLU",
                new Image("images/cards/RAGGIOCINETICOBLUE.png"));
        powerUpsMap.put("RAGGIOCINETICO ROSSO",
                new Image("images/cards/RAGGIOCINETICORED.png"));
        powerUpsMap.put("RAGGIOCINETICO GIALLO",
                new Image("images/cards/RAGGIOCINETICOYELLOW.png"));

        ammoTilesMap.put("BLUBLU", new Image("images/ammotiles/BLUBLU.png"));
        guiView.notifyPreloader(new ProgressNotification(0.3));
        ammoTilesMap.put("BLUGIALLOGIALLO", new Image("images/ammotiles/BLUGIALLOGIALLO.png"));
        ammoTilesMap.put("BLUROSSOROSSO", new Image("images/ammotiles/BLUROSSOROSSO.png"));
        ammoTilesMap.put("GIALLOBLU", new Image("images/ammotiles/GIALLOBLU.png"));
        ammoTilesMap.put("GIALLOBLUBLU", new Image("images/ammotiles/GIALLOBLUBLU.png"));
        ammoTilesMap.put("GIALLOGIALLO", new Image("images/ammotiles/GIALLOGIALLO.png"));
        ammoTilesMap.put("GIALLOROSSO", new Image("images/ammotiles/GIALLOROSSO.png"));
        ammoTilesMap.put("GIALLOROSSOROSSO", new Image("images/ammotiles/GIALLOROSSOROSSO.png"));
        guiView.notifyPreloader(new ProgressNotification(0.35));
        ammoTilesMap.put("ROSSOBLU", new Image("images/ammotiles/ROSSOBLU.png"));
        ammoTilesMap.put("ROSSOBLUBLU", new Image("images/ammotiles/ROSSOBLUBLU.png"));
        ammoTilesMap.put("ROSSOGIALLOGIALLO", new Image("images/ammotiles/ROSSOGIALLOGIALLO.png"));
        ammoTilesMap.put("ROSSOROSSO", new Image("images/ammotiles/ROSSOROSSO.png"));

        playersMap.put(":D-strutt-OR3", new Image("images/players/distruttore.png"));
        playersMap.put("Sprog", new Image("images/players/sprog.png"));
        playersMap.put("Violetta", new Image("images/players/violetta.png"));
        guiView.notifyPreloader(new ProgressNotification(0.4));
        playersMap.put("Dozer", new Image("images/players/dozer.png"));
        playersMap.put("Banshee", new Image("images/players/banshee.png"));

        bridgesMap.put("Bansheefalse",
                new Image("images/playerboards/Bansheefalse.png"));
        bridgesMap.put("Bansheetrue",
                new Image("images/playerboards/Bansheetrue.png"));
        bridgesMap.put("Dozerfalse",
                new Image("images/playerboards/Dozerfalse.png"));
        bridgesMap.put("Dozertrue",
                new Image("images/playerboards/Dozertrue.png"));
        bridgesMap.put("Violettafalse",
                new Image("images/playerboards/Violettafalse.png"));
        guiView.notifyPreloader(new ProgressNotification(0.45));
        bridgesMap.put("Violettatrue",
                new Image("images/playerboards/Violettatrue.png"));
        bridgesMap.put("Sprogfalse",
                new Image("images/playerboards/Sprogfalse.png"));
        bridgesMap.put("Sprogtrue",
                new Image("images/playerboards/Sprogtrue.png"));
        bridgesMap.put(":D-strutt-OR3false",
                new Image("images/playerboards/D-strutt-OR3_false_frenesia.png"));
        bridgesMap.put(":D-strutt-OR3true",
                new Image("images/playerboards/D-strutt-OR3_true_frenesia.png"));

        possibleActionsMap.put("Banshee0", new Image("images/actions/Banshee0.png"));
        possibleActionsMap.put("Banshee1", new Image("images/actions/Banshee1.png"));
        guiView.notifyPreloader(new ProgressNotification(0.5));
        possibleActionsMap.put("Banshee2", new Image("images/actions/Banshee2.png"));
        possibleActionsMap.put("Banshee3", new Image("images/actions/Banshee3.png"));
        possibleActionsMap.put("Banshee4", new Image("images/actions/Banshee4.png"));
        possibleActionsMap.put("Banshee5", new Image("images/actions/Banshee5.png"));
        possibleActionsMap.put("Banshee6", new Image("images/actions/Banshee6.png"));
        possibleActionsMap.put("Banshee7", new Image("images/actions/Banshee7.png"));
        possibleActionsMap.put("Banshee8", new Image("images/actions/Banshee8.png"));
        guiView.notifyPreloader(new ProgressNotification(0.55));
        possibleActionsMap.put("Banshee9", new Image("images/actions/Banshee9.png"));
        possibleActionsMap.put("Banshee10", new Image("images/actions/Banshee10.png"));
        possibleActionsMap.put(":D-strutt-OR30", new Image("images/actions/D-strutt-OR30.png"));
        possibleActionsMap.put(":D-strutt-OR31", new Image("images/actions/D-strutt-OR31.png"));
        possibleActionsMap.put(":D-strutt-OR32", new Image("images/actions/D-strutt-OR32.png"));
        possibleActionsMap.put(":D-strutt-OR33", new Image("images/actions/D-strutt-OR33.png"));
        possibleActionsMap.put(":D-strutt-OR34", new Image("images/actions/D-strutt-OR34.png"));
        guiView.notifyPreloader(new ProgressNotification(0.6));
        possibleActionsMap.put(":D-strutt-OR35", new Image("images/actions/D-strutt-OR35.png"));
        //possibleActionsMap
        // .put(":D-strutt-OR36", new Image("images/actions/D-strutt-OR36.png"));
        //possibleActionsMap
        // .put(":D-strutt-OR37", new Image("images/actions/D-strutt-OR38.png"));
        //possibleActionsMap
        //.put(":D-strutt-OR38", new Image("images/actions/D-strutt-OR38.png"));
        //possibleActionsMap
        // .put(":D-strutt-OR39", new Image("images/actions/D-strutt-OR39.png"));
        //possibleActionsMap
        //  .put(":D-strutt-OR310", new Image("images/actions/D-strutt-OR310.png"));
        possibleActionsMap.put("Dozer0", new Image("images/actions/Dozer0.png"));
        guiView.notifyPreloader(new ProgressNotification(0.65));
        possibleActionsMap.put("Dozer1", new Image("images/actions/Dozer1.png"));
        possibleActionsMap.put("Dozer2", new Image("images/actions/Dozer2.png"));
        possibleActionsMap.put("Dozer3", new Image("images/actions/Dozer3.png"));
        possibleActionsMap.put("Dozer4", new Image("images/actions/Dozer4.png"));
        possibleActionsMap.put("Dozer5", new Image("images/actions/Dozer5.png"));
        //possibleActionsMap.put("Dozer6", new Image("images/actions/Dozer6.png"));
        //possibleActionsMap.put("Dozer7", new Image("images/actions/Dozer7.png"));
        guiView.notifyPreloader(new ProgressNotification(0.7));
        //possibleActionsMap.put("Dozer8", new Image("images/actions/Dozer8.png"));
        //possibleActionsMap.put("Dozer9", new Image("images/actions/Dozer9.png"));
        //possibleActionsMap.put("Dozer10", new Image("images/actions/Dozer10.png"));
        possibleActionsMap.put("Sprog0", new Image("images/actions/Sprog0.png"));
        possibleActionsMap.put("Sprog1", new Image("images/actions/Sprog1.png"));
        possibleActionsMap.put("Sprog2", new Image("images/actions/Sprog2.png"));
        possibleActionsMap.put("Sprog3", new Image("images/actions/Sprog3.png"));
        guiView.notifyPreloader(new ProgressNotification(0.75));
        possibleActionsMap.put("Sprog4", new Image("images/actions/Sprog4.png"));
        possibleActionsMap.put("Sprog5", new Image("images/actions/Sprog5.png"));
        //possibleActionsMap.put("Sprog6", new Image("images/actions/Sprog6.png"));
        //possibleActionsMap.put("Sprog7", new Image("images/actions/Sprog7.png"));
        //possibleActionsMap.put("Sprog8", new Image("images/actions/Sprog8.png"));
        //possibleActionsMap.put("Sprog9", new Image("images/actions/Sprog9.png"));
        //possibleActionsMap.put("Spro10", new Image("images/actions/Sprog10.png"));
        guiView.notifyPreloader(new ProgressNotification(0.8));
        possibleActionsMap.put("Violetta0", new Image("images/actions/Violetta0.png"));
        possibleActionsMap.put("Violetta1", new Image("images/actions/Violetta1.png"));
        possibleActionsMap.put("Violetta2", new Image("images/actions/Violetta2.png"));
        possibleActionsMap.put("Violetta3", new Image("images/actions/Violetta3.png"));
        possibleActionsMap.put("Violetta4", new Image("images/actions/Violetta4.png"));
        possibleActionsMap.put("Violetta5", new Image("images/actions/Violetta5.png"));
        //possibleActionsMap.put("Violetta6", new Image("images/actions/Violetta6.png"));
        guiView.notifyPreloader(new ProgressNotification(0.85));
        //possibleActionsMap.put("Violetta7", new Image("images/actions/Violetta7.png"));
        //possibleActionsMap.put("Violetta8", new Image("images/actions/Violetta8.png"));
        //possibleActionsMap.put("Violetta9", new Image("images/actions/Violetta9.png"));
        //possibleActionsMap.put("Violetta10", new Image("images/actions/Violetta10.png"));

        dropsMap.put("GIALLO", new Image("images/drops/drop-yellow.png"));
        dropsMap.put("VERDE", new Image("images/drops/drop-green.png"));
        dropsMap.put("AZZURRO", new Image("images/drops/drop-blue.png"));
        guiView.notifyPreloader(new ProgressNotification(0.9));
        dropsMap.put("VIOLA", new Image("images/drops/drop-violet.png"));
        dropsMap.put("GRIGIO", new Image("images/drops/drop-gray.png"));
        dropsMap.put("morte", new Image("images/drops/teschio.jpg"));

        cubesMap.put("ROSSO", new Image("images/cubes/ROSSO.png"));
        cubesMap.put("GIALLO", new Image("images/cubes/GIALLO.png"));
        cubesMap.put("BLU", new Image("images/cubes/BLU.png"));

        imagesMap.put("background", new Image("images/background.png"));
        guiView.notifyPreloader(new ProgressNotification(0.95));
        imagesMap.put("button", new Image("images/button-2.png"));
        imagesMap.put("adrenalina", new Image("images/adrenaline_text.png"));
        imagesMap.put("rmi", new Image("images/cards/RMI.png"));
        imagesMap.put("tcp", new Image("images/cards/TCP.png"));
        imagesMap.put("distructor", new Image("images/distruttore_big.png"));

        guiView.notifyPreloader(new ProgressNotification(1));
    }
}
