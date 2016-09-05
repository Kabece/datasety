package application.interfaces.analyzer;

import application.interfaces.logicSentence.LogicSentence;
import application.implementations.checker.*;
import application.interfaces.checker.Checker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created by pawel on 19.08.2016.
 * <p>
 *     Interfejs zajmujący się analizą zbioru danych na podstawie zadanych formuł logicznych.
 * </p>
 */
public abstract class Analyzer {

    public Logger logger = LogManager.getLogger(Analyzer.class.getName());

    private Checker checker;
    private Map<String,LogicSentence> logicSentences;
    private Map<String, ArrayList<String>> dataMap;
    private List<String> dataHeaders;

    public Analyzer() {
        this.dataHeaders = new ArrayList<>();
        this.dataMap = new HashMap<>() ;
    }

    //TODO: tymczasowo bo nie wiem czym różnią się tryby analizera -> info w implementacjach

    /**
     * Funkcja analizująca zbiór danych pod kątem występowania listy wzorców określonych w GUI aplikacji.
     * @return boolean True jeżeli wszystkie wybrane wzorce występują w zbiorze danych, false jeżeli przynajmniej jeden nie występuje.
     */
    public boolean analyzeList() {
        logger.info("Starting analysing list of logic sentences ... ");

        List<Boolean> outcomes = new ArrayList<>();
        Iterator iterator = logicSentences.entrySet().iterator();


        while(iterator.hasNext()) {
           Map.Entry hashMapElement = (Map.Entry) iterator.next();

           switch(((LogicSentence)hashMapElement.getValue()).getChosenPattern()) {
               case ABSENCE:
                   checker = new Absence();
                   break;
               case EXISTENCE:
                   checker = new Existence();
                   break;
               case INVARIANCE:
                   checker = new Invariance();
                   break;
               case PERSISTENCE:
                   checker = new Persistence();
                   break;
               case RESPONSIVENESS:
                   checker = new Responsiveness();
                   break;
               case OBLIGATION:
                   checker = new Obligation();
                   break;
               default:
                    logger.error("Something gone bad and none of checker was chosen!");

           }
           checker.setLogicSentence((LogicSentence)hashMapElement.getValue());
           outcomes.add(checker.checkPattern(dataMap));
        }

        for (int i = 0; i < outcomes.size(); i++) {
            if (!outcomes.get(i)) {
                logger.debug("Process analyzeList, returned with false. First false logic sentence = {}", outcomes.get(i));
                return false;
            }
        }

        logger.info("Finished analyzing sentence list!");
        return true;
    }

    // FIXME: jesli zalezne od trybu analizatora, zmienic na metode abstrakcyjna
    public boolean isReady() {
        if (dataMap != null && dataHeaders != null /* && analyzerWorkType != null */) {
            // TODO Dodać żeby sprawdzał te kolekcje też w głąb
            return true;
        } else {
            return false;
        }
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    public Map<String, LogicSentence> getLogicSentences() {
        return logicSentences;
    }

    public void setLogicSentences(Map<String, LogicSentence> logicSentences) {
        this.logicSentences = logicSentences;
    }

    public Map<String, ArrayList<String>> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, ArrayList<String>> dataMap) {
        this.dataMap = dataMap;
    }

    public List<String> getDataHeaders() {
        return dataHeaders;
    }
}
