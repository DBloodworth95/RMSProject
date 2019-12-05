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

    /*
        Global declarations of Table & Columns.

     */
    private TableView tableView = new TableView();
    private TableColumn userID = new TableColumn("User ID");
    private TableColumn firstNameCol = new TableColumn("First Name");
    private TableColumn lastNameCol = new TableColumn("Last Name");


    //Launch application (Execute code in start(); method.
    public static void main(String[] args) {
        launch(args);
    }
    //Start(); Deals with GUI elements such as buttons, panes etc.
    @Override
    public void start(Stage primaryStage) throws SQLException {
        //Various elements being added to the GUI (Pretty self-explanatory).
        primaryStage.setTitle("JavaFX - Shitty example");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("Welcome to my example of creating, updating " + "\n" +
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
        //A list of Student which has been populated by called getTable();
        List<Student> entries = getTable();
        //populateTable(); populates a tableview which takes the List of Student as a argument.
        populateTable(entries);
        //Add the table to the Grid Pane.
        grid.add(tableView, 1, 5);
        //Shows the scene which contains the grid pane.
        primaryStage.setScene(scene);
        primaryStage.show();

        /*
            *Example of an EventListener for a button in the form of a Lambda expression.
            *Listens for a click event which calls updateEntry(); if true.
         */
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
        //Example of a EventListener in the form of an annonymous function.
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    //Login details for mySQL database stored in variables.
                    String dbUrl = "jdbc:mysql://localhost:3306/test";
                    String username = "root";
                    String password = "root";
                    //Store query as a String variable.
                    String query = "INSERT INTO person (firstname, lastname) VALUES (?,?)";
                    //Create the connection which passes the login details.
                    Connection myConnection = DriverManager.getConnection(dbUrl, username, password);
                    //Create Prepared Statement which passes the query String.
                    PreparedStatement preparedStatement = myConnection.prepareStatement(query);
                    //Sets the ?,? values in the query.
                    preparedStatement.setString(1, firstNameTextField.getText());
                    preparedStatement.setString(2, lastNameTextField.getText());
                    //Execute the statement.
                    preparedStatement.executeUpdate();

                    System.out.println("Student added!");
                    //Clears text fields and updates table contents.
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

    /*
        *getTable(); Connects to the Database, and essentially fetches all of the data from person table.
        * This also illustrates an example of a statement that hasn't been prepared (Don't do this).
     */
    public List<Student> getTable() throws SQLException {
        //Connect to database.
        String dbUrl = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "root";
        //Create connection.
        Connection myConnection = DriverManager.getConnection(dbUrl, username, password);
        //Create statement.
        Statement myStatement = myConnection.createStatement();
        //Store the query in a ResultSet.
        ResultSet myResultSet = myStatement.executeQuery("Select * from  person");
        //A list of Student stored as an entries.
        List<Student> entries = new ArrayList<>();
        //Loops through each row of the person table in the database and fetches each column value.
        while (myResultSet.next()) {
            int userID = Integer.parseInt(myResultSet.getString("id"));
            String firstName = myResultSet.getString("firstName");
            String lastName = myResultSet.getString("lastName");
            //Adds each row into the entries list as a Student type.
            entries.add(new Student(userID, firstName, lastName));
        }

        //Return the list of Student.
        return entries;
    }
    /*
        *populateTable() essentially populates our tableview with our list of Student which was populated using getTable();
     */
    public TableView populateTable(List<Student> entries) {

        //Defines which data is stored in each column (user ID column stores the user ID of a Student).
        userID.setCellValueFactory(new PropertyValueFactory<Student, Integer>("userID"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Student, String>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Student, String>("lastName"));
        //Set these columns editable for the purpose of updating entries.
        lastNameCol.setEditable(true);
        firstNameCol.setEditable(true);
        //Setting columns widths so table doesn't look retarded.
        firstNameCol.setMinWidth(200.00);
        lastNameCol.setMinWidth(200.00);
        //ObservableList is a list which can have listeners attached to track changes, we create a list of rows.
        ObservableList<Student> rows = FXCollections.observableArrayList();
        //For every student in the entries list, add it as a row (An ObservableList so we can track changes of each row).
        for (Student student : entries) {
            rows.add(student);

        }
        //Sets all items in the table as ObservableList.
        tableView.setItems(rows);
        //Adds the columns to the table.
        tableView.getColumns().addAll(userID,firstNameCol, lastNameCol);
        //More size settings for the table.
        tableView.setMinSize(200, 200);
        tableView.setMaxSize(475, 400);
        tableView.setEditable(true);
        //Allows you to select rows (so we can edit them). Only one can be selected at a time.
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //Set these columns as TextFields so we can edit them.
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        //Return the table once all of this has been done.
        return tableView;
    }

    /*
        *deleteEntry(); is called to remove a row from the table.
        * It essentially fetches the row that has been selected. Reads the data from it
        * and removes it from the table as well as deleting the row that matches the same first name from the database
        * table. (Need to change this so it uses the userID as the ref not the firstname lol)
     */
    public void deleteEntry(ActionEvent e) throws SQLException {
        //Get the selected items and store this as a variable.
        Object selectedItems = tableView.getSelectionModel().getSelectedItems().get(0);
        //Separates the selected row as columns and stores the first column as a variable.
        String firstColumn = selectedItems.toString().split(",")[0];
        //Random print for testing purposes.
        System.out.print(selectedItems);
        //Connect to database blah blah you've seen this shit previously.
        String dbUrl = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "root";
        String query = ("DELETE FROM person WHERE firstname ='" + firstColumn + "'");
        System.out.println(query);

        Connection myConnection = DriverManager.getConnection(dbUrl, username, password);
        PreparedStatement preparedStatement = myConnection.prepareStatement(query);

        preparedStatement.execute();
        //Defines all rows and a single row as ObservableList.
        ObservableList<Student> allRows, singleRow;
        //Stores all rows as a variable.
        allRows = tableView.getItems();
        //Stores a selected row as a variable.
        singleRow = tableView.getSelectionModel().getSelectedItems();
        //For each row that has been selected, remove it.
        singleRow.forEach(allRows::remove);

        //It is important to remove from the table first, otherwise it will error as it is unable to check what row has been selected as it's already been deleted.
    }
    /*
        *updateEntry(); is called when the "Save" button is clicked.
        *This method basically iterates through each row in the tableview, fetches the data and checks each row with each row in the mySQL table, using the userID as a reference.
        *It then updates the rows in the table if needed.
     */
    public void updateEntry(ActionEvent e) throws SQLException {
        System.out.println("Update Pressed!");
        //Creates an empty student for us to populate with the new values.
        Student student = new Student();
        //Creates an ArrayList for the Students in the table.
        List <List<String>> arrList = new ArrayList<>();
        //Loop iterates through each row.
        for (int i = 0; i < tableView.getItems().size(); i++) {
            //Store each row as type Student. Each row is stored as an index which is why we call get(i).
            student = (Student) tableView.getItems().get(i);
            arrList.add(new ArrayList<>());
            //Add the Students first and last name to row in the list.
            arrList.get(i).add(student.getFirstName());
            arrList.get(i).add(student.getLastName());
            //These basically listen for event changes (a first/last name being edited), reads the new values and updates the attributes (first/last name) for the Student.
            firstNameCol.setOnEditCommit(
                    (EventHandler<TableColumn.CellEditEvent<Student, String>>) studentStringCellEditEvent -> ((Student) studentStringCellEditEvent.getTableView().getItems().get(studentStringCellEditEvent.getTablePosition().getRow())).setFirstName(studentStringCellEditEvent.getNewValue()));
            lastNameCol.setOnEditCommit(
                    (EventHandler<TableColumn.CellEditEvent<Student, String>>) studentStringCellEditEvent -> ((Student) studentStringCellEditEvent.getTableView().getItems().get(studentStringCellEditEvent.getTablePosition().getRow())).setLastName(studentStringCellEditEvent.getNewValue()));
            //More database connection stuff and storing gets as variables so we get the returned String from the get call.
            int getPersonID = student.getUserID();
            String getFirstName = student.getFirstName();
            String getLastName = student.getLastName();
            String dbUrl = "jdbc:mysql://localhost:3306/test";
            String username = "root";
            String password = "root";
            //Update person table and set first/last name to the first/last name we have grabbed from the gets where the ID is the same as the ID we fetched from the get.
            String query = ("UPDATE person SET firstname='" + getFirstName + "', lastname='" + getLastName + "' WHERE id='" + getPersonID + "'");
            Connection myConnection = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement preparedStatement = myConnection.prepareStatement(query);

            preparedStatement.execute();

        }
        //Prints for testing.
        for (int i = 0; i < arrList.size(); i++) {
            for (int j = 0; j < arrList.get(i).size(); j++ ) {
                System.out.println(arrList.get(i).get(j));
                System.out.println(firstNameCol.getCellObservableValue(i));
            }
        }
    }


}








