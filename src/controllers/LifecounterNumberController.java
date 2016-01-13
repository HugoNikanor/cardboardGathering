package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class LifecounterNumberController implements Initializable {

	private IntegerProperty observableHealth;
	private IntegerProperty observablePoison;

	@FXML
	private Text lifeText;

	@FXML
	private Text poisonText;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void healthChange( ObservableValue<Number> ov, Number oVal, Number nVal ) {
		lifeText.setText( Integer.toString( nVal.intValue() ) );
	}

	@FXML
	private void poisonChange( ObservableValue<Number> ov, Number oVal, Number nVal ) {
		poisonText.setText( Integer.toString( nVal.intValue() ) );
	}

	public void bindNumbers( Property<Number> playerHealth, Property<Number> playerPoison ) {
		observableHealth.bindBidirectional( playerHealth );
		observablePoison.bindBidirectional( playerPoison );
	}


}
