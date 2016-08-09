package application;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parsing.FileType;

import java.io.File;
import java.util.List;

/**
 * Created by kczurylo on 2016-08-09.
 * Klasa pomocnicza, służąca do tworzenia komponentów widoku
 */
public class MainHelper {

	private static Logger logger = LogManager.getLogger(MainHelper.class.getName());
	private DynamicTable dynamicTable;

	public MainHelper() {
		this.dynamicTable = new DynamicTable();
	}

	/**
	 * Tworzy jeden rząd komponentów odpowiedzialnych za logike temporalną.
	 *
	 * @return GridPane zawierający komponenty.
	 */
	public GridPane createLogicControlsSection() {
		// Logic Controls Part
		final Label patternLabel = new Label("Wzorzec: ");
		final ComboBox patternComboBox = new ComboBox();
		patternComboBox.getItems().setAll(PatternType.values());
		patternComboBox.setPromptText("Wzorzec");
		patternComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process start, patternComboBox value has changed from = {} to = {}", oldValue, newValue);
			Label test = new Label("TEST");
			GridPane.setConstraints(test, 3, 3);
		});

		final GridPane controlsGridPane = new GridPane();
		GridPane.setConstraints(patternLabel, 0, 0);
		GridPane.setConstraints(patternComboBox, 0, 1);
		controlsGridPane.getChildren().addAll(patternLabel, patternComboBox);

		return controlsGridPane;
	}

	/**
	 * Tworzy komponenty odpowiedzialne za wybór pliku.
	 *
	 * @param primaryStage Główna scena aplikacji.
	 * @param tableView    Komponent tabeli.
	 * @return HBox zawierający komponenty.
	 */
	public HBox createFileInputSection(Stage primaryStage, TableView<ObservableList<StringProperty>> tableView) {
		final FileChooser fileChooser = new FileChooser();
		final Button openButton = new Button("Otwórz plik");
		final Button openMultipleButton = new Button("Otwórz wiele plików");
		final CheckBox headerCheckBox = new CheckBox("Dane mają nagłówek");

		openButton.setOnAction(e -> {
			FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
			FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML Files", "*.xml");
			FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON Files", "*.json");
			fileChooser.getExtensionFilters().add(csvFilter);
			fileChooser.getExtensionFilters().add(xmlFilter);
			fileChooser.getExtensionFilters().add(jsonFilter);
			File file = fileChooser.showOpenDialog(primaryStage);
			if (file != null) {
				if (fileChooser.getSelectedExtensionFilter().getDescription().equals("CSV Files")) {
					dynamicTable.populateTable(tableView, file, FileType.CSV, headerCheckBox.isSelected());
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

		final HBox fileInputHBox = new HBox(3); // spacing = 5
		fileInputHBox.getChildren().addAll(openButton, openMultipleButton, headerCheckBox);
		HBox.setHgrow(fileInputHBox, Priority.NEVER);

		return fileInputHBox;
	}
}
