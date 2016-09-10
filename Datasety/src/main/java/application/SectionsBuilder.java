package application;

import application.implementations.analyzer.CheckAnalyzer;
import application.implementations.analyzer.ShowAnalyzer;
import application.implementations.logicSentence.SingleLogicSentence;
import application.implementations.logicSentence.ExtendedLogicSentence;
import application.interfaces.analyzer.Analyzer;
import application.interfaces.logicSentence.LogicSentence;
import enums.AnalyzerWorkType;
import enums.FileType;
import enums.OperatorType;
import enums.PatternType;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

import static enums.AnalyzerWorkType.CHECK;
import static enums.AnalyzerWorkType.SHOW;

/**
 * Created by kczurylo on 2016-08-09.
 * <p>
 *    Klasa pomocnicza, służąca do tworzenia komponentów widoku.
 * </p>
 */
@SuppressWarnings({ "unchecked", "WeakerAccess" })
// TODO Podzielić to jeszcze bardziej - na inicjalizację, dodawanie listenerów itd.
public class SectionsBuilder {

	private static Logger logger = LogManager.getLogger(SectionsBuilder.class.getName());


	private Analyzer analyzer;


	public SectionsBuilder() {
	}
}
