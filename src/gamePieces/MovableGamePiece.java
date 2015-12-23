package gamePieces;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import network.Connection;

import serverPackets.CardFocusPacket;

public class MovableGamePiece extends StackPane {
	protected long pieceId;
	protected static long PIECE_ID_COUNTER_NEW = 0;

	protected double width;
	protected double height;

	/**
	 * Used when draging the card if the screen is resized <br>
	 */
	private double scaleFactor;

	protected Connection connection;
	protected boolean shouldSend;

	// These three are used by the SendCardDataThread in player
	protected double oldX;
	protected double oldY;
	protected double oldRotate;

	// These are the boundries of the battlefield node that the card is located in
	// TODO Should these maybe be static...
	protected double containerSizeX;
	protected double containerSizeY;

	/**
	 * used in playes SendCardDataThread to check if it's safe
	 * to let the card go into a collection, among other possible
	 * future uses.
	 */
	protected boolean beingUsed;

	private MouseEventHandler mouseEventHandler;

	/**
	 * Used as a check so only one card can be marked as current at a time, <br>
	 * Allows to demark the old "currentCard" as non current.
	 */
	private static MovableGamePiece currentPiece;

	/**
	 * The current location of the piece
	 */
	protected Collections currentLocation;

	/**
	 * If the piece should be able to be zoomed in if scrolled over
	 */
	private boolean zoomable;

	public MovableGamePiece( double width, double height, Collections position ) {
		currentLocation = position;
		zoomable = true;

		this.height = height;
		this.width = width;

		// JavaFX
		this.setHeight(height);
		this.setWidth(width);
		this.setMinSize(this.getWidth(), this.getHeight());
		this.setPrefSize(this.getWidth(), this.getHeight());

		this.getStyleClass().add( "movable-game-piece" );

		this.setCursor( Cursor.HAND );

		mouseEventHandler = new MouseEventHandler();

		// TODO these are only applicable for the own cards
		this.addEventHandler( MouseEvent.MOUSE_ENTERED, mouseEventHandler );
		this.addEventHandler( MouseEvent.MOUSE_PRESSED, mouseEventHandler );
		this.addEventHandler( MouseEvent.MOUSE_RELEASED, mouseEventHandler );
		this.addEventHandler( MouseEvent.MOUSE_DRAGGED, mouseEventHandler );
		this.addEventHandler( ScrollEvent.ANY, new ScrollEventHandler() );

		this.scaleFactor = 1;

		// if there is a better way to do this, tell me
		containerSizeX = Battlefield.WIDTH - this.getWidth();
		containerSizeY = Battlefield.HEIGHT - this.getHeight();

		this.setFocusTraversable( true );
	}

	public void giveFocus() {
		if( shouldSend && currentPiece != this ) {
			try {
				currentPiece.setId( null );
			} catch( NullPointerException e ) {
				// On the first go round nothing have been
				// asigned to currentPiece yet
			}
			currentPiece = this;

			connection.sendPacket( new CardFocusPacket( this.getPieceId()) );
			this.requestFocus();
			this.setId( "has-focus" );
		}
		this.toFront();
	}

	/**
	 * @return the pieceId
	 */
	public long getPieceId() {
		return pieceId;
	}

	/**
	 * @return the currentPiece
	 */
	public static MovableGamePiece getCurrentPiece() {
		return currentPiece;
	}

	/**
	 * @return the currentLocation
	 */
	public Collections getCurrentLocation() {
		return currentLocation;
	}

	/**
	 * @param currentLocation the currentLocation to set
	 */
	public void setCurrentLocation(Collections currentLocation) {
		this.currentLocation = currentLocation;
	}

	/**
	 * @return the zoomable
	 */
	public boolean isZoomable() {
		return zoomable;
	}

	/**
	 * @param zoomable the zoomable to set
	 */
	public void setZoomable(boolean zoomable) {
		this.zoomable = zoomable;
	}

	/**
	 * Zooms the card between two levels when the card is scrolled
	 * over
	 */
	private class ScrollEventHandler implements EventHandler<ScrollEvent> {
		@Override
		public void handle(ScrollEvent event) {
			if( zoomable ) {
				if( event.getDeltaY() > 0 )
					smoothSetScale( 2.5, 50 );
				else
					smoothSetScale( 1, 50 );
			}
		}
	}

	public void smoothSetScale( double scale ) {
		smoothSetScale( scale, 50 );
	}

	public void smoothSetScale( double scale, double duration ) {
		ScaleTransition st = new ScaleTransition( Duration.millis(duration), this );

		System.out.println( scale );
		st.setToX( scale );
		st.setToY( scale );

		st.play();
	}

	public void smoothSetRotate( double rotation ) {
		smoothSetRotate( rotation, 500 );
	}

	public void smoothSetRotate( double rotation, int duration ) {
		RotateTransition rt;
		rt = new RotateTransition( Duration.millis(duration), this );
		rt.setToAngle(rotation);
		rt.play();
	}

	// TODO rewrite this to work as the other smoothMove methods
	public void smoothFlip( double rotation ) {
		RotateTransition rt;
		if( this.getRotate() == 0 ) {
			// Rotates the card by 90 degrees
			// Always rotates clockwise
			rt = new RotateTransition(Duration.millis(300), this);
			rt.setAxis( Rotate.Y_AXIS );
			rt.setByAngle(rotation);
			rt.play();
		} else {
			// if the rotation isn't 90 degrees then return to 0 degrees
			// always rotates counter clockwise
			rt = new RotateTransition( Duration.millis(300), this );
			rt.setAxis(Rotate.Y_AXIS);
			rt.setByAngle( -1 * this.getRotate() );
			rt.play();
		}
	}

	/*
	public void giveFocus() {
		if( shouldSend && currentCard != this ) {
			try {
				currentCard.setId( null );
			} catch( NullPointerException e ) {
				// On the first go round nothing have been
				// asigned to currentCard yet
			}
			currentCard = this;

			connection.sendPacket( new CardFocusPacket(this.getCardId()) );
			this.requestFocus();
			this.setId( "has-focus" );
		}
		this.toFront();
	}
	*/

	/**
	 * Smoothly slides the card along,
	 * uses default movement speed of 30ms
	 * @param changeX how much the card should move horizontaly
	 * @param changeY how much the card should move verticly
	 */
	public void smoothMove( double changeX, double changeY ) {
		smoothMove( changeX, changeY, 30 );
	}
	/**
	 * Smoothly slides the card along
	 * @param changeX how much the card should move horizontaly
	 * @param changeY how much the card should move verticly
	 * @param moveSpeed how many milli secounds the move should last
	 */
	public void smoothMove( double changeX, double changeY, int moveSpeed ) {
		smoothPlace( getTranslateX() + changeX, getTranslateY() + changeY, moveSpeed );
	}

	/**
	 * Smoothly moves the card to the set coordinate <br>
	 * If the coordinate is out of bounds then it's set to the bound <br>
	 * This version uses the default transitionSpeed of 200
	 * @param posX the x coordinate the card should end up at
	 * @param posY the Y coordinate the card should end up at
	 */
	public void smoothPlace( double posX, double posY ) {
		smoothPlace( posX, posY, 200 );
	}

	/**
	 * Smoothly moves the card to the set coordinate <br>
	 * If the coordinate is out of bounds then it's set to the bound
	 * @param posX the x coordinate the card should end up at
	 * @param posY the Y coordinate the card should end up at
	 * @param transitionSpeed how long the animation should take, in milliseconds
	 */
	public void smoothPlace( double posX, double posY, int transitionSpeed ) {
		TranslateTransition tt;
		tt = new TranslateTransition( Duration.millis( transitionSpeed ), this );

		if( posX > Battlefield.WIDTH - this.getWidth() ) {
			tt.setToX( Battlefield.WIDTH - this.getWidth() );
		} else {
			tt.setToX( posX );
		}

		if( posY > Battlefield.HEIGHT - this.getHeight() ) {
			tt.setToY( Battlefield.HEIGHT - this.getHeight() );
		} else {
			tt.setToY( posY );
		}

		tt.play();

	}

	public void modifyTranslateX(double change) {
		this.setTranslateX(this.getTranslateX() + change);
	}
	public void modifyTranslateY(double change) {
		this.setTranslateY(this.getTranslateY() + change);
	}

	public void modifyRotate(double change) {
		this.setRotate(this.getRotate() + change);
	}

	/**
	 * @param containerSizeX the containerSizeX to set
	 */
	public void setContainerSizeX(double containerSizeX) {
		this.containerSizeX = containerSizeX;
	}

	/**
	 * @param containerSizeY the containerSizeY to set
	 */
	public void setContainerSizeY(double containerSizeY) {
		this.containerSizeY = containerSizeY;
	}

	/**
	 * @param scaleFactor the scaleFactor to set
	 */
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		//new Thread( new SendDataThread() ).start();
		this.shouldSend = true;
		this.connection = connection;
	}

	/**
	 * @return the shouldSend
	 */
	public boolean isShouldSend() {
		return shouldSend;
	}

	/**
	 * @return the oldX
	 */
	public double getOldX() {
		return oldX;
	}

	/**
	 * @param oldX the oldX to set
	 */
	public void setOldX(double oldX) {
		this.oldX = oldX;
	}

	/**
	 * @return the oldY
	 */
	public double getOldY() {
		return oldY;
	}

	/**
	 * @param oldY the oldY to set
	 */
	public void setOldY(double oldY) {
		this.oldY = oldY;
	}

	/**
	 * @return the oldRotate
	 */
	public double getOldRotate() {
		return oldRotate;
	}

	/**
	 * @param oldRotate the oldRotate to set
	 */
	public void setOldRotate(double oldRotate) {
		this.oldRotate = oldRotate;
	}

	/**
	 * @return the beingUsed
	 */
	public boolean isBeingUsed() {
		return beingUsed;
	}

	/**
	 * @param beingUsed the beingUsed to set
	 */
	public void setBeingUsed(boolean beingUsed) {
		this.beingUsed = beingUsed;
	}

	/**
	 * @return the mouseEventHandler
	 */
	public MouseEventHandler getMouseEventHandler() {
		return mouseEventHandler;
	}

	private class MouseEventHandler implements EventHandler<MouseEvent> {

		private double mouseInSceneX;
		private double mouseInSceneY;

		@Override
		public void handle(MouseEvent event) {
			if( currentLocation == Collections.BATTLEFIELD ) {
				if( event.getEventType() == MouseEvent.MOUSE_ENTERED ) {
					giveFocus();
				}

				// Used to get a difference in mouse position
				if( event.getEventType() == MouseEvent.MOUSE_PRESSED ) {
					this.mouseInSceneX = event.getSceneX();
					this.mouseInSceneY = event.getSceneY();
				}

				// Set the cursor back to normal when not draging any more
				if( event.getEventType() == MouseEvent.MOUSE_RELEASED ) {
					beingUsed = false;
					setCursor( Cursor.HAND );
				}

				// Moves the card when it's draged by the mouse
				if( event.getEventType() == MouseEvent.MOUSE_DRAGGED ) {
					beingUsed = true;
					setCursor(Cursor.MOVE);
					double xChange = event.getSceneX() - this.mouseInSceneX;
					double yChange = event.getSceneY() - this.mouseInSceneY;

					setTranslateX(getTranslateX() + xChange * ( 1/scaleFactor ));
					setTranslateY(getTranslateY() + yChange * ( 1/scaleFactor ));

					if( getTranslateX() < 0 ) {
						setTranslateX(0);
					}
					if( getTranslateY() < 0 ) {
						setTranslateY(0);
					}
					if( getTranslateX() > containerSizeX ) {
						setTranslateX(containerSizeX);
					}
					if( getTranslateY() > containerSizeY ) {
						setTranslateY(containerSizeY);
					}

					this.mouseInSceneX = event.getSceneX();
					this.mouseInSceneY = event.getSceneY();
				}
			}
		}
	}
}
