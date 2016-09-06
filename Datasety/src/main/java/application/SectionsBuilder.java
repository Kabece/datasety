package application;

import application.implementations.analyzer.CheckAnalyzer;
import application.implementations.analyzer.ShowAnalyzer;
import application.implementations.logicSentence.SingleLogicSentence;
import application.implementations.logicSentence.ExtendedLogicSentence;
import application.interfaces.analyzer.Analyzer;
import application.interfaces.logicSentence.LogicSentence;
import enums.AnalyzerWorkType;
import enums.FileType;
import enums.OperatorType;
import enums.PatternType;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

import static enums.AnalyzerWorkType.CHECK;
import static enums.AnalyzerWorkType.SHOW;

/**
 * Created by kczurylo on 2016-08-09.
 * <p>
 *    Klasa pomocnicza, służąca do tworzenia komponentów widoku.
 * </p>
 */
@SuppressWarnings({ "unchecked", "WeakerAccess" })
// TODO Podzielić to jeszcze bardziej - na inicjalizację, dodawanie listenerów itd.
public class SectionsBuilder {

	private static Logger logger = LogManager.getLogger(SectionsBuilder.class.getName());

	private IntegerProperty currentLogicSentencesRowNumber;
	private Map<String,LogicSentence> logicSentencesMap;
	private Analyzer analyzer;
	private String[] dataVariables;

	public SectionsBuilder() {
		this.logicSentencesMap = new HashMap<>();
		this.currentLogicSentencesRowNumber = new SimpleIntegerProperty(0);
	}

	/**
	 * Tworzy sekcję odpowiedzialną za startowanie analizy
	 *
	 * @return GridPane zawierający komponenty
	 */
	public GridPane createAnalyzerSection() {
		logger.info("Start createAnalyzerSection");

		// Analyzer Work Type
		final Label analyzerWorkTypeLabel = new Label("Tryb pracy analizatora:");
		final ComboBox analyzerWorkTypeComboBox = new ComboBox();

        // FIXME: fajnie byloby wrzucic wszystkie pola jakas metodka zmiast wymieniac
        analyzerWorkTypeComboBox.getItems().setAll(AnalyzerWorkType.CHECK, AnalyzerWorkType.SHOW);

        // FIXME: nie koniecznie fixme, ale tu sie ustawia domyslny tryb analizatora
        analyzerWorkTypeComboBox.setValue(AnalyzerWorkType.CHECK);
        analyzer = new CheckAnalyzer();

        analyzerWorkTypeComboBox.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					logger.debug("Process createLogicControlSection, analyzerWorkTypeComboBox has changed from = {} to = {}",
							oldValue, newValue);

					switch(oldValue.toString()) { 
						case CHECK:
							analyzer = new CheckAnalyzer();
							break;
						case SHOW:
							analyzer = new ShowAnalyzer();
                            break;
						default:
                            logger.error("Something gone terribly wrong and non-existing analyzer mode was chosen!");
					}

				});

		// Result Indicator
		final Label resultIndicatorLabel = new Label("Wynik: ");
		final Circle resultIndicatorCircle = new Circle();
		resultIndicatorCircle.setRadius(15.0f);
		resultIndicatorCircle.setFill(Color.DARKGRAY);

		// Analyzer
		final Button analyzeButton = new Button("Analizuj");


		analyzeButton.disableProperty().bind(new BooleanBinding(){
			{
				bind(currentLogicSentencesRowNumber);
			}

			@Override
			protected boolean computeValue() {
				return currentLogicSentencesRowNumber.get() < 1;
			}
		});

		analyzeButton.setDefaultButton(true);

		analyzeButton.setOnAction(event -> {
			resultIndicatorCircle.setFill(Color.DARKGRAY);
			logger.debug("Process createLogicControlSection, analyzeButton fired!");
			if (checkIfLogicSentencesAreComplete() && analyzer.isReady()) {
				logger.trace("Process createLogicControlSection, analyzer data = {}", analyzer.getDataMap());

                analyzer.setLogicSentences(logicSentencesMap);

				if (analyzer.analyzeList()) {
					resultIndicatorCircle.setFill(Color.FORESTGREEN);
				} else {
					resultIndicatorCircle.setFill(Color.MAROON);
				}

			} else {
				Alert analyzingNotReadyAlert = new Alert(Alert.AlertType.WARNING);
				analyzingNotReadyAlert.setTitle("Uwaga!");
				analyzingNotReadyAlert.setHeaderText("Analizator nie jest gotowy!");
				analyzingNotReadyAlert.setContentText("Poprawnie skonfiguruj zdanie logiczne oraz wybierz tryb pracy analizatora.");
				analyzingNotReadyAlert.showAndWait();
			}
		});

		final GridPane analyzerGridPane = new GridPane();
		analyzerGridPane.setHgap(3);
		analyzerGridPane.setVgap(3);
		GridPane.setConstraints(analyzerWorkTypeLabel, 0, 0);
		GridPane.setConstraints(analyzerWorkTypeComboBox, 0, 1);
		GridPane.setConstraints(analyzeButton, 1, 1);
		GridPane.setConstraints(resultIndicatorLabel, 0, 3);
		GridPane.setConstraints(resultIndicatorCircle, 0, 4);
		analyzerGridPane.getChildren().addAll(analyzerWorkTypeLabel, analyzerWorkTypeComboBox, analyzeButton, resultIndicatorLabel, resultIndicatorCircle);

		logger.info("Finish createAnalyzerSection");
		return analyzerGridPane;
	}

	public GridPane initializeLogicSentenceSection() {
		logger.info("Initializing logic sentence section.");

		final Button nextRowButton = new Button("Dodaj kolejne zdanie");
		nextRowButton.setOnMouseClicked(event -> {
			logger.debug("Process createLogicSentenceSection, nextRowButton clicked! currentLogicSentencesRowsNumber={}", currentLogicSentencesRowNumber);
			if (currentLogicSentencesRowNumber.get() < Config.MAX_LOGIC_SENTENCES_ROWS) {
				addNextLogicSentenceRow();
			}
		});

		nextRowButton.disableProperty().bind(new BooleanBinding() {
			{
				bind(currentLogicSentencesRowNumber);
			}
			@Override
			protected boolean computeValue() {
				return currentLogicSentencesRowNumber.get() >= Config.MAX_LOGIC_SENTENCES_ROWS;
			}
		});

		final GridPane controlsGridPane = new GridPane();
		controlsGridPane.setHgap(3);
		controlsGridPane.setVgap(3);
		GridPane.setConstraints(nextRowButton, 0, 2);
		controlsGridPane.getChildren()
				.addAll(nextRowButton);
		logger.info("Logic sentence section initialized sucessfully!");
		return controlsGridPane;
	}

	/**
	 * Tworzy jeden rząd komponentów odpowiedzialnych za logike temporalną.
	 *
	 * @return GridPane zawierający komponenty.
	 */
	public GridPane createLogicSentenceSection() {
		logger.info("Start createLogicSentenceSection");
		String logicSentenceId = UUID.randomUUID().toString();
		ObservableList<String> observableList = FXCollections.observableArrayList();
		observableList.addAll(dataVariables);
		logicSentencesMap.put(logicSentenceId, new SingleLogicSentence(PatternType.values()[0], observableList));


		// Pattern
		final Label patternLabel = new Label("Wzorzec: ");
		final ComboBox patternComboBox = new ComboBox();
		patternComboBox.getItems().setAll(PatternType.values());
		patternComboBox.setPromptText("Wzorzec");


		// Variable
		final Label variableLabel = new Label("Zmienna: ");
		final ComboBox variableComboBox = new ComboBox();
		variableComboBox.setPromptText("Zmienna");
		variableComboBox.setOnMouseClicked(event -> {
			if (variableComboBox.getItems().isEmpty()) {
				if (logicSentencesMap.get(logicSentenceId).getVariableList().isEmpty()) {
					Alert noDataSpecifiedAlert = new Alert(Alert.AlertType.WARNING);
					noDataSpecifiedAlert.setTitle("Uwaga!");
					noDataSpecifiedAlert.setHeaderText("Brak wybranych danych!");
					noDataSpecifiedAlert.setContentText("Wybierz poprawny z plik z danymi i poczekaj aż się załaduje.");
					noDataSpecifiedAlert.showAndWait();
				} else {
					variableComboBox.getItems().setAll(logicSentencesMap.get(logicSentenceId).getVariableList());
				}
			}
		});
		variableComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicSentenceSection, variableComboBox value has changed from = {} to = {}",
					oldValue, newValue);
			logicSentencesMap.get(logicSentenceId).setChosenVariable((String) newValue);
		});
		logicSentencesMap.get(logicSentenceId).getVariableList().addListener((ListChangeListener<String>) c -> {
			logger.debug("Process createLogicSentenceSection, variableList changed");
			// TODO Dodać sprawdzanie czy nie null / może optional?
			variableComboBox.getItems().setAll(logicSentencesMap.get(logicSentenceId).getVariableList());
		});

		// Operator
		final Label operatorLabel = new Label("Operator: ");
		final ComboBox operatorComboBox = new ComboBox();
		operatorComboBox.getItems().setAll(OperatorType.values());
		operatorComboBox.setPromptText("Operator");
		operatorComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicSentenceSection, operatorComboBox value has changed from = {} to = {}",
					oldValue, newValue);
			logicSentencesMap.get(logicSentenceId).setChosenOperator((OperatorType) newValue);
		}));

		// Value
		final Label valueLabel = new Label("Wartość: ");
		final TextField valueTextField = new TextField();
		valueTextField.setPromptText("Wpisz wartość");
		valueTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicSentenceSection, valueTextField value has been entered: {}",
					valueTextField.textProperty().getValueSafe());
			logicSentencesMap.get(logicSentenceId).setChosenValue(valueTextField.textProperty().getValueSafe());
		});

		Button removeSection = new Button();
		removeSection.setText("Usuń zdanie");
		removeSection.setVisible(true);
		removeSection.setOnMouseClicked(event -> {
			logger.info("Removing logic sentence {}...", logicSentenceId);

			VBox group = (VBox) removeSection.getParent().getParent();
			group.getChildren().remove(removeSection.getParent());
			logicSentencesMap.remove(removeSection.getParent().getId());
			currentLogicSentencesRowNumber.set(currentLogicSentencesRowNumber.get() -1);

			logger.info("Successfully removed logic sentence! {}", logicSentenceId);
		});

		/* FOR BINARY PATTERNS */

		// Second Variable
		final Label secondVariableLabel = new Label("Zmienna 2: ");
		final ComboBox secondVariableComboBox = new ComboBox();
		secondVariableComboBox.setPromptText("Zmienna 2");
		secondVariableComboBox.setOnMouseClicked(event -> {
			if (secondVariableComboBox.getItems().isEmpty()) {
				// Tutaj rzutowanie - zawsze zlozone zdanie bedzie typu ExtendedLogicSentence
				if (logicSentencesMap.get(logicSentenceId).getNextSentencePart().getVariableList().isEmpty()) {
					Alert noDataSpecifiedAlert = new Alert(Alert.AlertType.WARNING);
					noDataSpecifiedAlert.setTitle("Uwaga!");
					noDataSpecifiedAlert.setHeaderText("Brak wybranych danych!");
					noDataSpecifiedAlert.setContentText("Wybierz poprawny z plik z danymi i poczekaj aż się załaduje.");
					noDataSpecifiedAlert.showAndWait();
				} else {
					secondVariableComboBox.getItems().setAll(logicSentencesMap.get(logicSentenceId).getNextSentencePart().getVariableList());
				}
			}
		});
		secondVariableComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicSentenceSection, variableComboBox value has changed from = {} to = {}",
					oldValue, newValue);
			logicSentencesMap.get(logicSentenceId).getNextSentencePart().setChosenVariable((String) newValue);
		});
		logicSentencesMap.get(logicSentenceId).getNextSentencePart().getVariableList().addListener((ListChangeListener<String>) c -> {
			logger.debug("Process createLogicSentenceSection, variableList changed");
			// TODO Dodać sprawdzanie czy nie null / może optional?
			secondVariableComboBox.getItems().setAll(logicSentencesMap.get(logicSentenceId).getNextSentencePart().getVariableList());
		});

		// Value
		final Label secondValueLabel = new Label("Wartość: ");
		final TextField secondValueTextField = new TextField();
		secondValueTextField.setPromptText("Wpisz wartość");
		secondValueTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicSentenceSection, valueTextField value has been entered: {}",
					secondValueTextField.textProperty().getValueSafe());
			logicSentencesMap.get(logicSentenceId).getNextSentencePart().setChosenValue(secondValueTextField.textProperty().getValueSafe());
		});

		// Second Operator
		final Label secondOperatorLabel = new Label("Operator 2: ");
		final ComboBox secondOperatorComboBox = new ComboBox();
		secondOperatorComboBox.getItems().setAll(OperatorType.values());
		secondOperatorComboBox.setPromptText("Operator 2");
		secondOperatorComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicSentenceSection, operatorComboBox value has changed from = {} to = {}",
					oldValue, newValue);
			logicSentencesMap.get(logicSentenceId).getNextSentencePart().setChosenOperator((OperatorType) newValue);
		}));

		secondVariableLabel.setVisible(false);
		secondVariableComboBox.setVisible(false);
		secondOperatorLabel.setVisible(false);
		secondOperatorComboBox.setVisible(false);
		secondValueLabel.setVisible(false);
		secondValueTextField.setVisible(false);

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
		GridPane.setConstraints(removeSection,5,1);
		GridPane.setConstraints(secondVariableLabel, 1, 2);
		GridPane.setConstraints(secondVariableComboBox, 1, 3);
		GridPane.setConstraints(secondOperatorLabel, 2 ,2);
		GridPane.setConstraints(secondOperatorComboBox, 2, 3);
		GridPane.setConstraints(secondValueLabel,3,2);
		GridPane.setConstraints(secondValueTextField,3,3);


		controlsGridPane.getChildren()
				.addAll(patternLabel, patternComboBox, variableLabel, variableComboBox, operatorLabel, operatorComboBox,
						valueLabel, valueTextField, removeSection, secondVariableLabel, secondVariableComboBox, secondOperatorLabel, secondOperatorComboBox,
						secondValueLabel, secondValueTextField);
		controlsGridPane.setId(logicSentenceId);
		logger.info("Finish createLogicSentenceSection");

		patternComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicSentenceSection, patternComboBox value has changed from = {} to = {}", oldValue, newValue);
			logicSentencesMap.get(logicSentenceId).setChosenPattern((PatternType) newValue);

			if(newValue.equals(PatternType.RESPONSIVENESS) || newValue.equals(PatternType.OBLIGATION) ) {

				logicSentencesMap.put(logicSentenceId, new ExtendedLogicSentence(new SingleLogicSentence((PatternType) newValue, observableList)));

				secondVariableLabel.setVisible(true);
				secondVariableComboBox.setVisible(true);
				secondOperatorLabel.setVisible(true);
				secondOperatorComboBox.setVisible(true);
				secondValueLabel.setVisible(true);
				secondValueTextField.setVisible(true);


			} else {

				logicSentencesMap.put(logicSentenceId, new SingleLogicSentence((PatternType) newValue, observableList));

				secondVariableLabel.setVisible(false);
				secondVariableComboBox.setVisible(false);
				secondOperatorLabel.setVisible(false);
				secondOperatorComboBox.setVisible(false);
				secondValueLabel.setVisible(false);
				secondValueTextField.setVisible(false);

			}

			variableComboBox.setValue(null);
			operatorComboBox.setValue(null);
			valueTextField.setText(null);
			secondVariableComboBox.setValue(null);
			secondOperatorComboBox.setValue(null);
			secondValueTextField.setText(null);

		});

		return controlsGridPane;
	}

	private void addNextLogicSentenceRow() {
		logger.info("Start addNextLogicSentenceRow");

		((Pane) Main.getTabs().getTabs().get(Main.getCurrentlySelectedTabIndex()).getContent()).getChildren().add(currentLogicSentencesRowNumber.get() + 2, createLogicSentenceSection());
		currentLogicSentencesRowNumber.set(currentLogicSentencesRowNumber.get() + 1);
		logger.info("Finish addNextLogicSentenceRow");
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

		FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
		FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML Files", "*.xml");
		FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON Files", "*.json");
		fileChooser.getExtensionFilters().add(csvFilter);
		fileChooser.getExtensionFilters().add(xmlFilter);
		fileChooser.getExtensionFilters().add(jsonFilter);

		openButton.setOnAction(e -> {

			File file = fileChooser.showOpenDialog(primaryStage);
			if (file != null) {
				if (fileChooser.getSelectedExtensionFilter().getDescription().equals("CSV Files")) {

					DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.CSV, analyzer);
					dynamicTable.setCurrentSectionBuilder(this);
					dynamicTable.populateTable();

					Main.getTabs().getTabs().get(Main.getCurrentlySelectedTabIndex()).setText(file.getName().substring(0, Config.MAX_TAB_NAME_LENGHT) + "..");

				}

				if (fileChooser.getSelectedExtensionFilter().getDescription().equals("JSON Files")) {
					DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.JSON, analyzer);
					dynamicTable.setCurrentSectionBuilder(this);
					dynamicTable.populateTable();

					Main.getTabs().getTabs().get(Main.getCurrentlySelectedTabIndex()).setText(file.getName().substring(0, Config.MAX_TAB_NAME_LENGHT) + "..");
				}

				if (fileChooser.getSelectedExtensionFilter().getDescription().equals("XML Files")) {
					DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.XML, analyzer);
					dynamicTable.setCurrentSectionBuilder(this);
					dynamicTable.populateTable();

					Main.getTabs().getTabs().get(Main.getCurrentlySelectedTabIndex()).setText(file.getName().substring(0, Config.MAX_TAB_NAME_LENGHT) + "..");
				}

			}
		});

		// Póki co nic nie robi
		openMultipleButton.setOnAction(e -> {
			List<File> list = fileChooser.showOpenMultipleDialog(primaryStage);
			if (list != null) {
				for (File file : list) {
					// TODO Dodać obsługę wielu plików
				}
			}
		});

		//TODO: do testów, wyrzucić w wersji do oddania
		final Button loadTestData = new Button("Załaduj dane testowe");
		loadTestData.setOnAction(click -> {
			DynamicTable dynamicTable = new DynamicTable(tableView, new File(getClass().getClassLoader().getResource("TestData.csv").getFile()), FileType.CSV, analyzer);
			dynamicTable.setCurrentSectionBuilder(this);
			dynamicTable.populateTable();
			Main.getTabs().getTabs().get(Main.getCurrentlySelectedTabIndex()).setText("TestData");

		});

		final HBox fileInputHBox = new HBox(4);
		fileInputHBox.getChildren().addAll(openButton, openMultipleButton, loadTestData);
		HBox.setHgrow(fileInputHBox, Priority.NEVER);

		logger.info("Finish createDataInputSection");
		return fileInputHBox;
	}

	/**
	 * Metoda pomocnicza, sprawdzająca czy wszystkie zdania zdefiniowane w GUI są poprawne(kompletne)
	 * @return boolean true jeżeli wszystkie zdania sa zdefiniowane poprawnie, false w przeciwnym wypadku
	 */
	private boolean checkIfLogicSentencesAreComplete() {
		Iterator iterator = logicSentencesMap.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry hashMapElement = (Map.Entry) iterator.next();
			if (!((LogicSentence)hashMapElement.getValue()).isComplete()) {
				return false;
			}
		}
		return true;
	}

	public void setDataVariables(String[] dataVariables) {
		this.dataVariables = dataVariables;
	}
}
