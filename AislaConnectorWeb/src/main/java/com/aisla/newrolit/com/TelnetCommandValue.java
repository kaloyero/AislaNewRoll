package com.aisla.newrolit.com;

interface TelnetCommandValue {
    // Telnet commands
    public static final Integer TNC_IAC = 255;              // 0xFF uinterpret As Command
    public static final Integer TNC_DONT = 254;              // 0xFE Request not to do option
    public static final Integer TNC_DO = 253;              // 0xFD Offer to do - WILL/WONT response
    public static final Integer TNC_WONT = 252;              // 0xFC Refusal to do option
    public static final Integer TNC_WILL = 251;              // 0xFB Offer to do - DO/DONT response
    public static final Integer TNC_SB = 250;              // 0xFA Subnegotiation Begin
    public static final Integer TNC_GA = 249;              // 0xF9 Go Ahead
    public static final Integer TNC_EL = 248;              // 0xF8 Erase Line
    public static final Integer TNC_EC = 247;              // 0xF7Erase Character
    public static final Integer TNC_AYT = 246;              // 0xF6 Are You There
    public static final Integer TNC_AO = 245;              // 0xF5 Abort Output
    public static final Integer TNC_IP = 244;              // 0xF4 uinterrupt Process
    public static final Integer TNC_BRK = 243;              // 0xF3 Break
    public static final Integer TNC_DM = 242;              // 0xF2 Data Mark
    public static final Integer TNC_NOP = 241;              // 0xF1 No Operation
    public static final Integer TNC_SE = 240;              // 0xF0 Subnegotiation End
    public static final Integer TNC_EF = 239;              // 0xEF End Of Record
    
    // TN5250 Operation Codes (RFC1205)
    public static final Integer OP_NOP = 0x00;
    public static final Integer OP_INVITE = 0x01;
    public static final Integer OP_OUTONLY = 0x02;
    public static final Integer OP_PUTGET = 0x03;
    public static final Integer OP_SAVSCR = 0x04;
    public static final Integer OP_RSTSCR = 0x05;
    public static final Integer OP_RDIMMED = 0x06;
    public static final Integer OP_RESERVED = 0x07;
    public static final Integer OP_RDSCREEN = 0x08;
    public static final Integer OP_INVOFF = 0x0A;
    public static final Integer OP_MSGLTON = 0x0B;
    public static final Integer OP_MSGLTOFF = 0x0C;
    public static final Integer BITON = 1;
    public static final Integer BITOFF = 0;

    public static final Integer DS5250_ESCAPE = 0x04;

    public static final Integer BLANKCHAR = 0x20;
    public static final Integer NULLCHAR = 0x00;
    public static final Integer SYSRQS_KEY = 0x04;
    public static final Integer ATTN_KEY = 0x40;
    public static final Integer ERR_HELP = 0x01;
    public static final Integer ABORT_KEY = 0xfe;

    // Negotiation options
    public static final Integer TNO_TBIN = 0;                // Binary Transmission - RFC856
    public static final Integer TNO_SGA = 3;                // Suppress Go Ahead   - RFC858
    public static final Integer TNO_TMRK = 6;                // Timing Mark         - RFC860
    public static final Integer TNO_TTYP = 24;               // Terminal Type       - RFC1091
    public static final Integer TNO_EOR = 25;               // End of Record       - RFC885
    public static final Integer TNO_ENV = 39;               // New enviroment      - RFC1572

    public static final Integer TTYP_IS = 0;            // Terminal type is
    public static final Integer TTYP_SEND = 1;            // Terminal type send

    public static final Integer TENV_IS = 0;
    public static final Integer TENV_SEND = 1;
    public static final Integer TENV_INFO = 2;            // Environment Info
    public static final Integer TENV_VAR = 0;
    public static final Integer TENV_VALUE = 1;
    public static final Integer TENV_ESC = 2;
    public static final Integer TENV_USERVAR = 3;
    
    public static final String IBM5555C = "IBM-5555-C01"; // Color 24x80 DBCS
    public static final String IBM5555B = "IBM-5555-B01"; // Mono  24x80 DBCS
    public static final String IBM3477C = "IBM-3477-FC";  // Color 27x132
    public static final String INET3477C = "INET-3477-FC";  // Color 27x132
    public static final String IBM3477G = "IBM-3477-FG";  // Mono  27x132
    public static final String IBM_3180 = "IBM-3180-2";   // Mono  27x132
    public static final String IBM_3179 = "IBM-3179-2";   // Color 24x80
    public static final String IBM_3196 = "IBM-3196-A1";  // Mono  27x132
    public static final String IBM_5292 = "IBM-5292-2";   // Color 24x80
    public static final String IBM_5291 = "IBM-5291-1";   // Mono  24x80
    public static final String IBM_5250 = "IBM-5251-11";  // Mono  24x80
    
}
