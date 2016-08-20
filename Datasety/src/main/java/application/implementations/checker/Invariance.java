package application.implementations.checker;

import application.interfaces.checker.Checker;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by pawel on 19.08.2016.
 * <p>
 *     Implementacja wzorca niezmienniczość(bezpieczeństwo)
 * </p>
 */
public class Invariance extends Checker {

    public boolean checkPattern(Map<String, ArrayList<String>> dataMap) {
        logger.info("Starting checking invariance pattern ...");

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

        logger.info("Finished checking invariance pattern!");
        return true;
    }
}
