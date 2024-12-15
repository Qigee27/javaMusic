package com.pigeon;

import com.pigeon.file.MyFileReaderTxt;
import com.pigeon.file.MyStringDistributerIMPL;
import com.pigeon.note.MyNoteIMPL;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import java.io.RandomAccessFile;

public class Pigeon7 implements MetaEventListener {

    MyStringDistributerIMPL md = new MyStringDistributerIMPL();
    MyFileReaderTxt mf = new MyFileReaderTxt();
    Sequencer player;
    String file;

    public Pigeon7() throws Exception {
        player = MidiSystem.getSequencer();
    }

    public void setFile(String file) {
        this.file = file;
    }

    long tick;
    boolean first = true;

    @Override
    public void meta(MetaMessage meta) {
        if (meta.getType() == 127) {
            String data = new String(meta.getData());
            String[] arr = data.split(" ");
            String note = arr[0];
            long currentTick = player.getTickPosition();
            if (tick < currentTick / md.getMyNoteIMPL().getBarTick()) {
                tick = currentTick / md.getMyNoteIMPL().getBarTick();
                System.out.println();
                System.out.printf("%5d:当前第%3d/%3d节:  ", currentTick, (tick + 1), player.getTickLength() / md.getMyNoteIMPL().getBarTick());
                System.out.printf("%-5s", md.getMyNoteIMPL().calNoteNum(Integer.parseInt(note)));
            } else {
                if (first) {
                    System.out.printf("%5d:当前第%3d/%3d节:  ", currentTick, (tick + 1), player.getTickLength() / md.getMyNoteIMPL().getBarTick());
                    first = false;
                }
                String s = md.getMyNoteIMPL().calNoteNum(Integer.parseInt(note));
                System.out.printf("%-5s", s);
            }
        } else if (meta.getType() == 47) {
            System.out.println();
            System.out.println("播放完毕");
            player.close();
        } else {
            System.out.println(meta.getType());
        }
    }

    public MyNoteIMPL getMyNoteIMPL() {
        return md.getMyNoteIMPL();
    }

    public float getBPM() {
        return player.getTempoInBPM();
    }

    public String getPPQ() {
        return getMyNoteIMPL().getPpq();
    }

    public void play() throws Exception {
        RandomAccessFile read = mf.Read(file);
        md.distribute(read);
        player.setTempoInBPM(md.getMyNoteIMPL().getBpm());
        player.open();
        Thread.sleep(200);
        player.setSequence(md.getSequence());
        player.addMetaEventListener(this);
        if (player.getSequence() == null) {
            player.close();
            return;
        }
        player.start();
    }

    public static void main(String[] args) throws Exception {
        Pigeon7 p = new Pigeon7();
        p.setFile("一路生花.txt");
        p.play();
    }
}