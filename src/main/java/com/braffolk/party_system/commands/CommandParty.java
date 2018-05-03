package com.braffolk.party_system.commands;

import com.braffolk.party_system.PartySystem;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.*;

public class CommandParty extends CommandBase {
   /**
    * Gets the name of the command
    */
   @Override
   public String getName() {
      return "party";
   }
   
   /**
    * Return the required permission level for this command.
    */
   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }
   
   @Override
   public boolean checkPermission( MinecraftServer server, ICommandSender sender ) {
      return sender.canUseCommand( this.getRequiredPermissionLevel(), this.getName() );
   }
   
   /**
    * Gets the usage string for the command.
    */
   @Override
   public String getUsage( ICommandSender sender ) {
      return "commands.party.usage";
   }
   
   /**
    * Callback for when the command is executed
    */
   @Override
   public void execute( MinecraftServer server, ICommandSender sender, String[] args ) throws CommandException {
      if( sender instanceof EntityPlayer ) {
         if( args.length >= 1 ) {
            Scoreboard scoreboard = this.getScoreboard( server );
            ScorePlayerTeam current_team = scoreboard.getPlayersTeam( sender.getName() );
            String name = sender.getName();
            String[] _input = new String[1];
            _input[0] = sender.getName();
            
            switch( args[0].toLowerCase() ) {
               case "create":
                  
                  PartySystem.proxy.pCreate( scoreboard, name );
                  
                  /*if( current_team == null || !current_team.getName().equals( name ) ) {
                     if( scoreboard.getTeam( name ) == null ) {
                        addTeam( sender, _input, 0, server );
                        setTeamOption( sender, new String[]{ name, "collisionRule", "pushOtherTeams" }, 0, server );
                        setTeamOption( sender, new String[]{ name, "friendlyfire", "false" }, 0, server );
                        setTeamOption( sender, new String[]{ name, "color", "red" }, 0, server );
                     } else {
                        throw new WrongUsageException( "commands.party.error.rejoin_own_team", new Object[0] );
                     }
                     joinTeam( sender, _input, 0, server );
                  } else {
                     throw new WrongUsageException( "commands.party.create.failure", new Object[0] );
                  }*/
                  break;
               case "disband":
                  
                  PartySystem.proxy.pDisband( scoreboard, current_team, name );
                  /*if( scoreboard.getTeam( name ) != null ) {
                     removeTeam( sender, _input, 0, server );
                     notifyCommandListener( sender, this, "commands.party.disband.success", new Object[0] );
                  } else if( current_team != null ) {
                     throw new WrongUsageException( "commands.party.disband.failure.notOwner", new Object[0] );
                  } else {
                     throw new WrongUsageException( "commands.party.disband.failure.notIn" );
                  }*/
                  break;
               case "invite":
                  if( args.length == 2 ) {
                     if( scoreboard.getTeam( name ) != null ) {
                        Entity entity = getEntity( server, sender, args[1] );
                        if( entity.world != null ) {
                           PartySystem.proxy.addConsent( name, entity.getName() );
                           
                           
                           if( PartySystem.proxy.getConsent( name, entity.getName() ) ) {
                              //joinTeam( sender, new String[]{ name, entity.getName() }, 0, server );
                              PartySystem.proxy.pJoin( scoreboard, scoreboard.getTeam( name ), entity.getName() );
                              
                              PartySystem.proxy.resetConsent( name, entity.getName() );
                              
                              notifyCommandListener( sender, this, "commands.party.invite.success", new Object[]{ args[1] } );
                           } else {
                              entity.sendMessage( new TextComponentTranslation( "commands.party.inviteMessage", new Object[]{ name, name } ) );
                              
                              notifyCommandListener( sender, this, "commands.party.invite.invited", new Object[]{ args[1] } );
                           }
                        } else {
                           throw new WrongUsageException( "commands.party.invite.failure.noPlayer", new Object[0] );
                        }
                     } else {
                        throw new WrongUsageException( "commands.party.invite.failure.notOwner", new Object[0] );
                     }
                  } else {
                     throw new WrongUsageException( "commands.party.invite.usage", new Object[0] );
                  }
                  break;
               case "join":
                  if( args.length == 2 ) {
                     String target = args[1];
                     

                     if( scoreboard.getTeam( target ) != null ) {
                        if( scoreboard.getTeam( name ) == null ) {
                           Entity entity = getEntity( server, sender, args[1] );
                           
                           if( entity.world != null ) {
                              PartySystem.proxy.addConsent( name, entity.getName() );
                              
                              if( PartySystem.proxy.getConsent( name, entity.getName() ) ) {
                                 //joinTeam( sender, new String[]{ entity.getName(), name }, 0, server );
                                 //scoreboard.addPlayerToTeam( name, target );
                                 PartySystem.proxy.pJoin( scoreboard, scoreboard.getTeam( target ), name );
                                 PartySystem.proxy.resetConsent( name, entity.getName() );
                                 
                                 notifyCommandListener( sender, this, "commands.party.join.success", new Object[]{ args[1] } );
                              } else {
                                 entity.sendMessage( new TextComponentTranslation( "commands.party.joinRequest", new Object[]{ name, name } ) );
                                 
                                 notifyCommandListener( sender, this, "commands.party.join.requested", new Object[]{ args[1] } );
                              }
                           } else {
                              throw new WrongUsageException( "commands.party.join.failure.noPlayer", new Object[0] );
                           }
                        } else {
                           throw new WrongUsageException( "commands.party.join.failure.inParty", new Object[0] );
                        }
                     } else {
                        throw new WrongUsageException( "commands.party.join.failure", new Object[0] );
                     }
                  } else {
                     throw new WrongUsageException( "commands.party.join.usage", new Object[0] );
                  }
                  break;
               case "kick":
                  if( args.length == 2 ) {
                     notifyCommandListener( sender, this, "commands.party.kick.success", new Object[]{ args[1] } );
                  } else {
                     throw new WrongUsageException( "commands.party.kick.usage", new Object[0] );
                  }
                  break;
               case "leave":
                  PartySystem.proxy.pLeave( scoreboard, name );
                  /*if( current_team != null ) {
                     if( current_team.getName().equals( name ) ) { // Player is owner of the party
                        Collection<String> members = current_team.getMembershipCollection();
                        if( members.size() > 1 ){ // Change ownership
                           String new_owner = null;
                           for( String member : members ){
                              if( !member.equals( name ) ){
                                 new_owner = member;
                                 break;
                              }
                           }
                           if( new_owner != null ){
                              scoreboard.removePlayerFromTeam( name, current_team );
                              current_team = PartySystem.proxy.pChangeOwner( scoreboard, current_team, new_owner );
   
                              notifyCommandListener( sender, this, "commands.party.leave.success" );
                           } else { // Errored, disband party
                              scoreboard.removeTeam( current_team );
   
                              notifyCommandListener( sender, this, "commands.party.leave.success" );
                           }
                        } else { // Disband party
                           scoreboard.removeTeam( current_team );
   
                           notifyCommandListener( sender, this, "commands.party.leave.success" );
                        }
                     } else { // Player is a member of the party
                        scoreboard.removePlayerFromTeam( name, current_team );
   
                        notifyCommandListener( sender, this, "commands.party.leave.success" );
                     }
                  } else {
                     notifyCommandListener( sender, this, "commands.party.leave.failure" );
                  }*/
                  break;
               case "owner":
                  if( args.length == 2 ) {
                     if( current_team != null && current_team.getName().equals( name ) ) {
                        PartySystem.proxy.pChangeOwner( scoreboard, current_team, args[1] );
                     }
                  }
                  /*if( args.length == 2 ) {
                     if( current_team.getName() != null ) {
                        if( current_team.getName().equals( name ) ) { // Player is owner of the party
                           if( scoreboard.getPlayersTeam( args[1] ) != null && scoreboard.getPlayersTeam( args[1] ).equals( current_team ) ) {
                              current_team = PartySystem.proxy.pChangeOwner( scoreboard, current_team, args[1] );
                              notifyCommandListener( sender, this, "commands.party.owner.success", new Object[]{ args[1] } );
                           } else {
                              notifyCommandListener( sender, this, "commands.party.failure.UserNotInParty", new Object[]{ args[1] } );
                           }
                        } else {
                           notifyCommandListener( sender, this, "commands.party.failure.notOwner", new Object[]{ args[1] } );
                        }
                     } else {
                        notifyCommandListener( sender, this, "commands.party.failure.notIn", new Object[]{ args[1] } );
                     }
                  } else {
                     throw new WrongUsageException( "commands.party.owner.usage", new Object[0] );
                  }*/
                  break;
               case "color":
                  Collection<String> members = scoreboard.getTeam( scoreboard.getPlayersTeam( name ).getName() ).getMembershipCollection();
                  PlayerList list = server.getPlayerList();
                  for( String _target : members ) {
                     //PartySystem.snw.sendTo(new SPHealthUpdate(42), (EntityPlayerMP) list.getPlayerByUsername(_target) );
                     
                  }
                  break;
               
            }
         } else {
            throw new WrongUsageException( "commands.party.usage", new Object[0] );
         }
      } else {
         throw new WrongUsageException( "commands.party.error.onlyPlayers", new Object[0] );
      }
   }
   
   /**
    * Get a list of options for when the user presses the TAB key
    */
   public List<String> getTabCompletions( MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos ) {
      return getListOfStringsMatchingLastWord( args, server.getOnlinePlayerNames() );
   }
   
   /**
    * Return whether the specified command parameter index is a username parameter.
    */
    /*public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }*/
   protected Scoreboard getScoreboard( MinecraftServer server ) {
      return server.getWorld( 0 ).getScoreboard();
   }
   
   protected ScorePlayerTeam convertToTeam( String name, MinecraftServer server ) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard( server );
      ScorePlayerTeam scoreplayerteam = scoreboard.getTeam( name );
      
      if( scoreplayerteam == null ) {
         throw new CommandException( "commands.scoreboard.teamNotFound", new Object[]{ name } );
      } else {
         return scoreplayerteam;
      }
   }
   
   protected boolean teamExists( String name, MinecraftServer server ) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard( server );
      return scoreboard.getTeam( name ) != null;
   }
   
   protected void addTeam( ICommandSender sender, String[] args, int startIndex, MinecraftServer server ) throws CommandException {
      String s = args[startIndex++];
      Scoreboard scoreboard = this.getScoreboard( server );
      
      if( scoreboard.getTeam( s ) != null ) {
         throw new CommandException( "commands.scoreboard.teams.add.alreadyExists", new Object[]{ s } );
      } else if( s.length() > 16 ) {
         throw new SyntaxErrorException( "commands.scoreboard.teams.add.tooLong", new Object[]{ s, Integer.valueOf( 16 ) } );
      } else if( s.isEmpty() ) {
         throw new WrongUsageException( "commands.scoreboard.teams.add.usage", new Object[0] );
      } else {
         if( args.length > startIndex ) {
            String s1 = getChatComponentFromNthArg( sender, args, startIndex ).getUnformattedText();
            
            if( s1.length() > 32 ) {
               throw new SyntaxErrorException( "commands.scoreboard.teams.add.displayTooLong", new Object[]{ s1, Integer.valueOf( 32 ) } );
            }
            
            if( s1.isEmpty() ) {
               scoreboard.createTeam( s );
            } else {
               scoreboard.createTeam( s ).setDisplayName( s1 );
            }
         } else {
            scoreboard.createTeam( s );
         }
         
         notifyCommandListener( sender, this, "commands.scoreboard.teams.add.success", new Object[]{ s } );
      }
   }
   
   protected void setTeamOption( ICommandSender sender, String[] args, int startIndex, MinecraftServer server ) throws CommandException {
      ScorePlayerTeam scoreplayerteam = this.convertToTeam( args[startIndex++], server );
      
      if( scoreplayerteam != null ) {
         String s = args[startIndex++].toLowerCase( Locale.ROOT );
         
         if( !"color".equalsIgnoreCase( s ) && !"friendlyfire".equalsIgnoreCase( s ) && !"seeFriendlyInvisibles".equalsIgnoreCase( s ) && !"nametagVisibility".equalsIgnoreCase( s ) && !"deathMessageVisibility".equalsIgnoreCase( s ) && !"collisionRule".equalsIgnoreCase( s ) ) {
            throw new WrongUsageException( "commands.scoreboard.teams.option.usage", new Object[0] );
         } else if( args.length == 4 ) {
            if( "color".equalsIgnoreCase( s ) ) {
               throw new WrongUsageException( "commands.scoreboard.teams.option.noValue", new Object[]{ s, joinNiceStringFromCollection( TextFormatting.getValidValues( true, false ) ) } );
            } else if( !"friendlyfire".equalsIgnoreCase( s ) && !"seeFriendlyInvisibles".equalsIgnoreCase( s ) ) {
               if( !"nametagVisibility".equalsIgnoreCase( s ) && !"deathMessageVisibility".equalsIgnoreCase( s ) ) {
                  if( "collisionRule".equalsIgnoreCase( s ) ) {
                     throw new WrongUsageException( "commands.scoreboard.teams.option.noValue", new Object[]{ s, joinNiceString( Team.CollisionRule.getNames() ) } );
                  } else {
                     throw new WrongUsageException( "commands.scoreboard.teams.option.usage", new Object[0] );
                  }
               } else {
                  throw new WrongUsageException( "commands.scoreboard.teams.option.noValue", new Object[]{ s, joinNiceString( Team.EnumVisible.getNames() ) } );
               }
            } else {
               throw new WrongUsageException( "commands.scoreboard.teams.option.noValue", new Object[]{ s, joinNiceStringFromCollection( Arrays.asList( "true", "false" ) ) } );
            }
         } else {
            String s1 = args[startIndex];
            
            if( "color".equalsIgnoreCase( s ) ) {
               TextFormatting textformatting = TextFormatting.getValueByName( s1 );
               
               if( textformatting == null || textformatting.isFancyStyling() ) {
                  throw new WrongUsageException( "commands.scoreboard.teams.option.noValue", new Object[]{ s, joinNiceStringFromCollection( TextFormatting.getValidValues( true, false ) ) } );
               }
               
               scoreplayerteam.setColor( textformatting );
               scoreplayerteam.setPrefix( textformatting.toString() );
               scoreplayerteam.setSuffix( TextFormatting.RESET.toString() );
            } else if( "friendlyfire".equalsIgnoreCase( s ) ) {
               if( !"true".equalsIgnoreCase( s1 ) && !"false".equalsIgnoreCase( s1 ) ) {
                  throw new WrongUsageException( "commands.scoreboard.teams.option.noValue", new Object[]{ s, joinNiceStringFromCollection( Arrays.asList( "true", "false" ) ) } );
               }
               
               scoreplayerteam.setAllowFriendlyFire( "true".equalsIgnoreCase( s1 ) );
            } else if( "seeFriendlyInvisibles".equalsIgnoreCase( s ) ) {
               if( !"true".equalsIgnoreCase( s1 ) && !"false".equalsIgnoreCase( s1 ) ) {
                  throw new WrongUsageException( "commands.scoreboard.teams.option.noValue", new Object[]{ s, joinNiceStringFromCollection( Arrays.asList( "true", "false" ) ) } );
               }
               
               scoreplayerteam.setSeeFriendlyInvisiblesEnabled( "true".equalsIgnoreCase( s1 ) );
            } else if( "nametagVisibility".equalsIgnoreCase( s ) ) {
               Team.EnumVisible team$enumvisible = Team.EnumVisible.getByName( s1 );
               
               if( team$enumvisible == null ) {
                  throw new WrongUsageException( "commands.scoreboard.teams.option.noValue", new Object[]{ s, joinNiceString( Team.EnumVisible.getNames() ) } );
               }
               
               scoreplayerteam.setNameTagVisibility( team$enumvisible );
            } else if( "deathMessageVisibility".equalsIgnoreCase( s ) ) {
               Team.EnumVisible team$enumvisible1 = Team.EnumVisible.getByName( s1 );
               
               if( team$enumvisible1 == null ) {
                  throw new WrongUsageException( "commands.scoreboard.teams.option.noValue", new Object[]{ s, joinNiceString( Team.EnumVisible.getNames() ) } );
               }
               
               scoreplayerteam.setDeathMessageVisibility( team$enumvisible1 );
            } else if( "collisionRule".equalsIgnoreCase( s ) ) {
               Team.CollisionRule team$collisionrule = Team.CollisionRule.getByName( s1 );
               
               if( team$collisionrule == null ) {
                  throw new WrongUsageException( "commands.scoreboard.teams.option.noValue", new Object[]{ s, joinNiceString( Team.CollisionRule.getNames() ) } );
               }
               
               scoreplayerteam.setCollisionRule( team$collisionrule );
            }
            
            notifyCommandListener( sender, this, "commands.scoreboard.teams.option.success", new Object[]{ s, scoreplayerteam.getName(), s1 } );
         }
      }
   }
   
   protected void removeTeam( ICommandSender sender, String[] args, int startIndex, MinecraftServer server ) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard( server );
      ScorePlayerTeam scoreplayerteam = this.convertToTeam( args[startIndex], server );
      
      if( scoreplayerteam != null ) {
         scoreboard.removeTeam( scoreplayerteam );
         notifyCommandListener( sender, this, "commands.scoreboard.teams.remove.success", new Object[]{ scoreplayerteam.getName() } );
      }
   }
   
   protected void listTeams( ICommandSender sender, String[] args, int startIndex, MinecraftServer server ) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard( server );
      
      if( args.length > startIndex ) {
         ScorePlayerTeam scoreplayerteam = this.convertToTeam( args[startIndex], server );
         
         if( scoreplayerteam == null ) {
            return;
         }
         
         Collection<String> collection = scoreplayerteam.getMembershipCollection();
         sender.setCommandStat( CommandResultStats.Type.QUERY_RESULT, collection.size() );
         
         if( collection.isEmpty() ) {
            throw new CommandException( "commands.scoreboard.teams.list.player.empty", new Object[]{ scoreplayerteam.getName() } );
         }
         
         TextComponentTranslation textcomponenttranslation = new TextComponentTranslation( "commands.scoreboard.teams.list.player.count", new Object[]{ collection.size(), scoreplayerteam.getName() } );
         textcomponenttranslation.getStyle().setColor( TextFormatting.DARK_GREEN );
         sender.sendMessage( textcomponenttranslation );
         sender.sendMessage( new TextComponentString( joinNiceString( collection.toArray() ) ) );
      } else {
         Collection<ScorePlayerTeam> collection1 = scoreboard.getTeams();
         sender.setCommandStat( CommandResultStats.Type.QUERY_RESULT, collection1.size() );
         
         if( collection1.isEmpty() ) {
            throw new CommandException( "commands.scoreboard.teams.list.empty", new Object[0] );
         }
         
         TextComponentTranslation textcomponenttranslation1 = new TextComponentTranslation( "commands.scoreboard.teams.list.count", new Object[]{ collection1.size() } );
         textcomponenttranslation1.getStyle().setColor( TextFormatting.DARK_GREEN );
         sender.sendMessage( textcomponenttranslation1 );
         
         for( ScorePlayerTeam scoreplayerteam1 : collection1 ) {
            sender.sendMessage( new TextComponentTranslation( "commands.scoreboard.teams.list.entry", new Object[]{ scoreplayerteam1.getName(), scoreplayerteam1.getDisplayName(), scoreplayerteam1.getMembershipCollection().size() } ) );
         }
      }
   }
   
   protected void joinTeam( ICommandSender sender, String[] args, int startIndex, MinecraftServer server ) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard( server );
      String s = args[startIndex++];
      Set<String> set = Sets.<String>newHashSet();
      Set<String> set1 = Sets.<String>newHashSet();
      
      if( sender instanceof EntityPlayer && startIndex == args.length ) {
         String s4 = getCommandSenderAsPlayer( sender ).getName();
         
         if( scoreboard.addPlayerToTeam( s4, s ) ) {
            set.add( s4 );
         } else {
            set1.add( s4 );
         }
      } else {
         while( startIndex < args.length ) {
            String s1 = args[startIndex++];
            
            if( EntitySelector.isSelector( s1 ) ) {
               for( Entity entity : getEntityList( server, sender, s1 ) ) {
                  String s3 = getEntityName( server, sender, entity.getCachedUniqueIdString() );
                  
                  if( scoreboard.addPlayerToTeam( s3, s ) ) {
                     set.add( s3 );
                  } else {
                     set1.add( s3 );
                  }
               }
            } else {
               String s2 = getEntityName( server, sender, s1 );
               
               if( scoreboard.addPlayerToTeam( s2, s ) ) {
                  set.add( s2 );
               } else {
                  set1.add( s2 );
               }
            }
         }
      }
      
      if( !set.isEmpty() ) {
         sender.setCommandStat( CommandResultStats.Type.AFFECTED_ENTITIES, set.size() );
         notifyCommandListener( sender, this, "commands.scoreboard.teams.join.success", new Object[]{ set.size(), s, joinNiceString( set.toArray( new String[set.size()] ) ) } );
      }
      
      if( !set1.isEmpty() ) {
         throw new CommandException( "commands.scoreboard.teams.join.failure", new Object[]{ set1.size(), s, joinNiceString( set1.toArray( new String[set1.size()] ) ) } );
      }
   }
   
   protected void leaveTeam( ICommandSender sender, String[] args, int startIndex, MinecraftServer server ) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard( server );
      Set<String> set = Sets.<String>newHashSet();
      Set<String> set1 = Sets.<String>newHashSet();
      
      if( sender instanceof EntityPlayer && startIndex == args.length ) {
         String s3 = getCommandSenderAsPlayer( sender ).getName();
         
         if( scoreboard.removePlayerFromTeams( s3 ) ) {
            set.add( s3 );
         } else {
            set1.add( s3 );
         }
      } else {
         while( startIndex < args.length ) {
            String s = args[startIndex++];
            
            if( EntitySelector.isSelector( s ) ) {
               for( Entity entity : getEntityList( server, sender, s ) ) {
                  String s2 = getEntityName( server, sender, entity.getCachedUniqueIdString() );
                  
                  if( scoreboard.removePlayerFromTeams( s2 ) ) {
                     set.add( s2 );
                  } else {
                     set1.add( s2 );
                  }
               }
            } else {
               String s1 = getEntityName( server, sender, s );
               
               if( scoreboard.removePlayerFromTeams( s1 ) ) {
                  set.add( s1 );
               } else {
                  set1.add( s1 );
               }
            }
         }
      }
      
      if( !set.isEmpty() ) {
         sender.setCommandStat( CommandResultStats.Type.AFFECTED_ENTITIES, set.size() );
         notifyCommandListener( sender, this, "commands.scoreboard.teams.leave.success", new Object[]{ set.size(), joinNiceString( set.toArray( new String[set.size()] ) ) } );
      }
      
      if( !set1.isEmpty() ) {
         throw new CommandException( "commands.scoreboard.teams.leave.failure", new Object[]{ set1.size(), joinNiceString( set1.toArray( new String[set1.size()] ) ) } );
      }
   }
   
   protected void emptyTeam( ICommandSender sender, String[] args, int startIndex, MinecraftServer server ) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard( server );
      ScorePlayerTeam scoreplayerteam = this.convertToTeam( args[startIndex], server );
      
      if( scoreplayerteam != null ) {
         Collection<String> collection = Lists.newArrayList( scoreplayerteam.getMembershipCollection() );
         sender.setCommandStat( CommandResultStats.Type.AFFECTED_ENTITIES, collection.size() );
         
         if( collection.isEmpty() ) {
            throw new CommandException( "commands.scoreboard.teams.empty.alreadyEmpty", new Object[]{ scoreplayerteam.getName() } );
         } else {
            for( String s : collection ) {
               scoreboard.removePlayerFromTeam( s, scoreplayerteam );
            }
            
            notifyCommandListener( sender, this, "commands.scoreboard.teams.empty.success", new Object[]{ collection.size(), scoreplayerteam.getName() } );
         }
      }
   }
}








































