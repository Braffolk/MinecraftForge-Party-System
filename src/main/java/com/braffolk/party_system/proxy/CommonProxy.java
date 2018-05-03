package com.braffolk.party_system.proxy;

import com.braffolk.party_system.events.EventHandler;
import jline.internal.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class CommonProxy {
   private HashSet<String> consent;
   
   public void preInit( FMLPreInitializationEvent event ) {
      event.getModConfigurationDirectory();
   }
   
   public void init( FMLInitializationEvent event ) {
   
   }
   
   public void postInit( FMLPostInitializationEvent event ) {
      this.consent = new HashSet<String>();
      
   }
   
   public void addConsent( String from, String to ) {
      String consent_string = from + "-" + to;
      this.consent.add( consent_string );
   }
   
   public boolean givenConsent( String from, String to ) {
      String consent_string = from + "-" + to;
      return this.consent.contains( consent_string );
   }
   
   public boolean getConsent( String name1, String name2 ) {
      String consent_string1 = name1 + "-" + name2;
      String consent_string2 = name2 + "-" + name1;
      return ( this.consent.contains( consent_string1 ) && this.consent.contains( consent_string2 ) );
   }
   
   public void resetConsent( String name1, String name2 ) {
      String consent_string1 = name1 + "-" + name2;
      String consent_string2 = name2 + "-" + name1;
      this.consent.remove( consent_string1 );
      this.consent.remove( consent_string2 );
   }
   
   public void pDisband( Scoreboard scoreboard, ScorePlayerTeam team, String name ) {
      if( team != null && team.getName().equals( name ) ){
         scoreboard.removeTeam( team );
         //pLeave( scoreboard, name );
         
         
      }
   }
   
   public void pCreate( Scoreboard scoreboard, String name ){
      Random rand = new Random(  );
      this.pLeave( scoreboard, name );
      
      ScorePlayerTeam team = scoreboard.createTeam( name );
      team.setSeeFriendlyInvisiblesEnabled( true );
      team.setNameTagVisibility( Team.EnumVisible.HIDE_FOR_OTHER_TEAMS );
      team.setAllowFriendlyFire( false );
      team.setCollisionRule( Team.CollisionRule.HIDE_FOR_OTHER_TEAMS );
      team.setColor( TextFormatting.fromColorIndex( rand.nextInt(14) ) );
      
      scoreboard.addPlayerToTeam( name, team.getName() );
   
      PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
      EntityPlayerMP entity = playerList.getPlayerByUsername( name );
   
      EventHandler.sendPlayer( entity, 0.0F );
   }
   
   public void pJoin( Scoreboard scoreboard, ScorePlayerTeam team, String name ) {
      this.pLeave( scoreboard, name );
      

      scoreboard.addPlayerToTeam( name, team.getName() );
   
      PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
      EntityPlayerMP entity = playerList.getPlayerByUsername( name );
      
      EventHandler.sendTeamTo( entity );
      EventHandler.sendPlayer( entity, 0.0F );
   }
   
   @Nullable
   public ScorePlayerTeam pChangeOwner( Scoreboard scoreboard, ScorePlayerTeam team, String new_owner ) {
      ScorePlayerTeam playersTeam = scoreboard.getPlayersTeam( new_owner );
      if( playersTeam != null && playersTeam.equals( team ) ) {
         ScorePlayerTeam new_team = scoreboard.createTeam( new_owner );
         new_team.setAllowFriendlyFire( team.getAllowFriendlyFire() );
         new_team.setCollisionRule( team.getCollisionRule() );
         new_team.setColor( team.getColor() );
         new_team.setDeathMessageVisibility( team.getDeathMessageVisibility() );
         new_team.setFriendlyFlags( team.getFriendlyFlags() );
         new_team.setNameTagVisibility( team.getNameTagVisibility() );
         new_team.setPrefix( team.getPrefix() );
         new_team.setSuffix( team.getSuffix() );
         new_team.setSeeFriendlyInvisiblesEnabled( team.getSeeFriendlyInvisiblesEnabled() );
   
         Collection<String> collMembers = team.getMembershipCollection();
         String[] members = new String[collMembers.size()];
   
         int i = 0;
         for( String member : collMembers ) {
            members[i++] = member;
         }
         
         for( String member : members ) {
            scoreboard.addPlayerToTeam( member, new_owner );
         }
         
         scoreboard.removeTeam( team );
         
         return new_team;
      } else {
         return null;
      }
   }
   
   public String pLeave( Scoreboard scoreboard, String name ) {
      ScorePlayerTeam team = scoreboard.getPlayersTeam( name );
      String ret = "";
      
      if( team != null ) {
         if( team.getName().equals( name ) ) { // Player is owner of the party
            Collection<String> members = team.getMembershipCollection();
            if( members.size() > 1 ) { // Change ownership
               String new_owner = null;
               for( String member : members ) {
                  if( !member.equals( name ) ) {
                     new_owner = member;
                     break;
                  }
               }
               if( new_owner != null ) {
                  scoreboard.removePlayerFromTeam( name, team );
                  team = this.pChangeOwner( scoreboard, team, new_owner );
                  
                  ret = "commands.party.leave.success";
               } else { // Errored, disband party
                  scoreboard.removeTeam( team );
                  
                  ret = "commands.party.leave.success";
               }
            } else { // Disband party
               scoreboard.removeTeam( team );
               
               ret = "commands.party.leave.success";
            }
         } else { // Player is a member of the party
            scoreboard.removePlayerFromTeam( name, team );
            
            ret = "commands.party.leave.success";
         }
      } else {
         ScorePlayerTeam teamcheck = scoreboard.getTeam( name );
         if( teamcheck != null ){
            scoreboard.removeTeam( teamcheck );
         }
         ret = "commands.party.leave.failure";
      }
      
      
      return ret;
   }
   
}
