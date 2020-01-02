package postpone.the.inevitable.game;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

public class AnimatedSpritePool extends GenericPool<AnimatedSprite> {
	
	private final TiledTextureRegion mTextureRegion;
	private static VertexBufferObjectManager vertexBufferObjectManager;

	//Constructor
	public AnimatedSpritePool(TiledTextureRegion pTextureRegion) {
		 if (pTextureRegion == null) {
			 // Need to be able to create a Sprite so the Pool needs to have a TextureRegion
			 throw new IllegalArgumentException("The texture region must not be NULL");
		 }
		 mTextureRegion = pTextureRegion;
	 }
	
	 /**
	 * Set new  VertexBufferObjectManager
	 */
    public static void VertexBufferObjectManager(VertexBufferObjectManager pVertexBufferObjectManager) {
        vertexBufferObjectManager = pVertexBufferObjectManager;
    }

	 /**
	 * Called when a Sprite is required but there isn't one in the pool
	 */
	 @Override
	protected AnimatedSprite onAllocatePoolItem() {
		 return new AnimatedSprite(0,0,mTextureRegion.deepCopy(), vertexBufferObjectManager);
	}

	 /**
	  * Called when a Sprite is sent to the pool
	 */
	 @Override
	 protected void onHandleRecycleItem(final AnimatedSprite pSprite) {
		 pSprite.setIgnoreUpdate(true);
		 pSprite.setVisible(false);
	 }

	 /**
	 &nbsp;* Called just before a Sprite is returned to the caller, this is where you write your initialize code
	 &nbsp;* i.e. set location, rotation, etc.
	 */
	 @Override
	 protected void onHandleObtainItem(final AnimatedSprite pSprite) {
		 pSprite.reset();
	 }
}
