package application;

import constants.AnalyzerWorkType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kczurylo on 2016-08-10.
 * Klasa zajmująca się analizowaniem danych pod względem zadanej logiki
 */
@SuppressWarnings("WeakerAccess")
public class Analyzer {

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
	 * Podstawowy analizator wyrażeń
	 * @param logicSentence Analizowane wyrażenie
	 * @return true jeżeli wyrażenie spełnione, false w przeciwnym wypadku
	 */
	public boolean analyze(LogicSentence logicSentence) {
		// TODO
		return true;
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
