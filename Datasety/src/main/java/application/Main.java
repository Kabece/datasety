package application;

import application.implementations.analyzer.CheckAnalyzer;
import application.implementations.analyzer.ShowAnalyzer;
import application.implementations.logicSentence.ExtendedLogicSentence;
import application.implementations.logicSentence.SingleLogicSentence;
import application.interfaces.analyzer.Analyzer;
import application.interfaces.logicSentence.LogicSentence;
import enums.AnalyzerWorkType;
import enums.FileType;
import enums.OperatorType;
import enums.PatternType;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.plugin.javascript.navig.Anchor;

import java.io.File;
import java.util.*;

import static enums.AnalyzerWorkType.CHECK;
import static enums.AnalyzerWorkType.SHOW;

public class Main extends Application {

	private final static Logger logger = LogManager.getLogger(Main.class.getName());
	private final List<Pane> tabsContent = new ArrayList<>();
	private static int currentlySelectedTabIndex;
	private static TabPane tabs;
	private Analyzer analyzer;
	private Map<String,LogicSentence> logicSentencesMap = new HashMap<>();
	private IntegerProperty currentLogicSentencesRowNumber;
	public static MapProperty<String,List<String>> dataVariables = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private Stage stage;

	private List<TableView<ObservableList<StringProperty>>> tableViews;

	public static void main(String[] args) {
		logger.info("Start main");
		/* Przykładowe pobieranie - na forum pisalo ze lubi takie 'choinkowe' motywy - przy chwili czasu do przekminienia wszedzie*/
		Locale defaultLocale = new Locale("pl","PL");
		ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", defaultLocale);
		messages.getString("test");

		launch(args);
		logger.info("Finish main");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws Exception {
		logger.info("Start init");

		tableViews = new ArrayList<>();
		tabs = new TabPane();
		analyzer = new CheckAnalyzer();
		currentLogicSentencesRowNumber = new SimpleIntegerProperty(0);


		logger.info("Finish init");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(Stage primaryStage) {
		logger.info("Start start");

		try {
            stage = primaryStage;
			primaryStage.setTitle("Dataset analyzer");

			final GridPane root = new GridPane();
			final Button addNewTabButton = new Button("+");
			final FileChooser fileChooser = new FileChooser();

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(100);
            root.getColumnConstraints().add(column1);

			BorderPane menuPanel = new BorderPane();

			MenuBar menuBar = new MenuBar();
			menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

			Menu menuFile = new Menu("Plik");
			menuBar.getMenus().addAll(menuFile);
			MenuItem singleFile = new MenuItem("Otwórz plik...");
			MenuItem multipleFile = new MenuItem("Otwórz pliki...");
			menuFile.getItems().addAll(singleFile,multipleFile);

			FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
			FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML Files", "*.xml");
			FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON Files", "*.json");
			fileChooser.getExtensionFilters().add(csvFilter);
			fileChooser.getExtensionFilters().add(xmlFilter);
			fileChooser.getExtensionFilters().add(jsonFilter);

			singleFile.setOnAction(event -> {
				File file = fileChooser.showOpenDialog(primaryStage);
				if (file != null) {
					openFile(fileChooser.getSelectedExtensionFilter().getDescription(), file, tableViews.get(tableViews.size() - 1));

					Main.getTabs().getTabs().get(Main.getCurrentlySelectedTabIndex()).setText(file.getName().substring(0, Config.MAX_TAB_NAME_LENGHT) + "..");
				}
			});


            final GridPane controlsGridPane = initializeLogicSentenceSection();
            final GridPane analyzerGridPane = createAnalyzerSection();


       /*     AnchorPane.setTopAnchor(tabs, 25.0);
			AnchorPane.setLeftAnchor(tabs, 1.0);
			AnchorPane.setRightAnchor(tabs, 1.0);
			AnchorPane.setBottomAnchor(tabs, 25.0);
			AnchorPane.setTopAnchor(addNewTabButton, 25.0);
			AnchorPane.setLeftAnchor(addNewTabButton, 5.0);
            AnchorPane.setBottomAnchor(controlsGridPane, 45.0);
            AnchorPane.setLeftAnchor(controlsGridPane, 4.0);
            AnchorPane.setRightAnchor(controlsGridPane, 4.0);
            AnchorPane.setBottomAnchor(analyzerGridPane, 25.0);
            AnchorPane.setLeftAnchor(analyzerGridPane,40d);*/

            root.add(menuBar, 0, 0);
            root.add(tabs, 0, 1);
            root.add(controlsGridPane, 0, 2);
            root.add(analyzerGridPane, 0, 3);


			final Tab initTab = createTab(primaryStage, addNewTabButton);
			initTab.setClosable(false);
			tabs.getTabs().add(initTab);
			tabs.getSelectionModel().select(initTab);

			addNewTabButton.setOnAction(event -> {
				final Tab tab = createTab(primaryStage, addNewTabButton);
				tabs.getTabs().add(tab);
				tabs.getSelectionModel().select(tab);

				if (tabs.getTabs().size() == Config.MAX_TABS_AMOUNT) {
					addNewTabButton.setDisable(true);
				}
			});

		//	root.getChildren().addAll(tabs, addNewTabButton, menuBar, controlsGridPane, analyzerGridPane);

			Scene scene = new Scene(root, Config.INITIAL_SCENE_WIDTH, Config.INITIAL_SCENE_HEIGHT);
			scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			logger.error("Error in start", e);
		}

		logger.info("Finish start");
	}

	/**
	 * Tworzy nową kartę.
	 *
	 * @param primaryStage główna scena aplikacji.
	 * @param addNewTabButton przycisk dodawania nowej karty.
	 * @return stworzona karta.
	 */
	private Tab createTab(Stage primaryStage, Button addNewTabButton) {
		logger.info("Start createTab");

		final Tab tab = new Tab("New Tab");
		tab.closableProperty().setValue(true);

		tab.setOnSelectionChanged(event -> {
			if (tab.isSelected()) {
				currentlySelectedTabIndex = tabs.getSelectionModel().getSelectedIndex();
			}
		});

		tab.setOnClosed(event -> addNewTabButton.setDisable(false));

		tabsContent.add(new VBox(12));
		createTabContent(primaryStage);
		tab.setContent(tabsContent.get(tabsContent.size() - 1));

		logger.info("Finish createTab");
		return tab;
	}

	/**
	 * Tworzy zawartość dla ostatnio stworzonej karty
	 *
	 * @param primaryStage główna scena aplikacji.
	 */
	private void createTabContent(Stage primaryStage) {
		logger.info("Start createTabContent");

		SectionsBuilder sectionsBuilder = new SectionsBuilder();
		tableViews.add(new TableView<>());
		int lastTableView = tableViews.size() - 1;
		int lastTab = tabsContent.size() - 1;



		tabsContent.get(lastTab).setPadding(new Insets(12, 12, 12, 12));
		tabsContent.get(lastTab).getChildren().addAll(tableViews.get(lastTableView));

		logger.info("Finish createTabContent");
	}

	
	/**
	 * Getter dla głównego kontenera kart.
	 * @return główny kontener kart.
	 */
	public static TabPane getTabs() {
		return tabs;
	}

	/**
	 * Getter dla obecnie wybranej karty.
	 * @return index obecnie wybranej karty.
	 */
	public static int getCurrentlySelectedTabIndex() { return currentlySelectedTabIndex; }

	private void openFile(String extension, File file, TableView<ObservableList<StringProperty>> tableView) {
		if (extension.equals("CSV Files")) {
			 DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.CSV, analyzer);
			// dynamicTable.setCurrentSectionBuilder(tabs.getSelectionModel().getSelectedItem());
			 dynamicTable.populateTable();
		}

		if (extension.equals("JSON Files")) {
			 DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.JSON, analyzer);
			// dynamicTable.setCurrentSectionBuilder(this);
			 dynamicTable.populateTable();
		}

		if (extension.equals("XML Files")) {
			 DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.XML, analyzer);
			// dynamicTable.setCurrentSectionBuilder(this);
			 dynamicTable.populateTable();
		}
	}


	public GridPane initializeLogicSentenceSection() {
		logger.info("Initializing logic sentence section.");

		final Button nextRowButton = new Button("Dodaj kolejne zdanie");
		nextRowButton.setOnMouseClicked(event -> {
			logger.debug("Process createLogicSentenceSection, nextRowButton clicked! currentLogicSentencesRowsNumber={}", currentLogicSentencesRowNumber);
			if (currentLogicSentencesRowNumber.get() < Config.MAX_LOGIC_SENTENCES_ROWS) {
				//createLogicSentenceSection();
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
        controlsGridPane.setPadding(new Insets(25,5,25,25));
		GridPane.setConstraints(nextRowButton, 0, 0);
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
		//observableList.addAll(dataVariables.get);
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
		/*
		logicSentencesMap.get(logicSentenceId).getVariableList().addListener((ListChangeListener<String>) c -> {
			logger.debug("Process createLogicSentenceSection, variableList changed");
			// TODO Dodać sprawdzanie czy nie null / może optional?
			variableComboBox.getItems().setAll(logicSentencesMap.get(logicSentenceId).getVariableList());
		});*/

		//Dataset
		final Label datasetlabel = new Label("Dataset:");
		final ComboBox datasetComboBox = new ComboBox();
		datasetComboBox.setPromptText("Dataset");
		datasetComboBox.getItems().addAll(dataVariables.keySet());
		datasetComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			variableComboBox.getItems().clear();
            variableComboBox.getItems().addAll(dataVariables.get(datasetComboBox.getValue().toString()));
        });

		dataVariables.addListener((observable, oldValue, newValue) -> {
        	datasetComboBox.getItems().clear();
			datasetComboBox.getItems().addAll(dataVariables.keySet());
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

			GridPane group = (GridPane) removeSection.getParent().getParent();
			group.getChildren().remove(removeSection.getParent());
			logicSentencesMap.remove(removeSection.getParent().getId());
			currentLogicSentencesRowNumber.set(currentLogicSentencesRowNumber.get() -1);

			logger.info("Successfully removed logic sentence! {}", logicSentenceId);
		});

		/* FOR BINARY PATTERNS */


		//second dataset
		final Label secondDatasetlabel = new Label("Dataset 2:");
		final ComboBox secondDatasetComboBox = new ComboBox();
		secondDatasetComboBox.setPromptText("Dataset 2");

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
/*		logicSentencesMap.get(logicSentenceId).getNextSentencePart().getVariableList().addListener((ListChangeListener<String>) c -> {
			logger.debug("Process createLogicSentenceSection, variableList changed");
			// TODO Dodać sprawdzanie czy nie null / może optional?
			secondVariableComboBox.getItems().setAll(logicSentencesMap.get(logicSentenceId).getNextSentencePart().getVariableList());
		});*/

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

		secondDatasetlabel.setVisible(false);
		secondDatasetComboBox.setVisible(false);
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
		GridPane.setConstraints(datasetlabel, 1, 0);
		GridPane.setConstraints(datasetComboBox, 1, 1);
		GridPane.setConstraints(variableLabel, 2, 0);
		GridPane.setConstraints(variableComboBox, 2, 1);
		GridPane.setConstraints(operatorLabel, 3, 0);
		GridPane.setConstraints(operatorComboBox, 3, 1);
		GridPane.setConstraints(valueLabel, 4, 0);
		GridPane.setConstraints(valueTextField, 4, 1);
		GridPane.setConstraints(removeSection,6,1);

		GridPane.setConstraints(secondDatasetlabel, 1, 2);
		GridPane.setConstraints(secondDatasetComboBox, 1, 3);
		GridPane.setConstraints(secondVariableLabel, 2, 2);
		GridPane.setConstraints(secondVariableComboBox, 2, 3);
		GridPane.setConstraints(secondOperatorLabel, 3 ,2);
		GridPane.setConstraints(secondOperatorComboBox, 3, 3);
		GridPane.setConstraints(secondValueLabel, 4, 2);
		GridPane.setConstraints(secondValueTextField, 4, 3);


		controlsGridPane.getChildren()
				.addAll(patternLabel, patternComboBox, variableLabel, variableComboBox, operatorLabel, operatorComboBox, datasetlabel, datasetComboBox,
						valueLabel, valueTextField, removeSection, secondDatasetlabel, secondDatasetComboBox, secondVariableLabel, secondVariableComboBox, secondOperatorLabel, secondOperatorComboBox,
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
				secondDatasetlabel.setVisible(true);
				secondDatasetComboBox.setVisible(true);


			} else {

				logicSentencesMap.put(logicSentenceId, new SingleLogicSentence((PatternType) newValue, observableList));

				secondVariableLabel.setVisible(false);
				secondVariableComboBox.setVisible(false);
				secondOperatorLabel.setVisible(false);
				secondOperatorComboBox.setVisible(false);
				secondValueLabel.setVisible(false);
				secondValueTextField.setVisible(false);
				secondDatasetlabel.setVisible(false);
				secondDatasetComboBox.setVisible(false);
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

	//	((Pane) Main.getTabs().getTabs().get(Main.getCurrentlySelectedTabIndex()).getContent()).getChildren().add(currentLogicSentencesRowNumber.get() + 2, createLogicSentenceSection());
        //stage.getScene().getRoot().getChildren().get(2);
        ((GridPane)((GridPane)stage.getScene().getRoot()).getChildren().get(2)).add(createLogicSentenceSection(),0,currentLogicSentencesRowNumber.get() + 3);

        currentLogicSentencesRowNumber.set(currentLogicSentencesRowNumber.get() + 1);
		logger.info("Finish addNextLogicSentenceRow");
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


	/*	analyzeButton.disableProperty().bind(new BooleanBinding(){
			{
			//	bind(currentLogicSentencesRowNumber);
			}

	*//*		@Override
			protected boolean computeValue() {
				return currentLogicSentencesRowNumber.get() < 1;
			}*//*
		});
*/
        analyzeButton.setDefaultButton(true);

        analyzeButton.setOnAction(event -> {
            resultIndicatorCircle.setFill(Color.DARKGRAY);
            logger.debug("Process createLogicControlSection, analyzeButton fired!");
            if (/*checkIfLogicSentencesAreComplete() &&*/ analyzer.isReady()) {
                logger.trace("Process createLogicControlSection, analyzer data = {}", analyzer.getDataMap());

                //analyzer.setLogicSentences(logicSentencesMap);

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
        analyzerGridPane.setPadding(new Insets(0,0,25,25));
        GridPane.setConstraints(analyzerWorkTypeLabel, 0, 0);
        GridPane.setConstraints(analyzerWorkTypeComboBox, 0, 1);
        GridPane.setConstraints(analyzeButton, 1, 1);
        GridPane.setConstraints(resultIndicatorLabel, 0, 3);
        GridPane.setConstraints(resultIndicatorCircle, 0, 4);
        analyzerGridPane.getChildren().addAll(analyzerWorkTypeLabel, analyzerWorkTypeComboBox, analyzeButton, resultIndicatorLabel, resultIndicatorCircle);

        logger.info("Finish createAnalyzerSection");
        return analyzerGridPane;
    }





}
