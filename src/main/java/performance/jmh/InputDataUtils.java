package performance.jmh;

import org.apache.commons.io.FileUtils;

import java.io.*;

public class InputDataUtils {

    public static final String DATA_FOLDER = "./benchmark_data/";

    public static void cleanBenchDataFolder() throws IOException {
        File dir = new File(DATA_FOLDER);
        FileUtils.cleanDirectory(dir);
    }

    public static Object readObject(String filepath) {
        try(ObjectInputStream objectIS = new ObjectInputStream(new FileInputStream(filepath))) {
            return objectIS.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
