package com.braffolk.party_system.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SPHealthUpdate implements IMessage {
   public SPHealthUpdate(){}
   
   public ArrayList<single> players;
   
   public SPHealthUpdate( ArrayList<single> players ){
      this.players = players;
   }
   
   public SPHealthUpdate( single player ){
      this.players = new ArrayList<single>();
      this.players.add( player );
   }
   
   @Override
   public void toBytes(ByteBuf buf){
      // Writes the int into the buf
      buf.writeInt( players.size() );
      for( SPHealthUpdate.single player : players ){
         ByteBufUtils.writeUTF8String(buf, player.username);
         buf.writeFloat(player.health);
         buf.writeFloat(player.maxHealth);
      }
   }
   
   @Override
   public void fromBytes(ByteBuf buf) {
      // Reads the int back from the buf. Note that if you have multiple values, you must read in the same order you wrote.
      int count = buf.readInt();
      players = new ArrayList<SPHealthUpdate.single>();
      
      for(int i = 0; i < count; i++){
         String username = ByteBufUtils.readUTF8String(buf);
         float health = buf.readFloat();
         float maxHealth = buf.readFloat();
         players.add( new SPHealthUpdate.single( username, health, maxHealth ) );
      }
   }
   
   public static class single {
      String username;
      float health;
      float maxHealth;
      
      public single(String username, float health, float maxHealth ) {
         this.username = username;
         this.health = health;
         this.maxHealth = maxHealth;
      }
   }
}