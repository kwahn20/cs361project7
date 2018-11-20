/*
 * File: Main.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 7
 * Date: November 2, 2018
 * ---------------------------
 * Edited From: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou
 * Project 5
 * Date: October 12, 2018
 *
 */

package proj9AhnDeGrawHangSlager;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.lang.Exception;

/**
 * This Main class inherits the Application class from JavaFX, used to implement a
 * basic IDE in which the user can edit/save files with as many tabs as they prefer.
 *
 *
 * @author  Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou (Project 5)
 * @version 5.0
 * @since   09-30-2018
 */
public class Main extends Application{
    /**
     * Creates a scene containing a simple IDE. The IDE has tab with CodeAreas
     * and two menus： File and Edit
     *
     * @param stage A background window that contains the scene
     */
    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("resources/Main.fxml"));
        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Project 09");
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Launches an instance of class Main
     */
    public static void main(String args[]){
        launch(args);
    }
}