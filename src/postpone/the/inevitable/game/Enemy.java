package postpone.the.inevitable.game;

import java.util.ArrayList;

import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import postpone.the.inevitable.pathfinder.PathFinder;
import postpone.the.inevitable.pathfinder.SearchNode;
import android.graphics.Point;

public final class Enemy extends AnimatedSprite {

	private float timeSinceLastLoop = 0;
	
	public static float TIMER = 0;
	public static float DISTANCE_WALKED = 0;
	
	//Default cooldown
	private final static float DEFAULT_COOLDOWN_CONSTANT = 5f;
	
	//Speed
	private final static float DEFAULT_VELOCITY = 100;
	private float velocity = DEFAULT_VELOCITY;
	public boolean fastForward = false;
		
	//Following used by pathfinder
	private ArrayList<Point> shortestPath;
	private PathFinder pathfinder;
	private int currentNodeNbr = 0;
	private Point currentNode;
	
	//Read this values from mainclass
	private final static int MAZE_COLUMNS_SIZE = MazeActivity.MAZE_COLUMNS_SIZE; 
	private final static int MAZE_ROWS_SIZE = MazeActivity.MAZE_ROWS_SIZE;	
	private final static int MAZE_LEFT = MazeActivity.MAZE_LEFT + (MAZE_COLUMNS_SIZE - 48)/2;
	private final static int MAZE_BOT = MazeActivity.MAZE_BOT - 40;
	
	//THe node position used to determine if we have entered a new gridposition
	private int nodeX = -1;
	private int nodeY = -1;
	
	//Variables affecting the speed
	private float stunnedTime = 0;
	//private ArrayList<SearchNode> towersFiring;
	
	//A enumeration withthe available x directions
	private static enum xDirection {
	    LEFT, RIGHT, NONE 
	}
	//A enumeration withthe available y directions
	private static enum yDirection {
	    UP, DOWN, NONE 
	}

	//This enemy current direction
	private xDirection current_xDirection = xDirection.NONE;
	private yDirection current_yDirection = yDirection.NONE;
	
	//The pool with available explosions
	private static AnimatedSpritePool SPRITE_POOL;
	private final MazeActivity mActivity;
	
	//Spawn animation
	private final AnimatedSprite spawnAnimation;
	
	//Map theme
	private int map_theme = 0;

	
	public Enemy(final TiledTextureRegion pTextureRegion, PathFinder pathfinder, final TiledTextureRegion pExplosionRegion, MazeActivity activity, VertexBufferObjectManager vertexBufferObjectManager, AnimatedSprite spawnAnimation) {
		super(0, 0, 48, 64, pTextureRegion,vertexBufferObjectManager);
		this.setCurrentTileIndex(1);
		this.setVisible(false);
		this.pathfinder = pathfinder;
		AnimatedSpritePool.VertexBufferObjectManager(this.getVertexBufferObjectManager());
		SPRITE_POOL = new AnimatedSpritePool(pExplosionRegion);
		this.mActivity = activity;
		this.spawnAnimation = spawnAnimation;
		
		map_theme = this.mActivity.map_theme;
		
		//Dont run the update
		this.setIgnoreUpdate(true);
		
		TIMER = 0;
		DISTANCE_WALKED = 0;
	}

	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		
		timeSinceLastLoop += pSecondsElapsed;
		
		if (timeSinceLastLoop >= 0.01) {
		
			if (fastForward) {
				timeSinceLastLoop = timeSinceLastLoop * 10;
			}
	
			if (timeSinceLastLoop > 0.2f) {
				timeSinceLastLoop = 0.2f;
			}
			
			//Calculate speed
			if (stunnedTime > 0) {
	
				stunnedTime = stunnedTime - timeSinceLastLoop;
	
				if (stunnedTime <= 0){
					
					this.setColor(1.0f, 1.0f, 1.0f, 1.0f);
					velocity = Enemy.DEFAULT_VELOCITY;
					updateWalkAnimation();
					
					//Unless it is zero to avoid divide errors
					if (stunnedTime != 0) {
						float stunnedPart = timeSinceLastLoop + stunnedTime;
						TIMER+= stunnedPart;
					}
				}
				else {
					velocity = Enemy.DEFAULT_VELOCITY/2;
				}
			}
			else {
				velocity = Enemy.DEFAULT_VELOCITY;
			}		
			
			TIMER += timeSinceLastLoop;
			DISTANCE_WALKED += velocity * timeSinceLastLoop;
			updateMovement(velocity * timeSinceLastLoop);
			
			timeSinceLastLoop = 0;

		}
		super.onManagedUpdate(pSecondsElapsed);
	}

	private void checkIfPossibleHit(final float mDistance) {
		final int tmpNodeX = (int)(((this.mX - Enemy.MAZE_LEFT)/Enemy.MAZE_COLUMNS_SIZE) +0.5f);
		final int tmpNodeY = (int)(((this.mY-Enemy.MAZE_BOT)/Enemy.MAZE_ROWS_SIZE) +0.5f);
			
		//Only test once each node
		if (tmpNodeX != nodeX || tmpNodeY != nodeY) {

			nodeX = tmpNodeX;
			nodeY = tmpNodeY;
			
			if (map_theme != Utils.THEME_GRASS) {
				addFootStep();
			}			
			
			final SearchNode tempNode = this.pathfinder.getNode(tmpNodeX, tmpNodeY);
			
		    // We loop through each of the possible neighbors
			for (int i = 0; i < tempNode.Neighbors.length; i++)
			{
				//Their is a stundtower nearby. Activate stun and set cooldown on tower
				if (tempNode.Neighbors[i] != null && !tempNode.Neighbors[i].Walkable && tempNode.Neighbors[i].towerType == AbstractTower.TOWER_TYPE.TOTEM_TOWER  && (tempNode.Neighbors[i].cooldownTime + DEFAULT_COOLDOWN_CONSTANT) <= TIMER) {
					towerHit(tempNode.Neighbors[i],mDistance);
				}
			}
		    // We loop through each of the possible diagonal neighbors
			for (int i = 0; i < tempNode.NeighborsDiagonal.length; i++)
			{
				//Their is a stundtower nearby. Activate stun and set cooldown on tower
				if (tempNode.NeighborsDiagonal[i] != null && !tempNode.NeighborsDiagonal[i].Walkable && tempNode.NeighborsDiagonal[i].towerType == AbstractTower.TOWER_TYPE.TOTEM_TOWER && (tempNode.NeighborsDiagonal[i].cooldownTime + DEFAULT_COOLDOWN_CONSTANT) <= TIMER) {
					towerHit(tempNode.NeighborsDiagonal[i],mDistance);
				}
			}

		}
	}
	
	private void calculateIfWeAreDone(final float mDistance) {
		
		//Befor resetting directions and so on we must check if this position could result in an hit
		checkIfPossibleHit(mDistance);
		
		//Vi är framme  i xled
		if (this.current_xDirection == xDirection.RIGHT && 
				this.mX >= (MAZE_LEFT + currentNode.x*Enemy.MAZE_COLUMNS_SIZE) ||
	 				this.current_xDirection == xDirection.LEFT && 
	 					this.mX <= (MAZE_LEFT + currentNode.x*Enemy.MAZE_COLUMNS_SIZE)) {
			this.current_xDirection = xDirection.NONE;
		}

		//Vi är framme  i yled
		if (this.current_yDirection == yDirection.UP && 
				this.mY >= (MAZE_BOT + currentNode.y*Enemy.MAZE_ROWS_SIZE) ||
	 				this.current_yDirection == yDirection.DOWN && 
	 					this.mY <= (MAZE_BOT + currentNode.y*Enemy.MAZE_ROWS_SIZE)) {
			this.current_yDirection = yDirection.NONE;
		}
			
		
		//Vi är framme
		if (this.current_xDirection == xDirection.NONE && this.current_yDirection == yDirection.NONE) {
			
			currentNodeNbr++;
			if (currentNodeNbr < shortestPath.size()) {
				enemyDirection(currentNode,shortestPath.get(currentNodeNbr));
				currentNode = shortestPath.get(currentNodeNbr);
				if (mDistance > 0) {
					updateMovement(mDistance);
				}
			}
			else {
				//Goal
				
				//Adjust time. The player might have walked to far so we must adjust the time to avoid differences between normal and fast speed
				if (mDistance > 0) {
					TIMER -= mDistance/velocity;
					DISTANCE_WALKED -= mDistance;
				}
				this.stopAnimation(6);
				spawnAnimation();
				this.setIgnoreUpdate(true);
				this.mActivity.finnish();
			}
			
		}
		
	}
	
	
	private void updateMovement(final float mDistance) {

		float tmpDistance = 0;
		
		//Vi rör oss åt höger men inte framme än
		if (this.current_xDirection == xDirection.RIGHT && this.mX < (MAZE_LEFT + currentNode.x*Enemy.MAZE_COLUMNS_SIZE)) {
			this.mX = this.mX + mDistance;
			if (this.mX >= (MAZE_LEFT + currentNode.x*Enemy.MAZE_COLUMNS_SIZE)) {
				tmpDistance = (this.mX - (MAZE_LEFT + currentNode.x*Enemy.MAZE_COLUMNS_SIZE));
				this.mX = (MAZE_LEFT + currentNode.x*Enemy.MAZE_COLUMNS_SIZE);
				calculateIfWeAreDone(tmpDistance);
			}
		}
		//Vi rör oss åt vänster men inte framme än
		else if (this.current_xDirection == xDirection.LEFT && this.mX > (MAZE_LEFT + currentNode.x*Enemy.MAZE_COLUMNS_SIZE)) {
			this.mX = this.mX - mDistance;
			if (this.mX <= (MAZE_LEFT + currentNode.x*Enemy.MAZE_COLUMNS_SIZE)) {
				tmpDistance = ((MAZE_LEFT + currentNode.x*Enemy.MAZE_COLUMNS_SIZE) - this.mX);
				this.mX = (MAZE_LEFT + currentNode.x*Enemy.MAZE_COLUMNS_SIZE);
				calculateIfWeAreDone(tmpDistance);
			}
		}
		//Vi rör oss åt nedåt men inte framme än
		else if (this.current_yDirection == yDirection.UP && this.mY < (MAZE_BOT + currentNode.y*Enemy.MAZE_ROWS_SIZE)) {
			this.mY = this.mY + mDistance;
			if (this.mY >= (MAZE_BOT + currentNode.y*Enemy.MAZE_ROWS_SIZE)) {
				tmpDistance = this.mY - (MAZE_BOT + currentNode.y*Enemy.MAZE_ROWS_SIZE);
				this.mY = (MAZE_BOT + currentNode.y*Enemy.MAZE_ROWS_SIZE);
				calculateIfWeAreDone(tmpDistance);
			}
		}
		//Vi rör oss uppåt men inte framme än
		else if (this.current_yDirection == yDirection.DOWN && this.mY > (MAZE_BOT + currentNode.y*Enemy.MAZE_ROWS_SIZE)) {
			this.mY = this.mY - mDistance;
			if (this.mY <= (MAZE_BOT + currentNode.y*Enemy.MAZE_ROWS_SIZE)) {
				tmpDistance = (MAZE_BOT + currentNode.y*Enemy.MAZE_ROWS_SIZE) -  this.mY;
				this.mY = (MAZE_BOT + currentNode.y*Enemy.MAZE_ROWS_SIZE);
				calculateIfWeAreDone(tmpDistance);
			}
		}
	}
	
	//Runs when enemy spawns or finishes 
	private void spawnAnimation () {
		mActivity.playSound(MazeActivity.ID_SPAWN_SOUND);
		
		final float x = this.mX + this.getWidth()/2 - spawnAnimation.getWidth()/2;
		final float y = this.mY + this.getHeight() - spawnAnimation.getHeight()/2;
		
		spawnAnimation.setPosition(x,y);
		spawnAnimation.setCurrentTileIndex(0);
		spawnAnimation.setVisible(true);

		final int animation = fastForward ? 15: 40;
		
		spawnAnimation.animate(animation, false, new IAnimationListener () {
			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {
			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {
			}

			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				spawnAnimation.setVisible(false);
			}
		});
		
	}
	
	//Runs every time a tower hits the enemy
	private void towerHit(final SearchNode searchNode, final float mDistance) {
		
		//We calculate when the tower hit the enemy. 
		//Since the game does not update all the time we to adjust the time the tower hit the enemy to always be the same
		
		if (mDistance > 0) {
			final float timeOfThisOccurence = mDistance/velocity;
			stunnedTime = DEFAULT_COOLDOWN_CONSTANT -timeOfThisOccurence;
			searchNode.cooldownTime = TIMER - timeOfThisOccurence;
		}
		else {
			stunnedTime = DEFAULT_COOLDOWN_CONSTANT;
			searchNode.cooldownTime = TIMER;
		}
		
		velocity = Enemy.DEFAULT_VELOCITY/2;
		updateWalkAnimation();
		
		this.setColor(0.3f, 0.3f, 1.0f, 1.0f);

		searchNode.explosionSprite = this.getSpriteFromSpritePool();
		final float x = MAZE_LEFT + Enemy.MAZE_COLUMNS_SIZE/2 + searchNode.Position.x*Enemy.MAZE_COLUMNS_SIZE - searchNode.explosionSprite.getWidth()/2;
		final float y = MAZE_BOT + Enemy.MAZE_ROWS_SIZE/2 + 40 + searchNode.Position.y*Enemy.MAZE_ROWS_SIZE - searchNode.explosionSprite.getHeight()/2;
		searchNode.explosionSprite.setPosition(x,y);
		
		searchNode.explosionSprite.setCurrentTileIndex(0);

		final int animation = fastForward ? 20: 60;
		
		searchNode.explosionSprite.animate(new long[]{animation,animation,animation,animation }, 0, 3, 0, new IAnimationListener () {
			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {
			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {
			}

			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				mActivity.detachChild(searchNode.explosionSprite);
				sendSpriteToSpritePool(searchNode.explosionSprite);
			}
		});
		searchNode.explosionSprite.setScale(0.2f);
		searchNode.explosionSprite.registerEntityModifier(new ScaleModifier(fastForward ? 0.03f : 0.1f, 0.2f, 1.2f));
		mActivity.attachChild(searchNode.explosionSprite);
		
		mActivity.playSound(MazeActivity.ID_LASER_SOUND);
	}

	//Basic method used to decrease the numberr of available nodes for the path alhorith. The algorith saves every step and we only need the corners
	public void startMoving(Point startPoint, Point endPoint) {

	    shortestPath = pathfinder.FindPath(startPoint, endPoint);
	
		this.setVisible(true);
		this.mX = MAZE_LEFT + startPoint.x*MAZE_COLUMNS_SIZE;
		this.mY = MAZE_BOT + startPoint.y*MAZE_ROWS_SIZE;		
		currentNode = shortestPath.get(currentNodeNbr);
		enemyDirection(startPoint,currentNode);

		this.setIgnoreUpdate(false);		
		spawnAnimation();		
	}

	private void enemyDirection(Point from, Point to) {
		
		//Looking right
		if (from.x < to.x) {
			this.current_xDirection = xDirection.RIGHT;
		}
		//Looking left
		if (from.x > to.x) {
			this.current_xDirection = xDirection.LEFT;
		}
		//Looking up
		if (from.y < to.y) {
			this.current_yDirection = yDirection.UP;
		}
		//Looking down
		if (from.y > to.y) {
			this.current_yDirection = yDirection.DOWN;
		}


		updateWalkAnimation();
	}
	
	/**
	 * Update animation
	 */
	private void updateWalkAnimation() {

		int animationMultiplyer = 1;
		
		if (velocity > DEFAULT_VELOCITY/2) {
			animationMultiplyer = 2;
		} 
		
		if (fastForward) {
			animationMultiplyer  *= 10;
		}
		
		final long animationSpeed = 260/animationMultiplyer;
		
		if (current_xDirection == xDirection.RIGHT) {
			this.animate(new long[]{animationSpeed, animationSpeed, animationSpeed}, 3, 5, true);
		}
		else if (current_xDirection == xDirection.LEFT) {
			this.animate(new long[]{animationSpeed, animationSpeed, animationSpeed}, 9, 11, true);
		}
		else if (current_yDirection == yDirection.UP) {
			this.animate(new long[]{animationSpeed, animationSpeed, animationSpeed}, 6, 8, true);
		}
		else if (this.current_yDirection == yDirection.DOWN) {
			this.animate(new long[]{animationSpeed, animationSpeed, animationSpeed}, 0, 2, true);
		}
	}
	
	
	 /**
	 * Called because the sprite can be recycled
	 */
	 private void sendSpriteToSpritePool(AnimatedSprite pSprite) {
		 SPRITE_POOL.recyclePoolItem(pSprite);
	 }
	 
	 /**
	 * We need a sprite to display
	 */
	 private AnimatedSprite getSpriteFromSpritePool() {
		 return SPRITE_POOL.obtainPoolItem();
	 }

	 /**
	  * Method that adds footsteps to the map
	  */
	private void addFootStep() {

		if (currentNodeNbr < shortestPath.size()-1) {

			Point nextNode = shortestPath.get(currentNodeNbr+1);
			Point currentNode = shortestPath.get(currentNodeNbr);
			SearchNode node = pathfinder.getNode(nodeX,nodeY);
			
			if (current_xDirection == xDirection.RIGHT && currentNode.x < nextNode.x) {
				//vi kommer röra oss åt höger
				node.nodeSprite.setCurrentTileIndex(3);
			}
			if (current_xDirection == xDirection.LEFT && currentNode.x > nextNode.x) {
				//vi kommer röra oss åt vänster
				node.nodeSprite.setCurrentTileIndex(3);
			}
			else if (current_yDirection == yDirection.UP && currentNode.y < nextNode.y) {
				//vi kommer röra oss upp
				node.nodeSprite.setCurrentTileIndex(4);
			}
			else if (current_yDirection == yDirection.DOWN && currentNode.y > nextNode.y) {
				//vi kommer röra oss ner
				node.nodeSprite.setCurrentTileIndex(4);
			}
			else if (current_xDirection == xDirection.RIGHT && currentNode.y < nextNode.y) {
				//vi går åt höger men kommer röra oss ner
				node.nodeSprite.setCurrentTileIndex(5);
			}
			else if (current_xDirection == xDirection.RIGHT && currentNode.y > nextNode.y) {
				//vi går åt höger men kommer röra oss upp
				node.nodeSprite.setCurrentTileIndex(6);
			}
			else if (current_xDirection == xDirection.LEFT && currentNode.y < nextNode.y) {
				//vi går åt vänster men kommer röra oss ner
				node.nodeSprite.setCurrentTileIndex(8);
			}
			else if (current_xDirection == xDirection.LEFT && currentNode.y > nextNode.y) {
				//vi går åt vänster men kommer röra oss upp
				node.nodeSprite.setCurrentTileIndex(7);
			}
			else if (current_yDirection == yDirection.DOWN && currentNode.x < nextNode.x) {
				//Det verkar vara fel på direction, vi går uppåt men kommer röra oss åt höger
				node.nodeSprite.setCurrentTileIndex(8);
			}
			else if (current_yDirection == yDirection.DOWN && currentNode.x > nextNode.x) {
				//Det verkar vara fel på direction, vi går uppåt men kommer röra oss åt vänster
				node.nodeSprite.setCurrentTileIndex(5);
			}
			else if (current_yDirection == yDirection.UP && currentNode.x < nextNode.x) {
				//Det verkar vara fel på direction, vi går neråt men kommer röra oss åt höger
				node.nodeSprite.setCurrentTileIndex(7);
			}
			else if (current_yDirection == yDirection.UP && currentNode.x > nextNode.x) {
				//Det verkar vara fel på direction, vi går neråt men kommer röra oss åt vänster
				node.nodeSprite.setCurrentTileIndex(6);
			}
			else {
				node.nodeSprite.setVisible(false);
			}	
		
			node.nodeSprite.setVisible(true);
		}		
	}

}