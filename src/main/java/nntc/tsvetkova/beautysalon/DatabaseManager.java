package nntc.tsvetkova.beautysalon;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.prefs.Preferences;

public class DatabaseManager {
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static String SCHEMA;

    private Preferences prefs;

    private Connection connection;

    // конструктор
    // создали для инициализации prefs на уровне всего класса
    public DatabaseManager() {
        prefs = Preferences.userNodeForPackage(DesktopApplication.class);
    }

    // Метод для подключения к базе данных
    public void connect() throws SQLException {

        URL = String.format(
                "jdbc:postgresql://%s:%s/%s?currentSchema=%s",
                prefs.get("subdAddress", "localhost"),
                prefs.get("subdPort", "5432"),
                prefs.get("subdDbname", "postgres"),
                prefs.get("subdSchema", "public")
        );
        USER = prefs.get("subdUser", "postgres");
        PASSWORD = prefs.get("subdPassword", "postgres");
        SCHEMA = prefs.get("subdSchema", "public");

        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Успешно подключено к базе данных.");
    }

    // Метод для отключения от базы данных
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Соединение с базой данных закрыто.");
            } catch (SQLException e) {
                System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
                // Показываем модальное окно с ошибкой
                Platform.runLater(() -> ErrorDialog.showError("Ошибка при закрытии соединения: ", e.getMessage()));
            }
        }
    }

    // Метод развёртывания таблиц базы данных
    public void ensureTablesExists() {
        String ddlQueries = String.format("""
                    CREATE TABLE IF NOT EXISTS %s.customers (
                            	id serial4 NOT NULL PRIMARY KEY,
                            	"name" varchar(256) NOT NULL,
                            	email varchar(128) NOT NULL,
                            	CONSTRAINT customers_email_key UNIQUE (email)
                            );
                    CREATE TABLE IF NOT EXISTS %s.orders (
                                id serial4 NOT NULL PRIMARY KEY,
                                customer_id int4 NULL,
                                CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES %s.customers(id) ON DELETE CASCADE ON UPDATE CASCADE
                             );
                    CREATE TABLE IF NOT EXISTS %s.products (
                        id serial4 NOT NULL PRIMARY KEY,
                        "name" varchar(256) NOT NULL,
                        price numeric(10, 2) NOT NULL
                    );
                    CREATE TABLE IF NOT EXISTS %s.order_product (
                             	order_id int4 NOT NULL,
                             	product_id int4 NOT NULL,
                             	quantity int4 NOT NULL,
                             	CONSTRAINT order_product_pk PRIMARY KEY (order_id, product_id),
                             	CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES %s.products(id) ON DELETE CASCADE ON UPDATE CASCADE,
                             	CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES %s.orders(id) ON DELETE CASCADE ON UPDATE CASCADE
                             );
                """, SCHEMA, SCHEMA, SCHEMA, SCHEMA, SCHEMA, SCHEMA, SCHEMA);

        String dmlQueries = String.format("""
                    INSERT INTO %s.products ("name",price) VALUES
                    ('Сгущенное молоко',60),
                    ('Сыр',200),
                    ('Молоко',50),
                    ('Сахар',150),
                    ('Сливочное масло',150),
                    ('Батон',35);
                    INSERT INTO %s.customers ("name",email) VALUES
                    ('Вася','vasya@mail.ru'),
                    ('Маша','masha@mail.ru');
                    INSERT INTO %s.orders (customer_id) VALUES
                    (1),(2);
                    INSERT INTO %s.order_product (order_id, product_id, quantity) VALUES
                    (1, 1, 1),(1, 2, 2),(1, 3, 3),(2, 4, 3),(2, 5, 1),(2, 6, 2);
                """, SCHEMA, SCHEMA, SCHEMA, SCHEMA);


        try (Statement statement = connection.createStatement()) {
            statement.execute(ddlQueries);
            // statement.execute(dmlQueries); // Если нужно, то можно и DML запросы здесь запустить
            System.out.println("Проверка структуры базы данных завершена.");
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке/создании таблицы: " + e.getMessage());
            // Показываем модальное окно с ошибкой
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при проверке/создании таблицы: ", e.getMessage()));
        }
    }

    public void productsInsertData(String name, Float price) {
        String query = String.format("INSERT INTO %s (name, price) VALUES ('%s', %s)", SCHEMA.concat(".products"), name, String.format("%f", price).replace(",", "."));
        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Добавлено строк: " + rowsInserted);
        } catch (SQLException e) {
            System.out.println("Ошибка при вставке данных: " + e.getMessage());
            // Показываем модальное окно с ошибкой
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при вставке данных: ", e.getMessage()));
        }
    }

    public void productsUpdateData(Integer id, String name, float price) {
        String query = String.format("UPDATE %s SET name='%s', price=%s WHERE id=%d", SCHEMA.concat(".products"), name, String.format("%f", price).replace(",", "."), id);

        System.out.println("QUERY:");
        System.out.println(query);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println("Обновлено строк: " + rowsUpdated);
        } catch (SQLException e) {
            System.out.println("Ошибка при изменении данных: " + e.getMessage());
            // Показываем модальное окно с ошибкой
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при изменении данных: ", e.getMessage()));
        }
    }

    public void productsDeleteData(int id) {
        String query = String.format("DELETE FROM %s WHERE id=%d", SCHEMA.concat(".products"), id);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println("Удалено строк: " + rowsDeleted);
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении данных: " + e.getMessage());
            // Показываем модальное окно с ошибкой
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при удалении данных: ", e.getMessage()));
        }
    }

    // Пример: Получение данных
//    public ObservableList<Service> productsFetchData() {
//
//        ObservableList<Service> re = FXCollections.observableArrayList();
//
//        String query = String.format("SELECT id, name, price FROM %s", SCHEMA.concat(".products"));
//
//        try (var preparedStatement = connection.prepareStatement(query);
//             var resultSet = preparedStatement.executeQuery()) {
//
//            while (resultSet.next()) {
//                re.add(new Service(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getFloat("price")));
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
//            // Показываем модальное окно с ошибкой
//            Platform.runLater(() -> ErrorDialog.showError("Ошибка при выполнении запроса: ", e.getMessage()));
//        }
//        return re;
//    }

    public void clientsInsertData(String name, String email) {
        String query = String.format("INSERT INTO %s (name, email) VALUES ('%s', '%s')", SCHEMA.concat(".clients"), name, email);
        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Добавлено строк: " + rowsInserted);
        } catch (SQLException e) {
            System.out.println("Ошибка при вставке данных: " + e.getMessage());
            // Показываем модальное окно с ошибкой
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при вставке данных: ", e.getMessage()));
        }
    }

    public void clientsUpdateData(Integer id, String name, String email) {
        String query = String.format("UPDATE %s SET name='%s', email='%s' WHERE id=%d", SCHEMA.concat(".clients"), name, email, id);

        System.out.println("QUERY:");
        System.out.println(query);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println("Обновлено строк: " + rowsUpdated);
        } catch (SQLException e) {
            System.out.println("Ошибка при изменении данных: " + e.getMessage());
            // Показываем модальное окно с ошибкой
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при изменении данных: ", e.getMessage()));
        }
    }

    public void clientsDeleteData(int id) {
        String query = String.format("DELETE FROM %s WHERE id=%d", SCHEMA.concat(".clients"), id);

        try (var preparedStatement = connection.prepareStatement(query)) {
            int rowsDeleted = preparedStatement.executeUpdate();
            System.out.println("Удалено строк: " + rowsDeleted);
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении данных: " + e.getMessage());
            // Показываем модальное окно с ошибкой
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при удалении данных: ", e.getMessage()));
        }
    }

    // Пример: Получение данных
    public ObservableList<Client> clientFetchData() {

        ObservableList<Client> re = FXCollections.observableArrayList();

        String query = String.format("SELECT id, name, email FROM %s", SCHEMA.concat(".clients"));

        try (var preparedStatement = connection.prepareStatement(query);
             var resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                re.add(new Client(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("email")));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
            // Показываем модальное окно с ошибкой
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при выполнении запроса: ", e.getMessage()));
        }
        return re;
    }

}
