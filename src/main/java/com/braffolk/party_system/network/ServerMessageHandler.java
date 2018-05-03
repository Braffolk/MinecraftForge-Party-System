package com.braffolk.party_system.network;

import com.braffolk.party_system.PartySystem;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerMessageHandler implements IMessageHandler<SPHealthUpdate, IMessage> {
    // Do note that the default constructor is required, but implicitly defined in this case

    @Override
    public IMessage onMessage( SPHealthUpdate message, MessageContext ctx) {
       PartySystem.INSTANCE.log("Server received SPHealthUpdate, This shouldn't happen");

        // No response packet
        return null;
    }
}
