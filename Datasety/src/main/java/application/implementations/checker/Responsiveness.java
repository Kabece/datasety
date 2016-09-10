package application.implementations.checker;

import application.interfaces.checker.Checker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pawel on 26.08.2016.
 * <p>
 *     Implementacja wzorca responsywność
 *
 * </p>
 *
 */

/*
        TODO: chyba zeby to robilo wiekszy sens, powinno sie sortować dataset po timestampach.
        Na ten moment zakładam, że dataset jest posortowany
        P.S. nie daje sobie reki uciac, ze ten wzorzec jest dobrze zaimplementowany
*/

public class Responsiveness extends Checker {

    private int firstSentenceOccurenceIndex;
    private int secondSentenceOccurenceIndex;

    public boolean checkPattern(Map<String, Map<String, List<String>>> dataMap) {

        logger.info("Starting checking responsiveness pattern ...");
        List<String> values = dataMap.get(logicSentence.getChosenDataset()).get(logicSentence.getChosenVariable());

        switch (logicSentence.getChosenOperator()) {
            case EQ:

                for (int i = 0; i < values.size() ; i++) {
                    if(values.get(i).equals(logicSentence.getChosenValue())) {
                        firstSentenceOccurenceIndex = i;
                        if (checkSuccessorEvent(dataMap.get(logicSentence.getChosenDataset()).get(logicSentence.getNextSentencePart().getChosenVariable()),i)) {
                           boolean duplication = false;

                           for (int j = firstSentenceOccurenceIndex + 1; j < secondSentenceOccurenceIndex; j ++) {
                               if(values.get(j).equals(values.get(i))) {
                                   duplication = true;
                                   break;
                               }
                           }
                           if(!duplication) return true;
                        }
                    }
                }
                break;
            case NE:
                for (int i = 0; i < values.size() ; i++) {
                    if(!values.get(i).equals(logicSentence.getChosenValue())) {
                        firstSentenceOccurenceIndex = i;
                        if (checkSuccessorEvent(dataMap.get(logicSentence.getChosenDataset()).get(logicSentence.getNextSentencePart().getChosenVariable()),i)) {
                           boolean duplication = false;

                           for (int j = firstSentenceOccurenceIndex + 1; j < secondSentenceOccurenceIndex; j ++) {
                               if(!values.get(j).equals(values.get(i))) {
                                   duplication = true;
                                   break;
                               }
                           }
                           if(!duplication) return true;
                        }
                    }
                }
                break;
            default:
                logger.warn(
                        "Warning in checkResponsiveness, default switch option used! chosenOperator={}, chosenVariable={}, chosenValue={}",
                        logicSentence.getChosenOperator(), logicSentence.getChosenVariable(), logicSentence.getChosenValue());
        }

        logger.info("Finished checking responsiveness pattern!");
        return false;
    }

    private boolean checkSuccessorEvent(List<String> values, int recordPosition) {

        switch (logicSentence.getNextSentencePart().getChosenOperator()) {
            case EQ:
                for (int i = recordPosition; i < values.size(); i++) {
                    if (values.get(i).equals(logicSentence.getNextSentencePart().getChosenValue())) {
                        secondSentenceOccurenceIndex = i;
                        return true;
                    }
                }
                break;
            case NE:
                for (int i = recordPosition; i < values.size(); i++) {
                    if (!values.get(i).equals(logicSentence.getNextSentencePart().getChosenValue())) {
                        secondSentenceOccurenceIndex = i;
                        return true;
                    }
                }
                break;
            default:
                logger.warn(
                        "Warning while checking successor event (Responsiveness pattern), default switch option used! chosenOperator={}, chosenVariable={}, chosenValue={}",
                        logicSentence.getNextSentencePart().getChosenOperator(), logicSentence.getNextSentencePart().getChosenVariable(), logicSentence.getNextSentencePart().getChosenValue());
        }
        return false;
    }
}
