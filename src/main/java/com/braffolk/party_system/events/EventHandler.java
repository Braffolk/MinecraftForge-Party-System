package com.braffolk.party_system.events;

import com.braffolk.party_system.PartySystem;
import com.braffolk.party_system.network.SPHealthUpdate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collection;


//@SideOnly(Side.SERVER)
@Mod.EventBusSubscriber
public class EventHandler {
   
   @SubscribeEvent
   public static void onLivingDamage( LivingDamageEvent event ) {
      sendPlayer( event.getEntity(), -event.getAmount() );
   }
   
   @SubscribeEvent
   public static void onLivingHeal( LivingHealEvent event ) {
      sendPlayer( event.getEntity(), event.getAmount() );
   }
   
   @SubscribeEvent
   public static void onPlayerRespawn( net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event ) {
      sendPlayer( event.player, 0.0F );
   }


    /*@SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event){
        if( sendPlayer(event.getEntity(), event.getWorld()) ) {
            PartySystem.INSTANCE.log("onLivingSpawn UPDATED");
        }
    }*/
   
   public static boolean sendPlayer( Entity entity, float healthChange ) {
      if( entity instanceof EntityPlayer ) {
         EntityPlayer entityPlayer = (EntityPlayer) entity;
         Team team = entityPlayer.getTeam();
         
         if( team != null ) {
            PlayerList playerList = entityPlayer.getEntityWorld().getMinecraftServer().getPlayerList();
            
            if( playerList != null ) {
               Collection<String> members = team.getMembershipCollection();
               SPHealthUpdate packet = new SPHealthUpdate( new SPHealthUpdate.single( entityPlayer.getName(), entityPlayer.getHealth() + healthChange, entityPlayer.getMaxHealth() ) );
               
               for( String member : members ) {
                  EntityPlayerMP entityMP = playerList.getPlayerByUsername( member );
                  
                  if( entityMP != null ) {
                     PartySystem.snw.sendTo( packet, entityMP );
                  }
               }
               return true;
            }
         }
      }
      return false;
   }
   
   public static boolean sendTeamTo( Entity entity ) {
      if( entity instanceof EntityPlayer ) {
         EntityPlayer entityPlayer = (EntityPlayer) entity;
         Team team = entityPlayer.getTeam();
         
         if( team != null ) {
            PlayerList playerList = entityPlayer.getEntityWorld().getMinecraftServer().getPlayerList();
            
            if( playerList != null ) {
               Collection<String> members = team.getMembershipCollection();
               ArrayList<SPHealthUpdate.single> players = new ArrayList<>();
               
               for( String member : members ) {
                  EntityPlayerMP entityMP = playerList.getPlayerByUsername( member );
                  if( entityMP.getName() != null ) {
                     players.add( new SPHealthUpdate.single( entityMP.getName(), entityMP.getHealth(), entityMP.getMaxHealth() ) );
                  }
               }
   
               SPHealthUpdate packet = new SPHealthUpdate( players );
               PartySystem.snw.sendTo( packet, (EntityPlayerMP) entityPlayer );
               return true;
            }
         }
      }
      return false;
   }
}

