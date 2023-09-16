package me.redth.ingamecursor.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import me.redth.ingamecursor.InGameCursor;

@SuppressWarnings("unused")
public class ModConfig extends Config {

    @Slider(name = "Front Circle Radius", min = 0, max = 20)
    public static float frontRadius = 6;

    @Color(name = "Front Circle Color")
    public static OneColor frontColor = new OneColor(0xFFFFFFFF);

    @Slider(name = "Back Circle Size", min = 0, max = 20)
    public static float backRadius = 10;

    @Color(name = "Back Circle Color")
    public static OneColor backColor = new OneColor(0x80000000);

    @Slider(name = "Clicked Front Circle Radius", min = 0, max = 20)
    public static float clickedFrontRadius = 8;

    @Color(name = "Clicked Front Circle Color")
    public static OneColor clickedFrontColor = new OneColor(0xFFFFFFFF);

    @Slider(name = "Clicked Back Circle Size", min = 0, max = 20)
    public static float clickedBackRadius = 12;

    @Color(name = "Clicked Back Circle Color")
    public static OneColor clickedBackColor = new OneColor(0x80000000);

    @Slider(name = "Animation Duration (ms)", min = 0, max = 250)
    public static int animationDuration = 100;

    @Switch(name = "Replace crosshair")
    public static boolean replaceCrosshair = false;

    public ModConfig() {
        super(new Mod(InGameCursor.NAME, ModType.HUD), InGameCursor.MODID + ".json");
        initialize();
        addListener("replaceCrosshair", InGameCursor::updateCrosshair);
    }

}

