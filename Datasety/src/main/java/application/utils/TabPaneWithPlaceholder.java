package application.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Created by pawel on 11.09.2016.
 */
public class TabPaneWithPlaceholder extends VBox {
    private TabPane tabPane;
    private Text placeholderHeader;
    private Text placeholder;


    public TabPaneWithPlaceholder(String placeholderHeaderText, String placeholderText, Tab... tabs )
    {

        //TODO: Ustawienie placeholdera po zamknieciu taba

        this.tabPane = new TabPane( tabs );
        placeholderHeader = new Text( placeholderHeaderText );
        placeholderHeader.setFont( Font.font( null, FontWeight.BOLD, 20 ) );

        placeholder = new Text(placeholderText);


        BooleanBinding bb = Bindings.isEmpty( tabPane.getTabs() );
        placeholderHeader.visibleProperty().bind( bb );
        placeholderHeader.managedProperty().bind( bb );
        placeholder.visibleProperty().bind( bb );
        placeholder.managedProperty().bind( bb );

        getChildren().addAll( placeholderHeader, placeholder,tabPane );
        alignmentProperty().set(Pos.CENTER);
    }

    public ObservableList<Tab> getTabs()
    {
        return tabPane.getTabs();
    }

    public TabPane getTabPane() {
        return tabPane;
    }
}
