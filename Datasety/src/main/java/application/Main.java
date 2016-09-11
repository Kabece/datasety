package application;

import application.implementations.analyzer.CheckAnalyzer;
import application.implementations.logicSentence.ExtendedLogicSentence;
import application.implementations.logicSentence.SingleLogicSentence;
import application.interfaces.analyzer.Analyzer;
import application.interfaces.logicSentence.LogicSentence;
import application.utils.TabPaneWithPlaceholder;
import enums.FileType;
import enums.OperatorType;
import enums.PatternType;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public class Main extends Application {

	private final static Logger logger = LogManager.getLogger(Main.class.getName());
	private final List<Pane> tabsContent = new ArrayList<>();
	private static int currentlySelectedTabIndex;
    private static TabPaneWithPlaceholder tabs;
	private Analyzer analyzer;
	private Map<String,LogicSentence> logicSentencesMap = new HashMap<>();
	private IntegerProperty currentLogicSentencesRowNumber;
	public static MapProperty<String,List<String>> dataVariables = new SimpleMapProperty<>(FXCollections.observableHashMap());
    private Stage stage;

	private List<TableView<ObservableList<StringProperty>>> tableViews;

	public static void main(String[] args) {
		logger.info("Start main");
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
		tabs = new TabPaneWithPlaceholder("Dataset analyser","Please upload datasets using menu at the top.");
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
			final FileChooser fileChooser = new FileChooser();

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(100);
            root.getColumnConstraints().add(column1);

			MenuBar menuBar = new MenuBar();
			menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

			Menu menuFile = new Menu("File");
			menuBar.getMenus().addAll(menuFile);
			MenuItem singleFile = new MenuItem("Open single file ...");
			MenuItem multipleFile = new MenuItem("Open multiple files ...");
			menuFile.getItems().addAll(singleFile,multipleFile);

			FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV Files", "*.csv");
			FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML Files", "*.xml");
			FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON Files", "*.json");
			fileChooser.getExtensionFilters().add(csvFilter);
			fileChooser.getExtensionFilters().add(xmlFilter);
			fileChooser.getExtensionFilters().add(jsonFilter);

			singleFile.setOnAction(event -> {
				File file = fileChooser.showOpenDialog(primaryStage);
				if (file != null && tabs.getTabs().size() < Config.MAX_TABS_AMOUNT) {

                    final Tab tab = createTab(primaryStage);
                    tabs.getTabs().add(tab);
                    tabs.getTabPane().getSelectionModel().select(tab);
                    openFile(fileChooser.getSelectedExtensionFilter().getDescription(), file, tableViews.get(tableViews.size() - 1));

                    tab.setOnCloseRequest(event1 -> {
						analyzer.getDatasets().remove(file.getName());
                        dataVariables.remove(file.getName());
                    });

                    tab.setText(file.getName().substring(0, Config.MAX_TAB_NAME_LENGHT) + "..");
				} else if (tabs.getTabs().size() >= Config.MAX_TABS_AMOUNT ){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("WARNING!");
                    alert.setContentText("Maximum of " + Config.MAX_TABS_AMOUNT + " files opened simultaneously has been reached!\n You cannot open more dataset files.");
                    alert.showAndWait();
                }
			});

            multipleFile.setOnAction(event -> {


                if(tabs.getTabs().size() == Config.MAX_TABS_AMOUNT) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("WARNING!");
                    alert.setContentText("Maximum of " + Config.MAX_TABS_AMOUNT + " files opened simultaneously has been reached!\n You cannot open more dataset files.");
                    alert.showAndWait();
                } else {
                    List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);

                    if (files.size() + tabs.getTabs().size() > Config.MAX_TABS_AMOUNT) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("WARNING!");
                        alert.setContentText("Maximum of " + Config.MAX_TABS_AMOUNT + " files opened simultaneously has been reached!\n Following files will not be opened: ");

                        for (int i  = Config.MAX_TABS_AMOUNT - tabs.getTabs().size(); i < files.size(); i++) {
                            alert.setContentText(alert.getContentText() + "\n   " + files.get(i).getName());
                        }
                        alert.showAndWait();

                        for (int i = 0; i < Config.MAX_TABS_AMOUNT - tabs.getTabs().size(); i++) {
                            final Tab tab = createTab(primaryStage);
                            final String fileName = files.get(i).getName();
                            tabs.getTabs().add(tab);
                            tabs.getTabPane().getSelectionModel().select(tab);
                            openFile(fileChooser.getSelectedExtensionFilter().getDescription(), files.get(i), tableViews.get(tableViews.size() - 1));

                            tab.setOnCloseRequest(event1 -> dataVariables.remove(fileName));

                            tab.setText(fileName.substring(0, Config.MAX_TAB_NAME_LENGHT) + "..");
                        }


                    } else {
                        for (int j = 0; j < files.size(); j++) {
                            final Tab tab = createTab(primaryStage);
                            final String fileName = files.get(j).getName();
                            tabs.getTabs().add(tab);
                            tabs.getTabPane().getSelectionModel().select(tab);
                            openFile(fileChooser.getSelectedExtensionFilter().getDescription(), files.get(j), tableViews.get(tableViews.size() - 1));

                            tab.setOnCloseRequest(event1 -> dataVariables.remove(fileName));

                            tab.setText(fileName.substring(0, Config.MAX_TAB_NAME_LENGHT) + "..");
                        }

                    }
                }
            });

            final GridPane controlsGridPane = initializeLogicSentenceSection();
            final GridPane analyzerGridPane = createAnalyzerSection();

			tabs.setPrefHeight(320d);

            root.add(menuBar, 0, 0);
            root.add(tabs,0,1);
            root.add(controlsGridPane, 0, 2);
            root.add(analyzerGridPane, 0, 3);


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
	 * @return stworzona karta.
	 */
	private Tab createTab(Stage primaryStage) {
		logger.info("Start createTab");

		final Tab tab = new Tab("New Tab");
		tab.closableProperty().setValue(true);

		tab.setOnSelectionChanged(event -> {
			if (tab.isSelected()) {
				currentlySelectedTabIndex = tabs.getTabPane().getSelectionModel().getSelectedIndex();
			}
		});

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
		return tabs.getTabPane();
	}

	/**
	 * Getter dla obecnie wybranej karty.
	 * @return index obecnie wybranej karty.
	 */
	public static int getCurrentlySelectedTabIndex() { return currentlySelectedTabIndex; }

	private void openFile(String extension, File file, TableView<ObservableList<StringProperty>> tableView) {
		if (extension.equals("CSV Files")) {
			 DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.CSV, analyzer);
			 dynamicTable.populateTable();
		}

		if (extension.equals("JSON Files")) {
			 DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.JSON, analyzer);
			 dynamicTable.populateTable();
		}

		if (extension.equals("XML Files")) {
			 DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.XML, analyzer);
			 dynamicTable.populateTable();
		}
	}


	public GridPane initializeLogicSentenceSection() {
		logger.info("Initializing logic sentence section.");

		final Button nextRowButton = new Button("Add next logic sentence");
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

		logicSentencesMap.put(logicSentenceId, new SingleLogicSentence(PatternType.values()[0], observableList));

		// Pattern
		final Label patternLabel = new Label("Pattern: ");
		final ComboBox patternComboBox = new ComboBox();
		patternComboBox.getItems().setAll(PatternType.values());
		patternComboBox.setPromptText("Pattern");

		// Variable
		final Label variableLabel = new Label("Variable: ");
		final ComboBox variableComboBox = new ComboBox();
		variableComboBox.setPromptText("Variable");
		variableComboBox.setOnMouseClicked(event -> {
			if (variableComboBox.getItems().isEmpty()) {
				if (logicSentencesMap.get(logicSentenceId).getVariableList().isEmpty()) {
					alertWarning("No dataset selected!","Select dataset in 'dataset' field.");
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

		//Dataset
		final Label datasetlabel = new Label("Dataset:");
		final ComboBox datasetComboBox = new ComboBox();
		datasetComboBox.setPromptText("Dataset");
        datasetComboBox.setOnMouseClicked(event -> {
            if (dataVariables.isEmpty()) {
                alertWarning("No data selected!","Upload appropriate dataset file and wait while it load.");
            }
        });

		datasetComboBox.getItems().addAll(dataVariables.keySet());

        datasetComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicSentenceSection, datasetComboBox value has changed from = {} to = {}",
                oldValue, newValue);
            logicSentencesMap.get(logicSentenceId).setChosenDataset((String) newValue);
			logicSentencesMap.get(logicSentenceId).getVariableList().clear();
			logicSentencesMap.get(logicSentenceId).getVariableList().addAll(dataVariables.get(newValue));
        });

		datasetComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
		    if(datasetComboBox.getValue() != null) {
                variableComboBox.getItems().clear();
                variableComboBox.getItems().addAll(dataVariables.get(datasetComboBox.getValue().toString()));
            }
        });


        datasetComboBox.itemsProperty().bind(Bindings.createObjectBinding(() ->
                        FXCollections.observableArrayList(dataVariables.keySet()),dataVariables)
        );

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
		final Label valueLabel = new Label("Value: ");
		final TextField valueTextField = new TextField();
		valueTextField.setPromptText("Enter value");
		valueTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicSentenceSection, valueTextField value has been entered: {}",
					valueTextField.textProperty().getValueSafe());
			logicSentencesMap.get(logicSentenceId).setChosenValue(valueTextField.textProperty().getValueSafe());
		});

		Button removeSection = new Button();
		removeSection.setText("Remove logic sentence");
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
        secondDatasetComboBox.setOnMouseClicked(event -> {
            if (dataVariables.isEmpty()) {
                alertWarning("No data selected!","Upload appropriate dataset file and wait while it load.");
            }
        });
        // Second Variable
		final Label secondVariableLabel = new Label("Variable 2: ");
		final ComboBox secondVariableComboBox = new ComboBox();
		secondVariableComboBox.setPromptText("Variable 2");
		secondVariableComboBox.setOnMouseClicked(event -> {
			if (secondVariableComboBox.getItems().isEmpty()) {
				// Tutaj rzutowanie - zawsze zlozone zdanie bedzie typu ExtendedLogicSentence
				if (logicSentencesMap.get(logicSentenceId).getNextSentencePart().getVariableList().isEmpty()) {
                    alertWarning("No dataset selected!","Select dataset in 'dataset' field.");
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


        secondDatasetComboBox.getItems().addAll(dataVariables.keySet());
        secondDatasetComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(secondDatasetComboBox.getValue() != null) {
                secondVariableComboBox.getItems().clear();
                secondVariableComboBox.getItems().addAll(dataVariables.get(secondDatasetComboBox.getValue().toString()));
            }
        });

        secondDatasetComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			logger.debug("Process createLogicSentenceSection, datasetComboBox value has changed from = {} to = {}",
                oldValue, newValue);
            logicSentencesMap.get(logicSentenceId).getNextSentencePart().setChosenDataset((String) newValue);
			logicSentencesMap.get(logicSentenceId).getNextSentencePart().getVariableList().clear();
			logicSentencesMap.get(logicSentenceId).getNextSentencePart().getVariableList().addAll(dataVariables.get(newValue));
        });


        secondDatasetComboBox.itemsProperty().bind(Bindings.createObjectBinding(() ->
                FXCollections.observableArrayList(dataVariables.keySet()),dataVariables)
        );

		// Value
		final Label secondValueLabel = new Label("Value: ");
		final TextField secondValueTextField = new TextField();
		secondValueTextField.setPromptText("Enter value");
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

				logicSentencesMap.put(logicSentenceId, new ExtendedLogicSentence(new SingleLogicSentence((PatternType) newValue, observableList), FXCollections.observableArrayList()));

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

            datasetComboBox.setValue(null);
			variableComboBox.setValue(null);
			operatorComboBox.setValue(null);
			valueTextField.setText(null);
            secondDatasetComboBox.setValue(null);
			secondVariableComboBox.setValue(null);
			secondOperatorComboBox.setValue(null);
			secondValueTextField.setText(null);

		});

		return controlsGridPane;
	}

	private void addNextLogicSentenceRow() {
		logger.info("Start addNextLogicSentenceRow");

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


        // Result Indicator
        final Label resultIndicatorLabel = new Label("Result: ");
        final Circle resultIndicatorCircle = new Circle();
        resultIndicatorCircle.setRadius(15.0f);
        resultIndicatorCircle.setFill(Color.DARKGRAY);

        // Analyzer
        final Button analyzeButton = new Button("Analyse");


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
                logger.trace("Process createLogicControlSection, analyzer data = {}", analyzer.getDatasets());

                analyzer.setLogicSentences(logicSentencesMap);

                if (analyzer.analyzeList()) {
                    resultIndicatorCircle.setFill(Color.FORESTGREEN);
                } else {
                    resultIndicatorCircle.setFill(Color.MAROON);
                }

            } else {
                Alert analyzingNotReadyAlert = new Alert(Alert.AlertType.WARNING);
                analyzingNotReadyAlert.setTitle("Warning!");
                analyzingNotReadyAlert.setHeaderText("Analyser is not ready to work!");
                analyzingNotReadyAlert.setContentText("Fill the logic sentences properly.");
                analyzingNotReadyAlert.showAndWait();
            }
        });

        final GridPane analyzerGridPane = new GridPane();
        analyzerGridPane.setHgap(3);
        analyzerGridPane.setVgap(3);
        analyzerGridPane.setPadding(new Insets(0,0,25,25));
        GridPane.setConstraints(analyzeButton, 0, 1);
        GridPane.setConstraints(resultIndicatorLabel, 0, 3);
        GridPane.setConstraints(resultIndicatorCircle, 0, 4);
        analyzerGridPane.getChildren().addAll( analyzeButton, resultIndicatorLabel, resultIndicatorCircle);

        logger.info("Finish createAnalyzerSection");
        return analyzerGridPane;
    }

    private void alertWarning(String header, String content) {
        Alert noDataSpecifiedAlert = new Alert(Alert.AlertType.WARNING);
        noDataSpecifiedAlert.setTitle("Warning!");
        noDataSpecifiedAlert.setHeaderText(header);
        noDataSpecifiedAlert.setContentText(content);
        noDataSpecifiedAlert.showAndWait();
    }
}
