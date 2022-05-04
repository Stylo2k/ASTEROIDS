package nl.rug.aoop.asteroids.util.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.nio.file.Path;
import java.util.List;

/**
 * Handles database manipulation i.e saving and loading player data
 */
public class DatabaseManager {
    /**
     * Manager of database managers
     */
    private EntityManagerFactory managerFactory;

    /**
     * Manager for database
     */
    private EntityManager manager;

    public DatabaseManager(String dbName) {
        initDatabase(dbName);
    }

    /**
     * Initialises a new database with the given name or open existing one with given name
     * @param dbName name of database
     */
    private void initDatabase(String dbName) {
        Path dbPath = Path.of("db",dbName + ".odb");
        managerFactory = Persistence.createEntityManagerFactory(dbPath.toString());
        manager = managerFactory.createEntityManager();
    }

    /**
     * Called once the application stops to close the database
     */
    public void closeDatabase() {
        manager.close();
        managerFactory.close();
    }

    /**
     * Adds a new player to the database
     * @param gameData game data that is to be added to the database
     */
    public void addNewPlayer(GameData gameData) {
        try {
            var query = manager.createQuery(Query.ADD_NEW_PLAYER.getText(), GameData.class);
            gameData.setId(query.getSingleResult().getId()+1);
        } catch (NullPointerException e) {
            gameData.setId(1);
        }
        manager.getTransaction().begin();
        manager.persist(gameData);
        manager.getTransaction().commit();
    }

    /**
     * Updates existing player in database if player get higher score or adds a new player if the username doesn't exist
     * @param gameData game data that is to be added to the database
     */
    public void updatePlayers(GameData gameData) {
        try {
            var query = manager.createQuery(Query.UPDATE_PLAYER.getText() + gameData.getName() + "'",
                    GameData.class);
            GameData matchedScore = query.getSingleResult();
            if (gameData.getScore() > matchedScore.getScore()) {
                manager.getTransaction().begin();
                matchedScore.setScore(gameData.getScore());
                manager.getTransaction().commit();
            }
        } catch (javax.persistence.NoResultException e) {
            addNewPlayer(gameData);
        }
    }

    /**
     * @return Sorted game data from database
     */
    public List<GameData> getSortedScores() {
        var query = manager.createQuery(Query.GET_ALL_PLAYERS.getText(), GameData.class);
        return query.getResultList();
    }

}
