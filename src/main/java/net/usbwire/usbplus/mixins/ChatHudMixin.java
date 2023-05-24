// https://github.com/mrbuilder1961/ChatPatches/blob/1.18.x/src/main/java/obro1961/chatpatches/mixin/chat/ChatHudMixin.java
package net.usbwire.usbplus.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gg.essential.universal.wrappers.message.UTextComponent;
import net.usbwire.usbplus.util.MixinHelper;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatHud.class)
public abstract class ChatHudMixin extends DrawableHelper {
  @Inject(
    method = "addMessage(Lnet/minecraft/text/Text;I)V",
    at = @At("HEAD"),
    cancellable = true
  )
  private void usbplus_onMessage (Text message, int id, CallbackInfo ci) {
    // Pass this to UniversalCraft's text compoment parser and then process it later at some point!
    // System.out.println(message.toString());
    MixinHelper.INSTANCE.onMessage(message);
  }
}