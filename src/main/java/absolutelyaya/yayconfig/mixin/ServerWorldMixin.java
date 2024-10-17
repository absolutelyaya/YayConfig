package absolutelyaya.yayconfig.mixin;

import absolutelyaya.yayconfig.config.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ProgressListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin
{
	@Shadow @Final private MinecraftServer server;
	
	@Inject(method = "save", at = @At("HEAD"))
	void onSave(ProgressListener progressListener, boolean flush, boolean savingDisabled, CallbackInfo ci)
	{
		Config.saveAll(server);
	}
}
