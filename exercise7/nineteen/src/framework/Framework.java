import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by Junxian Chen on 2020-05-14.
 */
public class Framework {

    private static final String ROOT_DIR = "/home/runner/swe262p-exercises/exercise7/nineteen/bin/";
    // private static final String ROOT_DIR = "./";
    // Relative path does not work. It throws ClassNotFoundException.
    private static final String PROP_PATH = ROOT_DIR + "config.properties";
    private static final Properties PROPERTIES = new Properties();

    private static ITermFreqApp app;

    private static void loadPlugins() throws Exception {
        PROPERTIES.load(new FileInputStream(PROP_PATH));
        var appClassName = PROPERTIES.getProperty("app");
        var appJarName = appClassName + ".jar";
        var appJarURL = new URL("file://"+ ROOT_DIR + appJarName);
        app = (ITermFreqApp) new URLClassLoader(new URL[]{appJarURL})
                .loadClass(appClassName)
                .getDeclaredConstructor()
                .newInstance();
    }

    private static void validateArguments(String[] args) {
        if (args.length != 1) {
            System.err.println("Please provide exactly ONE argument. Current: " + args.length);
            System.exit(1);
        }

        final Path path = Path.of(args[0]);
        if (!path.toFile().exists()) {
            System.err.println(path + " does not exist.");
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        validateArguments(args);
        loadPlugins();
        var wordFreqs = app.top25(app.extractWords(args[0]));
        wordFreqs.forEach(entry ->
                System.out.println(entry.getKey() + "  -  " + entry.getValue()));
    }
}
