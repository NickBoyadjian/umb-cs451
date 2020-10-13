import java.lang.Integer;
import java.lang.System;
import java.util.Random;

public class SwitchStatement {
    public static void main(String[] args) {
        Random rng = new Random();
        int rank = rng.nextInt(13) + 1;
        int suit = rng.nextInt(3) + 1;
        String rankStr = "";
        switch (rank) {
        case 1:
            rankStr = "Ace";
        case 11:
            rankStr = "Jack";
        default:
          rankStr = "yoo";
        }
    }
}
