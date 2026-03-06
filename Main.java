import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        FileWriter writer = new FileWriter("/data/output.txt");
        writer.write("Hello from inside a container!");
        writer.close();
        System.out.println("File written successfully!");
    }
}
