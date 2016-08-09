package application;

import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import parsing.FileType;

import java.io.File;
import java.util.List;

public class Main extends Application {

   private static Logger logger = Logger.getLogger(Main.class);

   private DynamicTable dynamicTable = new DynamicTable();

   @Override
   public void start(Stage primaryStage) {
	  logger.info("Start start");
	  try {
		 primaryStage.setTitle("Apka");

		 // TableView Part
		 final TableView<ObservableList<StringProperty>> table = new TableView<>();
		 final CheckBox headerCheckBox = new CheckBox("Data has header line");
		 HBox controls = new HBox();
		 controls.getChildren().addAll(headerCheckBox);
		 HBox.setHgrow(headerCheckBox, Priority.NEVER);

		 // FileBrowser Part
		 final FileChooser fileChooser = new FileChooser();

		 final Button openButton = new Button("Open a File");
		 final Button openMultipleButton = new Button("Open multiple Files");

		 openButton.setOnAction(e -> {
			ExtensionFilter csvFilter = new ExtensionFilter("CSV Files", "*.csv");
			ExtensionFilter xmlFilter = new ExtensionFilter("XML Files", "*.xml");
			ExtensionFilter jsonFilter = new ExtensionFilter("JSON Files", "*.json");
			fileChooser.getExtensionFilters().add(csvFilter);
			fileChooser.getExtensionFilters().add(xmlFilter);
			fileChooser.getExtensionFilters().add(jsonFilter);
			File file = fileChooser.showOpenDialog(primaryStage);
			if (file != null) {
			   if (fileChooser.getSelectedExtensionFilter().getDescription().equals("CSV Files")) {
				  dynamicTable.populateTable(table, file, FileType.CSV, headerCheckBox.isSelected());
			   }
			   if (fileChooser.getSelectedExtensionFilter().getDescription().equals("JSON Files")) {
				  // TODO
			   }
			}
		 });

		 // Póki co nic nie robi
		 openMultipleButton.setOnAction(e -> {
			List<File> list = fileChooser.showOpenMultipleDialog(primaryStage);
			if (list != null) {
			   for (File file : list) {
				  // TODO
			   }
			}
		 });

		 final GridPane inputGridPane = new GridPane();

		 GridPane.setConstraints(openButton, 0, 0);
		 GridPane.setConstraints(openMultipleButton, 1, 0);
		 inputGridPane.setHgap(6);
		 inputGridPane.setVgap(6);
		 inputGridPane.getChildren().addAll(openButton, openMultipleButton);

		 // Main Part
		 final Pane rootGroup = new VBox(12);
		 rootGroup.getChildren().addAll(inputGridPane);
		 rootGroup.setPadding(new Insets(12, 12, 12, 12));
		 rootGroup.getChildren().addAll(controls);
		 rootGroup.getChildren().addAll(table);

		 Scene scene = new Scene(rootGroup, 400, 400);
		 scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
		 primaryStage.setScene(scene);
		 primaryStage.show();

	  } catch (Exception e) {
		 logger.error("Error in start", e);
	  }

	  logger.info("Finish start");
   }

   public static void main(String[] args) {
	  launch(args);
   }
}
