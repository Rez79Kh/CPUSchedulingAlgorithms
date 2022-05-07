import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        Management management = new Management();
        ArrayList<Process> inp = management.initialInputs("Input File Address");
        management.runFCFS(inp);
        management.runSJF(inp);
        management.runRR(inp);
    }
}
