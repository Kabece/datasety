package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.OperatorType;
import model.PatternType;

/**
 * Created by kczurylo on 2016-08-09.
 * Przedstawia jedno logiczne zdanie
 */
@SuppressWarnings("WeakerAccess")
public class LogicSentence {

	private static ObservableList<String> variableList;

	private PatternType chosenPattern;
	private OperatorType chosenOperator;
	private String chosenVariable;
	private String chosenValue;

	public LogicSentence() {
		variableList = FXCollections.observableArrayList();
	}

	public static ObservableList<String> getVariableList() {
		return variableList;
	}

	public PatternType getChosenPattern() {
		return chosenPattern;
	}

	public void setChosenPattern(PatternType chosenPattern) {
		this.chosenPattern = chosenPattern;
	}

	public String getChosenVariable() {
		return chosenVariable;
	}

	public void setChosenVariable(String chosenVariable) {
		this.chosenVariable = chosenVariable;
	}

	public OperatorType getChosenOperator() {
		return chosenOperator;
	}

	public void setChosenOperator(OperatorType chosenOperator) {
		this.chosenOperator = chosenOperator;
	}

	public String getChosenValue() {
		return chosenValue;
	}

	public void setChosenValue(String chosenValue) {
		this.chosenValue = chosenValue;
	}

	@Override
	public String toString() {
		return "LogicSentence{" + "chosenPattern=" + chosenPattern + ", chosenOperator=" + chosenOperator
		       + ", chosenVariable='" + chosenVariable + '\'' + ", chosenValue='" + chosenValue + '\'' + '}';
	}
}
