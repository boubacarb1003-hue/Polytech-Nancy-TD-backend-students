package com.example.todoapp.dao;

import com.example.todoapp.model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Data Access Object for {@link Task} model.
 */
public class TaskDao {

    private static final String DATABASE_URL = "jdbc:sqlite:tasks.db";

    public TaskDao() {
        initDatabase();
        initDefaultTasks();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    private void initDatabase() {
        String sql = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY,
                    title TEXT NOT NULL,
                    description TEXT,
                    done INTEGER NOT NULL
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la base SQLite", e);
        }
    }

    private void initDefaultTasks() {
        if (!findAll().isEmpty()) {
            return;
        }

        save(new Task(1, "Réviser DS de maths", "Séries numériques et probabilités.", false));
        save(new Task(2, "Valider mon PIVE", "PIVE Club Poker.", true));
        save(new Task(3, "Choisir mon parcours de 4A", "SIR ou SIA ?", false));
    }

    public Task save(Task task) {
        String sql = """
                INSERT OR REPLACE INTO tasks (id, title, description, done)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, task.id());
            statement.setString(2, task.title());
            statement.setString(3, task.description());
            statement.setInt(4, task.done() ? 1 : 0);

            statement.executeUpdate();
            return task;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la tâche", e);
        }
    }

    public Optional<Task> findById(int id) {
        String sql = "SELECT id, title, description, done FROM tasks WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(toTask(resultSet));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la tâche", e);
        }
    }

    public Collection<Task> findAll() {
        Collection<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, description, done FROM tasks";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                tasks.add(toTask(resultSet));
            }

            return tasks;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des tâches", e);
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la tâche", e);
        }
    }

    public boolean update(int id, Task task) {
        String sql = """
                UPDATE tasks
                SET title = ?, description = ?, done = ?
                WHERE id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.title());
            statement.setString(2, task.description());
            statement.setInt(3, task.done() ? 1 : 0);
            statement.setInt(4, id);

            int affectedRows = statement.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de la tâche", e);
        }
    }

    private Task toTask(ResultSet resultSet) throws SQLException {
        return new Task(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getInt("done") == 1
        );
    }
}