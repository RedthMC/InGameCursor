package me.redth.ingamecursor.mixin;

import me.redth.ingamecursor.InGameCursor;
import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHelper.class)
public class MouseHelperMixin {
    @Redirect(method = {"grabMouseCursor", "ungrabMouseCursor"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;setGrabbed(Z)V", remap = false))
    public void onGrabbed(boolean grabbed) {
        if (!InGameCursor.lastEnabled) Mouse.setGrabbed(grabbed);
        InGameCursor.hideCursor = grabbed;
    }
}
