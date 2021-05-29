import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    ArrayList<String[]> cmd;

    public static ArrayList<String[]> parse(String command) {
        ArrayList<String[]> commands = new ArrayList<>();
        String[] pipeSplit = command.split("\\|");
        for (String cmds : pipeSplit) {
            String[] spaceSplit = cmds.strip().split(" ");
            ArrayList<String> mergedSpaceSplit = new ArrayList<>();
            mergedSpaceSplit.add(spaceSplit[0]);
            String fullArg = "";
            boolean flag = false;
            for (int i = 1; i < spaceSplit.length; i++) {
                if (spaceSplit[i].startsWith("\"")) {
                    flag = true;
                }
                if (spaceSplit[i].endsWith("\"") || i == spaceSplit.length - 1) {
                    flag = false;
                }
                fullArg += spaceSplit[i].strip() + " ";
                if (!flag) {
                    if (fullArg.strip().startsWith("\"")) fullArg = fullArg.strip().substring(1);
                    if (fullArg.strip().endsWith("\""))
                        fullArg = fullArg.strip().substring(0, fullArg.strip().length() - 1);
                    mergedSpaceSplit.add(fullArg.strip());
                    fullArg = "";
                }
            }
            if (mergedSpaceSplit.size() < 3) {
                for (int i = 0; i < 3 - mergedSpaceSplit.size(); i++) {
                    mergedSpaceSplit.add("");
                }
            }
            spaceSplit = mergedSpaceSplit.toArray(new String[mergedSpaceSplit.size()]);
            commands.add(spaceSplit);
        }
        return commands;
    }

    public ArrayList<String[]> getCmd() {
        return cmd;
    }
}