package com.braffolk.party_system.network;

import com.braffolk.party_system.PartySystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class ClientMessageHandler implements IMessageHandler<SPHealthUpdate, IMessage> {
   // Do note that the default constructor is required, but implicitly defined in this case
   
   @Override
   public IMessage onMessage( SPHealthUpdate message, MessageContext ctx ) {
      for( SPHealthUpdate.single player : message.players ){
         NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().player.connection.getPlayerInfo( player.username );
         if( playerInfo != null ) {
            playerInfo.setDisplayHealth( (int) player.health );
         } else {
            PartySystem.INSTANCE.log( "FAILED FAILED FAILED to: Player " + player.username + " health set to " + player.health + "/" + player.maxHealth );
         }
      }

      // No response packet
      return null;
   }
}