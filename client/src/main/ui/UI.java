package ui;

import com.google.gson.Gson;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import responses.CreateGameResult;
import responses.LoginResult;
import responses.RegisterResult;
import responses.ResponseMessage;
import server.ServerFascade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Scanner;

public class UI {

    public static void main(String[] args) {

        ServerFascade fascade = new ServerFascade();
        boolean loggedIn = false;
        String user = "";
        String auth = null;

        System.out.print("Do you want to play a game? Enter 'help' for options \n\n");

        while (true) {


            if (loggedIn) {
                System.out.print("[" + user + "] >>> ");
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
                            res = fascade.sendAndReceive("/user", "POST", new Gson().toJson(req), null, RegisterResult.class);
                        } catch (URISyntaxException | IOException e) {
                            System.out.print(e.getMessage() + "\n");
                        }

                        if (res.getAuthToken() != null) {
                            auth = res.getAuthToken();
                            user = res.getUsername();
                            loggedIn = true;
                            System.out.print("registration success :))\n");
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
                            res = fascade.sendAndReceive("/session", "POST", new Gson().toJson(req), null, LoginResult.class);
                        } catch (URISyntaxException | IOException e) {
                            System.out.print(e.getMessage() + "\n");
                        }

                        if (res.getAuthToken() != null) {
                            loggedIn = true;
                            user = res.getUsername();
                            auth = res.getAuthToken();
                            System.out.print("login success :))\n");
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
                            fascade.sendAndReceive("/game", "POST", new Gson().toJson(req), auth, CreateGameResult.class);
                            System.out.print("creation success!\n");
                        } catch (URISyntaxException | IOException e) {
                            System.out.print(e.getMessage() + "\n");
                        }
                    }

                }

                else if (Objects.equals(inputs[0], "list")) {

                }

                else if (Objects.equals(inputs[0], "join")) {

                    if (inputs.length != 3) {
                        System.out.print("invalid args :/\n");
                    }
                    else {
                        JoinGameRequest req = new JoinGameRequest(inputs[2], Integer.parseInt(inputs[1]));
                        // TODO: 11/22/2023 set auth as well
                    }
                }

                else if (Objects.equals(inputs[0], "observe")) {

                    if (inputs.length != 2) {
                        System.out.print("invalid args :/\n");
                    }
                    else {
                        JoinGameRequest req = new JoinGameRequest(null, Integer.parseInt(inputs[1]));
                        // TODO: 11/22/2023 set auth as well
                    }

                }

                else if (Objects.equals(inputs[0], "logout")) {

                    try {
                        fascade.sendAndReceive("/session", "DELETE", "", auth, ResponseMessage.class);
                        loggedIn = false;
                    } catch (URISyntaxException | IOException e) {
                        System.out.print(e.getMessage() + "\n");
                    }

                }

            }

            System.out.print("\n");

        }

    }
}
