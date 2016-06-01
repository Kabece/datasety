package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


public class Main extends Application {

	private Desktop desktop = Desktop.getDesktop();

	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Apka");

			// FileBrowser Part Start
			final FileChooser fileChooser = new FileChooser();

	        final Button openButton = new Button("Open a File");
	        final Button openMultipleButton = new Button("Open multiple Files");

	        openButton.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    File file = fileChooser.showOpenDialog(primaryStage);
	                    if (file != null) {
	                        openFile(file);
	                    }
	                }
	            });

	        openMultipleButton.setOnAction(
	            new EventHandler<ActionEvent>() {
	                @Override
	                public void handle(final ActionEvent e) {
	                    List<File> list =
	                        fileChooser.showOpenMultipleDialog(primaryStage);
	                    if (list != null) {
	                        for (File file : list) {
	                            openFile(file);
	                        }
	                    }
	                }
	            });


	        final GridPane inputGridPane = new GridPane();

	        GridPane.setConstraints(openButton, 0, 0);
	        GridPane.setConstraints(openMultipleButton, 1, 0);
	        inputGridPane.setHgap(6);
	        inputGridPane.setVgap(6);
	        inputGridPane.getChildren().addAll(openButton, openMultipleButton);

	        final Pane rootGroup = new VBox(12);
	        rootGroup.getChildren().addAll(inputGridPane);
	        rootGroup.setPadding(new Insets(12, 12, 12, 12));

			// FileBrowser Part Finish

	        Scene scene = new Scene(rootGroup,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                Main.class.getName()).log(
                    Level.SEVERE, null, ex
                );
        }
    }
}
