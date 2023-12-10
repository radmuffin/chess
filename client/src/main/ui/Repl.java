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
import server.NotificationHandler;
import server.ServerFacade;
import webSocketMessages.serverMessages.ErrorMess;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Repl implements NotificationHandler {
    private final ServerFacade facade = new ServerFacade(this);
    private static final ArrayList<Game> games = new ArrayList<>();
    private static final HashMap<Integer, Integer> gameIDs = new HashMap<>();        //<listed, actual>

    private  UI.State state = UI.State.LOGGED_OUT;
    private  AuthToken user = null;
    private final Game game = new Game("localCopy");

    public void run() {

        while (state != UI.State.QUIT) {

            outputState();

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var inputs = line.split(" ");

            switch (state) {
                case LOGGED_OUT -> loggedOutActions(inputs);
                case LOGGED_IN -> loggedInActions(inputs);
                case OBSERVING -> observingActions(inputs);
                case PLAYING_BLACK, PLAYING_WHITE -> gameplayActions(inputs);
            }

            System.out.print("\n");
        }
    }

    private void gameplayActions(String[] inputs) {
        switch (inputs[0]) {
            case "redraw" -> redrawBoard();
            case "highlight" -> highlight(inputs);
            case "move" -> move(inputs);
            case "resign" -> resignGame();
            case "leave" -> leaveGame();
            default -> System.out.print("""
                'help' displays this again\s
                'redraw' the board\s
                'highlight <PIECE LOCATION>' legal moves\s
                'move <START> <END>' a piece\s
                'resign' and surrender\s
                'leave' the game\s
                """);
        }
    }

    private void observingActions(String[] inputs) {
        switch (inputs[0]) {
            case "leave" -> leaveGame();
            case "redraw" -> redrawBoard();
            case "highlight" -> highlight(inputs);
            default -> System.out.print("""
                    'redraw' the board\s
                    'highlight <PIECE LOCATION>' legal moves\s
                    'leave' game when you want to go\s
                    'help' for this again\s
                    """);
        }
    }

    private  void loggedInActions(String[] inputs) {
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

    private  void loggedOutActions(String[] inputs) {
        switch (inputs[0]) {
            case "quit" -> quit();
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

    private  void quit() {
        state = UI.State.QUIT;
        System.out.print("see ya :/\n");
    }

    private void leaveGame() {
        LeaveCmd cmd = new LeaveCmd(user.getAuthToken(), game.getGameID());
        try {
            facade.sendWSCmd(cmd);
            facade.disconnectWS();
            state = UI.State.LOGGED_IN;
        } catch (IOException e) {
            System.out.print(e.getMessage() + "\n");
        }
    }

    private void resignGame() {
        ResignCmd cmd = new ResignCmd(user.getAuthToken(), game.getGameID());
        try {
            facade.sendWSCmd(cmd);
        } catch (IOException e) {
            System.out.print(e.getMessage() + "\n");
        }
    }

    private  void move(String[] inputs) {
        if (inputs.length < 3 || inputs[1].length() != 2 || inputs[2].length() != 2) System.out.print("where?\n");
        else {
            ChessPosition start = getChessPosition(inputs, 1);
            ChessPosition end = getChessPosition(inputs, 2);
            ChessPiece.PieceType promo = getPromo(inputs);
            ChessMove move = new ChessMoveImp(start, end, promo);
            MakeMoveCmd cmd = new MakeMoveCmd(user.getAuthToken(), game.getGameID(), move);
            try {
                facade.sendWSCmd(cmd);
            } catch (IOException e) {
                System.out.print(e.getMessage() + "\n");
            }
        }
    }

    private  void highlight(String[] inputs) {
        if (inputs.length < 2) System.out.print("which piece?\n");
        else {
            ChessPosition start = getChessPosition(inputs, 1);
            Collection<ChessMove> moves = game.getGame().validMoves(start);
            HashSet<ChessPosition> ops = new HashSet<>();
            for (ChessMove m : moves) {
                ops.add(m.getEndPosition());
            }
            printBoard(game.getGame().getBoard(), start, ops);
        }
    }

    private void redrawBoard() {
        printBoard(game.getGame().getBoard(), null, null);
    }

    private  void observeGame(String[] inputs) {
        if (inputs.length != 2) {
            System.out.print("invalid args :/\n");
        }
        else {
            if (Integer.parseInt(inputs[1]) < games.size()) {
                game.setGameID(gameIDs.get(Integer.parseInt(inputs[1])));
                JoinGameRequest req = new JoinGameRequest(null, game.getGameID());

                try {
                    facade.sendHTTP("/game", "PUT", new Gson().toJson(req), user.getAuthToken(), ResponseMessage.class);
                    facade.connectWS();
                    JoinObserverCmd cmd = new JoinObserverCmd(user.getAuthToken(), game.getGameID());
                    facade.sendWSCmd(cmd);

                    state = UI.State.OBSERVING;
                } catch (URISyntaxException | IOException | DeploymentException e) {
                    System.out.print(e.getMessage() + "\n");
                }
            }
            else {
                System.out.print("invalid game number\n");
            }

        }
    }

    private  void joinGame(String[] inputs) {
        if (inputs.length != 3) {
            System.out.print("invalid args :/\n");
        }
        else {
            if (Integer.parseInt(inputs[1]) < games.size()) {
                game.setGameID(gameIDs.get(Integer.parseInt(inputs[1])));
                if (Objects.equals(inputs[2], "black")) inputs[2] = "BLACK";
                else if (Objects.equals(inputs[2], "white")) inputs[2] = "WHITE";
                JoinGameRequest req = new JoinGameRequest(inputs[2], game.getGameID());

                try {
                    facade.sendHTTP("/game", "PUT", new Gson().toJson(req), user.getAuthToken(), ResponseMessage.class);

                    facade.connectWS();
                    ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
                    if (Objects.equals(req.getPlayerColor(), "BLACK")) color = ChessGame.TeamColor.BLACK;
                    JoinPlayerCmd cmd = new JoinPlayerCmd(user.getAuthToken(), game.getGameID(), color);
                    facade.sendWSCmd(cmd);

                    switch (req.getPlayerColor()) {
                        case "WHITE" -> state = UI.State.PLAYING_WHITE;
                        case "BLACK" -> state = UI.State.PLAYING_BLACK;
                    }

                } catch (URISyntaxException | IOException | DeploymentException e) {
                    System.out.print(e.getMessage() + "\n");
                }
            }
            else {
                System.out.print("invalid game number\n");
            }
        }
    }

    private  void listGames() {
        try {
            ListGamesResult res = facade.sendHTTP("/game", "GET", "", user.getAuthToken(), ListGamesResult.class);
            resetClientGamesList(res);
            printGames();
        } catch (URISyntaxException | IOException e) {
            System.out.print(e.getMessage() + "\n");
        }
    }

    private  void createGame(String[] inputs) {
        if (inputs.length != 2) {
            System.out.print("invalid args :/\n");
        }
        else {
            CreateGameRequest req = new CreateGameRequest(inputs[1]);
            try {
                facade.sendHTTP("/game", "POST", new Gson().toJson(req), user.getAuthToken(), CreateGameResult.class);
                System.out.print("creation success!\n");
            } catch (URISyntaxException | IOException e) {
                System.out.print(e.getMessage() + "\n");
            }
        }
    }

    private  void logout() {
        try {
            facade.sendHTTP("/session", "DELETE", "", user.getAuthToken(), ResponseMessage.class);
            state = UI.State.LOGGED_OUT;
        } catch (URISyntaxException | IOException e) {
            System.out.print(e.getMessage() + "\n");
        }
    }

    private  void login(String[] inputs) {
        if (inputs.length != 3) {
            System.out.print("invalid args :/\n");
        }
        else {
            LoginRequest req = new LoginRequest(inputs[1], inputs[2]);
            LoginResult res = new LoginResult();

            try {
                res = facade.sendHTTP("/session", "POST", new Gson().toJson(req), null, LoginResult.class);
            } catch (URISyntaxException | IOException e) {
                System.out.print(e.getMessage() + "\n");
            }

            if (res.getAuthToken() != null) {
                state = UI.State.LOGGED_IN;
                user = new AuthToken(res.getUsername(), res.getAuthToken());
                System.out.print("welcome back " + user.getUsername() + " :))\n");
            }

        }
    }

    private  void register(String[] inputs) {
        if (inputs.length != 4) {
            System.out.print("invalid args :/\n");
        } else {
            RegisterRequest req = new RegisterRequest(inputs[1], inputs[2], inputs[3]);
            RegisterResult res = new RegisterResult();

            try {
                res = facade.sendHTTP("/user", "POST", new Gson().toJson(req), null, RegisterResult.class);
            } catch (URISyntaxException | IOException e) {
                System.out.print(e.getMessage() + "\n");
            }

            if (res.getAuthToken() != null) {
                user = new AuthToken(res.getUsername(), res.getAuthToken());
                state = UI.State.LOGGED_IN;
                System.out.print("registration success :))\nwelcome " + user.getUsername() + "\n");
            }

        }
    }

    private static ChessPosition getChessPosition(String[] inputs, int index) {
        int col = inputs[index].charAt(0) - 'a' + 1;
        int row = Character.getNumericValue(inputs[index].charAt(1));
        return new ChessPositionImp(row, col);
    }

    private static ChessPiece.PieceType getPromo(String[] inputs) {
        ChessPiece.PieceType promo = null;
        if (inputs.length == 4) {
            switch (inputs[3]) {
                case "queen" -> promo = ChessPiece.PieceType.QUEEN;
                case "rook" -> promo = ChessPiece.PieceType.ROOK;
                case "bishop" -> promo = ChessPiece.PieceType.BISHOP;
                case "knight" -> promo = ChessPiece.PieceType.KNIGHT;
            }
        }
        return promo;
    }

    private String getWhiteStatus() {
        String status = "";
        if (game.getGame() != null) switch (game.getGame().getGameState()) {
            case WHITE_TURN, NEW_GAME -> status = "|your move";
            case BLACK_TURN -> status = "|waiting on black";
            case WHITE_CHECKED -> status = "|you're in check";
            case BLACK_CHECKED -> status = "|nice move, maybe";
            case STALEMATE -> status = "|stalemate";
            case WHITE_WON -> status = "|victorious";
            case BLACK_WON -> status = "|rip";
        }
        return status;
    }

    private String getBlackStatus() {
        String status = "";
        if (game.getGame() != null) switch (game.getGame().getGameState()) {
            case WHITE_TURN, NEW_GAME -> status = "|waiting on white";
            case BLACK_TURN -> status = "|your move";
            case WHITE_CHECKED -> status = "|nice move, maybe";
            case BLACK_CHECKED -> status = "|you're in check";
            case STALEMATE -> status = "|stalemate";
            case WHITE_WON -> status = "|rip";
            case BLACK_WON -> status = "|victorious";
        }
        return status;
    }

    private String getObserverStatus() {
        String status = "";
        if (game.getGame() != null) switch (game.getGame().getGameState()) {
            case WHITE_TURN, NEW_GAME -> status = "|waiting on white";
            case BLACK_TURN -> status = "|waiting on black";
            case WHITE_CHECKED -> status = "|white in check";
            case BLACK_CHECKED -> status = "black in check";
            case STALEMATE -> status = "|stalemate";
            case WHITE_WON -> status = "|white wins";
            case BLACK_WON -> status = "|black wins";
        }
        return status;
    }

    private void resetClientGamesList(ListGamesResult res) {
        games.clear();
        gameIDs.clear();
        int i = 0;
        for (Game g : res.getGames()) {
            games.add(g);
            gameIDs.put(i, g.getGameID());
            ++i;
        }
    }
    private  void outputState() {
        switch (state) {
            case LOGGED_OUT -> System.out.print("[LOGGED_OUT] >>> ");
            case LOGGED_IN -> System.out.print("[" + user.getUsername() + "] >>> ");
            case PLAYING_WHITE -> System.out.print("[" + user.getUsername() + ":WHITE" + getWhiteStatus() + "] >>> ");
            case PLAYING_BLACK -> System.out.print("[" + user.getUsername() + ":BLACK" + getBlackStatus() + "] >>> ");
            case OBSERVING -> System.out.print("[" + user.getUsername() + ":OBSERVING" + getObserverStatus() + "] >>> ");
        }
    }

    private void printGames() {
        for (int i = 0; i < games.size(); ++ i) {
            String state = "";
            switch (games.get(i).getGame().getGameState()) {
                case NEW_GAME -> state = "NEW GAME";
                case BLACK_TURN -> state = "BLACK TURN";
                case WHITE_TURN -> state = "WHITE TURN";
                case WHITE_CHECKED -> state = "WHITE IN CHECK";
                case BLACK_CHECKED -> state = "BLACK IN CHECK";
                case STALEMATE -> state = "STALEMATE";
                case WHITE_WON -> state = "WHITE WON";
                case BLACK_WON -> state = "BLACK WON";
            }
            System.out.print(i + ": " + games.get(i).getGameName() + " [" + state +"]\n\twhite: " + games.get(i).getWhiteUsername() +
                    ", black: " + games.get(i).getBlackUsername() + "\n");
        }
    }

     void printBoard(ChessBoard board, ChessPosition start, Collection<ChessPosition> options) {
        boolean white = state != UI.State.PLAYING_BLACK;
         if (!white) {
            for (int row = 0; row < 10; ++row) {
                for (int col = 0; col < 10; ++col) {
                    printSquare(board, row, col, start, options);
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR + "\n");
            }
        }
        else {
            for (int row = 9; row >= 0; --row) {
                for (int col = 0; col < 10; ++col) {
                    printSquare(board, row, col, start, options);
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR + "\n");
            }
        }
    }

    private void printSquare(ChessBoard board, int row, int col, ChessPosition start, Collection<ChessPosition> options) {
        if (row == 0 || row == 9) {
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN);

            if (col == 0 || col == 9) System.out.print(EscapeSequences.EMPTY);
            else System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREY + " " + (char) (col - 1 + 'a') + "\u2003" + EscapeSequences.RESET_TEXT_COLOR);
        }
        else {
            if (col == 0 || col == 9) System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.SET_TEXT_COLOR_DARK_GREY + "\u2003" + row + " " + EscapeSequences.RESET_TEXT_COLOR);

            else {
                mightBeAPieceHere(board, row, col, start, options);
            }
        }
    }

    private static void mightBeAPieceHere(ChessBoard board, int row, int col, ChessPosition start, Collection<ChessPosition> options) {
        ChessPosition pos = new ChessPositionImp(row, col);
        ChessPiece piece = board.getPiece(pos);

        boolean whiteSquare = (row % 2 == 1 && col % 2 == 1) || (row % 2 == 0 && col % 2 == 0);
        if (start != null) {
            highlightSequence(board, start, options, pos, piece, whiteSquare);
        }
        else if (whiteSquare) System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        else System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);

        if (piece == null) System.out.print(EscapeSequences.EMPTY);
        else {
            switch (piece.getTeamColor()) {
                case BLACK -> printBlackPiece(piece);
                case WHITE -> printWhitePiece(piece);
            }
        }
    }

    private static void highlightSequence(ChessBoard board, ChessPosition start, Collection<ChessPosition> options, ChessPosition pos, ChessPiece piece, boolean whiteSquare) {
        if (pos.equals(start)) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE);
        }
        else if (options.contains(pos)) {
            if (board.getPiece(start) != null && piece != null) System.out.print(EscapeSequences.SET_BG_COLOR_RED);
            else System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
        }
        else if (whiteSquare) System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        else System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
    }

    private static void printWhitePiece(ChessPiece piece) {
        switch (piece.getPieceType()) { //black pieces are just filled and look more white, so I used black for white and vice versa
            case KING -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_KING + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            case QUEEN -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_QUEEN + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            case BISHOP -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_BISHOP + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            case KNIGHT -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_KNIGHT + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            case ROOK -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_ROOK + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            case PAWN -> System.out.print(EscapeSequences.SET_TEXT_FAINT + EscapeSequences.BLACK_PAWN + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        }
    }

    private static void printBlackPiece(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case KING -> System.out.print(EscapeSequences.WHITE_KING);
            case QUEEN -> System.out.print(EscapeSequences.WHITE_QUEEN);
            case BISHOP -> System.out.print(EscapeSequences.WHITE_BISHOP);
            case KNIGHT -> System.out.print(EscapeSequences.WHITE_KNIGHT);
            case ROOK -> System.out.print(EscapeSequences.WHITE_ROOK);
            case PAWN -> System.out.print(EscapeSequences.WHITE_PAWN);
        }
    }

    @Override
    public void receiveServerMessage(String message) {
        ServerMessage message1 = new Gson().fromJson(message, ServerMessage.class);
        switch (message1.getServerMessageType()) {
            case LOAD_GAME -> receiveLoadGame(message);
            case ERROR -> receiveErr(message);
            case NOTIFICATION -> receiveNotification(message);
        }
    }

    private void receiveNotification(String message) {
        Notification notification = new Gson().fromJson(message, Notification.class);
        System.out.print(notification.getMessage() + "\n");
        outputState();
    }

    private void receiveErr(String message) {
        ErrorMess errorMess = new Gson().fromJson(message, ErrorMess.class);
        System.out.print(errorMess.getErrorMessage() + "\n");
        outputState();
    }

    private void receiveLoadGame(String message) {
        LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
        this.game.setGame(loadGameMessage.getGame());
        System.out.print("\n");
        printBoard(game.getGame().getBoard(), null, null);
        System.out.print("\n");
        outputState();
    }
}
