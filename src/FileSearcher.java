import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileSearcher {

    public static void main(String[] args) {
        var exit = false;
        while (!exit) {
            System.out.print("enter a command\n>");
            var command = parseCommand();

            switch (command.commandType) {
                case Search -> search(command);
                case Help -> help();
                case Exit -> {
                    exit = true;
                }
                case Invalid -> invalid(command);
            }
        }
    }

    private static Command parseCommand() {
        Scanner scanner = new Scanner(System.in);
        var parts = scanner.nextLine().split(" ");

        return switch (parts[0]) {
            case "search" -> parseSearch(parts);
            case "help" -> new Command(CommandType.Help);
            case "exit" -> new Command(CommandType.Exit);
            default -> new Command(CommandType.Invalid, parts);
        };
    }

    private static Command parseSearch(String[] parts) {
        if (parts.length != 3) {
            return new Command(CommandType.Invalid, parts);
        }
        return new Command(CommandType.Search, parts[1], parts[2]);
    }

    private static void search(Command command) {
        File file = new File(command.parameters[1]);

        String output = "";

        try {
            output = "lines containing \"" + command.parameters[0] + "\": \n"
                    + Files.lines(file.toPath())
                            .filter(l -> l.contains(command.parameters[0]))
                            .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            output = "failed to open file \"" + file + "\"";
        } finally {
            System.out.println(output);
        }
    }

    private static void help() {
        System.out.println("available commands:");
        Arrays.stream(CommandType.values())
                .filter(c -> c != CommandType.Invalid)
                .forEach(c -> System.out.println(c.description));
    }

    private static void invalid(Command command) {
        System.out.println("the command \"" + String.join(" ", command.parameters) + "\" was invalid, see help for more info");
    }

    private record Command(CommandType commandType, String... parameters) {
    }

    private enum CommandType {
        Search("search <pattern> <file> : searches the file for pattern"),
        Help("help : prints the available commands"),
        Exit("exit : exits the program"),
        Invalid("invalid command");

        private final String description;

        CommandType(String description) {
            this.description = description;
        }
    }
}
