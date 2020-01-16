package Task_1_Server;

import com.sun.istack.internal.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVReader implements DataReaderInterface{
    private List<List<String>> userInfo;
    private List<List<String>> userData;

    public CSVReader() {
        userInfo = new LinkedList<>();
        userData = new LinkedList<>();
    }

    public List<List<String>> getUserInfo() {
        return userInfo;
    }

    public List<List<String>> getUserData() {
        return userData;
    }

    public static void main(String[] args) {

        CSVReader csvReader = new CSVReader();
       // try {
        csvReader.readUsers("src\\Users.csv", 2);
        csvReader.readData("src\\Data.csv", 3);
        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }

    public List<List<String>> readData(@NotNull String path, @NotNull int columns) {
        try {
            userData = readFromFile(path, columns);
            return userData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<List<String>> readUsers(@NotNull String path, @NotNull int columns) {
        try {
            userInfo = readFromFile(path, columns);
            return userInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<List<String>> readFromFile (@NotNull String path, @NotNull int columns) throws IOException {

        String line;
        List<List<String>> listOfData = new LinkedList<>();
        //Map<String, String> userInfoLocal = new HashMap<String, String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            line = reader.readLine();  // пропускаем первую строку с заголовком
            while ((line = reader.readLine()) != null) {
                if (line.split(";").length != columns) {
                    listOfData = null;
                    throw new IOException("Incorrect data in Users.txt file");
                } else {
                    listOfData.add(Arrays.asList(line.split(";")));
                }
            }
        }
        return listOfData;
    }
}
