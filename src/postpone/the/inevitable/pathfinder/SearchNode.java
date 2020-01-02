package postpone.the.inevitable.pathfinder;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;

import postpone.the.inevitable.game.AbstractTower;

import android.graphics.Point;

public class SearchNode {
	public Point Position;
	public boolean Walkable;
	public SearchNode[] Neighbors;
	public SearchNode[] NeighborsDiagonal;

	public AbstractTower.TOWER_TYPE towerType = AbstractTower.TOWER_TYPE.NO_TOWER;
	//Will contain the time for last time the shower shot
	public float cooldownTime = -10;
	
	//Sprite associated with this node.
	public TiledSprite nodeSprite;

	//Reference to a explosion
	public AnimatedSprite explosionSprite;
	
	/// <summary>
	/// A reference to the node that transfered this node to
	/// the open list. This will be used to trace our path back
	/// from the goal node to the start node.
	/// </summary>
	public SearchNode Parent;

	/// <summary>
	/// Provides an easy way to check if this node
	/// is in the open list.
	/// </summary>
	public boolean InOpenList;
	/// <summary>
	/// Provides an easy way to check if this node
	/// is in the closed list.
	/// </summary>
	public boolean InClosedList;

	/// <summary>
	/// The approximate distance from the start node to the
	/// goal node if the path goes through this node. (F)
	/// </summary>
	public float DistanceToGoal;
	/// <summary>
	/// Distance traveled from the spawn point. (G)
	/// </summary>
	public float DistanceTraveled;

}


