package com.particle.asset.manager.swaggerResponses;

import com.particle.asset.manager.models.Error;

public class TicketResponses
{
    public static final Error BAD_REQUEST =
            new Error("400", "Empty Body or Null Value");
    // 422 → Unprocessable Entity
    public static final Error OPERATION_ERROR =
            new Error("422", "Wrong Data for that Operation");
    public static final Error USER_NOT_FOUND =
            new Error("404", "User Not Found");
    public static final Error ASSET_NOT_FOUND =
            new Error("404", "Asset Not Found");
    public static final Error ASSET_TYPE_NOT_FOUND =
            new Error("404", "Asset Type Not Found");
    public static final Error TICKET_NOT_FOUND =
            new Error("404", "Ticket Not Found");
    public static final Error INVALID_USER_TYPE =
            new Error("400", "Admins Cannot Open Tickets");
    public static final Error CANNOT_REPLY =
            new Error("423", "Can't Reply To Closed Tickets");
    public static final Error ALREADY_REPLIED =
            new Error("409", "You already replied. Wait for the other party to respond");
    public static final Error CANNOT_CLOSE =
            new Error("403", "Only Admins can Close Tickets");
}
