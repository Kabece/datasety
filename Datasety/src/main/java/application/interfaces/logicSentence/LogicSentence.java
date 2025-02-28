package application.interfaces.logicSentence;

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
public abstract class LogicSentence {

	private ObservableList<String> variableList = FXCollections.observableArrayList();

	private PatternType chosenPattern;
	private OperatorType chosenOperator;
	private String chosenDataset;
	private String chosenVariable;
	private String chosenValue;

	public abstract LogicSentence getNextSentencePart();

	public LogicSentence(PatternType patternType, ObservableList<String> variables) {
		chosenPattern = patternType;
		variableList = variables;
	}

	/**
	 * Sprawdza czy zdanie logiczne jest kompletne
	 * @return true jeżeli kompletne, false w przeciwnym wypadku
	 */
	public boolean isComplete() {
		if (chosenPattern != null&& chosenDataset != null && chosenOperator != null && chosenVariable != null && chosenValue != null && !variableList.isEmpty()) {
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

	public String getChosenDataset() {
		return chosenDataset;
	}

	public void setChosenDataset(String chosenDataset) {
		this.chosenDataset = chosenDataset;
	}

	@Override
	public String toString() {
		return "LogicSentence{" +
				"chosenPattern=" + chosenPattern +
				", chosenOperator=" + chosenOperator +
				", chosenDataset='" + chosenDataset + '\'' +
				", chosenVariable='" + chosenVariable + '\'' +
				", chosenValue='" + chosenValue + '\'' +
				'}';
	}
}
