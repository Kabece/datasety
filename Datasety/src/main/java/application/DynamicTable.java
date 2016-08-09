package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import parsing.FileType;

public class DynamicTable {

	public void populateTable(final TableView<ObservableList<StringProperty>> table, final File file, final FileType fileType, final boolean hasHeader) {
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
	}

	private Task<Void> parseCsvContent(final TableView<ObservableList<StringProperty>> table, final File file, final boolean hasHeader) {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				BufferedReader in = new BufferedReader(new FileReader(file));
				// Header line
				if (hasHeader) {
					final String headerLine = in.readLine();
					final String[] headerValues = headerLine.split(",");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							for (int column = 0; column < headerValues.length; column++) {
								table.getColumns().add(createColumn(column, headerValues[column]));
							}
						}
					});
				}

				// Data:

				String dataLine;
				while ((dataLine = in.readLine()) != null) {
					final String[] dataValues = dataLine.split(",");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// Add additional columns if necessary:
							for (int columnIndex = table.getColumns()
									.size(); columnIndex < dataValues.length; columnIndex++) {
								table.getColumns().add(createColumn(columnIndex, ""));
							}
							// Add data to table:
							ObservableList<StringProperty> data = FXCollections.observableArrayList();
							for (String value : dataValues) {
								data.add(new SimpleStringProperty(value));
							}
							table.getItems().add(data);
						}
					});
				}
				in.close();
				return null;
			}
		};

		return task;
	}

	private TableColumn<ObservableList<StringProperty>, String> createColumn(final int columnIndex,
			String columnTitle) {
		TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
		String title;
		if (columnTitle == null || columnTitle.trim().length() == 0) {
			title = "Column " + (columnIndex + 1);
		} else {
			title = columnTitle;
		}
		column.setText(title);
		column.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures) {
						ObservableList<StringProperty> values = cellDataFeatures.getValue();
						if (columnIndex >= values.size()) {
							return new SimpleStringProperty("");
						} else {
							return cellDataFeatures.getValue().get(columnIndex);
						}
					}
				});
		return column;
	}

	private BufferedReader getReaderFromUrl(String urlSpec) throws Exception {
		URL url = new URL(urlSpec);
		URLConnection connection = url.openConnection();
		InputStream in = connection.getInputStream();
		return new BufferedReader(new InputStreamReader(in));
	}
}
