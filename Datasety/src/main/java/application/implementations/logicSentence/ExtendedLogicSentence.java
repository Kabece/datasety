package application.implementations.logicSentence;

import application.interfaces.logicSentence.LogicSentence;
import javafx.collections.ObservableList;

/**
 * Created by pawel on 27.08.2016.
 */
public class ExtendedLogicSentence extends LogicSentence{
    LogicSentence basis;

    @Override
    public LogicSentence getNextSentencePart() {
        return getBasis();
    }

    public ExtendedLogicSentence(LogicSentence basis) {
        super(basis.getChosenPattern(), basis.getVariableList());
        this.basis = basis;
    }

    public LogicSentence getBasis() {
        return basis;
    }

}
