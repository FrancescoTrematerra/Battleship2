package battleship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tasks Test Suite")
class TasksTest {

    private Scanner scanner;
    private String input;

    // ==================== readPosition ====================

    @Test
    void testReadPositionValid() {
        input = "5 7";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Position pos = Tasks.readPosition(scanner);

        assertEquals(5, pos.getRow());
        assertEquals(7, pos.getColumn());
    }

    @Test
    void testReadPositionNull() {
        assertThrows(AssertionError.class, () -> Tasks.readPosition(null));
    }

    // ==================== readShip ====================

    @Test
    void testReadShipValid() {
        input = "galeao 3 4 n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Ship ship = Tasks.readShip(scanner);

        assertNotNull(ship);
        assertEquals("Galeao", ship.getCategory());
        assertEquals(Compass.NORTH, ship.getBearing());
    }

    @Test
    void testReadShipFrigate() {
        input = "fragata 1 2 e";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Ship ship = Tasks.readShip(scanner);

        assertEquals("Fragata", ship.getCategory());
    }

    @Test
    void testReadShipInvalid() {
        input = "invalido 1 1 n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Ship ship = Tasks.readShip(scanner);

        assertNull(ship);
    }

    @Test
    void testReadShipNull() {
        assertThrows(AssertionError.class, () -> Tasks.readShip(null));
    }

    // ==================== readClassicPosition ====================

    @Test
    void testClassicPositionCompact() {
        scanner = new Scanner("A3");

        IPosition pos = Tasks.readClassicPosition(scanner);

        assertEquals(0, pos.getRow());
        assertEquals(2, pos.getColumn());
    }

    @Test
    void testClassicPositionInvalid() {
        scanner = new Scanner("123");

        assertThrows(IllegalArgumentException.class,
                () -> Tasks.readClassicPosition(scanner));
    }

    @Test
    void testClassicPositionNull() {
        assertThrows(IllegalArgumentException.class,
                () -> Tasks.readClassicPosition(null));
    }

    // ==================== menuHelp ====================

    @Test
    void testMenuHelp() {
        assertDoesNotThrow(() -> Tasks.menuHelp());
    }
}