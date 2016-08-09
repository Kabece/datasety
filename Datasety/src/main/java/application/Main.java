package application;

import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {

	private static Logger logger = LogManager.getLogger(Main.class.getName());
	private TableView<ObservableList<StringProperty>> tableView;
	private Controls controls;

	public static void main(String[] args) {
		logger.info("Start main");
		launch(args);
		logger.info("Finish main");
	}

	@Override
	public void init() throws Exception {
		logger.info("Start init");

		controls = new Controls();
		tableView = new TableView<>();

		logger.info("Finish init");
	}

	@Override
	public void start(Stage primaryStage) {
		logger.info("Start start");
		try {
			primaryStage.setTitle("HorseApp");

			SectionsBuilder sectionsBuilder = new SectionsBuilder();

			final HBox fileInputHBox = sectionsBuilder.createDataInputSection(primaryStage, tableView);
			final GridPane controlsGridPane = sectionsBuilder.createLogicControlsSection();

			final Pane rootGroup = new VBox(12);
			rootGroup.setPadding(new Insets(12, 12, 12, 12));
			rootGroup.getChildren().addAll(fileInputHBox, tableView, controlsGridPane);

			Scene scene = new Scene(rootGroup, 800, 600);
			scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			logger.error("Error in start", e);
		}

		logger.info("Finish start");
	}

}
