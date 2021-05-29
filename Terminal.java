import java.io.*;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

public class Terminal {
    boolean flag = true;
    File current_directory = new File(System.getProperty("user.dir"));
    PrintStream output = new PrintStream(System.out);
    FileOutputStream outFile = null;
    boolean redirected = false;

    public String getFullPath(String relativePath) {
        String fullPath = current_directory.getAbsolutePath();
        File testFile = new File(fullPath, relativePath);
        if (testFile.isAbsolute()) fullPath = testFile.getAbsolutePath();
        else {
            testFile = new File(relativePath);
            if (testFile.isAbsolute()) fullPath = testFile.getAbsolutePath();
        }
        return fullPath;
    }

    public void redirect(String target, boolean overwrite) {
        File targetFile = new File(getFullPath(target));
        createFile(targetFile.getAbsolutePath());
        if (!targetFile.equals(null)) {
            try {
                outFile = new FileOutputStream(targetFile, !overwrite);
                output = new PrintStream(outFile);
                redirected = true;
            } catch (FileNotFoundException e) {
                output.println("Could not redirect output to file");
            }
        }
    }

    public void returnOutput() {
        output = new PrintStream(System.out);
        if (redirected) {
            try {
                outFile.close();
                redirected = false;
            } catch (IOException e) {
            }
        }
    }

    public File createFile(String fileName) {
        File newFile = new File(fileName);
        try {
            if (!newFile.getParentFile().equals(null) && !newFile.getParentFile().isDirectory()) {
                newFile.getParentFile().mkdirs();
            }
            newFile.createNewFile();
        } catch (IOException e) {
        }
        return newFile;
    }

    public void cd(String newDirectory) {
        if (!newDirectory.equals("")) {
            File newDir = new File(current_directory.getAbsolutePath(), newDirectory);
            if (!newDir.exists()) newDir = new File(newDirectory);
            if (!newDir.exists()) output.println(newDirectory + " doesn't exist");
            else {
                if (newDir.isDirectory()) current_directory = newDir;
                else output.println(newDirectory + " is not a directory");
            }
        } else args("cd");
    }

    public void ls() {
        File[] directory_files = current_directory.listFiles();
        for (File file : directory_files) {
            output.println(file.getName());
        }
    }

    public void mv(String sourcePath, String destinationPath) {
        if (!(sourcePath.equals("") || destinationPath.equals(""))) {
            File source = new File(current_directory.getAbsolutePath(), sourcePath);
            if (!source.exists()) source = new File(sourcePath);
            if (!source.exists()) output.println(sourcePath + " does not exist");
            else {
                File dest = new File(current_directory.getAbsolutePath(), destinationPath);
                if (!(dest.exists() && dest.isDirectory())) dest = new File(destinationPath);
                if (!(dest.exists() && dest.isDirectory())) output.println(destinationPath + " does not exist");
                else {
                    if (source.isDirectory()) {
                        mv_dir(source, dest);
                        output.println("Directory moved successfully.");
                    } else {
                        cp(source.getAbsolutePath(), dest.getAbsolutePath());
                        source.delete();
                        output.println("File moved successfully.");
                    }
                }
            }
        } else args("mv");
    }

    public void mv_dir(File directory, File destination) {
        String newDir = directory.getName();
        File newDirFile = new File(destination.getAbsolutePath(), newDir);
        mkdir(newDirFile.getAbsolutePath());
        for (File subFile : directory.listFiles()) {
            if (subFile.isDirectory()) {
                mv_dir(subFile, newDirFile);
            } else {
                cp(subFile.getAbsolutePath(), newDirFile.getAbsolutePath());
                rm(subFile.getAbsolutePath());
            }
        }
        directory.delete();
    }

    public void cp(String fileName, String destination) {
        if (!(fileName.equals("") || destination.equals(""))) {
            File source = new File(current_directory.getAbsolutePath(), fileName);
            if (!source.exists()) source = new File(fileName);
            if (!source.exists()) output.println(fileName + " does not exist");
            else {
                if (source.isFile()) {
                    File dest = new File(current_directory.getAbsolutePath(), destination);
                    if (!dest.exists()) dest = new File(destination);
                    if (!dest.exists()) output.println(destination + " does not exist");
                    else {
                        if (dest.isDirectory()) {
                            dest = new File(dest.getAbsolutePath(), source.getName());
                            writeToFile(source.getAbsolutePath(), dest.getAbsolutePath());
                        } else output.println(destination + " is not a directory");
                    }
                } else output.println(fileName + " is not a file");
            }
        } else args("cp");
    }

    public String readFile(String fileDirectory) {
        String data = "";
        try {
            File file = new File(fileDirectory);
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                data += myReader.nextLine() + "\n";
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            output.println("Could not find specified file");
        }
        int lastNewLine = data.lastIndexOf("\n");
        if (lastNewLine == -1) return data;
        else return data.substring(0, lastNewLine);
    }

    public void writeToFile(String sourceFileDirectory, String destinationFileDirectory) {
        File file = new File(destinationFileDirectory);
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(readFile(sourceFileDirectory));
            myWriter.close();
            output.println("Successfully wrote to the file.");
        } catch (IOException e) {
            output.println("Could not write to file.");
        }
    }

    public void mkdir(String dir) {
        if (!dir.equals("")) {
            File newDir = new File(current_directory.getAbsolutePath(), dir);
            if (!newDir.mkdirs()) {
                newDir = new File(dir);
                if (!newDir.mkdirs()) output.println("Coudln't create directory");
                else output.println("Directory created successfully");
            } else output.println("Directory created successfully");
        } else args("mkdir");
    }

    public void rm(String fileName) {
        if (!fileName.equals("")) {
            File target = new File(current_directory.getAbsolutePath(), fileName);
            if (!target.exists()) target = new File(fileName);
            if (!target.exists()) output.println("File not found");
            else {
                if (target.isFile()) {
                    target.delete();
                    output.println("File deleted successfully");
                } else output.println(fileName + " is not a file");
            }
        } else args("rm");
    }

    public void rmdir(String path) {
        if (!path.equals("")) {
            File target = new File(current_directory.getAbsolutePath(), path);
            if (!target.exists()) target = new File(path);
            if (!target.exists()) output.println("Directory not found");
            else {
                if (target.isDirectory()) {
                    if (isEmpty(target)) {
                        deleteDirs(target);
                        output.println("Directory removed successfully.");
                    } else {
                        output.println("Directory not empty");
                    }
                } else output.println(path + " is not a directory");
            }
        } else args("rmdir");
    }

    public void deleteDirs(File directory) {
        for (File subFile : directory.listFiles()) {
            if (subFile.isDirectory()) {
                deleteDirs(subFile);
            }
        }
        directory.delete();
    }

    public boolean isEmpty(File directory) {
        boolean flag = true;
        for (File subFile : directory.listFiles()) {
            if (subFile.isDirectory()) {
                flag = isEmpty(subFile);
            } else {
                return false;
            }
        }
        return flag;
    }

    public void date() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE ");
        Date day = new Date();
        Formatter month = new Formatter();
        Calendar cal = Calendar.getInstance();
        //month = new Formatter();
        month.format("%tb ", cal, cal);
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd HH:mm:ss zzz yyyy");
        Date date = new Date();
        output.println(formatter.format(day) + month + formatter2.format(date));
    }

    public void pwd() {
        output.print(current_directory.getAbsolutePath());
    }

    public void cat(String[] paths) {
        if (paths.length > 0 && paths[0].length() > 0) {
            for (String path : paths) {
                if (path.length() > 0) {
                    File target = new File(current_directory.getAbsolutePath(), path);
                    if (!target.exists()) target = new File(path);
                    if (!target.exists()) output.println("File doesn't exist");
                    else {
                        if (target.isDirectory()) output.println(path + " is a directory");
                        else output.println(readFile(target.getAbsolutePath()));
                    }
                }
            }
        } else args("cat");
    }

    public void more(String file) {
        if (!file.equals("")) {
            File targetFile = new File(file);
            if (!targetFile.exists()) targetFile = new File(current_directory.getAbsolutePath(), file);
            if (!targetFile.exists()) output.println("File doesn't exist");
            else {
                if (targetFile.isDirectory()) output.println(file + " is a directory");
                else {
                    String data = readFile(targetFile.getAbsolutePath());
                    String[] lines = data.split("\n");
                    Scanner input = new Scanner(System.in);
                    int maxLines = 20;
                    if (lines.length > maxLines) {
                        int i = 0;
                        for (; i < maxLines; i++) {
                            output.println(lines[i]);
                        }
                        for (; i < lines.length; i++) {
                            output.println(lines[i]);
                            if (i < lines.length - 1) input.nextLine();
                        }
                        output.println("###END OF FILE###");
                    } else output.println(data);
                }
            }
        } else args("more");
    }

    public void help() {
        output.println("CLI Help Menu");
        output.println("=============");
        output.println(String.format("%-50s %s", "[COMMAND]", "[DESCRIPTION]"));
        output.println(String.format("%-50s %s", "cd", "changes current directory to specified path"));
        output.println(String.format("%-50s %s", "ls", "lists content of current directory"));
        output.println(String.format("%-50s %s", "cp [source file] [destination]", "copy file from specified directory to destination path"));
        output.println(String.format("%-50s %s", "mv [source file/directory] [destination]", "moves file or directory to destination path"));
        output.println(String.format("%-50s %s", "rm [target file]", "deletes file from specified path"));
        output.println(String.format("%-50s %s", "rmdir [target directory]", "deletes directory from specified path"));
        output.println(String.format("%-50s %s", "mkdir [directory path]", "creates new directory in specified path"));
        output.println(String.format("%-50s %s", "cat [file1] [file2]...", "print contents of file1, then file2, then..."));
        output.println(String.format("%-50s %s", "more [file]", "view contents of file without spamming console"));
        output.println(String.format("%-50s %s", "args [command]", "display args required for a command"));
        output.println(String.format("%-50s %s", "date", "displays current system date"));
        output.println(String.format("%-50s %s", "pwd", "retrieve current directory"));
        output.println(String.format("%-50s %s", "clear", "clear console"));
        output.println(String.format("%-50s %s", "help", "display this help menu"));
        output.println("=========================================================================================");
    }

    public void args(String command) {
        if (command.equalsIgnoreCase("cd")) {
            output.println(String.format("%-10s %s", "command: cd", "arg1: target directory"));
        } else if (command.equalsIgnoreCase("ls")) {
            output.println(String.format("%-10s %s", "ls", "arg1: N/A"));
        } else if (command.equalsIgnoreCase("cp")) {
            output.println(String.format("%-10s %s", "command: cp", "arg1:[source file] arg2:[destination]"));
        } else if (command.equalsIgnoreCase("mv")) {
            output.println(String.format("%-10s %s", "command: mv", "arg1:[source file/directory] arg2:[destination]"));
        } else if (command.equalsIgnoreCase("rm")) {
            output.println(String.format("%-10s %s", "command: rm", "arg1:[target file]"));
        } else if (command.equalsIgnoreCase("rmdir")) {
            output.println(String.format("%-10s %s", "command: rmdir", "arg1:[target directory]"));
        } else if (command.equalsIgnoreCase("mkdir")) {
            output.println(String.format("%-10s %s", "command: mkdir", "arg1:[directory path]"));
        } else if (command.equalsIgnoreCase("cat")) {
            output.println(String.format("%-10s %s", "command: cat", "arg1:[file1] arg2:[file2]..."));
        } else if (command.equalsIgnoreCase("more")) {
            output.println(String.format("%-10s %s", "command: more", "arg1:[file]"));
        } else if (command.equalsIgnoreCase("args")) {
            output.println(String.format("%-10s %s", "command: args", "arg1:[command]"));
        } else if (command.equalsIgnoreCase("date")) {
            output.println(String.format("%-10s %s", "command: date", "arg1: N/A"));
        } else if (command.equalsIgnoreCase("pwd")) {
            output.println(String.format("%-10s %s", "command: pwd", "arg1: N/A"));
        } else if (command.equalsIgnoreCase("clear")) {
            output.println(String.format("%-10s %s", "command: clear", "arg1: N/A"));
        } else if (command.equalsIgnoreCase("help")) {
            output.println(String.format("%-10s %s", "command: help", "arg1: N/A"));
        } else args("args");
    }

    public void clear() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                output.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {
        }
    }

    public void echo(String argument) {
        output.println(argument);
    }

    public void exit() {
        System.exit(0);
    }

}