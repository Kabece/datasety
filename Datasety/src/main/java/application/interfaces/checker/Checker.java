package application.interfaces.checker;

import application.LogicSentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by pawel on 19.08.2016.
 */
public abstract class Checker {

    public Logger logger = LogManager.getLogger(Checker.class.getName());

    public LogicSentence logicSentence;

    public abstract boolean checkPattern(Map<String, ArrayList<String>> dataMap);

    public LogicSentence getLogicSentence() {
        return logicSentence;
    }

    public void setLogicSentence(LogicSentence logicSentence) {
        this.logicSentence = logicSentence;
    }

}
