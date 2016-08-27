package application.interfaces.checker;

import application.interfaces.logicSentence.LogicSentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by pawel on 19.08.2016.
 * <p>
 *     Klasa abstrakcyjna - jej rozszerzenia implementują poszczególne wzorce używane w aplikacji
 * </p>
 */
public abstract class Checker {

    public Logger logger = LogManager.getLogger(Checker.class.getName());

    public LogicSentence logicSentence;

    /**
    * Metoda abstrakcyjna sprawdzająca, czy wzorzec występuje w zbiorze danych
     * @param dataMap Zbior danych
     * @return boolean Zwraca true jeśli wzorzec występuje, false jeżeli nie występuje
     *
    */
    public abstract boolean checkPattern(Map<String, ArrayList<String>> dataMap);

    public LogicSentence getLogicSentence() {
        return logicSentence;
    }

    public void setLogicSentence(LogicSentence logicSentence) {
        this.logicSentence = logicSentence;
    }

}
