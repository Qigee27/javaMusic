package com.pigeon.resolver;


import com.pigeon.note.MyNoteIMPL;
import com.pigeon.note.NoteInfo;

/**
 * 处理倚音倚音接口定义
 */
public interface AppoggiaturaResolver {
    NoteInfo[] appoggiatura(String noteFrontString, String noteAfterString, int noteStart, MyNoteIMPL myNoteIMPL, SingleNoteResolver singleNoteResolver);
}
