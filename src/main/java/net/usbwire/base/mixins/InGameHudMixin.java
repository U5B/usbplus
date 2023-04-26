// https://gist.github.com/John-Paul-R/06a746e4611c79538722fc930dcb41e4
package net.usbwire.base.mixins;

import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;
import net.usbwire.base.access.InGameHudAccess;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.usbwire.base.BaseMod;

import java.util.List;
import java.util.Map;
// From https://gist.github.com/John-Paul-R/06a746e4611c79538722fc930dcb41e4
@Mixin(InGameHud.class)
public class InGameHudMixin implements InGameHudAccess {

    @Shadow
    @Final
    private Map<MessageType, List<ClientChatListener>> listeners;
    public void registerChatListener(MessageType messageType, ClientChatListener listener) {
        this.listeners.get(messageType).add(listener);
    }
}