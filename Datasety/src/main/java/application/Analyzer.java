package application;

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

	public Analyzer() {
		dataMap = new HashMap<>();
		dataHeaders = new ArrayList<>();
	}

	public Map<String, ArrayList<String>> getDataMap() {
		return dataMap;
	}

	public List<String> getDataHeaders() {
		return dataHeaders;
	}
}
