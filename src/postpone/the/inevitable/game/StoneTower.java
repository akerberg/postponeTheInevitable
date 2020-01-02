package postpone.the.inevitable.game;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class StoneTower extends AbstractTower {
	
	public StoneTower(ITextureRegion pTextureRegion,
			MazeActivity pBaseActivity,
			VertexBufferObjectManager vertexBufferObjectManager,
			TemporaryTower temporaryTower,
			TOWER_OFFSET tower_marker_offset) {
		
		super(677, 82, pTextureRegion, pBaseActivity, TOWER_TYPE.STONE_TOWER,
				vertexBufferObjectManager, temporaryTower,tower_marker_offset);
	}
	
}
