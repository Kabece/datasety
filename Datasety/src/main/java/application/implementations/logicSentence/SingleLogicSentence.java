package application.implementations.logicSentence;

import application.interfaces.logicSentence.LogicSentence;
import enums.PatternType;
import javafx.collections.ObservableList;

/**
 * Created by pawel on 27.08.2016.
 */
public class SingleLogicSentence extends LogicSentence{

    @Override
    public LogicSentence getNextSentencePart() {
        return this;
    }

    public SingleLogicSentence(PatternType patternType, ObservableList<String> variables) {
        super(patternType, variables);
    }

}
