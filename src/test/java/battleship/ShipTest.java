package battleship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Ship
 */
public class ShipTest {

    // -----------------------------
    // BUILD SHIP
    // -----------------------------

    @Test
    void testBuildShipValidTypes() {
        Position pos = new Position(1, 1);

        assertTrue(Ship.buildShip("barca", Compass.NORTH, pos) instanceof Barge);
        assertTrue(Ship.buildShip("caravela", Compass.NORTH, pos) instanceof Caravel);
        assertTrue(Ship.buildShip("nau", Compass.NORTH, pos) instanceof Carrack);
        assertTrue(Ship.buildShip("fragata", Compass.NORTH, pos) instanceof Frigate);
        assertTrue(Ship.buildShip("galeao", Compass.NORTH, pos) instanceof Galleon);
    }

    @Test
    void testBuildShipInvalidType() {
        Position pos = new Position(1, 1);

        Ship s = Ship.buildShip("invalid", Compass.NORTH, pos);
        assertNull(s);
    }

    // -----------------------------
    // GET ADJACENT POSITIONS
    // -----------------------------

    @Test
    void testGetAdjacentPositions() {
        Ship ship = new Barge(Compass.NORTH, new Position(5, 5));

        List<IPosition> adj = ship.getAdjacentPositions();

        assertNotNull(adj);
        assertFalse(adj.isEmpty());
    }

    // -----------------------------
    // STILL FLOATING
    // -----------------------------

    @Test
    void testStillFloatingTrueAndFalse() {
        Ship ship = new Barge(Compass.NORTH, new Position(1, 1));

        // inicialmente não está afundado
        assertTrue(ship.stillFloating());

        // afunda tudo
        ship.sink();

        assertFalse(ship.stillFloating());
    }

    // -----------------------------
    // EXTREME POSITIONS
    // -----------------------------

    @Test
    void testExtremePositions() {
        Ship ship = new Barge(Compass.NORTH, new Position(5, 5));

        int top = ship.getTopMostPos();
        int bottom = ship.getBottomMostPos();
        int left = ship.getLeftMostPos();
        int right = ship.getRightMostPos();

        assertTrue(top <= bottom);
        assertTrue(left <= right);
    }

    // -----------------------------
    // OCCUPIES
    // -----------------------------

    @Test
    void testOccupiesTrueAndFalse() {
        Ship ship = new Barge(Compass.NORTH, new Position(2, 2));

        IPosition p = ship.getPositions().get(0);
        IPosition far = new Position(99, 99);

        assertTrue(ship.occupies(p));
        assertFalse(ship.occupies(far));
    }

    // -----------------------------
    // TOO CLOSE TO (POSITION)
    // -----------------------------

    @Test
    void testTooCloseToPosition() {
        Ship ship = new Barge(Compass.NORTH, new Position(5, 5));

        IPosition base = ship.getPositions().get(0);
        IPosition adjacent = new Position(base.getRow(), base.getColumn() + 1);
        IPosition far = new Position(20, 20);

        assertTrue(ship.tooCloseTo(adjacent));
        assertFalse(ship.tooCloseTo(far));
    }

    // -----------------------------
    // TOO CLOSE TO (SHIP)
    // -----------------------------

    @Test
    void testTooCloseToShipTrueAndFalse() {
        Ship ship1 = new Barge(Compass.NORTH, new Position(5, 5));
        Ship ship2 = new Barge(Compass.NORTH, new Position(5, 6)); // perto
        Ship ship3 = new Barge(Compass.NORTH, new Position(50, 50)); // longe

        assertTrue(ship1.tooCloseTo(ship2));
        assertFalse(ship1.tooCloseTo(ship3));
    }

    // -----------------------------
    // SHOOT
    // -----------------------------

    @Test
    void testShootHitsCorrectPosition() {
        Ship ship = new Barge(Compass.NORTH, new Position(3, 3));

        IPosition p = ship.getPositions().get(0);

        ship.shoot(p);

        assertTrue(p.isHit());
    }

    @Test
    void testShootMiss() {
        Ship ship = new Barge(Compass.NORTH, new Position(3, 3));

        // posição válida mas fora do ship
        IPosition miss = new Position(0, 0);

        ship.shoot(miss);

        for (IPosition p : ship.getPositions()) {
            assertFalse(p.isHit());
        }
    }

    // -----------------------------
    // SINK
    // -----------------------------

    @Test
    void testSink() {
        Ship ship = new Barge(Compass.NORTH, new Position(3, 3));

        ship.sink();

        for (IPosition p : ship.getPositions()) {
            assertTrue(p.isHit());
        }
    }

    // -----------------------------
    // GETTERS
    // -----------------------------

    @Test
    void testGetters() {
        Position pos = new Position(1,1);
        Ship ship = new Barge(Compass.NORTH, pos);

        assertEquals(pos, ship.getPosition());
        assertEquals(Compass.NORTH, ship.getBearing());
        assertNotNull(ship.getPositions());
        assertTrue(ship.getSize() > 0);
    }

    // -----------------------------
    // TOSTRING
    // -----------------------------

    @Test
    void testToString() {
        Ship ship = new Barge(Compass.NORTH, new Position(1, 1));

        String str = ship.toString();

        assertTrue(str.contains(ship.getCategory()));
        assertTrue(str.contains(ship.getBearing().toString()));
    }
}