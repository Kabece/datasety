package application;

import enums.AnalyzerWorkType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kczurylo on 2016-08-10.
 * <p>
 *    Klasa zajmująca się analizowaniem danych pod względem zadanej logiki.
 * </p>
 */
@SuppressWarnings("WeakerAccess")
public class Analyzer {

	private Logger logger = LogManager.getLogger(Analyzer.class.getName());

	private Map<String, ArrayList<String>> dataMap;
	private List<String> dataHeaders;
	private AnalyzerWorkType analyzerWorkType;

	/**
	 * Podstawowy konstruktor
	 */
	public Analyzer() {
		dataMap = new HashMap<>();
		dataHeaders = new ArrayList<>();
	}

	/**
	 * Analizator dla wielu wyrażeń logicznych połączonych
	 *
	 * @param logicSentences lista analizowanych zdań.
	 * @return true jeżeli wyrażenia spełnione, false w przeciwnym wypadku
	 */
	public boolean analyzeList(List<LogicSentence> logicSentences) {
		logger.info("Start analyzeList");

		List<Boolean> outcomes = new ArrayList<>();

		for (LogicSentence logicSentence : logicSentences) {
			outcomes.add(analyze(logicSentence));
		}
		for (int i = 0; i < outcomes.size(); i++) {
			if (!outcomes.get(i)) {
				logger.debug("Process analyzeList, returned with false. First false logic sentence = {}", outcomes.get(i));
				return false;
			}
		}

		logger.info("Finish analyzeList");
		return true;
	}
	/**
	 * Podstawowy analizator wyrażeń
	 *
	 * @param logicSentence Analizowane zdanie
	 * @return true jeżeli wyrażenie spełnione, false w przeciwnym wypadku
	 */
	public boolean analyze(LogicSentence logicSentence) {
		logger.info("Start analyze");
		boolean outcome = false;

		switch (logicSentence.getChosenPattern()) {
			case EXISTENCE:
				outcome = checkExistence(logicSentence);
				break;

			case ABSENCE:
				outcome = checkAbsence(logicSentence);
				break;

			case INVARIANCE:
				outcome = checkInvariance(logicSentence);
				break;

			case PERSISTENCE:
				outcome = checkPersistence(logicSentence);
				break;

			default:
				logger.warn(
						  "Warning in analyze, default switch option used! chosenOperator={}, chosenVariable={}, chosenValue={}",
						  logicSentence.getChosenOperator(), logicSentence.getChosenVariable(), logicSentence.getChosenValue());
				break;

		}

		logger.info("Finish analyze");
		return outcome;
	}

	// TODO Bardzo powtarzalny kod - przekminić jakąś ekstrakcję tego
	/**
	 * Sprawdza wzorzec EXISTENCE - możliwość (gwarantowalność)
	 *
	 * @param logicSentence Analizowane zdanie
	 * @return true jeżeli wzorzec spełniony, false w przeciwnym wypadku
	 */
	private boolean checkExistence(LogicSentence logicSentence) {
		logger.info("Start checkExistence");

		switch (logicSentence.getChosenOperator()) {
			case EQ:
				for (String value : dataMap.get(logicSentence.getChosenVariable())) {
					if (value.equals(logicSentence.getChosenValue())) {
						return true;
					}
				}
				break;

			case NE:
				for (String value : dataMap.get(logicSentence.getChosenVariable())) {
					// NOT EQUALS TO NIE TO SAMO CO ABSENCE!
					if (!value.equals(logicSentence.getChosenValue())) {
						return true;
					}
				}
				break;

			default:
				logger.warn(
						  "Warning in checkExistence, default switch option used! chosenOperator={}, chosenVariable={}, chosenValue={}",
						  logicSentence.getChosenOperator(), logicSentence.getChosenVariable(), logicSentence.getChosenValue());
				break;
		}

		logger.info("Finish checkExistence");
		return false;
	}

	/**
	 * Sprawdza wzorzec ABSENCE - absencja
	 *
	 * @param logicSentence Analizowane zdanie
	 * @return true jeżeli wzorzec spełniony, false w przeciwnym wypadku
	 */
	private boolean checkAbsence(LogicSentence logicSentence) {
		logger.info("Start checkAbsence");

		switch (logicSentence.getChosenOperator()) {
			case EQ:
				for (String value : dataMap.get(logicSentence.getChosenVariable())) {
					if (value.equals(logicSentence.getChosenValue())) {
						return false;
					}
				}
				break;

			case NE:
				for (String value : dataMap.get((logicSentence.getChosenVariable()))) {
					if (!value.equals(logicSentence.getChosenValue())) {
						return false;
					}
				}
				break;

			default:
				logger.warn("Warning in checkAbsence, default switch option used! chosenOperator={}, chosenVariable={}, chosenValue={}", logicSentence.getChosenOperator(), logicSentence.getChosenVariable(), logicSentence.getChosenValue());
				break;
		}

		logger.info("Finish checkAbsence");
		return true;
	}

	/**
	 * Sprawdza wzorzec INVARIANCE - niezmienniczość (bezpieczeństwo)
	 *
	 * @param logicSentence Analizowane zdanie
	 * @return true jeżeli wzorzec spełniony, false w przeciwnym wypadku
	 */
	private boolean checkInvariance(LogicSentence logicSentence) {
		logger.info("Start checkInvariance");

		switch (logicSentence.getChosenOperator()) {
			case EQ:
				for (String value : dataMap.get(logicSentence.getChosenVariable())) {
					if (!value.equals(logicSentence.getChosenValue())) {
						return false;
					}
				}
				break;

			case NE:
				for (String value : dataMap.get(logicSentence.getChosenVariable())) {
					if (value.equals(logicSentence.getChosenValue())) {
						return false;
					}
				}
				break;

			default:
				logger.warn(
						  "Warning in checkInvariance, default switch option used! chosenOperator={}, chosenVariable={}, chosenValue={}",
						  logicSentence.getChosenOperator(), logicSentence.getChosenVariable(), logicSentence.getChosenValue());
				break;
		}

		logger.info("Finish checkInvariance");
		return true;
	}

	/**
	 * Sprawdza wzorzec PERSISTENCE - persystencja (trwałość)
	 *
	 * @param logicSentence Analizowane zdanie
	 * @return true jeżeli wzorzec spełniony, false w przeciwnym wypadku
	 */
	private boolean checkPersistence(LogicSentence logicSentence) {
		logger.info("Start checkPersistence");
		boolean hasOccured = false;

		switch (logicSentence.getChosenOperator()) {
			case EQ:
				for (String value : dataMap.get(logicSentence.getChosenVariable())) {
					if (!hasOccured && value.equals(logicSentence.getChosenValue())) {
						hasOccured = true;
						continue;
					}
					if (hasOccured && !value.equals(logicSentence.getChosenValue())) {
						hasOccured = false;
					}
				}
				break;

			case NE:
				for (String value : dataMap.get(logicSentence.getChosenVariable())) {
					if (!hasOccured && !value.equals(logicSentence.getChosenValue())) {
						hasOccured = true;
						continue;
					}
					if (hasOccured && value.equals(logicSentence.getChosenValue())) {
						hasOccured = false;
					}
				}
				break;

			default:
				logger.warn(
						  "Warning in checkPersistence, default switch option used! chosenOperator={}, chosenVariable={}, chosenValue={}",
						  logicSentence.getChosenOperator(), logicSentence.getChosenVariable(), logicSentence.getChosenValue());
				break;
		}

		logger.info("Finish checkPersistence");
		return hasOccured;
	}

	/**
	 * Sprawdza czy analizator jest gotowy do działania
	 *
	 * @return true jeżeli jest gotowy, false w przeciwnym wypadku
	 */
	public boolean isReady() {
		if (dataMap != null && dataHeaders != null && analyzerWorkType != null) {
			// TODO Dodać żeby sprawdzał te kolekcje też w głąb
			return true;
		} else {
			return false;
		}
	}

	public Map<String, ArrayList<String>> getDataMap() {
		return dataMap;
	}

	public List<String> getDataHeaders() {
		return dataHeaders;
	}

	public AnalyzerWorkType getAnalyzerWorkType() {
		return analyzerWorkType;
	}

	public void setAnalyzerWorkType(AnalyzerWorkType analyzerWorkType) {
		this.analyzerWorkType = analyzerWorkType;
	}
}
