package nntc.tsvetkova.beautysalon;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseManager {

    //Переменные окружения для подключения к СУБД
    private static final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL"):"jdbc:postgresql://89.109.54.20:6543/user36";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER"):"user36";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD"):"63563";
    private static final String TABLE_NAME = System.getenv("DB_TABLE") != null ? System.getenv("DB_TABLE"):"users";

    private Connection connection;

    //Метод для подключения к бд
    public void connect(){
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Успешно подключено к БД");
        } catch (SQLException e) {
                System.out.println("Ошибка при подключении к БД: " + e.getMessage());
                Platform.runLater(() -> ErrorDialog.showError("Ошибка при подключении к БД: ", e.getMessage()));
            }
    }
    //Метод для отключения от бд
    public void disconnect(){
        if (connection != null){
            try {
                connection.close();
                System.out.println("Соединение с бд закрыто");
            } catch (SQLException e){
                System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
                Platform.runLater(() -> ErrorDialog.showError("Ошибка при закрытии соединения: ", e.getMessage()));
            }
        }
    }

    //Метод для проверки и создания таблицы
    public void ensureTableExists(){
        String createTableQuery = String.format("""
                CREATE TABLE IF NOT EXISTS %s (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL
                );
        """, TABLE_NAME);

        try (Statement statement = connection.createStatement()){
            statement.execute(createTableQuery);
            System.out.println("Проверка таблицы завершена. Таблица готова к использованию");
        }catch (SQLException e){
            System.out.println("Ошибка при создании/проверке таблицы: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при создании/проверке таблицы: ",  e.getMessage()));
        }
    }

    public ObservableList<User> fetchData(){

        ObservableList<User> re = FXCollections.observableArrayList();

        String query = String.format("SELECT id, name FROM %s", TABLE_NAME);

        try (var preparedStatement = connection.prepareStatement(query);
        var resultSet = preparedStatement.executeQuery()){

            while (resultSet.next()){
                re.add(new User(resultSet.getInt("id"),resultSet.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при выполнении запроса: ", e.getMessage()));
        }
        return re;
    }

    //add data
    public void insertData(int id, String name){
        String query = String.format("INSERT INTO %s (id, name) VALUES (%d, '%s1')", TABLE_NAME, id, name);

        try (var preparedStatement = connection.prepareStatement(query)){
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Добавлено строк: " + rowsInserted);
        } catch (SQLException e){
            System.out.println("Ошибка при вставке данных: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при вставке данных: ", e.getMessage()));
        }
    }

    //edit data
    public void updateData(int id, String name){
        String query = String.format("UPDATE %s SET name='%s' WHERE id=%d", TABLE_NAME, name, id);

        try (var preparedStatement = connection.prepareStatement(query)){
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println("Обновлено строк: " + rowsUpdated);
        } catch (SQLException e){
            System.out.println("Ошибка при изменении данных: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при изменении данных: ", e.getMessage()));
        }
    }

    //delete data
    public void deleteData(int id) {
        String query = String.format("DELETE FROM %s WHERE id=%d", TABLE_NAME, id);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println("Удалено строк: " + rowsDeleted);
        }catch (SQLException e) {
            System.out.println("Ошибка при удалении данных: " + e.getMessage());
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при удалении данных: ", e.getMessage()));

        }
    }




}
