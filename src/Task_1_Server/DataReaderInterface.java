package Task_1_Server;

import com.sun.istack.internal.NotNull;

import java.util.List;

public interface DataReaderInterface {
    List<List<String>> readUsers(@NotNull String path, @NotNull int columns);
    List<List<String>> readData(@NotNull String path, @NotNull int columns);
}
