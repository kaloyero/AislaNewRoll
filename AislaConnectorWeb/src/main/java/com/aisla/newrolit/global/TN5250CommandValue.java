package com.aisla.newrolit.global;

public class TN5250CommandValue {
    // 5250 Escape (0x04) Commands (14.2)
    public static final Integer SAVE_SCREEN = 0x02;
    public static final Integer SAVE_SCR_PARTIAL = 0x03;
    public static final Integer WRITE_TO_DISPLAY = 0x11;
    public static final Integer RESTORE_SCREEN = 0x12;
    public static final Integer RESTORE_SCR_PARTIAL = 0x13;
    public static final Integer COPY_TO_PRINTER = 0X16;
    public static final Integer CLEAR_UNIT_ALT = 0x20;
    public static final Integer WRITE_ERRCODE = 0x21;
    public static final Integer WRITE_ERRCODE_WINDOW = 0x22;
    public static final Integer ROLL_SCREEN = 0x23;
    public static final Integer CLEAR_UNIT = 0x40;
    public static final Integer READ_INPFLDS = 0x42;
    public static final Integer CLEAR_FMTTBL = 0x50;
    public static final Integer READ_MDTFLDS = 0x52;
    public static final Integer READ_SCREEN = 0x62;
    public static final Integer READ_SCREEN_EA = 0x64;
    public static final Integer READ_SCRTOPRT = 0x66;
    public static final Integer READ_SCRTOPRT_EA = 0x68;
    public static final Integer READ_SCRTOPRT_GL = 0x6A;
    public static final Integer READ_SCRTOPRT_EA_GL = 0x6C;
    public static final Integer READ_IMMEDIATE = 0x72;
    public static final Integer READ_MDTFLDS_ALT = 0x82;
    public static final Integer READ_IMMEDIATE_ALT = 0x83;
    public static final Integer GRAPHICS_ON = 0x93;
    public static final Integer GRAPHICS_OFF = 0x94;
    public static final Integer END_GRAPHICS = 0x95;
    public static final Integer WRITE_STRUCTFLD = 0xF3;
    public static final Integer WRITE_STRUCTFLD_SNGL = 0xF4;

    // WriteToDisplay orders (0x11)
    public static final Integer SOH_ORDER = 0x01; // Start of header
    public static final Integer RA_ORDER = 0x02; // Repeat to address
    public static final Integer EA_ORDER = 0x03; // Erase to address
    public static final Integer TD_ORDER = 0x10; // Transparent data
    public static final Integer SBA_ORDER = 0x11; // Set buffer address
    public static final Integer WEA_ORDER = 0x12; // Write extended attribute
    public static final Integer IC_ORDER = 0x13; // Insert cursor
    public static final Integer MC_ORDER = 0x14; // Move cursor
    public static final Integer WDSF_ORDER = 0x15; // WriteToDisplay structured field
    public static final Integer SF_ORDER = 0x1D; // Start of field

    // WTD structured field orders
    public static final Integer DEFINE_SELECTION_FLD = 0x50;
    public static final Integer CREATE_WINDOW = 0x51;
    public static final Integer UNRESTRICTED_WIN_CSR = 0x52;
    public static final Integer DEFINE_SCROLL_BAR = 0x53;
    public static final Integer WRITE_DATA = 0x54;
    public static final Integer PROGRAM_MOUSE_BUTTON = 0x55;
    public static final Integer REMOVE_GUI_SEL_FLD = 0x58;
    public static final Integer REMOVE_GUI_WINDOW = 0x59;
    public static final Integer REMOVE_GUI_SCROLLBAR = 0x5B;
    public static final Integer REMOVE_ALL_GUI = 0x5F;
    public static final Integer DRAW_GRID_LINES = 0x60;
    public static final Integer CLEAR_GRID_LINES = 0x61;
    
    public static final Long AidAutoEnter 	  =  0x00000000L;
    public static final Long AidCommand1      =  0x00000001L;
    public static final Long AidCommand2      =  0x00000002L;
    public static final Long AidCommand3      =  0x00000004L;
    public static final Long AidCommand4      =  0x00000008L;
    public static final Long AidCommand5      =  0x00000010L;
    public static final Long AidCommand6      =  0x00000020L;
    public static final Long AidCommand7      =  0x00000040L;
    public static final Long AidCommand8      =  0x00000080L;
    public static final Long AidCommand9      =  0x00000100L;
    public static final Long AidCommand10     =  0x00000200L;
    public static final Long AidCommand11     =  0x00000400L;
    public static final Long AidCommand12     =  0x00000800L;
    public static final Long AidCommand13     =  0x00001000L;
    public static final Long AidCommand14     =  0x00002000L;
    public static final Long AidCommand15     =  0x00004000L;
    public static final Long AidCommand16     =  0x00008000L;
    public static final Long AidCommand17     =  0x00010000L;
    public static final Long AidCommand18     =  0x00020000L;
    public static final Long AidCommand19     =  0x00040000L;
    public static final Long AidCommand20     =  0x00080000L;
    public static final Long AidCommand21     =  0x00100000L;
    public static final Long AidCommand22     =  0x00200000L;
    public static final Long AidCommand23     =  0x00400000L;
    public static final Long AidCommand24     =  0x00800000L;
    public static final Long AidClear         =  0x01000000L;
    public static final Long AidEnter         =  0x02000000L;
    public static final Long AidHelp          =  0x04000000L;
    public static final Long AidRollDown      =  0x08000000L;
    public static final Long AidRollUp        =  0x10000000L;
    public static final Long AidPrint         =  0x20000000L;
    public static final Long AidBackRecord    =  0x40000000L;
    public static final Long AidSystemRequest =  0x80000000L;
    public static final Long AidAttention     =  0x100000000L;
    public static final Long AidAbort         =  0x200000000L;
    public static final Long AidErrorHelp     =  0x400000000L;
}
