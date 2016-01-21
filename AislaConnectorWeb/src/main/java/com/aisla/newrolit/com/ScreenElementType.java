package com.aisla.newrolit.com;

/**
 * ProcessMode enum all the field types 
 * <p>
 * Main objects are the following
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public enum ScreenElementType {
    Terminate,
    Set24x80,
    Set27x132,
    lockKeyboard,
    UnlockKeyboard,
    ClearInput,
    SaveDisplay,
    BeginRestore,
    EnableKeys,
    NonTransmitKeys,
    LibraryName,
    MemberName,
    FormName,
    ClearDisplayArea,
    RollUp,
    HostField,
    HostFieldExtend,
    ExecuteCommand,
    HelpFormName,
    EndHelp,
    StatusText,
    ErrorNotification,
    Set32x80,
    Set43x80,
    MsgWaitingOn,
    MsgWaitingOff,
    SoundAlarm,
    None
};
