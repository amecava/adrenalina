package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.board.rooms.Connection;
import it.polimi.ingsw.model.board.rooms.Room;
import it.polimi.ingsw.model.board.rooms.Square;
import java.io.PipedReader;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class BoardBuilder {

    private Board board;
    private List<Room> roomsList;
    private List<Square> squaresList;
    private JSONParser parser;
    private static final String NORTH = "north";
    private static final String SOUTH = "south";
    private static final String EAST = "east";
    private static final String WEST = "west";
    private static final String ROOMINDEX = "roomIndex";
    private static final String SQUAREINDEX = "squareIndex";
    private static final String CONNECTION = "connection";


    public BoardBuilder() {
        this.board = new Board();
        this.roomsList = new ArrayList<>();
        this.squaresList = new ArrayList<>();
        this.parser = new JSONParser();
    }

    public Board buildBoard(int boardID) {

        this.setAndConnectFromJson(boardID);
        this.board.setRoomsList(this.roomsList);

        return this.board;
    }

    public Board getBoard() {
        return this.board;
    }

    private void setAndConnectFromJson(int boardID) {

        try {
            FileReader reader = new FileReader(
                    "lib/Board.json");
            JSONArray b = (JSONArray) this.parser.parse(reader);
            JSONObject jsonObject = (JSONObject) b.get(0);
            JSONArray a;
            b = (JSONArray) jsonObject.get("boards");
            jsonObject = (JSONObject) b.get(boardID);
            b = (JSONArray) jsonObject.get("rooms");
            JSONArray c = b;

            int i = 0;
            int j;
            for (Object o : b) {
                jsonObject = (JSONObject) b.get(i);
                this.roomsList.add(new Room(Color.valueOf((String) jsonObject.get("roomColor"))));
                a = (JSONArray) jsonObject.get("squares");
                j = 0;
                for (Object square : a) {
                    jsonObject = (JSONObject) a.get(j);
                    this.squaresList.add(new Square(roomsList.get(i),
                            Math.toIntExact((Long) jsonObject.get("squareId"))));
                    j++;
                }
                this.roomsList.get(i).addSquaresList(this.squaresList);
                this.squaresList.clear();
                i++;
            }

            JSONObject jsonObjectNorth;
            JSONObject jsonObjectSouth;
            JSONObject jsonObjectEast;
            JSONObject jsonObjectWest;
            b = c;

            for (Room r : this.roomsList) {

                jsonObject = (JSONObject) b.get(roomsList.indexOf(r));
                for (Square s : r.getSquaresList()) {

                    c = (JSONArray) jsonObject.get("squares");
                    i = r.getSquaresList().indexOf(s);

                    jsonObjectNorth = (JSONObject) c.get(i);
                    jsonObjectSouth = jsonObjectNorth;
                    jsonObjectEast = jsonObjectSouth;
                    jsonObjectWest = jsonObjectEast;

                    jsonObjectNorth = (JSONObject) jsonObjectNorth.get(NORTH);
                    jsonObjectSouth = (JSONObject) jsonObjectSouth.get(SOUTH);
                    jsonObjectEast = (JSONObject) jsonObjectEast.get(EAST);
                    jsonObjectWest = (JSONObject) jsonObjectWest.get(WEST);

                    s.setConnections(Connection.valueOf((String) jsonObjectNorth.get(CONNECTION)),
                            Connection.valueOf((String) jsonObjectSouth.get(CONNECTION)),
                            Connection.valueOf((String) jsonObjectEast.get(CONNECTION)),
                            Connection.valueOf((String) jsonObjectWest.get(CONNECTION)));

                    if (s.getNorthConnection() != Connection.ENDMAP) {
                        s.setNorth(this.roomsList
                                .get(Math.toIntExact((Long) jsonObjectNorth.get(ROOMINDEX)))
                                .getSquaresList()
                                .get(Math.toIntExact((Long) jsonObjectNorth.get(SQUAREINDEX))));
                    }

                    if (s.getSouthConnection() != Connection.ENDMAP) {
                        s.setSouth(this.roomsList
                                .get(Math.toIntExact((Long) jsonObjectSouth.get(ROOMINDEX)))
                                .getSquaresList()
                                .get(Math.toIntExact((Long) jsonObjectSouth.get(SQUAREINDEX))));
                    }

                    if (s.getEastConnection() != Connection.ENDMAP) {
                        s.setEast(this.roomsList
                                .get(Math.toIntExact((Long) jsonObjectEast.get(ROOMINDEX)))
                                .getSquaresList()
                                .get(Math.toIntExact((Long) jsonObjectEast.get(SQUAREINDEX))));
                    }

                    if (s.getWestConnection() != Connection.ENDMAP) {
                        s.setWest(this.roomsList
                                .get(Math.toIntExact((Long) jsonObjectWest.get(ROOMINDEX)))
                                .getSquaresList()
                                .get(Math.toIntExact((Long) jsonObjectWest.get(SQUAREINDEX))));
                    }
                }
            }

            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
