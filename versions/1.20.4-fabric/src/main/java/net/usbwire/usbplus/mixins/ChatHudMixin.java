package net.usbwire.usbplus.mixins;
// https://github.com/mrbuilder1961/ChatPatches/blob/1.18.x/src/main/java/obro1961/chatpatches/mixin/chat/ChatHudMixin.java

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.usbwire.usbplus.util.MixinHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatHud.class)

public abstract class ChatHudMixin {

  @Inject(
          method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
          at = @At("HEAD"),
          cancellable = true
  )
  private void usbplus_onMessage (Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator, CallbackInfo ci) {
    // Pass this to UniversalCraft's text compoment parser and then process it later at some point!
    // System.out.println(message.toString());
    MixinHelper.INSTANCE.onMessage(message);
  }
}
