package battleship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.*;

public class Game implements IGame {

    public static final int BOARD_SIZE = 10;
    public static final int NUMBER_SHOTS = 3;

    private static final char EMPTY_MARKER = '.';
    private static final char SHIP_MARKER = '#';
    private static final char SHOT_SHIP_MARKER = '*';
    private static final char SHOT_WATER_MARKER = 'o';
    private static final char SHIP_ADJACENT_MARKER = '-';

    private final IFleet myFleet;
    private final List<IMove> alienMoves;

    private final IFleet alienFleet;
    private final List<IMove> myMoves;

    private Integer countInvalidShots;
    private Integer countRepeatedShots;
    private Integer countHits;
    private Integer countSinks;
    private int moveNumber;

    public Game(IFleet myFleet) {
        this.moveNumber = 1;

        this.alienMoves = new ArrayList<>();
        this.myMoves = new ArrayList<>();

        this.alienFleet = new Fleet();
        this.myFleet = myFleet;

        this.countInvalidShots = 0;
        this.countRepeatedShots = 0;
        this.countHits = 0;
        this.countSinks = 0;
    }

    @Override
    public IFleet getMyFleet() { return myFleet; }

    @Override
    public List<IMove> getAlienMoves() { return alienMoves; }

    @Override
    public IFleet getAlienFleet() { return alienFleet; }

    @Override
    public List<IMove> getMyMoves() { return myMoves; }

    @Override
    public void over() {
        System.out.println();
        System.out.println("+--------------------------------------------------------------+");
        System.out.println("| Maldito sejas, Java Sparrow, eu voltarei, glub glub glub ... |");
        System.out.println("+--------------------------------------------------------------+");
    }

    @Override
    public void printMyBoard(boolean show_shots, boolean show_legend) {
        Game.printBoard(this.myFleet, this.alienMoves, show_shots, show_legend);
    }

    @Override
    public void printAlienBoard(boolean show_shots, boolean show_legend) {
        Game.printBoard(this.alienFleet, this.myMoves, show_shots, show_legend);
    }

    // ===================== REFACTORED =====================

    public String randomEnemyFire() {

        Random random = new Random(System.currentTimeMillis());

        Set<IPosition> usablePositions = getUsablePositions();
        List<IPosition> candidateShots = new ArrayList<>(usablePositions);

        List<IPosition> shots = generateRandomShots(candidateShots, random);

        printShots(shots);

        this.fireShots(shots);

        return Game.jsonShots(shots);
    }

    @Override
    public String readEnemyFire(Scanner in) {

        assert in != null;

        String input = in.nextLine().trim();

        List<IPosition> shots = new ArrayList<>();

        Scanner inputScanner = new Scanner(input);
        while (shots.size() < NUMBER_SHOTS && inputScanner.hasNext()) {

            String token = inputScanner.next();

            if (token.matches("[A-Za-z]")) {
                if (inputScanner.hasNextInt()) {
                    int row = inputScanner.nextInt();
                    shots.add(new Position(token.toUpperCase().charAt(0), row));
                } else {
                    throw new IllegalArgumentException(
                            "Posição incompleta! A coluna '" + token + "' não é seguida por uma linha."
                    );
                }
            } else {
                Scanner singleScanner = new Scanner(token);
                shots.add(Tasks.readClassicPosition(singleScanner));
            }
        }

        if (shots.size() != NUMBER_SHOTS) {
            throw new IllegalArgumentException(
                    "Você deve inserir exatamente " + NUMBER_SHOTS + " posições!"
            );
        }

        this.fireShots(shots);

        return Game.jsonShots(shots);
    }

    private Set<IPosition> getUsablePositions() {

        Set<IPosition> usablePositions = new HashSet<>();

        for (int r = 0; r < BOARD_SIZE; r++)
            for (int c = 0; c < BOARD_SIZE; c++)
                usablePositions.add(new Position(r, c));

        this.myFleet.getSunkShips()
                .forEach(ship -> usablePositions.removeAll(ship.getAdjacentPositions()));

        this.alienMoves
                .forEach(move -> usablePositions.removeAll(move.getShots()));

        return usablePositions;
    }

    private List<IPosition> generateRandomShots(List<IPosition> candidateShots, Random random) {

        List<IPosition> shots = new ArrayList<>();

        if (candidateShots.size() >= NUMBER_SHOTS) {
            fillShotsRandomly(shots, candidateShots, NUMBER_SHOTS, random);
        } else {
            fillShotsRandomly(shots, candidateShots, candidateShots.size(), random);
            repeatLastShot(shots, NUMBER_SHOTS);
        }

        return shots;
    }

    private void fillShotsRandomly(List<IPosition> shots, List<IPosition> candidates,
                                   int limit, Random random) {

        if (candidates.isEmpty()) return;

        while (shots.size() < limit) {
            IPosition newShot = candidates.get(random.nextInt(candidates.size()));
            if (!shots.contains(newShot)) {
                shots.add(newShot);
            }
        }
    }

    private void repeatLastShot(List<IPosition> shots, int targetSize) {

        if (shots.isEmpty()) return;

        IPosition last = shots.get(shots.size() - 1);

        while (shots.size() < targetSize) {
            shots.add(last);
        }
    }

    private void printShots(List<IPosition> shots) {
        System.out.println();
        System.out.print("rajada ");
        for (IPosition shot : shots) {
            System.out.print(shot + " ");
        }
        System.out.println();
    }

    // ===================== ORIGINAL METHODS =====================

    public static void printBoard(IFleet fleet, List<IMove> moves, boolean show_shots, boolean showLegend) {

        char[][] map = new char[BOARD_SIZE][BOARD_SIZE];

        for (int r = 0; r < BOARD_SIZE; r++)
            for (int c = 0; c < BOARD_SIZE; c++)
                map[r][c] = EMPTY_MARKER;

        for (IShip ship : fleet.getShips()) {
            for (IPosition ship_pos : ship.getPositions())
                map[ship_pos.getRow()][ship_pos.getColumn()] = SHIP_MARKER;

            if (!ship.stillFloating())
                for (IPosition adj : ship.getAdjacentPositions())
                    map[adj.getRow()][adj.getColumn()] = SHIP_ADJACENT_MARKER;
        }

        if (show_shots)
            for (IMove move : moves)
                for (IPosition shot : move.getShots()) {
                    if (shot.isInside()) {
                        int r = shot.getRow();
                        int c = shot.getColumn();

                        if (map[r][c] == SHIP_MARKER)
                            map[r][c] = SHOT_SHIP_MARKER;
                        else if (map[r][c] == EMPTY_MARKER || map[r][c] == SHIP_ADJACENT_MARKER)
                            map[r][c] = SHOT_WATER_MARKER;
                    }
                }

        System.out.println();
        for (int r = 0; r < BOARD_SIZE; r++) {
            System.out.print((char) ('A' + r) + " ");
            for (int c = 0; c < BOARD_SIZE; c++)
                System.out.print(map[r][c] + " ");
            System.out.println();
        }
    }

    public void fireShots(List<IPosition> shots) {

        if (shots.size() != NUMBER_SHOTS)
            throw new IllegalArgumentException();

        List<ShotResult> results = new ArrayList<>();
        List<IPosition> used = new ArrayList<>();

        for (IPosition pos : shots) {
            results.add(fireSingleShot(pos, used.contains(pos)));
            used.add(pos);
        }

        Move move = new Move(moveNumber, shots, results);
        move.processEnemyFire(true);

        alienMoves.add(move);
        moveNumber++;
    }

    public ShotResult fireSingleShot(IPosition pos, boolean isRepeated) {

        if (!pos.isInside()) {
            countInvalidShots++;
            return new ShotResult(false, false, null, false);
        }

        if (isRepeated || repeatedShot(pos)) {
            countRepeatedShots++;
            return new ShotResult(true, true, null, false);
        }

        IShip ship = myFleet.shipAt(pos);

        if (ship == null)
            return new ShotResult(true, false, null, false);

        ship.shoot(pos);
        countHits++;

        if (!ship.stillFloating())
            countSinks++;

        return new ShotResult(true, false, ship, !ship.stillFloating());
    }

    public boolean repeatedShot(IPosition pos) {
        for (IMove move : alienMoves)
            if (move.getShots().contains(pos))
                return true;
        return false;
    }

    @Override public int getRepeatedShots() { return countRepeatedShots; }
    @Override public int getInvalidShots() { return countInvalidShots; }
    @Override public int getHits() { return countHits; }
    @Override public int getSunkShips() { return countSinks; }
    @Override public int getRemainingShips() { return myFleet.getFloatingShips().size(); }

    public static String jsonShots(List<IPosition> shots) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<Map<String, Object>> list = new ArrayList<>();

        for (IPosition shot : shots) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("row", String.valueOf(shot.getClassicRow()));
            m.put("column", shot.getClassicColumn());
            list.add(m);
        }

        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}