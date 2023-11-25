package ui;

import chess.*;
import com.google.gson.Gson;
import models.Game;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.*;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class UI {

    public static void main(String[] args) {

        ServerFacade facade = new ServerFacade();
        boolean loggedIn = false;
        String user = "";
        String auth = null;
        ArrayList<Game> games = new ArrayList<>();
        HashMap<Integer, Integer> gameIDs = new HashMap<>();
        int curGameID;
        String role = "";


        System.out.print("Do you want to play a game? Enter 'help' for options \n\n");

        while (true) {


            if (loggedIn) {
                System.out.print("[" + user + role +"] >>> ");
            }
            else System.out.print("[LOGGED_OUT] >>> ");

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var inputs = line.split(" ");

            if (!loggedIn) {                                    //pre-login

                if (Objects.equals(inputs[0], "quit")) {
                    System.out.print("see ya :/\n");
                    break;
                }

                else if (Objects.equals(inputs[0], "help")) {
                    System.out.print("""
                        'register <USERNAME> <PASSWORD> <EMAIL>' to create an account\s
                        'login <USERNAME> <PASSWORD>' to login and play\s
                        'quit' to leave :'(\s
                        'help' for possible commands\s
                        """);
                }

                else if (Objects.equals(inputs[0], "register")) {

                    if (inputs.length != 4) {
                        System.out.print("invalid args :/\n");
                    }
                    else {
                        RegisterRequest req = new RegisterRequest(inputs[1], inputs[2], inputs[3]);
                        RegisterResult res = new RegisterResult();

                        try {
                            res = facade.sendAndReceive("/user", "POST", new Gson().toJson(req), null, RegisterResult.class);
                        } catch (URISyntaxException | IOException e) {
                            System.out.print(e.getMessage() + "\n");
                        }

                        if (res.getAuthToken() != null) {
                            auth = res.getAuthToken();
                            user = res.getUsername();
                            loggedIn = true;
                            System.out.print("registration success :))\nwelcome " + user + "\n");
                        }

                    }

                }

                else if (Objects.equals(inputs[0], "login")) {

                    if (inputs.length != 3) {
                        System.out.print("invalid args :/\n");
                    }
                    else {
                        LoginRequest req = new LoginRequest(inputs[1], inputs[2]);
                        LoginResult res = new LoginResult();

                        try {
                            res = facade.sendAndReceive("/session", "POST", new Gson().toJson(req), null, LoginResult.class);
                        } catch (URISyntaxException | IOException e) {
                            System.out.print(e.getMessage() + "\n");
                        }

                        if (res.getAuthToken() != null) {
                            loggedIn = true;
                            user = res.getUsername();
                            auth = res.getAuthToken();
                            System.out.print("welcome back " + user + " :))\n");
                        }

                    }

                }
            }

            else {                                              //post login

                if (Objects.equals(inputs[0], "help")) {
                    System.out.print("""
                        'create <NAME>' a game\s
                        'list' games\s
                        'join <ID> [WHITE|BLACK]' a game\s
                        'observe <ID>' a game\s
                        'logout' when you're done\s
                        'help' for possible commands\s
                        """);
                }

                else if (Objects.equals(inputs[0], "create")) {

                    if (inputs.length != 2) {
                        System.out.print("invalid args :/\n");
                    }
                    else {
                        CreateGameRequest req = new CreateGameRequest(inputs[1]);
                        try {
                            facade.sendAndReceive("/game", "POST", new Gson().toJson(req), auth, CreateGameResult.class);
                            System.out.print("creation success!\n");
                        } catch (URISyntaxException | IOException e) {
                            System.out.print(e.getMessage() + "\n");
                        }
                    }

                }

                else if (Objects.equals(inputs[0], "list")) {

                    try {
                        ListGamesResult res = facade.sendAndReceive("/game", "GET", "", auth, ListGamesResult.class);
                        resetClientGamesList(games, gameIDs, res);
                        printGames(games);
                    } catch (URISyntaxException | IOException e) {
                        System.out.print(e.getMessage() + "\n");
                    }

                }

                else if (Objects.equals(inputs[0], "join")) {

                    if (inputs.length != 3) {
                        System.out.print("invalid args :/\n");
                    }
                    else {
                        if (Integer.parseInt(inputs[1]) < games.size()) {
                            curGameID = gameIDs.get(Integer.parseInt(inputs[1]));
                            JoinGameRequest req = new JoinGameRequest(inputs[2],curGameID);

                            try {
                                facade.sendAndReceive("/game", "PUT", new Gson().toJson(req), auth, ResponseMessage.class);

                                switch (req.getPlayerColor()) {
                                    case "WHITE" -> role = ":WHITE";
                                    case "BLACK" -> role = ":BLACK";
                                    default -> role = ": OBSERVER";
                                }

                                dummyBoards();
                            } catch (URISyntaxException | IOException e) {
                                System.out.print(e.getMessage() + "\n");
                            }
                        }
                        else {
                            System.out.print("invalid game number\n");
                        }


                    }
                }

                else if (Objects.equals(inputs[0], "observe")) {

                    if (inputs.length != 2) {
                        System.out.print("invalid args :/\n");
                    }
                    else {
                        if (Integer.parseInt(inputs[1]) < games.size()) {
                            curGameID = gameIDs.get(Integer.parseInt(inputs[1]));
                            JoinGameRequest req = new JoinGameRequest(null, curGameID);

                            try {
                                facade.sendAndReceive("/game", "PUT", new Gson().toJson(req), auth, ResponseMessage.class);
                                role = ":OBSERVER";
                                System.out.print("observing...\n\n");
                                dummyBoards();
                            } catch (URISyntaxException | IOException e) {
                                System.out.print(e.getMessage() + "\n");
                            }
                        }
                        else {
                            System.out.print("invalid game number\n");
                        }

                    }

                }

                else if (Objects.equals(inputs[0], "logout")) {

                    try {
                        facade.sendAndReceive("/session", "DELETE", "", auth, ResponseMessage.class);
                        loggedIn = false;
                    } catch (URISyntaxException | IOException e) {
                        System.out.print(e.getMessage() + "\n");
                    }

                }

            }

            System.out.print("\n");

        }

    }

    private static void resetClientGamesList(ArrayList<Game> games, HashMap<Integer, Integer> gameIDs, ListGamesResult res) {
        games.clear();
        gameIDs.clear();
        int i = 0;
        for (Game g : res.getGames()) {
            games.add(g);
            gameIDs.put(i, g.getGameID());
            ++i;
        }
    }

    private static void printGames(ArrayList<Game> games) {
        for (int i = 0; i < games.size(); ++ i) {
            System.out.print(i + ": " + games.get(i).getGameName() + "\n\twhite: " + games.get(i).getWhiteUsername() +
                    ", black: " + games.get(i).getBlackUsername() + "\n");
        }
    }

    private static void dummyBoards() {
        ChessBoard board = new ChessBoardImp();
        board.resetBoard();
        printBoard(board, true);
        System.out.print("\n\n");
        printBoard(board, false);
    }

    private static void printBoard(ChessBoard board, boolean whitePOV) {
        if (whitePOV) {
            for (int row = 0; row < 10; ++row) {
                for (int col = 0; col < 10; ++col) {
                    printRow(board, row, col);
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR + "\n");
            }
        }
        else {
            for (int row = 9; row >= 0; --row) {
                for (int col = 0; col < 10; ++col) {
                    printRow(board, row, col);
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR + "\n");
            }
        }
    }


    private static void printRow(ChessBoard board, int row, int col) {
        if (row == 0 || row == 9) {
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);

            if (col == 0 || col == 9) System.out.print(EscapeSequences.EMPTY);
            else System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREY + " " + (char) (col - 1 + 'a') + "\u2003" + EscapeSequences.RESET_TEXT_COLOR);
        }
        else {
            if (col == 0 || col == 9) System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.SET_TEXT_COLOR_DARK_GREY + "\u2003" + row + " " + EscapeSequences.RESET_TEXT_COLOR);

            else {
                if ((row % 2 == 1 && col % 2 == 1) || (row % 2 == 0 && col % 2 == 0)) System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                else System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);

                ChessPosition pos = new ChessPositionImp(row, col);
                ChessPiece piece = board.getPiece(pos);

                if (piece == null) System.out.print(EscapeSequences.EMPTY);
                else {
                    switch (piece.getTeamColor()) {

                        case WHITE -> {
                            switch (piece.getPieceType()) {

                                case KING -> {
                                    System.out.print(EscapeSequences.WHITE_KING);
                                }
                                case QUEEN -> {
                                    System.out.print(EscapeSequences.WHITE_QUEEN);
                                }
                                case BISHOP -> {
                                    System.out.print(EscapeSequences.WHITE_BISHOP);
                                }
                                case KNIGHT -> {
                                    System.out.print(EscapeSequences.WHITE_KNIGHT);
                                }
                                case ROOK -> {
                                    System.out.print(EscapeSequences.WHITE_ROOK);
                                }
                                case PAWN -> {
                                    System.out.print(EscapeSequences.WHITE_PAWN);
                                }
                            }
                        }
                        case BLACK -> {
                            switch (piece.getPieceType()) {

                                case KING -> {
                                    System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_KING + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                }
                                case QUEEN -> {
                                    System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_QUEEN + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                }
                                case BISHOP -> {
                                    System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_BISHOP + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                }
                                case KNIGHT -> {
                                    System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_KNIGHT + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                }
                                case ROOK -> {
                                    System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_ROOK + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                }
                                case PAWN -> {
                                    System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_PAWN + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                }
                            }
                        }
                    }

                }


            }
        }
    }


}
