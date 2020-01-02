package postpone.the.inevitable.game;

import java.util.ArrayList;


import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import postpone.the.inevitable.pathfinder.SearchNode;

import android.graphics.Point;

public abstract class AbstractTower extends Sprite {

	//Final static variables
	public static enum TOWER_TYPE {
		NO_TOWER, STONE_TOWER, TOTEM_TOWER, TEMPORARY_TOWER
	}
	
	//A enumeration with the offsets
	public static enum TOWER_OFFSET {
	    LEFT, CENTER, RIGHT 
	}	
	
	//Final
	protected final MazeActivity mBaseActivity;
	protected final float x;
	protected final float y;
	
	private final TOWER_TYPE towertype;

	private final int tower_offset_value;
	
	protected final TemporaryTower temporaryTower;

	public boolean isTowerActive = true;
	
	protected boolean startMovingTower = false;
	
	/**
	 * Constructor
	 * @param pX
	 * @param pY
	 * @param pTextureRegion
	 * @param pBaseActivity
	 * @param towertype
	 * @param vertexBufferObjectManager
	 * @param temporaryTower
	 */
	public AbstractTower(float pX, float pY, ITextureRegion pTextureRegion, MazeActivity pBaseActivity, TOWER_TYPE towertype, VertexBufferObjectManager vertexBufferObjectManager, TemporaryTower temporaryTower, TOWER_OFFSET tower_marker_offset) {
		super(pX, pY, pTextureRegion,vertexBufferObjectManager);
		x = pX;
		y = pY;
		mBaseActivity = pBaseActivity;
		this.towertype = towertype;
		this.temporaryTower = temporaryTower;
		
		if (tower_marker_offset.equals(TOWER_OFFSET.LEFT)) {
			tower_offset_value = -60;
		} else if (tower_marker_offset.equals(TOWER_OFFSET.RIGHT)) {
			tower_offset_value = 60;			
		}
		else {
			tower_offset_value = 0;			
		}
		
	}

	/**
	 * Simple method used to deactivate this tower
	 */
	public void deactivateTower() {
		this.setAlpha(0.2f);
		this.isTowerActive = false;
    	this.setPosition(x, y); 
	}

	/**
	 * 
	 */
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        
		if (!isTowerActive) {
        	this.setPosition(x, y);          
			return true;
		}
		
		//The player drops the tower. Either he does not want to place it or he has decided where to place the tower.
		if(pSceneTouchEvent.isActionUp() || pSceneTouchEvent.isActionCancel()) {
			this.placeTower();			
			mBaseActivity.releaseExclusivity(this.towertype);
			startMovingTower = false;
		}
		else if (pSceneTouchEvent.isActionDown()) {

			if (!startMovingTower) {

				//Ask main if we can start moving this tower
				startMovingTower = mBaseActivity.requestExclusivity(this.towertype);
				
				if (startMovingTower) {
					//Put tower on top
					this.setZIndex(2);
					mBaseActivity.pScene.sortChildren();				
			
					//Set the opacity
					this.setAlpha(0.5f);
				}				
			}
			
		}
		else if (pSceneTouchEvent.isActionMove()) {

			if (startMovingTower) {
				//Set position
				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2 + tower_offset_value, pSceneTouchEvent.getY() - this.getHeight() / 2);
		
				//IF we are inside the gaming area let the player place tower.
				float posX = this.getX() + 60;
				float posY = this.getY()+ 60;
				
				if (inSideMaze(posX,posY)) {
					
					posX = posX - MazeActivity.MAZE_LEFT;
					posY = posY - MazeActivity.MAZE_BOT;
					
					mBaseActivity.highLightNode(posX,posY);						
				}
				else {
					mBaseActivity.stopHighLightLastNode();
				}
			}
	    }
		else {
			//Otherwise we reset the tower
			this.setPosition(x, y);            
		}
	
		return true;
	}
	
	/**
	 * place tower on map
	 */
	protected void placeTower() {

		//Set the opacity
		this.setAlpha(1.0f);
	
		final SearchNode node = mBaseActivity.getHighLightedNode();
		
		if (node != null) {

			if (node.Walkable) {
											
			    node.Walkable = false;
                final ArrayList<Point> shortestPath = mBaseActivity.returnShortestPath();

                if (shortestPath.size() > 0) {
                	
                	if (mBaseActivity.buyTower(towertype)) {	                        		
                    	node.nodeSprite.setCurrentTileIndex(towertype.ordinal());
        				node.nodeSprite.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        				node.nodeSprite.setVisible(true);
        				node.towerType = towertype;
        				
        				temporaryTower.setNode(node);
        				
        				mBaseActivity.playSound(MazeActivity.ID_BUY_SOUND);
                	}
                	else {
	            		//This should never happen
	                	node.Walkable = true;
	                	node.towerType = TOWER_TYPE.NO_TOWER;
                	}
                }
                else {
                	node.Walkable = true;
                	node.towerType = TOWER_TYPE.NO_TOWER;
					mBaseActivity.showMessage(MazeActivity.ID_TOWER_ERROR_2);
                }
			}
			else {
				mBaseActivity.showMessage(MazeActivity.ID_TOWER_ERROR_1);
			}
		}
	
		//Always reset tower and stophighlighting the node
		this.setPosition(x, y);
		//Last we stop higlightning the node
		this.mBaseActivity.stopHighLightLastNode();
	}

	/**
	 * Is the position inside the level(maze)
	 * @param posX
	 * @param posY
	 * @return
	 */
	private static boolean inSideMaze(float posX, float posY) {
		return (posY < MazeActivity.MAZE_TOP && posY >= MazeActivity.MAZE_BOT && posX >= MazeActivity.MAZE_LEFT && posX < MazeActivity.MAZE_RIGHT);
	}

	/**
	 * reset tower back to original position
	 */
	public void resetPosition() {
		
		startMovingTower = false;
		
		if (isTowerActive) {
			//If active, reset the tower
			this.setPosition(x, y);
			this.setAlpha(1.0f);
		}
		else {
			//Otherwise reset using the deactivate method
			deactivateTower();
		}
	}
	
}
