package me.redth.ingamecursor;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.RenderEvent;
import cc.polyfrost.oneconfig.events.event.Stage;
import cc.polyfrost.oneconfig.gui.animations.Animation;
import cc.polyfrost.oneconfig.gui.animations.EaseInOutQuad;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import me.redth.ingamecursor.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import java.nio.IntBuffer;


@Mod(modid = InGameCursor.MODID, name = InGameCursor.NAME, version = InGameCursor.VERSION)
public class InGameCursor {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static final NanoVGHelper NANOVG = NanoVGHelper.INSTANCE;
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static ModConfig config;

    public static boolean lastEnabled;

    public static Animation frontAnimation;
    public static Animation backAnimation;
    public static boolean lastButtonDown;


    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new ModConfig();
        EventManager.INSTANCE.register(this);
        lastEnabled = config.enabled;
        updateCursor();
        frontAnimation = new EaseInOutQuad(0, ModConfig.frontRadius, ModConfig.frontRadius, false);
        backAnimation = new EaseInOutQuad(0, ModConfig.backRadius, ModConfig.backRadius, false);
    }

    @Subscribe
    public void onRender(RenderEvent event) {
        if (event.stage != Stage.END) return;
        if (lastEnabled != config.enabled) {
            lastEnabled = config.enabled;
            updateCursor();
        }
        if (!lastEnabled) return;
        if (shouldRenderCursor())
            drawCircle(Mouse.getX(), mc.displayHeight - Mouse.getY());
        else if (shouldRenderCrosshair())
            drawCircle(mc.displayWidth / 2, mc.displayHeight / 2);
    }

    public static void drawCircle(int mouseX, int mouseY) {
        boolean buttonDown = Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2);

        if (lastButtonDown != buttonDown) {
            float frontRadius = buttonDown ? ModConfig.clickedFrontRadius : ModConfig.frontRadius;
            float backRadius = buttonDown ? ModConfig.clickedBackRadius : ModConfig.backRadius;
            frontAnimation = new EaseInOutQuad(ModConfig.animationDuration, frontAnimation.get(), frontRadius, false);
            backAnimation = new EaseInOutQuad(ModConfig.animationDuration, backAnimation.get(), backRadius, false);
            lastButtonDown = buttonDown;
        }

        int frontColor = buttonDown ? ModConfig.clickedFrontColor.getRGB() : ModConfig.frontColor.getRGB();
        int backColor = buttonDown ? ModConfig.clickedBackColor.getRGB() : ModConfig.backColor.getRGB();

        NANOVG.setupAndDraw(vg -> {
            NANOVG.drawCircle(vg, mouseX, mouseY, backAnimation.get(), backColor);
            NANOVG.drawCircle(vg, mouseX, mouseY, frontAnimation.get(), frontColor);
        });
    }

    public static void updateCursor() {
        updateCrosshair();

        try {
            Mouse.setNativeCursor(!lastEnabled ? null : new Cursor(1, 1, 0, 0, 1, IntBuffer.allocate(1), null));
        } catch (LWJGLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateCrosshair() {
        GuiIngameForge.renderCrosshairs = !lastEnabled || !ModConfig.replaceCrosshair;
    }

    public static boolean shouldRenderCrosshair() {
        return Mouse.isGrabbed() && ModConfig.replaceCrosshair;
    }

    public static boolean shouldRenderCursor() {
        return !Mouse.isGrabbed() && Mouse.isInsideWindow();
    }
}
