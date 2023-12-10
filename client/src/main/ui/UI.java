package ui;

public class UI  {
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
        new Repl().run();

    }


}
