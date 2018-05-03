package com.braffolk.party_system.client.gui;


import com.braffolk.party_system.PartySystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class GuiPartyOverlay extends GuiScreen {
   public static GuiPartyOverlay INSTANCE;
   private final ResourceLocation tex_overlay = new ResourceLocation( PartySystem.MODID, "textures/gui/overlay.png" );
   
   PriorityQueue<NetworkPlayerInfo> renderQueue;
   
   public void init() {
      this.renderQueue = new PriorityQueue<NetworkPlayerInfo>( 11,
          new Comparator<NetworkPlayerInfo>() {
             @Override
             public int compare( NetworkPlayerInfo o1, NetworkPlayerInfo o2 ) {
                return (int) ( o1.getDisplayHealth() - o2.getDisplayHealth() );
             }
          } );
   }
   
   @SubscribeEvent
   public void renderOverlay( RenderGameOverlayEvent event ) {
      if( event.getType() == RenderGameOverlayEvent.ElementType.TEXT ) {
         Minecraft mc = Minecraft.getMinecraft();
         ScorePlayerTeam team = mc.world.getScoreboard().getPlayersTeam( mc.player.getName() );
         
         if( team != null ) {
            renderQueue.clear();
            RenderHelper.enableGUIStandardItemLighting();
            mc.renderEngine.bindTexture( tex_overlay );
            int xoff = 4; // Overlay location
            int yoff = 4;
            
            
            Collection<NetworkPlayerInfo> coll = mc.player.connection.getPlayerInfoMap(); // All players in the server
            Collection<String> members = team.getMembershipCollection(); // Members of our team
            NetworkPlayerInfo[] renderArray = new NetworkPlayerInfo[members.size()]; // The actual array to render from
            
            int b_width = 35;
            int b_width_max = 150;
            for( NetworkPlayerInfo playerInfo : coll ) {
               if( members.contains( playerInfo.getGameProfile().getName() ) ) {
                  b_width = Math.min( Math.max( mc.fontRenderer.getStringWidth( playerInfo.getGameProfile().getName() ) + 40, b_width ), b_width_max );
                  renderQueue.add( playerInfo );
               }
            }
            renderQueue.toArray( renderArray );
            
            for( NetworkPlayerInfo player : renderArray ) {
               String name = player.getGameProfile().getName();
               float health = Math.min( ( player.getDisplayHealth() + 1.0F ) / 20.0F, 1.0F );
               
               mc.renderEngine.bindTexture( tex_overlay );
               
               // empty healthbar
               drawScaledCustomSizeModalRect( xoff, yoff + 3, 0, 0, 4, 20, 4, 20, 64.0F, 64.0F );
               drawScaledCustomSizeModalRect( xoff + 4, yoff + 3, 4, 0, 4, 20, b_width - 8, 20, 64.0F, 64.0F );
               drawScaledCustomSizeModalRect( xoff + 4 + b_width - 8, yoff + 3, 8, 0, 4, 20, 4, 20, 64.0F, 64.0F );
               
               
               // full healthbar
               float n1 = 4.0F / b_width;
               float n2 = ( b_width - 4.0F ) / b_width;
               float nw = ( b_width - 8.0F ) / b_width;
               
               //health = (float) (Math.sin( Math.floorMod(System.currentTimeMillis(), 7200) / 7200.0F * Math.PI ));
               if( health > 0.0F ) {
                  int w1 = (int) ( Math.min( health, n1 ) / n1 * 4 );
                  drawScaledCustomSizeModalRect( xoff, yoff + 3, 16, 0, w1, 20, w1, 20, 64.0F, 64.0F );
                  if( health >= n1 ) {
                     int w2 = (int) ( Math.min( health - n1, nw ) / nw * ( b_width - 8 ) );
                     drawScaledCustomSizeModalRect( xoff + 4, yoff + 3, 20, 0, 4, 20, w2, 20, 64.0F, 64.0F );
                     if( health >= n2 ) {
                        int w3 = (int) ( Math.min( health - n2, n1 ) / n1 * 4 );
                        drawScaledCustomSizeModalRect( xoff + b_width - 4, yoff + 3, 24, 0, w3, 20, w3, 20, 64.0F, 64.0F );
                     }
                  }
                  if( health < (b_width - 2.0F) / b_width && health > 2.0F / b_width ) {
                     drawScaledCustomSizeModalRect( xoff + (int) ( b_width * health ), yoff + 3, 12, 0, 1, 20, 1, 20, 64.0F, 64.0F );
                     drawScaledCustomSizeModalRect( xoff + (int) ( b_width * health ) - 1, yoff + 3, 28, 0, 1, 20, 1, 20, 64.0F, 64.0F );
                  }
               }
               // head container
               drawScaledCustomSizeModalRect( xoff + 4, yoff, 0, 32, 27, 26, 27, 26, 64.0F, 64.0F );
               
               // Owner of the party
               if( team.getName().equals( name ) ) {
                  drawScaledCustomSizeModalRect( xoff - 3, yoff - 7, 32, 0, 16, 16, 16, 16, 64.0F, 64.0F );
               }
               
               // Name
               if( name != null ) {
                  mc.fontRenderer.drawStringWithShadow( name, xoff + 35, yoff + 9, -1 );
               }
               
               
               // Head
               mc.getTextureManager().bindTexture( player.getLocationSkin() );
               
               drawTexturedPlane( // Front
                   xoff + 9.0F, yoff + 4.0F, 17.0F, 18.5F,
                   0.5F, 16.0F / 37.0F,
                   1.0F, 8.0F / 37.0F,
                   1.0F, 29.0F / 37.0F,
                   0.5F, 1.0F,
                   8.0F / 64.0F, 8.0F / 64.0F,
                   8.0F / 64.0F, 8.0F / 64.0F,
                   0.65F
               );
               drawTexturedPlane( // Top
                   xoff + 9.0F, yoff + 4.0F, 17.0F, 18.5F,
                   0.0F, 8.0F / 37.0F,
                   0.5F, 0.0F,
                   1.0F, 8.0F / 37.0F,
                   0.5F, 16.0F / 37.0F,
                   8.0F / 64.0F, 0.0F,
                   8.0F / 64.0F, 8.0F / 64.0F,
                   1.0F
               );
               drawTexturedPlane( // Leftside
                   xoff + 9.0F, yoff + 4.0F, 17.0F, 18.5F,
                   0.0F, 8.0F / 37.0F,
                   0.5F, 16.0F / 37.0F,
                   0.5F, 1.0F,
                   0.0F, 29.0F / 37.0F,
                   0.0F, 8.0F / 64.0F,
                   8.0F / 64.0F, 8.0F / 64.0F,
                   0.8F
               );
               
               
               drawTexturedPlane( // Layer 2 Front
                   xoff + 9.0F - 1.0F, yoff + 4.0F - 1.08825F, 19.0F, 20.6765F,
                   0.5F, 16.0F / 37.0F,
                   1.0F, 8.0F / 37.0F,
                   1.0F, 29.0F / 37.0F,
                   0.5F, 1.0F,
                   40.0F / 64.0F, 8.0F / 64.0F,
                   8.0F / 64.0F, 8.0F / 64.0F,
                   0.65F
               );
               drawTexturedPlane( // Layer 2 Top
                   xoff + 9.0F - 1.0F, yoff + 4.0F - 1.08825F, 19.0F, 20.6765F,
                   0.0F, 8.0F / 37.0F,
                   0.5F, 0.0F,
                   1.0F, 8.0F / 37.0F,
                   0.5F, 16.0F / 37.0F,
                   40.0F / 64.0F, 0.0F,
                   8.0F / 64.0F, 8.0F / 64.0F,
                   1.0F
               );
               drawTexturedPlane( // Layer 2 Leftside
                   xoff + 9.0F - 1.0F, yoff + 4.0F - 1.08825F, 19.0F, 20.6765F,
                   0.0F, 8.0F / 37.0F,
                   0.5F, 16.0F / 37.0F,
                   0.5F, 1.0F,
                   0.0F, 29.0F / 37.0F,
                   32.0F / 64.0F, 8.0F / 64.0F,
                   8.0F / 64.0F, 8.0F / 64.0F,
                   0.8F
               );
               
               yoff += 27;
            }
         }
      }
   }
   
   
   public void drawTexturedPlane( float xoff, float yoff, float scaleX, float scaleY, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4,
                                  float u, float v, float uWidth, float vHeight, float shade ) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin( 7, DefaultVertexFormats.POSITION_TEX_COLOR );
      bufferbuilder.pos( (double) ( x4 * scaleX ) + xoff, (double) ( y4 * scaleY ) + yoff, (double) this.zLevel ).tex( (double) ( u + 0 ), (double) ( v + vHeight ) ).color( 1.0F * shade, 1.0F * shade, 1.0F * shade, 1.0F ).endVertex();
      bufferbuilder.pos( (double) ( x3 * scaleX ) + xoff, (double) ( y3 * scaleY ) + yoff, (double) this.zLevel ).tex( (double) ( u + uWidth ), (double) ( v + vHeight ) ).color( 1.0F * shade, 1.0F * shade, 1.0F * shade, 1.0F ).endVertex();
      bufferbuilder.pos( (double) ( x2 * scaleX ) + xoff, (double) ( y2 * scaleY ) + yoff, (double) this.zLevel ).tex( (double) ( u + uWidth ), (double) ( v + 0 ) ).color( 1.0F * shade, 1.0F * shade, 1.0F * shade, 1.0F ).endVertex();
      bufferbuilder.pos( (double) ( x1 * scaleX ) + xoff, (double) ( y1 * scaleY ) + yoff, (double) this.zLevel ).tex( (double) ( u + 0 ), (double) ( v + 0 ) ).color( 1.0F * shade, 1.0F * shade, 1.0F * shade, 1.0F ).endVertex();
      tessellator.draw();
   }
   
}
