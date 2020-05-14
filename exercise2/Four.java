import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Junxian Chen on 2020-04-07.
 *
 * @see https://github.com/crista/exercises-in-programming-style/tree/master/05-cookbook
 */
public class Four {

    private static Path path;

    private static void processArguments(String[] args) {
        // process arguments
        if (args.length != 1) {
            System.err.println("Please provide exactly ONE argument. Current: " + args.length);
            System.exit(1);
        }

        path = Path.of(args[0]);
        if (!path.toFile().exists()) {
            System.err.println(path + " does not exist.");
            System.exit(1);
        }
    }

    private static final Set<String> stopWords = new HashSet<>();

    private static void loadStopWords() {
        // load stop words
        final String PATH_STOP_WORDS = "../stop_words.txt";
        try {
            final byte[] bytes = Files.readAllBytes(Path.of(PATH_STOP_WORDS));
            final String[] words = new String(bytes).split(",");
            stopWords.addAll(Arrays.asList((words)));
        } catch (IOException e) {
            System.err.println("Error reading stop_words.txt");
            System.exit(1);
        }
    }

    private final static HashMap<String, Integer> freqMap = new HashMap<>();

    private static void countFrequencies() {
        // start counting
        try {
            try (Stream<String> lines = Files.lines(path)) {
                lines.forEach(line -> {
                    // Be careful. Some words are wrapped with underscores.
                    // Examples: _very_ | _as good_ | _Mr. Darcy_
                    // \w (word character) = [a-zA-Z0-9_]
                    // \W (non-word-character) = [^a-zA-Z0-9_]
                    String[] words = line.split("[^a-zA-Z]+");
                    for (String word : words) {
                        String w = word.toLowerCase();
                        if (!stopWords.contains(w) && w.length() > 1) {
                            if (freqMap.containsKey(w)) {
                                freqMap.put(w, freqMap.get(w) + 1);
                            } else {
                                freqMap.put(w, 1);
                            }
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static List<Map.Entry<String, Integer>> descendingList;

    private static void sortResults() {
        // sort results
        descendingList = new ArrayList<>(freqMap.entrySet());
        descendingList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    }

    private static void printResults() {
        // print first 25 words
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < 25; ++i) {
            final Map.Entry<String, Integer> entry = descendingList.get(i);
            result.append(entry.getKey()).append("  -  ").append(entry.getValue()).append("\n");
        }
        System.out.println(result);
    }

    public static void main(String[] args) {
        processArguments(args);
        loadStopWords();
        countFrequencies();
        sortResults();
        printResults();
    }
}