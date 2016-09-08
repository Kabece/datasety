package application;

import enums.FileType;
import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

	private final static Logger logger = LogManager.getLogger(Main.class.getName());
	private final List<Pane> tabsContent = new ArrayList<>();
	private static int currentlySelectedTabIndex;
	private static TabPane tabs;

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

		logger.info("Finish init");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(Stage primaryStage) {
		logger.info("Start start");

		try {
			primaryStage.setTitle("Dataset analyzer");

			final AnchorPane root = new AnchorPane();
			final Button addNewTabButton = new Button("+");
			final FileChooser fileChooser = new FileChooser();

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
			//		openFile(fileChooser.getSelectedExtensionFilter().getDescription(), file, tableView);

					Main.getTabs().getTabs().get(Main.getCurrentlySelectedTabIndex()).setText(file.getName().substring(0, Config.MAX_TAB_NAME_LENGHT) + "..");
				}
			});

			AnchorPane.setTopAnchor(tabs, 25.0);
			AnchorPane.setLeftAnchor(tabs, 1.0);
			AnchorPane.setRightAnchor(tabs, 1.0);
			AnchorPane.setBottomAnchor(tabs, 25.0);
			AnchorPane.setTopAnchor(addNewTabButton, 25.0);
			AnchorPane.setLeftAnchor(addNewTabButton, 5.0);


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

			root.getChildren().addAll(tabs, addNewTabButton, menuBar);

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

		tab.setOnClosed(event -> {
			addNewTabButton.setDisable(false);
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

		SectionsBuilder sectionsBuilder = new SectionsBuilder();
		tableViews.add(new TableView<>());
		int lastTableView = tableViews.size() - 1;
		int lastTab = tabsContent.size() - 1;

		final HBox fileInputHBox = sectionsBuilder.createDataInputSection(primaryStage, tableViews.get(lastTableView));
		final GridPane controlsGridPane = sectionsBuilder.initializeLogicSentenceSection();
		final GridPane analyzerGridPane = sectionsBuilder.createAnalyzerSection();

		tabsContent.get(lastTab).setPadding(new Insets(12, 12, 12, 12));
		tabsContent.get(lastTab).getChildren().addAll(fileInputHBox, tableViews.get(lastTableView), controlsGridPane, analyzerGridPane);

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
			// DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.CSV, analyzer);
			// dynamicTable.setCurrentSectionBuilder(tabs.getSelectionModel().getSelectedItem());
			// dynamicTable.populateTable();
		}

		if (extension.equals("JSON Files")) {
			// DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.JSON, analyzer);
			// dynamicTable.setCurrentSectionBuilder(this);
			// dynamicTable.populateTable();
		}

		if (extension.equals("XML Files")) {
			// DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.XML, analyzer);
			// dynamicTable.setCurrentSectionBuilder(this);
			// dynamicTable.populateTable();
		}
	}

}
