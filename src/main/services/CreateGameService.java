package services;

import chess.ChessBoard;
import chess.ChessBoardImp;
import chess.ChessGame;
import chess.ChessGameImp;
import dataAccess.*;
import models.Game;
import requests.CreateGameRequest;
import responses.CreateGameResult;

/**
 * CreateGameService class
 */
public class CreateGameService {

    private final AuthDAO authDAO = new DbAuthDAO();
    private final GameDAO gameDAO = new DbGameDAO();

    /**
     * Creates a new game.
     * @param request object
     * @return error message or gameID through object
     */
    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        CreateGameResult result = new CreateGameResult();

        if (request.gameName() == null || authToken == null) {
            result.setReturnCode(400);
            result.setMessage("Error: bad request");
            return result;
        }

        try {
            authDAO.find(authToken);    //validate authentication, throws if dne

            Game game = new Game(request.gameName());
            ChessBoard board = new ChessBoardImp();
            board.resetBoard();
            ChessGame chessGame = new ChessGameImp();
            chessGame.setBoard(board);
            game.setGame(chessGame);
            gameDAO.insert(game);

            result.setGameID(game.getGameID());
            result.setReturnCode(200);
        }
        catch (DataAccessException e) {
            result.setReturnCode(401);
            result.setMessage("Error: unauthorized");
        }
        return result;
    }
}
