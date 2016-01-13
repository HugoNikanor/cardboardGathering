package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class LifecounterNumberController implements Initializable {

	private IntegerProperty observableHealth;
	private IntegerProperty observablePoison;

	@FXML
	private Text healthText;

	@FXML
	private Text poisonText;

	@Override
	public void initialize(URL location, ResourceBundle resources) { 
		observableHealth = new SimpleIntegerProperty();
		observablePoison = new SimpleIntegerProperty();

		observableHealth.addListener( (ov, oVal, nVal) -> {
			healthText.setText( nVal.toString() );
		});
		observablePoison.addListener( (ov, oVal, nVal) -> {
			poisonText.setText( nVal.toString() );
		});
	}

	public LifecounterNumberController 
		bindNumbers( Property<Number> playerHealth, Property<Number> playerPoison ) {
		observableHealth.bindBidirectional( playerHealth );
		observablePoison.bindBidirectional( playerPoison );

		return this;
	}
}
