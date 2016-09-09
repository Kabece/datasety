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


	private Analyzer analyzer;


	public SectionsBuilder() {
		//this.logicSentencesMap = new HashMap<>();

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
				openFile(fileChooser.getSelectedExtensionFilter().getDescription(), file, tableView);

				Main.getTabs().getTabs().get(Main.getCurrentlySelectedTabIndex()).setText(file.getName().substring(0, Config.MAX_TAB_NAME_LENGHT) + "..");
			}
		});


		openMultipleButton.setOnAction(e -> {
			List<File> list = fileChooser.showOpenMultipleDialog(primaryStage);

			if (list != null) {
				for (File file : list) {
					openFile(fileChooser.getSelectedExtensionFilter().getDescription(), file, tableView);

					Main.getTabs().getTabs().get(Main.getCurrentlySelectedTabIndex()).setText(file.getName().substring(0, Config.MAX_TAB_NAME_LENGHT) + "..");
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

	private void openFile(String extension, File file, TableView<ObservableList<StringProperty>> tableView) {
		if (extension.equals("CSV Files")) {
			DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.CSV, analyzer);
			dynamicTable.setCurrentSectionBuilder(this);
			dynamicTable.populateTable();
		}

		if (extension.equals("JSON Files")) {
			DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.JSON, analyzer);
			dynamicTable.setCurrentSectionBuilder(this);
			dynamicTable.populateTable();
		}

		if (extension.equals("XML Files")) {
			DynamicTable dynamicTable = new DynamicTable(tableView, file, FileType.XML, analyzer);
			dynamicTable.setCurrentSectionBuilder(this);
			dynamicTable.populateTable();
		}
	}


	/*public void setDataVariables(String[] dataVariables) {
		this.dataVariables = dataVariables;
	}*/
}
