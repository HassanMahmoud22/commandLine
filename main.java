import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Terminal t = new Terminal();
        Parser p = new Parser();
        Scanner input = new Scanner(System.in);
        while (true) {
            t.pwd();
            System.out.print(">");
            ArrayList<String[]> parsed = p.parse(input.nextLine());
            if (parsed.size() > 0) {
                for (String[] cmd : parsed) {
                    runCMD(cmd, t);
                }
            }
        }
    }

    public static void runCMD(String[] cmd, Terminal terminal) {
        if (cmd[0].equals("mv")) {
            terminal.mv(cmd[1], cmd[2]);
        } else if (cmd[0].equals("cp")) {
            terminal.cp(cmd[1], cmd[2]);
        } else if (cmd[0].equals("rm")) {
            terminal.rm(cmd[1]);
        } else if (cmd[0].equals("cd")) {
            terminal.cd(cmd[1]);
        } else if (cmd[0].equals("ls")) {
            if (cmd[1].equals(">")) terminal.redirect(cmd[2], true);
            else if (cmd[1].equals(">>")) terminal.redirect(cmd[2], false);
            terminal.ls();
            terminal.returnOutput();
        } else if (cmd[0].equals("mkdir")) {
            terminal.mkdir(cmd[1]);
        } else if (cmd[0].equals("rmdir")) {
            terminal.rmdir(cmd[1]);
        } else if (cmd[0].equals("cat")) {
            int index = cmd.length - 2;
            if (!(cmd[index].equals(">") || cmd[index].equals(">>"))) index = cmd.length;
            else {
                if (cmd[index].equals(">")) terminal.redirect(cmd[index + 1], true);
                else if (cmd[index].equals(">>")) terminal.redirect(cmd[index + 1], false);
            }
            terminal.cat(Arrays.copyOfRange(cmd, 1, index));
            terminal.returnOutput();
        } else if (cmd[0].equals("more")) {
            terminal.more(cmd[1]);
        } else if (cmd[0].equals("pwd")) {
            if (cmd[1].equals(">")) terminal.redirect(cmd[2], true);
            else if (cmd[1].equals(">>")) terminal.redirect(cmd[2], false);
            terminal.pwd();
            System.out.println("");
            terminal.returnOutput();
        } else if (cmd[0].equals("date")) {
            if (cmd[1].equals(">")) terminal.redirect(cmd[2], true);
            else if (cmd[1].equals(">>")) terminal.redirect(cmd[2], false);
            terminal.date();
            terminal.returnOutput();
        } else if (cmd[0].equals("args")) {
            if (cmd[2].equals(">")) terminal.redirect(cmd[3], true);
            else if (cmd[2].equals(">>")) terminal.redirect(cmd[3], false);
            terminal.args(cmd[1]);
            terminal.returnOutput();
        } else if (cmd[0].equals("help")) {
            if (cmd[1].equals(">")) terminal.redirect(cmd[2], true);
            else if (cmd[1].equals(">>")) terminal.redirect(cmd[2], false);
            terminal.help();
            terminal.returnOutput();
        } else if (cmd[0].equals("clear")) {
            terminal.clear();
        } else if (cmd[0].equals("exit")) {
            terminal.exit();
        } else {
            System.out.println("Invalid command");
        }
    }
}