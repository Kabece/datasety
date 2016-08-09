package application;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.log4j.Logger;
import parsing.FileType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Klasa zajmująca się tworzeniem widoku danych w postaci tabeli
 */
public class DynamicTable {

   private static final Logger logger = Logger.getLogger(DynamicTable.class);

   /**
	* Metoda zajmująca się zapełnianiem tabeli danymi
	* @param table Zapełniana tabela
	* @param file Plik z danymi
	* @param fileType Typ (rozszerzenie) pliku
	* @param hasHeader true jeżeli plik zawiera nagłówek, false w przeciwnym wypadku
	*/
   public void populateTable(final TableView<ObservableList<StringProperty>> table, final File file,
		 final FileType fileType, final boolean hasHeader) {
	  logger.info("Start populateTable");

	  table.getItems().clear();
	  table.getColumns().clear();
	  table.setPlaceholder(new Label("Loading..."));
	  Task<Void> task;
	  switch (fileType) {
		 case CSV:
			task = parseCsvContent(table, file, hasHeader);
			break;
		 default:
			task = null;
			break;
	  }
	  if (task != null) {
		 Thread thread = new Thread(task);
		 thread.setDaemon(true);
		 thread.start();
	  }

	  logger.info("Finish populateTable");
   }

   /**
	* Przetwarza dane z pliku CSV, następnie dodaje je do tabeli
	* @param table Zapełniana tabela
	* @param file Plik z danymi
	* @param hasHeader true jeżeli plik ma nagłówek, false w przeciwnym wypadku
	* @return Zadanie (Task) parsujące CSV
	*/
   private Task<Void> parseCsvContent(final TableView<ObservableList<StringProperty>> table, final File file,
		 final boolean hasHeader) {
	  logger.info("Start parseCsvContent");

	  Task<Void> task = new Task<Void>() {
		 @Override
		 protected Void call() throws Exception {
			logger.info("Start call (inside parseCsvContent)");

			BufferedReader in = new BufferedReader(new FileReader(file));
			// Header line
			if (hasHeader) {
			   final String headerLine = in.readLine();
			   final String[] headerValues = headerLine.split(",");
			   Platform.runLater(() -> {
				  for (int column = 0; column < headerValues.length; column++) {
					 table.getColumns().add(createColumn(column, headerValues[column]));
				  }
			   });
			}

			// Data:

			String dataLine;
			while ((dataLine = in.readLine()) != null) {
			   final String[] dataValues = dataLine.split(",");
			   Platform.runLater(() -> {
				  // Add additional columns if necessary:
				  for (int columnIndex = table.getColumns().size(); columnIndex < dataValues.length; columnIndex++) {
					 table.getColumns().add(createColumn(columnIndex, ""));
				  }
				  // Add data to table:
				  ObservableList<StringProperty> data = FXCollections.observableArrayList();
				  for (String value : dataValues) {
					 data.add(new SimpleStringProperty(value));
				  }
				  table.getItems().add(data);
			   });
			}
			in.close();

			logger.info("Finish call (inside parseCsvContent)");
			return null;
		 }
	  };

	  logger.info("Finish parseCsvContent");
	  return task;
   }

   /**
	* Tworzy kolumny tabeli, z odpowiednimi nagłówkami oraz dodaje fabrykę komórek tabeli
	* @param columnIndex Indeks kolumny
	* @param columnTitle Nazwa kolumny (nagłówek)
	* @return Kolumna tabeli
	*/
   private TableColumn<ObservableList<StringProperty>, String> createColumn(final int columnIndex, String columnTitle) {
	  logger.info("Start createColumn");

	  TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
	  String title;
	  if (columnTitle == null || columnTitle.trim().length() == 0) {
		 title = "Column " + (columnIndex + 1);
	  } else {
		 title = columnTitle;
	  }
	  column.setText(title);

	  column.setCellValueFactory(cellDataFeatures -> {
		 ObservableList<StringProperty> values = cellDataFeatures.getValue();
		 if (columnIndex >= values.size()) {
			return new SimpleStringProperty("");
		 } else {
			return cellDataFeatures.getValue().get(columnIndex);
		 }
	  });

	  logger.info("Finish createColumn");
	  return column;
   }
}
