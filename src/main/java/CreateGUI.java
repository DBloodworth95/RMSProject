import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public class CreateGUI extends Application {

    private TableView tableView = new TableView();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws SQLException {


        primaryStage.setTitle("JavaFX - Shitty example");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("Welcome to my shitty example of creating, updating " + "\n" +
                " and deleting records!");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label firstName = new Label("First Name:");
        grid.add(firstName, 0, 1);

        TextField firstNameTextField = new TextField();
        grid.add(firstNameTextField, 1, 1);

        Label lastName = new Label("Last Name:");
        grid.add(lastName, 0, 2);



        TextField lastNameTextField = new TextField();
        grid.add(lastNameTextField, 1, 2);
        Button btn = new Button("Add");
        Button delbtn = new Button("Delete");
        Button savbtn = new Button("Save Changes");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        hbBtn.getChildren().add(delbtn);
        hbBtn.getChildren().add(savbtn);
        grid.add(hbBtn, 1, 4);
        Scene scene = new Scene(grid, 700, 500);
        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        List<Student> entries = getTable();
        populateTable(entries);
        grid.add(tableView,1,5);

        primaryStage.setScene(scene);
        primaryStage.show();

        savbtn.setOnAction(e -> {
            try {
                updateEntry(e);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        delbtn.setOnAction(e -> {
            try {
                deleteEntry(e);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    String dbUrl = "jdbc:mysql://localhost:3306/test";
                    String username = "root";
                    String password = "root";
                    String query = "INSERT INTO person (firstname, lastname) VALUES (?,?)";

                    Connection myConnection = DriverManager.getConnection(dbUrl, username, password);
                    PreparedStatement preparedStatement = myConnection.prepareStatement(query);
                    preparedStatement.setString(1, firstNameTextField.getText());
                    preparedStatement.setString(2, lastNameTextField.getText());
                    preparedStatement.executeUpdate();

                    System.out.println("Student added!");

                    firstNameTextField.clear();
                    lastNameTextField.clear();
                    tableView.getItems().clear();
                    tableView.getItems().addAll(getTable());



                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Details added to database");

            }
        });

    }



    public List<Student> getTable() throws SQLException {
        String dbUrl="jdbc:mysql://localhost:3306/test";
        String username="root";
        String password="root";
        Connection myConnection=DriverManager.getConnection(dbUrl, username, password);
        Statement myStatement=myConnection.createStatement();

        ResultSet myResultSet=myStatement.executeQuery("Select * from  person");

        List<Student> entries = new ArrayList<>();

        while (myResultSet.next()) {
            String firstName = myResultSet.getString("firstName");
            String lastName = myResultSet.getString("lastName");

            entries.add(new Student(firstName, lastName));
        }


        return entries;
    }

    public TableView populateTable(List<Student> entries) {


        TableColumn firstNameCol = new TableColumn("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Student,String>("firstName"));
        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Student,String>("lastName"));
        lastNameCol.setEditable(true);
        firstNameCol.setEditable(true);
        firstNameCol.setMinWidth(200.00);
        lastNameCol.setMinWidth(200.00);

        ObservableList<Student> rows = FXCollections.observableArrayList();
        for (Student student: entries) {
            rows.add(student);

        }
        tableView.setItems(rows);
        tableView.getColumns().addAll(firstNameCol, lastNameCol);
        tableView.setMinSize(200,200);
        tableView.setMaxSize(400,400);
        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());




        return tableView;
    }




    public void deleteEntry(ActionEvent e) throws SQLException{

        Object selectedItems = tableView.getSelectionModel().getSelectedItems().get(0);
        String firstColumn = selectedItems.toString().split(",")[0];
        System.out.print(selectedItems);
        String dbUrl = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "root";
        String query = ("DELETE FROM person WHERE firstname ='"+firstColumn + "'");
        System.out.println(query);

        Connection myConnection = DriverManager.getConnection(dbUrl, username, password);
        PreparedStatement preparedStatement = myConnection.prepareStatement(query);

        preparedStatement.execute();
        ObservableList<Student> allRows, singleRow;
        allRows = tableView.getItems();
        singleRow = tableView.getSelectionModel().getSelectedItems();
        singleRow.forEach(allRows::remove);

    }

    public void updateEntry(ActionEvent e) throws SQLException{
        System.out.println("Update Pressed!");
    }






}








