package com.braffolk.party_system.events;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderEvents {
    private RenderEvents() {}

    public static void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
    }

    @SubscribeEvent
    public void onTickStart(TickEvent.RenderTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            //HudElement.resetBounds();
        }
    }

    @SubscribeEvent
    public void onRenderTick(RenderGameOverlayEvent.Pre event){

    }
}
