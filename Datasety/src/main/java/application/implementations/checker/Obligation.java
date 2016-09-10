package application.implementations.checker;

import application.interfaces.checker.Checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pawel on 26.08.2016.
 *  *<p>
 *     Implementacja wzorca obligacja
 * </p>
 */

/* I have no idea what I am doing, ale tak by wnikało z tego pdfa  */
public class Obligation extends Checker{

    public boolean checkPattern(Map<String, Map<String, List<String>>> dataMap) {

        logger.info("Starting checking obligation pattern ...");

                Existence sentenceOneChecker = new Existence();
                Existence sentenceTwoChecker = new Existence();

                sentenceOneChecker.setLogicSentence(logicSentence);
                sentenceTwoChecker.setLogicSentence(logicSentence.getNextSentencePart());

                if(sentenceOneChecker.checkPattern(dataMap) && sentenceTwoChecker.checkPattern(dataMap)) return true;

        logger.info("Finished checking obligation pattern!");
        return false;
    }

}
