package com.pigeon.resolver;


import com.pigeon.note.MyNoteIMPL;
import com.pigeon.note.NoteInfo;

/**
 * 单个音符解析接口定义
 */
public interface SingleNoteResolver {
    NoteInfo singleNoteResolve(MyNoteIMPL myNoteIMPL, String noteString, int originTick);
}
