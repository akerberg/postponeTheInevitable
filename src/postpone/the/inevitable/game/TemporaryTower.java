package postpone.the.inevitable.game;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import postpone.the.inevitable.pathfinder.SearchNode;
import android.graphics.Point;

public class TemporaryTower extends AbstractTower {
	
	private SearchNode tempNode;
	private final float towerOffsetX;
	private final float towerOffsetY;
	
	public TemporaryTower(ITextureRegion pTextureRegion,
			MazeActivity pBaseActivity,
			VertexBufferObjectManager vertexBufferObjectManager,
			TOWER_OFFSET tower_marker_offset) {
		
		super(-100, -100, pTextureRegion, pBaseActivity, TOWER_TYPE.TEMPORARY_TOWER,
				vertexBufferObjectManager, null, tower_marker_offset);
		
		towerOffsetX = MazeActivity.MAZE_COLUMNS_SIZE/2 - this.mWidth/2;
		towerOffsetY = MazeActivity.MAZE_ROWS_SIZE/2 - this.mHeight/2;
		
		this.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    final IEntityModifier iem = new LoopEntityModifier(
	    		new SequenceEntityModifier(
		    		new AlphaModifier(1, 0.5f, 0.2f),
		    		new AlphaModifier(1, 0.2f, 0.5f)));
	    this.registerEntityModifier(iem);
		
	}

	public void setNode(SearchNode node) {
		this.tempNode = node;
		
		if (node.towerType == TOWER_TYPE.STONE_TOWER) {
			this.setColor(0.0f, 0.25f, 0.00f, 0.5f);
		}
		else if (node.towerType == TOWER_TYPE.TOTEM_TOWER) {
			this.setColor(0.0f, 0.00f, 0.85f, 0.5f);
		}
		else {
			this.setColor(1.0f, 1.0f, 1.0f, 0.5f);
		}
		
		this.setPosition(node.nodeSprite.getX()+towerOffsetX,node.nodeSprite.getY()+towerOffsetY);
	}
	
	/**
	 * Special place tower method since this tower has already been bought and should just be placed
	 */
	@Override
	protected void placeTower() {
		
		//Set the opacity
		this.setAlpha(1.0f);
	
		final SearchNode node = mBaseActivity.getHighLightedNode();
		
		if (node != null) {

			if (node.Walkable) {
								
				//Since the last node might be switched we need to reset it before cheching closest path
				tempNode.Walkable = true;
				
			    node.Walkable = false;
                final ArrayList<Point> shortestPath = mBaseActivity.returnShortestPath();

                if (shortestPath.size() > 0) {
                	
                	node.nodeSprite.setCurrentTileIndex(tempNode.towerType.ordinal());
    				node.nodeSprite.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    				node.nodeSprite.setVisible(true);
    				node.towerType = tempNode.towerType;

    				//Reset the old node
    				tempNode.nodeSprite.setCurrentTileIndex(0);
    				tempNode.nodeSprite.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    				tempNode.nodeSprite.setVisible(false);
    				tempNode.towerType = TOWER_TYPE.NO_TOWER;

    				setNode(node);
    				
    				mBaseActivity.playSound(MazeActivity.ID_CLICK_SOUND);
                }
                else {
					tempNode.Walkable = false;
				    node.Walkable = true;
                	node.towerType = TOWER_TYPE.NO_TOWER;
					mBaseActivity.showMessage(MazeActivity.ID_TOWER_ERROR_2);
                }
			}
			else if (node != tempNode){
				mBaseActivity.showMessage(MazeActivity.ID_TOWER_ERROR_1);
			}
			
		}
		
		//Always reset tower and stophighlighting the node
		this.setPosition(tempNode.nodeSprite.getX()+towerOffsetX,tempNode.nodeSprite.getY()+towerOffsetY);
		
		//Last we stop higlightning the node
		this.mBaseActivity.stopHighLightLastNode();		
		
	}

	//Custom reset method that resets back to position above tower
	public void resetTemporaryTowerPosition() {
		
		startMovingTower = false;
		
		if (this.getX() != this.x && this.getY() != this.y) {
			this.setAlpha(1.0f);
			this.setPosition(tempNode.nodeSprite.getX()+towerOffsetX,tempNode.nodeSprite.getY()+towerOffsetY);
		}
		else {
			this.resetPosition();
		}
	}

}
