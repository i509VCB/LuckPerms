package me.lucko.luckperms.fabric.mixin;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"), method = "sendCommandTree", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void luckperms_makeTreeForSource(ServerPlayerEntity playerEntity, CallbackInfo ci, Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> serverNodeToClientNode, RootCommandNode<CommandSource> clientBoundTree) {

    }
}
