package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by kczurylo on 2016-08-09.
 */
public class Controls {

   private ObservableList<String> variableList;

   public Controls() {
      this.variableList = FXCollections.observableArrayList();
   }

   public ObservableList<String> getVariableList() {
      return variableList;
   }
}
