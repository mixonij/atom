import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {

    private static final Random random = new Random();
    private List<String> words;
    public String word;

    public Game(List<String> words) {
        this.words = words;
    }

    private String chooseSecretWord() {
        return words.get(random.nextInt(words.size()));
    }

    public void setSecretWord(String word) {
        this.word = word;
    }

    /**
     * Вычисляем количество быков
     * */
    public int computeBulls(String guess) {
        int bulls = 0;
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == word.charAt(i)) {
                bulls++;
            }
        }
        return bulls;
    }

    /**
     * Вычисляем количество коров
     * */
    private int computeCows(String guess) {
        int cows = 0;
        for (int i = 0; i < guess.length(); i++) {
            int pos = word.indexOf(guess.charAt(i));
            if (pos != -1) {
                cows++;
            }
        }
        return cows;
    }

    /**
     * Старт
     */
    public void start() {
        System.out.println("Hello!");
        String playAgain = "Yes";
        Scanner scanner = new Scanner(System.in);
        do {
            word = chooseSecretWord();
            System.out.println("I offered a " + word.length() + "-letter word, your guess?");
            playRound(scanner);
            System.out.println("Wanna play again? Yes/No");
            playAgain = scanner.next();
        } while (playAgain.equalsIgnoreCase("Yes"));
    }

    /**
     * Играть
     */
    private void playRound(Scanner scanner) {
        int losses = 0;
        while (losses < 15) {
            String guess = scanner.next();
            if (guess.length() != word.length()) {
                System.out.println("Wrong length guess. Try again.");
                continue;
            }
            if (guess.equals(word)) {
                System.out.println("You won!");
                return;
            }
            int bulls = computeBulls(guess);
            int cows = computeCows(guess) - bulls;
            System.out.println("Bulls: " + bulls);
            System.out.println("Cows: " + cows);
            losses++;
        }
        System.out.println("You lose :(");
    }


}
