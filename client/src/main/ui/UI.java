package ui;

import chess.*;
import com.google.gson.Gson;
import models.AuthToken;
import models.Game;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.*;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class UI {
    private static final ServerFacade facade = new ServerFacade();
    private static final ArrayList<Game> games = new ArrayList<>();
    private static final HashMap<Integer, Integer> gameIDs = new HashMap<>();        //<listed, actual>

    private static State state = State.LOGGED_OUT;
    private static AuthToken user = null;
    private static Game game = new Game("");

    public enum State {
        LOGGED_OUT,
        LOGGED_IN,
        PLAYING_WHITE,
        PLAYING_BLACK,
        OBSERVING,
        QUIT
    }
    public static void main(String[] args) {

        System.out.print("Do you want to play a game? Enter 'help' for options \n\n");

        run();

    }

    private static void run() {

        while (state != State.QUIT) {


            outputState();

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var inputs = line.split(" ");

            switch (state) {
                case LOGGED_OUT -> {

                    switch (inputs[0]) {
                        case "quit" -> {
                            state = State.QUIT;
                            System.out.print("see ya :/\n");
                        }
                        case "register" -> register(inputs);
                        case "login" -> login(inputs);
                        default -> System.out.print("""
                            'register <USERNAME> <PASSWORD> <EMAIL>' to create an account\s
                            'login <USERNAME> <PASSWORD>' to login and play\s
                            'quit' to leave :'(\s
                            'help' for possible commands\s
                            """);
                    }
                }
                case LOGGED_IN, OBSERVING -> {
                    switch (inputs[0]) {

                        case "create" -> createGame(inputs);
                        case "list" -> listGames();
                        case "join" -> joinGame(inputs);
                        case "observe" -> observeGame(inputs);
                        case "logout" -> logout();
                        default -> System.out.print("""
                            'create <NAME>' a game\s
                            'list' games\s
                            'join <ID> [WHITE|BLACK]' a game\s
                            'observe <ID>' a game\s
                            'logout' when you're done\s
                            'help' for possible commands\s
                            """);
                    }
                }
                case PLAYING_BLACK, PLAYING_WHITE -> {
                    switch (inputs[0]) {
                        case "redraw" -> {

                        }
                        case "highlight" -> highlight(inputs);
                        case "move" -> move(inputs);
                        case "resign" -> {

                        }
                        case "leave" -> {

                        }
                        default -> {
                            System.out.print("""
                                'help' displays this again\s
                                'redraw' the board\s
                                'highlight <PIECE LOCATION>' legal moves\s
                                'move <START> <END>' a piece\s
                                'resign' and surrender\s
                                'leave' the game\s
                                """);
                        }
                    }
                }
            }

            System.out.print("\n");

        }

    }

    private static void move(String[] inputs) {
        if (inputs.length < 3 || inputs[1].length() != 2 || inputs[2].length() != 2) System.out.print("where?\n");
        else {
            int startCol = inputs[1].charAt(0) - 'a' + 1;
            int startRow = Character.getNumericValue(inputs[1].charAt(1));
            ChessPosition start = new ChessPositionImp(startRow, startCol);
            int endCol = inputs[2].charAt(0) - 'a' + 1;
            int endRow = Character.getNumericValue(inputs[2].charAt(1));
            ChessPosition end = new ChessPositionImp(endRow, endCol);
            ChessMove move = new ChessMoveImp(start, end, null);    // TODO: 11/28/2023 add promotion piece functionality

            // TODO: 11/28/2023 make move and send new game back
        }
    }

    private static void highlight(String[] inputs) {
        if (inputs.length < 2) System.out.print("which piece?\n");
        else {
            int startCol = inputs[1].charAt(0) - 'a' + 1;
            int startRow = Character.getNumericValue(inputs[1].charAt(1));
            ChessPosition start = new ChessPositionImp(startRow, startCol);
            Collection<ChessMove> spots = game.getGame().validMoves(start);
            // TODO: 11/28/2023 print board with those highlit
        }
    }

    private static void logout() {
        try {
            facade.sendAndReceive("/session", "DELETE", "", user.getAuthToken(), ResponseMessage.class);
            state = State.LOGGED_OUT;
        } catch (URISyntaxException | IOException e) {
            System.out.print(e.getMessage() + "\n");
        }
    }

    private static void observeGame(String[] inputs) {
        if (inputs.length != 2) {
            System.out.print("invalid args :/\n");
        }
        else {
            if (Integer.parseInt(inputs[1]) < games.size()) {
                game.setGameID(gameIDs.get(Integer.parseInt(inputs[1])));
                JoinGameRequest req = new JoinGameRequest(null, game.getGameID());

                try {
                    facade.sendAndReceive("/game", "PUT", new Gson().toJson(req), user.getAuthToken(), ResponseMessage.class);
                    state = State.OBSERVING;
                    System.out.print("observing...\n\n");

                    // TODO: 11/28/2023 add websocket stuff

                } catch (URISyntaxException | IOException e) {
                    System.out.print(e.getMessage() + "\n");
                }
            }
            else {
                System.out.print("invalid game number\n");
            }

        }
    }

    private static void joinGame(String[] inputs) {
        if (inputs.length != 3) {
            System.out.print("invalid args :/\n");
        }
        else {
            if (Integer.parseInt(inputs[1]) < games.size()) {
                game.setGameID(gameIDs.get(Integer.parseInt(inputs[1])));
                JoinGameRequest req = new JoinGameRequest(inputs[2], game.getGameID());

                try {
                    facade.sendAndReceive("/game", "PUT", new Gson().toJson(req), user.getAuthToken(), ResponseMessage.class);

                    switch (req.getPlayerColor()) {
                        case "WHITE" -> state = State.PLAYING_WHITE;
                        case "BLACK" -> state = State.PLAYING_BLACK;
                    }


                    // TODO: 11/28/2023 add websocket fun


                } catch (URISyntaxException | IOException e) {
                    System.out.print(e.getMessage() + "\n");
                }
            }
            else {
                System.out.print("invalid game number\n");
            }


        }
    }

    private static void listGames() {
        try {
            ListGamesResult res = facade.sendAndReceive("/game", "GET", "", user.getAuthToken(), ListGamesResult.class);
            resetClientGamesList(res);
            printGames();
        } catch (URISyntaxException | IOException e) {
            System.out.print(e.getMessage() + "\n");
        }
    }

    private static void createGame(String[] inputs) {
        if (inputs.length != 2) {
            System.out.print("invalid args :/\n");
        }
        else {
            CreateGameRequest req = new CreateGameRequest(inputs[1]);
            try {
                facade.sendAndReceive("/game", "POST", new Gson().toJson(req), user.getAuthToken(), CreateGameResult.class);
                System.out.print("creation success!\n");
            } catch (URISyntaxException | IOException e) {
                System.out.print(e.getMessage() + "\n");
            }
        }
    }

    private static void login(String[] inputs) {
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
                state = State.LOGGED_IN;
                user = new AuthToken(res.getUsername(), res.getAuthToken());
                System.out.print("welcome back " + user.getUsername() + " :))\n");
            }

        }
    }

    private static void register(String[] inputs) {
        if (inputs.length != 4) {
            System.out.print("invalid args :/\n");
        } else {
            RegisterRequest req = new RegisterRequest(inputs[1], inputs[2], inputs[3]);
            RegisterResult res = new RegisterResult();

            try {
                res = facade.sendAndReceive("/user", "POST", new Gson().toJson(req), null, RegisterResult.class);
            } catch (URISyntaxException | IOException e) {
                System.out.print(e.getMessage() + "\n");
            }

            if (res.getAuthToken() != null) {
                user = new AuthToken(res.getUsername(), res.getAuthToken());
                state = State.LOGGED_IN;
                System.out.print("registration success :))\nwelcome " + user.getUsername() + "\n");
            }

        }
    }

    private static void outputState() {
        switch (state) {
            case LOGGED_OUT -> System.out.print("[LOGGED_OUT] >>> ");
            case LOGGED_IN -> System.out.print("[" + user.getUsername() + "] >>> ");
            case PLAYING_WHITE -> System.out.print("[" + user.getUsername()  + ":WHITE] >>> "); // TODO: 11/30/2023 add turn
            case PLAYING_BLACK -> System.out.print("[" + user.getUsername() +  ":BLACK] >>> ");
            case OBSERVING -> System.out.print("[" + user.getUsername() + ":OBSERVING] >>> ");
        }
    }

    private static void resetClientGamesList(ListGamesResult res) {
        UI.games.clear();
        UI.gameIDs.clear();
        int i = 0;
        for (Game g : res.getGames()) {
            UI.games.add(g);
            UI.gameIDs.put(i, g.getGameID());
            ++i;
        }
    }

    private static void printGames() {
        for (int i = 0; i < UI.games.size(); ++ i) {
            System.out.print(i + ": " + UI.games.get(i).getGameName() + "\n\twhite: " + UI.games.get(i).getWhiteUsername() +
                    ", black: " + UI.games.get(i).getBlackUsername() + "\n");
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

                                case KING -> System.out.print(EscapeSequences.WHITE_KING);
                                case QUEEN -> System.out.print(EscapeSequences.WHITE_QUEEN);
                                case BISHOP -> System.out.print(EscapeSequences.WHITE_BISHOP);
                                case KNIGHT -> System.out.print(EscapeSequences.WHITE_KNIGHT);
                                case ROOK -> System.out.print(EscapeSequences.WHITE_ROOK);
                                case PAWN -> System.out.print(EscapeSequences.WHITE_PAWN);
                            }
                        }
                        case BLACK -> {
                            switch (piece.getPieceType()) {

                                case KING -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_KING + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                case QUEEN -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_QUEEN + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                case BISHOP -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_BISHOP + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                case KNIGHT -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_KNIGHT + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                case ROOK -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_ROOK + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                                case PAWN -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_PAWN + EscapeSequences.RESET_TEXT_BOLD_FAINT);
                            }
                        }
                    }
                }
            }
        }
    }


}
