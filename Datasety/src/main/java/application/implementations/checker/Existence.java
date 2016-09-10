package application.implementations.checker;

import application.interfaces.checker.Checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pawel on 19.08.2016.
 * <p>
 *     Implementacja wzorca możliwość (gwarantowalność)
 * </p>
 */
public class Existence extends Checker{

    public boolean checkPattern(Map<String, Map<String, List<String>>> dataMap) {
        logger.info("Starting checking existence pattern ...");

        switch (logicSentence.getChosenOperator()) {
            case EQ:
                for (String value : dataMap.get(logicSentence.getChosenDataset()).get(logicSentence.getChosenVariable())) {
                    if (value.equals(logicSentence.getChosenValue())) {
                        return true;
                    }
                }
                break;

            case NE:
                for (String value : dataMap.get(logicSentence.getChosenDataset()).get(logicSentence.getChosenVariable())) {
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

        logger.info("Finished checking existence pattern!");
        return false;
    }
}
