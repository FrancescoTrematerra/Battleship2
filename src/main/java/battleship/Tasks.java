package battleship;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class Tasks {

    private static final List<Score> scoreboard = new ArrayList<>();

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String GOODBYE_MESSAGE = "Bons ventos!";

    private static final String AJUDA = "ajuda";
    private static final String GERAFROTA = "gerafrota";
    private static final String LEFROTA = "lefrota";
    private static final String DESISTIR = "desisto";
    private static final String RAJADA = "rajada";
    private static final String TIROS = "tiros";
    private static final String MAPA = "mapa";
    private static final String STATUS = "estado";
    private static final String SIMULA = "simula";
    private static final String SCOREBOARD = "scoreboard";

    public static void menu() {

        int jogadas = 0;
        long gameStartTime = System.currentTimeMillis();

        IFleet myFleet = null;
        IGame game = null;

        menuHelp();

        Scanner in = new Scanner(System.in);
        printPrompt();
        String command = readCommand(in);

        while (!command.equals(DESISTIR)) {

            Result result = handleCommand(command, in, myFleet, game, jogadas, gameStartTime);

            myFleet = result.myFleet;
            game = result.game;
            jogadas = result.jogadas;

            printPrompt();
            command = readCommand(in);
        }

        System.out.println(GOODBYE_MESSAGE);
    }

    private static Result handleCommand(String command, Scanner in, IFleet myFleet, IGame game, int jogadas, long gameStartTime) {

        switch (command) {

            case GERAFROTA:
                myFleet = Fleet.createRandom();
                game = new Game(myFleet);
                game.printMyBoard(false, true);
                break;

            case LEFROTA:
                myFleet = buildFleet(in);
                game = new Game(myFleet);
                game.printMyBoard(false, true);
                break;

            case STATUS:
                if (myFleet != null)
                    myFleet.printStatus();
                break;

            case MAPA:
                if (myFleet != null)
                    game.printMyBoard(false, true);
                break;

            case RAJADA:
                jogadas = handleRajada(game, myFleet, in, jogadas, gameStartTime);
                break;

            case SIMULA:
                jogadas = handleSimula(game, myFleet, jogadas, gameStartTime);
                break;

            case TIROS:
                if (game != null)
                    game.printMyBoard(true, true);
                break;

            case SCOREBOARD:
                handleScoreboard();
                break;

            case AJUDA:
                menuHelp();
                break;

            default:
                System.out.println("Que comando é esse??? Repete ...");
        }

        return new Result(myFleet, game, jogadas);
    }

    private static int handleRajada(IGame game, IFleet myFleet, Scanner in, int jogadas, long startTime) {
        if (game != null) {
            jogadas++;
            game.readEnemyFire(in);
            myFleet.printStatus();
            game.printMyBoard(true, false);

            if (game.getRemainingShips() == 0) {
                endGame(game, jogadas, startTime);
            }
        }
        return jogadas;
    }

    private static int handleSimula(IGame game, IFleet myFleet, int jogadas, long startTime) {
        if (game != null) {
            while (game.getRemainingShips() > 0) {
                jogadas++;
                game.randomEnemyFire();
                myFleet.printStatus();
                game.printMyBoard(true, false);
                sleep();
            }

            if (game.getRemainingShips() == 0) {
                endGame(game, jogadas, startTime);
            }
        }
        return jogadas;
    }

    private static void endGame(IGame game, int jogadas, long startTime) {
        game.over();
        long endTime = System.currentTimeMillis();
        double tempo = (endTime - startTime) / 1000.0;
        scoreboard.add(new Score("Vitoria", jogadas, tempo));
    }

    private static void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void handleScoreboard() {
        if (scoreboard.isEmpty()) {
            System.out.println("Sem jogos registados.");
        } else {
            for (Score s : scoreboard) {
                System.out.println(
                        "Resultado: " + s.getResultado() +
                                " | Jogadas: " + s.getJogadas() +
                                " | Tempo: " + s.getTempo() + "s"
                );
            }
        }
    }

    private static void printPrompt() {
        System.out.print("> ");
    }

    private static String readCommand(Scanner in) {
        return in.next();
    }

    public static void menuHelp() {
        System.out.println("======================= AJUDA DO MENU =========================");
        System.out.println("- " + GERAFROTA);
        System.out.println("- " + LEFROTA);
        System.out.println("- " + STATUS);
        System.out.println("- " + MAPA);
        System.out.println("- " + RAJADA);
        System.out.println("- " + SIMULA);
        System.out.println("- " + TIROS);
        System.out.println("- " + SCOREBOARD);
        System.out.println("- " + DESISTIR);
        System.out.println("===============================================================");
    }

    public static Fleet buildFleet(Scanner in) {

        if (in == null) {
            throw new IllegalArgumentException("Scanner não pode ser null");
        }

        Fleet fleet = new Fleet();
        int i = 0;

        while (i < Fleet.FLEET_SIZE) {
            IShip s = readShip(in);

            if (s != null && fleet.addShip(s)) {
                i++;
            } else {
                LOGGER.info("Erro ao adicionar navio");
            }
        }

        return fleet;
    }

    public static Ship readShip(Scanner in) {

        if (in == null) {
            throw new IllegalArgumentException("Scanner não pode ser null");
        }

        String shipKind = in.next();
        Position pos = readPosition(in);
        char c = in.next().charAt(0);
        Compass bearing = Compass.charToCompass(c);

        return Ship.buildShip(shipKind, bearing, pos);
    }

    public static Position readPosition(Scanner in) {

        if (in == null) {
            throw new IllegalArgumentException("Scanner não pode ser null");
        }

        int row = in.nextInt();
        int column = in.nextInt();

        return new Position(row, column);
    }

    public static IPosition readClassicPosition(@NotNull Scanner in) {

        validateScannerInput(in);

        String input = readPositionInput(in);

        if (input.matches("[A-Z]\\d+")) {
            char column = input.charAt(0);
            int row = Integer.parseInt(input.substring(1));
            return new Position(column, row);
        }

        throw new IllegalArgumentException("Formato inválido. Use 'A3' ou 'A 3'.");
    }

    private static String readPositionInput(Scanner in) {
        String part1 = in.next();

        if (in.hasNextInt()) {
            return (part1 + in.next()).toUpperCase();
        }

        return part1.toUpperCase();
    }

    private static void validateScannerInput(@NotNull Scanner in) {
        if (in == null || !in.hasNext()) {
            throw new IllegalArgumentException("Input inválido");
        }
    }

    private static class Result {
        IFleet myFleet;
        IGame game;
        int jogadas;

        Result(IFleet f, IGame g, int j) {
            myFleet = f;
            game = g;
            jogadas = j;
        }
    }
}