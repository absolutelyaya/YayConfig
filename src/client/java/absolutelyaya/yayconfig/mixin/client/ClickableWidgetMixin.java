package absolutelyaya.yayconfig.mixin.client;

import absolutelyaya.yayconfig.accessor.WidgetAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClickableWidget.class)
public class ClickableWidgetMixin implements WidgetAccessor
{
	@Shadow public boolean active;
	@Shadow protected float alpha;
	@Unique int offsetX, offsetY;
	
	@ModifyReturnValue(method = "getX", at = @At("RETURN"))
	int onGetX(int original)
	{
		return original + offsetX;
	}
	
	@ModifyReturnValue(method = "getY", at = @At("RETURN"))
	int onGetY(int original)
	{
		return original + offsetY;
	}
	
	@Override
	public void setOffset(Vector2i pos)
	{
		offsetX = pos.x;
		offsetY = pos.y;
	}
	
	@Override
	public Vector2i getOffset()
	{
		return new Vector2i(offsetX, offsetY);
	}
	
	@Override
	public void setActive(boolean b)
	{
		active = b;
	}
	
	@Override
	public void setAlpha(float alpha)
	{
		this.alpha = alpha;
	}
}
