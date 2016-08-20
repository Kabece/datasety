package application.implementations.checker;

import application.interfaces.checker.Checker;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by pawel on 19.08.2016.
 * <p>
 *     Implementacja wzorca trwałość (persystencja)
 * </p>
 */
public class Persistence extends Checker {

    public boolean checkPattern(Map<String, ArrayList<String>> dataMap) {
        logger.info("Starting checking persistence pattern ...");
        boolean hasOccurred = false;

        switch (logicSentence.getChosenOperator()) {
            case EQ:
                for (String value : dataMap.get(logicSentence.getChosenVariable())) {
                    if (!hasOccurred && value.equals(logicSentence.getChosenValue())) {
                        hasOccurred = true;
                        continue;
                    }
                    if (hasOccurred && !value.equals(logicSentence.getChosenValue())) {
                        hasOccurred = false;
                    }
                }
                break;

            case NE:
                for (String value : dataMap.get(logicSentence.getChosenVariable())) {
                    if (!hasOccurred && !value.equals(logicSentence.getChosenValue())) {
                        hasOccurred = true;
                        continue;
                    }
                    if (hasOccurred && value.equals(logicSentence.getChosenValue())) {
                        hasOccurred = false;
                    }
                }
                break;

            default:
                logger.warn(
                        "Warning in checkPersistence, default switch option used! chosenOperator={}, chosenVariable={}, chosenValue={}",
                        logicSentence.getChosenOperator(), logicSentence.getChosenVariable(), logicSentence.getChosenValue());
                break;
        }

        logger.info("Finished checking persistence pattern!");
        return hasOccurred;
    }


}
