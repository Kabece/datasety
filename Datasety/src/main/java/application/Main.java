package application;

import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

	private final static Logger logger = LogManager.getLogger(Main.class.getName());
	private final List<Pane> tabsContent = new ArrayList<>();
	private static int currentlySelectedTabIndex;
	private static TabPane tabs;

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

			AnchorPane.setTopAnchor(tabs, 5.0);
			AnchorPane.setLeftAnchor(tabs, 5.0);
			AnchorPane.setRightAnchor(tabs, 5.0);
			AnchorPane.setBottomAnchor(tabs, 5.0);
			AnchorPane.setTopAnchor(addNewTabButton, 5.0);
			AnchorPane.setLeftAnchor(addNewTabButton, 5.0);

			addNewTabButton.setOnAction(event -> {
				final Tab tab = createTab(primaryStage, addNewTabButton);
				tabs.getTabs().add(tab);
				tabs.getSelectionModel().select(tab);

				if (tabs.getTabs().size() == Config.MAX_TABS_AMOUNT) {
					//TODO: zmienic na disabled?
					addNewTabButton.setVisible(false);
				}
			});

			root.getChildren().addAll(tabs, addNewTabButton);

			Scene scene = new Scene(root, Config.INITIAL_SCENE_WIDTH, Config.INITIAL_SCENE_HEIGHT);
			scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			// TODO Lepij to można zrobić xD
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
			addNewTabButton.setVisible(true);
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


}
