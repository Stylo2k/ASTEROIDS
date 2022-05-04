package aoop.asteroids.database;

import nl.rug.aoop.asteroids.util.database.DatabaseManager;
import nl.rug.aoop.asteroids.util.database.GameData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseManagerTest {

    @Test
    void updatePlayers() {
        DatabaseManager databaseManager = new DatabaseManager("updateTest");
        databaseManager.updatePlayers(new GameData("Dominic", 6));
        databaseManager.updatePlayers(new GameData("Dominic", 11));
        databaseManager.updatePlayers(new GameData("Dominic", 10));
        databaseManager.updatePlayers(new GameData("Dominic", 9));

        //only updates is score is larger test
        assertEquals(databaseManager.getSortedScores().get(0).getScore(), 11);

        //first added record to db has unique id of 1
        assertEquals(databaseManager.getSortedScores().get(0).getId(), 1);

        //correct name test
        assertEquals(databaseManager.getSortedScores().get(0).getName(), "Dominic");

        databaseManager.closeDatabase();
    }

    @Test
    void getSortedScores() {
        DatabaseManager databaseManager = new DatabaseManager("SortedScoreTest");
        databaseManager.updatePlayers(new GameData("P0", 6));
        databaseManager.updatePlayers(new GameData("P1", 11));
        databaseManager.updatePlayers(new GameData("P2", 10));
        databaseManager.updatePlayers(new GameData("P3", 0));

        assertEquals(databaseManager.getSortedScores().get(0).getScore(),11);
        assertEquals(databaseManager.getSortedScores().get(1).getScore(),10);
        assertEquals(databaseManager.getSortedScores().get(2).getScore(),6);
        assertEquals(databaseManager.getSortedScores().get(3).getScore(),0);

    }
}