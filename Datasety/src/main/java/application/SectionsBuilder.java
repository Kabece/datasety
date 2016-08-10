package application;

import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.OperatorType;
import model.PatternType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.FileType;

import java.io.File;
import java.util.List;

/**
 * Created by kczurylo on 2016-08-09.
 * Klasa pomocnicza, służąca do tworzenia komponentów widoku
 */
@SuppressWarnings({ "unchecked", "WeakerAccess" })
public class SectionsBuilder {

	private static Logger logger = LogManager.getLogger(SectionsBuilder.class.getName());
	private LogicSentence logicSentence;
	private Analyzer analyzer;

	public SectionsBuilder() {
		this.analyzer = new Analyzer();
		this.logicSentence = new LogicSentence();
	}

	/**
	 * Tworzy jeden rząd komponentów odpowiedzialnych za logike temporalną.
	 *
	 * @return GridPane zawierający komponenty.
	 */
	public GridPane createLogicControlsSection() {
		logger.info("Start createLogicControlsSection");

		// Pattern
		final Label patternLabel = new Label("Wzorzec: ");
		final ComboBox patternComboBox = new ComboBox();
		patternComboBox.getItems().setAll(PatternType.values());
		patternComboBox.setPromptText("Wzorzec");
		patternComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process start, patternComboBox value has changed from = {} to = {}", oldValue, newValue);
			logicSentence.setChosenPattern((PatternType) newValue);
		});

		// Variable
		final Label variableLabel = new Label("Zmienna: ");
		final ComboBox variableComboBox = new ComboBox();
		variableComboBox.setPromptText("Zmienna");
		variableComboBox.setOnMouseClicked(event -> {
			if (variableComboBox.getItems().isEmpty()) {
				Alert noDataSpecifiedAlert = new Alert(Alert.AlertType.WARNING);
				noDataSpecifiedAlert.setTitle("Uwaga!");
				noDataSpecifiedAlert.setHeaderText("Brak wybranych danych!");
				noDataSpecifiedAlert.setContentText("Wybierz poprawny z plik z danymi i poczekaj aż się załaduje.");
				noDataSpecifiedAlert.showAndWait();
			}
		});
		variableComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicControlSection, variableComboBox value has changed from = {} to = {}", oldValue, newValue);
			logicSentence.setChosenVariable((String) newValue);
		});
		LogicSentence.getVariableList().addListener((ListChangeListener<String>) c -> {
			logger.debug("Process createLogicControlsSection, variableList changed");
			// TODO Dodać sprawdzanie czy nie null / może optional?
			variableComboBox.getItems().setAll(LogicSentence.getVariableList());
		});


		// Operator
		final Label operatorLabel = new Label("Operator: ");
		final ComboBox operatorComboBox = new ComboBox();
		operatorComboBox.getItems().setAll(OperatorType.values());
		operatorComboBox.setPromptText("Operator");
		operatorComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicControlSection, operatorComboBox value has changed from = {} to = {}", oldValue, newValue);
			logicSentence.setChosenOperator((OperatorType) newValue);
		}));

		// Value
		final Label valueLabel = new Label("Wartość: ");
		final TextField valueTextField = new TextField();
		valueTextField.setPromptText("Wpisz wartość");
		valueTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicControlSection, valueTextField value has been entered: {}", valueTextField.textProperty().getValueSafe());
			logicSentence.setChosenValue(valueTextField.textProperty().getValueSafe());
		});

		// Analyze
		final Button analyzeButton = new Button("Analizuj");
		analyzeButton.setDefaultButton(true);
		analyzeButton.setOnAction(event -> {
			logger.debug("Process createLogicControlSection, analyzeButton fired!");
			logger.debug(analyzer.getDataMap());
		});

		final GridPane controlsGridPane = new GridPane();
		controlsGridPane.setHgap(3);
		controlsGridPane.setVgap(3);
		GridPane.setConstraints(patternLabel, 0, 0);
		GridPane.setConstraints(patternComboBox, 0, 1);
		GridPane.setConstraints(variableLabel, 1, 0);
		GridPane.setConstraints(variableComboBox, 1, 1);
		GridPane.setConstraints(operatorLabel, 2, 0);
		GridPane.setConstraints(operatorComboBox, 2, 1);
		GridPane.setConstraints(valueLabel, 3, 0);
		GridPane.setConstraints(valueTextField, 3, 1);
		GridPane.setConstraints(analyzeButton, 0, 10);
		controlsGridPane.getChildren().addAll(patternLabel, patternComboBox, variableLabel, variableComboBox, operatorLabel, operatorComboBox, valueLabel, valueTextField, analyzeButton);

		logger.info("Finish createLogicControlsSection");
		return controlsGridPane;
	}

	/**
	 * Tworzy komponenty odpowiedzialne za wybór pliku.
	 *
	 * @param primaryStage Główna scena aplikacji.
	 * @param tableView    Komponent tabeli.
	 * @return HBox zawierający komponenty.
	 */
	public HBox createDataInputSection(Stage primaryStage, TableView<ObservableList<StringProperty>> tableView) {
		logger.info("Start createDataInputSection");

		final FileChooser fileChooser = new FileChooser();
		final Button openButton = new Button("Otwórz plik");
		final Button openMultipleButton = new Button("Otwórz wiele plików");
		final CheckBox headerCheckBox = new CheckBox("Dane mają nagłówek");
		headerCheckBox.setSelected(true);

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
					DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.CSV, headerCheckBox.isSelected(), analyzer);
					dynamicTable.populateTable();
					for (ObservableList<StringProperty> o : tableView.getItems()) {
						for (StringProperty s : o) {
							logger.debug(s);
						}
					}
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

		logger.info("Finish createDataInputSection");
		return fileInputHBox;
	}
}
