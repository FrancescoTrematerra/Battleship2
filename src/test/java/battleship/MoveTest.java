package battleship;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {

    private IGame.ShotResult createShot(boolean valid, boolean repeated, IShip ship, boolean sunk) {
        return new IGame.ShotResult(valid, repeated, ship, sunk);
    }

    private IShip createShip(String name) {
        return Ship.buildShip(name.toLowerCase(), Compass.NORTH, new Position(0, 0));
    }

    // ================= BASE =================

    @Test
    void testFullScenario() {

        IShip fragata = createShip("fragata");
        IShip galeao = createShip("galeao");

        List<IGame.ShotResult> results = new ArrayList<>();

        results.add(createShot(false, false, null, false)); // inválido
        results.add(createShot(true, true, null, false));   // repetido
        results.add(createShot(true, false, null, false));  // água
        results.add(createShot(true, false, fragata, false)); // hit
        results.add(createShot(true, false, galeao, true));   // sunk

        Move move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        assertNotNull(json);
    }

    // ================= RAMOS IMPORTANTES =================

    @Test
    void testOnlyRepeatedBranch() {

        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(createShot(true, true, null, false));
        results.add(createShot(true, true, null, false));

        Move move = new Move(2, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        assertTrue(json.contains("repeatedShots"));
    }

    @Test
    void testOnlyHitsNoMissedShots() {

        IShip fragata = createShip("fragata");

        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(createShot(true, false, fragata, false));
        results.add(createShot(true, false, fragata, false));

        Move move = new Move(3, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        assertTrue(json.contains("hitsOnBoats"));
    }

    @Test
    void testSunkBoatsMultiple() {

        IShip galeao = createShip("galeao");

        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(createShot(true, false, galeao, true));
        results.add(createShot(true, false, galeao, true));

        Move move = new Move(4, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        assertTrue(json.contains("sunkBoats"));
    }

    @Test
    void testOutsideShotsBranch() {

        List<IGame.ShotResult> results = new ArrayList<>();

        // poucos tiros válidos → força outsideShots
        results.add(createShot(true, false, null, false));

        Move move = new Move(5, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        assertTrue(json.contains("outsideShots"));
    }

    // ================= PLURAL / STRINGS =================

    @Test
    void testPluralStrings() {

        List<IGame.ShotResult> results = new ArrayList<>();

        results.add(createShot(true, false, null, false));
        results.add(createShot(true, false, null, false));

        Move move = new Move(6, new ArrayList<>(), results);

        String json = move.processEnemyFire(true);

        assertNotNull(json); // apenas garantir execução
    }

    // ================= GETTERS =================

    @Test
    void testGettersAndToString() {

        Move move = new Move(10, new ArrayList<>(), new ArrayList<>());

        assertEquals(10, move.getNumber());
        assertNotNull(move.getShots());
        assertNotNull(move.getShotResults());
        assertTrue(move.toString().contains("Move"));
    }

    // ================= VERBOSE FALSE =================

    @Test
    void testVerboseFalse() {

        List<IGame.ShotResult> results = new ArrayList<>();
        results.add(createShot(true, false, null, false));

        Move move = new Move(7, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        assertNotNull(json);
    }
}