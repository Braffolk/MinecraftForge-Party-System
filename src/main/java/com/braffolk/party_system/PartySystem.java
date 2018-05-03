package com.braffolk.party_system;

import com.braffolk.party_system.commands.CommandParty;
import com.braffolk.party_system.network.ClientMessageHandler;
import com.braffolk.party_system.network.SPHealthUpdate;
import com.braffolk.party_system.network.ServerMessageHandler;
import com.braffolk.party_system.proxy.CommonProxy;
import com.braffolk.party_system.tab.CreativeTabParty;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import net.minecraft.creativetab.CreativeTabs;

@Mod(modid = PartySystem.MODID, name = PartySystem.NAME, version = PartySystem.VERSION)
public class PartySystem {
   public static final String MODID = "party_system";
   public static final String NAME = "Party System";
   public static final String VERSION = "1.0";
   public static SimpleNetworkWrapper snw;
   
   
   @SideOnly(Side.CLIENT)
   public static Minecraft MC;
   
   @SidedProxy(
       clientSide = "com.braffolk.party_system.proxy.ClientProxy",
       serverSide = "com.braffolk.party_system.proxy.CommonProxy")
   public static CommonProxy proxy;
   
   @Mod.Instance
   public static PartySystem INSTANCE;
   private static Logger logger;
   public static CreativeTabParty tabParty;
   
   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      tabParty = new CreativeTabParty(CreativeTabs.getNextID(), "party_system");
      logger = event.getModLog();
      snw = NetworkRegistry.INSTANCE.newSimpleChannel("party_system");
      PartySystem.snw.registerMessage(ClientMessageHandler.class, SPHealthUpdate.class, 2, Side.CLIENT);
      PartySystem.snw.registerMessage(ServerMessageHandler.class, SPHealthUpdate.class, 2, Side.SERVER);
   
      proxy.preInit(event);
   }
   
   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }
   
   public void log(String message) {
      logger.info(message);
   }
   
   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      
      proxy.postInit(event);
   }
   
   @EventHandler
   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandParty());
      
      //MinecraftForge.EVENT_BUS.register(new com.braffolk.party_system.events.EventHandler());
   }
   
   

   
   
}
