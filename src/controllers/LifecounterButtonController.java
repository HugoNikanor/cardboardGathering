package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

public class LifecounterButtonController implements Initializable {

	private IntegerProperty observableHealth;
	private IntegerProperty observablePoison;

	private static int defaultHealth;
	private static int defaultPoison;

	@FXML
	private GridPane innerNumber;

	@FXML
	private LifecounterNumberController innerNumberController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		observableHealth = new SimpleIntegerProperty();
		observablePoison = new SimpleIntegerProperty();

		innerNumberController.  bindNumbers( observableHealth, observablePoison );
	}

	public LifecounterButtonController setDefaultValues( int health, int poison ) {
		defaultHealth = health;
		defaultPoison = poison;

		return this;
	}

	public LifecounterButtonController 
		bindNumbers( Property<Number> playerHealth, Property<Number> playerPoison ) {
		observableHealth.bindBidirectional( playerHealth );
		observablePoison.bindBidirectional( playerPoison );

		return this;
	}

	@FXML
	private void healthUp( Event e ) {
		observableHealth.set( observableHealth.get() + 1 );
	}
	@FXML
	private void healthDown( Event e ) {
		observableHealth.set( observableHealth.get() - 1 );
	}
	@FXML
	private void poisonUp( Event e ) {
		observablePoison.set( observablePoison.get() + 1 );
	}
	@FXML
	private void poisonDown( Event e ) {
		observablePoison.set( observablePoison.get() - 1 );
	}
	@FXML
	private void resetValues( Event e ) {
		observableHealth.set( defaultHealth );
		observablePoison.set( defaultPoison );
	}
}
