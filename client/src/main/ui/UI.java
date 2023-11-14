package ui;

import java.util.Objects;
import java.util.Scanner;

public class UI {

    public static void main(String[] args) {

        boolean loggedIn = false;

        System.out.print("Do you want to play a game? Enter 'help' for options \n\n");

        while (true) {


            if (loggedIn) {
                System.out.print("[LOGGED_IN] >>> ");
            }
            else System.out.print("[LOGGED_OUT] >>> ");

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var inputs = line.split(" ");

            if (Objects.equals(inputs[0], "quit")) break;

            if (Objects.equals(inputs[0], "help")) {
                System.out.print("""
                        'register <USERNAME> <PASSWORD> <EMAIL>' to create an account\s
                        'login <USERNAME> <PASSWORD>' to login and play\s
                        'quit' to leave :'(\s
                        'help' for possible commands\s
                        """);
            }


            System.out.print("\n");

        }

    }
}
