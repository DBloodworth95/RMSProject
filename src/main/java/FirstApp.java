import java.sql.*;

class FirstApp {



    public static void insert(Connection connection, String tname, String firstname, String lastname) {
        try{
            String dbUrl="jdbc:mysql://localhost:3306/test";
            String username="root";
            String password="root";
            String query = "INSERT INTO " + tname+" (firstname, lastname) VALUES (?,?)";

            Connection myConnection=DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement preparedStatement = myConnection.prepareStatement(query);
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, lastname);
            preparedStatement.executeUpdate();

            System.out.println("Student added: " + firstname + " " + lastname);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) throws Exception {
            String dbUrl="jdbc:mysql://localhost:3306/test";
            String username="root";
            String password="root";
            //get a connection(step 1)
            Connection myConnection=DriverManager.getConnection(dbUrl, username, password);
            //Create statement object(Step 2)
            Statement myStatement=myConnection.createStatement();
            //execute query(step 3)
            String students = "students";
            ResultSet myResultSet=myStatement.executeQuery("Select * from  " + students);
            //process the result set.
            while(myResultSet.next()){
                System.out.println("Student full name: "+ myResultSet.getString("firstname")+" " +myResultSet.getString("lastname"));
            }
        insert(myConnection, "students", "String", "string");
        
        }
    }


