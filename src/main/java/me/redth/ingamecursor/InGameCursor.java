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
import org.lwjgl.input.Mouse;


@Mod(modid = InGameCursor.MODID, name = InGameCursor.NAME, version = InGameCursor.VERSION)
public class InGameCursor {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static final NanoVGHelper NANOVG = NanoVGHelper.INSTANCE;
    public static final Minecraft mc = Minecraft.getMinecraft();

    @Mod.Instance(MODID)
    public static InGameCursor INSTANCE;
    public static ModConfig config;

    public static boolean lastEnabled;

    public static boolean hideCursor;
    public static Animation frontAnimation;
    public static Animation backAnimation;
    public static boolean lastButtonDown;


    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new ModConfig();
        EventManager.INSTANCE.register(this);
        Mouse.setGrabbed(config.enabled);
        frontAnimation = new EaseInOutQuad(0, ModConfig.frontRadius, ModConfig.frontRadius, false);
        backAnimation = new EaseInOutQuad(0, ModConfig.backRadius, ModConfig.backRadius, false);
    }

    @Subscribe
    public void onRender(RenderEvent event) {
        if (event.stage != Stage.END) return;
        if (lastEnabled != config.enabled) {
            lastEnabled = config.enabled;
            Mouse.setGrabbed(lastEnabled);
            updateCrosshair();
        }
        if (lastEnabled && (!hideCursor || ModConfig.replaceCrosshair)) NANOVG.setupAndDraw(InGameCursor::drawNano);
    }

    public static void drawNano(long vg) {
        int x = hideCursor ? mc.displayWidth / 2 : Mouse.getX();
        int y = hideCursor ? mc.displayHeight / 2 : mc.displayHeight - Mouse.getY();

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

        NANOVG.drawCircle(vg, x, y, backAnimation.get(), backColor);
        NANOVG.drawCircle(vg, x, y, frontAnimation.get(), frontColor);
    }

    public static void updateCrosshair() {
        GuiIngameForge.renderCrosshairs = !lastEnabled || !ModConfig.replaceCrosshair;
    }

}
