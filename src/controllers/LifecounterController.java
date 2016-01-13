package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class LifecounterController implements Initializable {

	@FXML
	private Button healthDownBtn;

	@FXML
	private Button healthUpBtn;

	// these should be bound to the health of the player
	private IntegerProperty observableHealth;
	private IntegerProperty observablePoison;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		observableHealth = new SimpleIntegerProperty();
		observablePoison = new SimpleIntegerProperty();

	}

	public void setDefaultNumbers( int health, int poison ) {
		observableHealth.set( health );
		observablePoison.set( poison );
	}

	/**
	 * @return the observableHealth
	 */
	public IntegerProperty getObservableHealth() {
		return observableHealth;
	}

	/**
	 * @return the observablePoison
	 */
	public IntegerProperty getObservablePoison() {
		return observablePoison;
	}
}
