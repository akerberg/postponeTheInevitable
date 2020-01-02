package postpone.the.inevitable.game;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class TotemTower extends AbstractTower {
	
	public TotemTower(ITextureRegion pTextureRegion,
			MazeActivity pBaseActivity,
			VertexBufferObjectManager vertexBufferObjectManager,
			TemporaryTower temporaryTower,
			TOWER_OFFSET tower_marker_offset) {
		
		super(677, 219, pTextureRegion, pBaseActivity, TOWER_TYPE.TOTEM_TOWER,
				vertexBufferObjectManager, temporaryTower, tower_marker_offset);
	}
	
}
