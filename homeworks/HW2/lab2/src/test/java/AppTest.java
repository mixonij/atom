import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AppTest {

    @Test
    public void getWords() {
        List<String> words = App.getWords("../dictionary.txt");
        Assert.assertEquals(52976, words.size());
    }

    @Test
    public void computeCows() {
        Game game = new Game(null);
        game.SetSecretWord("aaaa");
        int result = game.computeBulls("aaaa");
        Assert.assertEquals(4, result);
    }
}
