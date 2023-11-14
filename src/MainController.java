import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;


public class MainController implements Initializable {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnInsert;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Books, String> colAuthor;

    @FXML
    private TableColumn<Books, Integer> colId;

    @FXML
    private TableColumn<Books, Integer> colPages;

    @FXML
    private TableColumn<Books, String> colTitle;

    @FXML
    private TableColumn<Books, Integer> colYear;

    @FXML
    private TextField tfAuthor;

    @FXML
    private TextField tfPages;

    @FXML
    private TextField tfTitle;

    @FXML
    private TextField tfYear;

    @FXML
    private TextField tfid;

    @FXML
    private TableView<Books> tvBooks;

    @FXML
    void handleButtonAction(ActionEvent event) {
        if (event.getSource() == btnInsert) {
            insertRecord();
        } else if (event.getSource() == btnUpdate) {
            updateRecord();
        } else if (event.getSource() == btnDelete) {
            deleteButton();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showBooks();
    }

    public Connection getConnection() {
        Connection conn;
        try {

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "root");
            return conn;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public ObservableList<Books> getBooksList() {
        ObservableList<Books> bookList = FXCollections.observableArrayList();
        Connection conn = getConnection();
        String query = "SELECT * FROM books";
        Statement st;
        ResultSet rs;

        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Books books;
            while (rs.next()) {
                books = new Books(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("year"),
                        rs.getInt("pages"));
                bookList.add(books);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookList;
    }

    public void showBooks() {
        ObservableList<Books> list = getBooksList();
        colId.setCellValueFactory(new PropertyValueFactory<Books, Integer>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<Books, String>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<Books, String>("author"));
        colYear.setCellValueFactory(new PropertyValueFactory<Books, Integer>("year"));
        colPages.setCellValueFactory(new PropertyValueFactory<Books, Integer>("pages"));

        tvBooks.setItems(list);
    }
    
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void insertRecord() {
        String idText = tfid.getText();
        String title = tfTitle.getText();
        String author = tfAuthor.getText();
        String yearText = tfYear.getText();
        String pagesText = tfPages.getText();

        if (idText.isEmpty() || title.isEmpty() || author.isEmpty() || yearText.isEmpty() || pagesText.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "All fields are required.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            int year = Integer.parseInt(yearText);
            int pages = Integer.parseInt(pagesText);

            String query = "INSERT INTO books VALUES (" + id + ",'" + title + "','" + author + "'," + year + "," + pages
                    + ")";
            executeQuery(query);
            showBooks();
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error", "Invalid input for ID, Year, or Pages.");
        }
    }

    private void updateRecord() {
        String query = "UPDATE  books SET title  = '" + tfTitle.getText() + "', author = '" + tfAuthor.getText()
                + "', year = " +
                tfYear.getText() + ", pages = " + tfPages.getText() + " WHERE id = " + tfid.getText() + "";
        executeQuery(query);
        showBooks();
    }

    private void deleteButton() {
        String query = "DELETE FROM books WHERE id =" + tfid.getText() + "";
        executeQuery(query);
        showBooks();
    }

    private void executeQuery(String query) {
        Connection conn = getConnection();
        Statement st;
        try {
            st = conn.createStatement();
            st.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
