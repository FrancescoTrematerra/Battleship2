package battleship;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Fleet.
 */
public class Fleet implements IFleet {

    public static IFleet createRandom() {

        Fleet randomFleet = new Fleet();

        String[] shipTypes = {
                "galeao",
                "fragata",
                "nau", "nau",
                "caravela", "caravela", "caravela",
                "barca", "barca", "barca", "barca"
        };

        int fleetSize = 0;

        while (fleetSize < shipTypes.length) {

            Ship ship = Ship.buildShip(
                    shipTypes[fleetSize],
                    Compass.randomBearing(),
                    Position.randomPosition()
            );

            if (ship != null && randomFleet.addShip(ship)) {
                fleetSize++;
            }
        }

        return randomFleet;
    }

    // -----------------------------------------------------

    private final List<IShip> ships;

    public Fleet() {
        ships = new ArrayList<>();
    }

    @Override
    public List<IShip> getShips() {
        return ships;
    }

    /**
     * REFACTORED METHOD
     */
    @Override
    public boolean addShip(IShip s) {

        assert s != null;

        if (canAddShip(s)) {
            ships.add(s);
            return true;
        }

        return false;
    }

    /**
     * Extract Method → melhora legibilidade e remove complexidade
     */
    private boolean canAddShip(IShip s) {
        return hasCapacity()
                && isInsideBoard(s)
                && !hasCollision(s);
    }

    /**
     * Extract Method → responsabilidade isolada
     */
    private boolean hasCapacity() {
        return ships.size() <= FLEET_SIZE;
    }

    /**
     * Rename + Extract → nome mais claro
     */
    private boolean hasCollision(IShip s) {

        for (IShip ship : ships) {
            if (ship.tooCloseTo(s)) {
                return true;
            }
        }
        return false;
    }

    private boolean colisionRisk(IShip s) {
        return hasCollision(s);
    }

    @Override
    public List<IShip> getShipsLike(String category) {

        assert category != null;

        List<IShip> shipsLike = new ArrayList<>();

        for (IShip s : ships)
            if (s.getCategory().equals(category))
                shipsLike.add(s);

        return shipsLike;
    }

    @Override
    public List<IShip> getFloatingShips() {

        List<IShip> floatingShips = new ArrayList<>();

        for (IShip s : ships)
            if (s.stillFloating())
                floatingShips.add(s);

        return floatingShips;
    }

    @Override
    public List<IShip> getSunkShips() {

        List<IShip> sunkShips = new ArrayList<>();

        for (IShip s : ships)
            if (!s.stillFloating())
                sunkShips.add(s);

        return sunkShips;
    }

    @Override
    public IShip shipAt(IPosition pos) {

        assert pos != null;

        for (IShip ship : ships)
            if (ship.occupies(pos))
                return ship;

        return null;
    }

    private boolean isInsideBoard(IShip s) {

        assert s != null;

        return s.getLeftMostPos() >= 0 &&
                s.getRightMostPos() <= Game.BOARD_SIZE - 1 &&
                s.getTopMostPos() >= 0 &&
                s.getBottomMostPos() <= Game.BOARD_SIZE - 1;
    }

    public void printShips(List<IShip> ships) {

        assert ships != null;

        for (IShip ship : ships)
            System.out.println(ship);
    }

    public void printStatus() {
        System.out.println("Estado da Frota: "
                + this.getFloatingShips().size() + " a flutuar, "
                + this.getSunkShips().size() + " afundados!");
    }

    public void printShipsByCategory(String category) {

        assert category != null;

        printShips(getShipsLike(category));
    }

    public void printFloatingShips() {
        printShips(getFloatingShips());
    }

    void printAllShips() {
        printShips(ships);
    }
}