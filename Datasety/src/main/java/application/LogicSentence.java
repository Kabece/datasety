package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import enums.OperatorType;
import enums.PatternType;

/**
 * Created by kczurylo on 2016-08-09.
 * <p>
 *    Przedstawia jedno logiczne zdanie.
 * </p>
 */
@SuppressWarnings("WeakerAccess")
public class LogicSentence {

	private ObservableList<String> variableList = FXCollections.observableArrayList();

	private PatternType chosenPattern;
	private OperatorType chosenOperator;
	private String chosenVariable;
	private String chosenValue;

	public LogicSentence(ObservableList<String> variables) {
		variableList = variables;
	}

	/**
	 * Sprawdza czy zdanie logiczne jest kompletne
	 * @return true jeżeli kompletne, false w przeciwnym wypadku
	 */
	public boolean isComplete() {
		if (chosenPattern != null && chosenOperator != null && chosenVariable != null && chosenValue != null && !variableList.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public  ObservableList<String> getVariableList() {
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
