package com.github.nyuppo.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@Config(name = "hotbarcycle")
public class ClothConfigHotbarCycleConfig extends HotbarCycleConfig implements ConfigData {

    public boolean playSound = true;
    public boolean reverseCycleDirection = false;
    @Tooltip(count = 2)
    public boolean holdAndScroll = false;
    @Tooltip
    public boolean repeatSlotToCycle = false;
    @Tooltip
    public boolean cycleWhenPickingBlock = false;
    @Tooltip
    public boolean pickCyclesWholeHotbar = false;

    @Category("rows")
    @Tooltip
    public boolean enableRow1 = true;
    @Category("rows")
    public boolean enableRow2 = true;
    @Category("rows")
    @Tooltip
    public boolean enableRow3 = true;

    @Category("columns")
    @Tooltip
    public boolean enableColumn0 = true;
    @Category("columns")
    public boolean enableColumn1 = true;
    @Category("columns")
    public boolean enableColumn2 = true;
    @Category("columns")
    public boolean enableColumn3 = true;
    @Category("columns")
    public boolean enableColumn4 = true;
    @Category("columns")
    public boolean enableColumn5 = true;
    @Category("columns")
    public boolean enableColumn6 = true;
    @Category("columns")
    public boolean enableColumn7 = true;
    @Category("columns")
    @Tooltip
    public boolean enableColumn8 = true;

    @Override
    public boolean getPlaySound() {
        return playSound;
    }

    @Override
    public boolean getReverseCycleDirection() {
        return reverseCycleDirection;
    }

    @Override
    public boolean getHoldAndScroll() {
        return holdAndScroll;
    }

    @Override
    public boolean getRepeatSlotToCycle() {
        return repeatSlotToCycle;
    }

    @Override
    public boolean getCycleWhenPickingBlock() {
        return cycleWhenPickingBlock;
    }

    @Override
    public boolean getPickCyclesWholeHotbar() {
        return pickCyclesWholeHotbar;
    }

    @Override
    public boolean getEnableRow1() {
        return enableRow1;
    }

    @Override
    public boolean getEnableRow2() {
        return enableRow2;
    }

    @Override
    public boolean getEnableRow3() {
        return enableRow3;
    }

    @Override
    public boolean getEnableColumn0() {
        return enableColumn0;
    }

    @Override
    public boolean getEnableColumn1() {
        return enableColumn1;
    }

    @Override
    public boolean getEnableColumn2() {
        return enableColumn2;
    }

    @Override
    public boolean getEnableColumn3() {
        return enableColumn3;
    }

    @Override
    public boolean getEnableColumn4() {
        return enableColumn4;
    }

    @Override
    public boolean getEnableColumn5() {
        return enableColumn5;
    }

    @Override
    public boolean getEnableColumn6() {
        return enableColumn6;
    }

    @Override
    public boolean getEnableColumn7() {
        return enableColumn7;
    }

    @Override
    public boolean getEnableColumn8() {
        return enableColumn8;
    }
}
