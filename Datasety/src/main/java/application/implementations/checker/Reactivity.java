package application.implementations.checker;

import application.interfaces.checker.Checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pawel on 26.08.2016.
 */
public class Reactivity extends Checker {
    @Override
    public boolean checkPattern(Map<String, Map<String, List<String>>> dataMap) {
        return false;
    }
}
