# MySound

//        p.setInstrument(40,1);
//       p.setFile("起风了.txt");
//       p.setFile("Tassel.txt");
//       p.setFile("菊次郎的夏天.txt");
//       p.setFile("My Soul.txt");
//        p.setFile("动天.txt");//不好听
//        p.setFile("夜的钢琴曲五.txt");
//        p.setFile("生日快乐歌.txt");//不好听
//        p.setFile("komorebi.txt");
//        p.setFile("告白之夜.txt");
//p.setFile("悬溺.txt");
//p.setFile("起风了.txt");
//p.setFile("My Soul.txt");
//p.setFile("夜的钢琴曲五.txt");
//p.setFile("komorebi.txt");
//p.setFile("菊次郎的夏天.txt");
//p.setFile("Tassel.txt");
//p.setFile("动天.txt");//不好听






//对外接口
public class Pigeon7 implements MetaEventListener {

    /*    (command >= 0xF0 || command < 0x80) //0x80开始，0x90结束，0xa0，0xb0，0xc0，0xd0，0xe0
        (channel & 0xFFFFFFF0) != 0)
        满足上边两个条件会报错*/
    MyStringDistributerIMPL md = new MyStringDistributerIMPL();
    MyFileReaderTxt mf = new MyFileReaderTxt();
    Sequencer player;
    String file;
    private boolean reserve = false;

    public void setReserve(boolean reserve) {
        this.reserve = reserve;
    }

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
        if (reserve) {
            reversePlay();
        }
        player.setSequence(md.getSequence());
        player.addMetaEventListener(this);
        if (player.getSequence() == null) {
            player.close();
            return;
        }
        player.start();
    }
    //倒放
    private void reversePlay() {
        Track track = md.getSequence().getTracks()[0];
        Track track1 = md.getSequence().createTrack();
        CopyOnWriteArrayList<NoteInfo> noteInfoList = md.getNoteInfoList();
        CopyOnWriteArrayList<NoteInfo> noteInfoList2 = new CopyOnWriteArrayList<>();

        for (int i = 0; i < track.size(); i++) {
            MidiEvent midiEvent = track.get(i);
            long tick = track.ticks() - midiEvent.getTick();
            midiEvent.setTick(tick);
            track1.add(midiEvent);
        }
        for (int i = noteInfoList.size() - 1; i >= 0; i--) {
            long tick = track1.ticks();
            NoteInfo noteInfo = noteInfoList.get(i);
            noteInfo.setOriginTick(tick - noteInfo.getOriginTick());
            noteInfoList2.add(noteInfo);
        }
        md.setNoteInfoList(noteInfoList2);
        md.getSequence().deleteTrack(track);
    }

//p.setReserve(false);//true倒放
### 介绍

自定义乐谱解释器

### 安装教程

1. 下载源码到本地
2. 用IDEA打开,其他的(如eclipse)需要自己调试
3. 运行MyPlayer中的main方法即可
4. 不需要任何第三方库,装了java就可以玩

## 重要!!!

1. 不要过度研究当下版本,可以当玩具研究下.目前设计上还有很多缺陷,后续会大改
近期没时间,有时间会继续完善
2. 还有,java的就业方向以web开发为主,还在上学的小伙伴一定要研究好再选择,切
不可因一时好玩草率选择编程语言
3. 着急研究midi相关api的小伙伴可移步[Oracle官方demo]
4. 着急用音乐编程达到更好效果的小伙伴可以搜索关键字 JFugue, abc4j(太老了)这些项目
开源且成熟,能快速实现一个乐队(前提你懂乐理)
5. 着急编曲的小伙伴请搜索引擎自行搜索 打谱软件 会找到你想要的

[Oracle官方demo]:https://www.oracle.com/java/technologies/java-sound-demo.html

#### 使用说明

`|  符号  | 示例       |                 说明                  |  
|:----:|:---------|:-----------------------------------|  
| 单纯数字 | 1        |               表示四分音符                |
|  -   | -3       |               表示八分音符                |
|  --  | --3      |           表示十六分音符,后边依此类推            |
|  \+  | 3\+      |            表示延长一个四分音符长度             |
| \+\+ | 3\+\+    |         表示延长二个四分音符长度,后边依此类推         |
|  l   | 3l       |          表示低一个八度(l:low的简写)          |
|  ll  | 3ll      |           表示低二个八度,后边依此类推            |
|  h   | 3h       |         表示高一个八度(h:high的简写)          |
|  hh  | 3hh      |           表示高二个八度,后边依此类推            |
|  :   | 3:2      |              表示同时按下3和2              |
|  :   | 1:2:3    |             表示同时按下1,2和3             |
|  u   | 3u       |            升符号(up),表示升3             |
|  b   | 3b       |           降符号(blow),表示降3            |
|  .   | 3.       |    表示延长本音节原有长度的一半(3.的音长是3的1.5倍)     |
|  ^   | ^3       |             表示本音节符号不发音              |
|  \>  | 3\>      |       顿音符号,表示本音符发音时长占音符时长的1/4       |
|  <  | 3<2      | 倚音符号,音节时长由后边的音符决定,前边音符发音占本音节时长的1/16 |

``|    关键字     |     示例     | 说明                 |  
|:----------:|:----------:|:-------------------|  
|   major    |  major:c   | 声明简谱是C调的,简谱中可变换多次  |
|    PPQ     |  PPQ:3/4   | 声明简谱是4分音符为一拍,没小节3拍 |
|    BPM     |  BPM:155   | 声明简谱每分钟155拍        |
| rightStart | rightStart | 右手轨道开始             |
| leftStart  | leftStart  | 左手轨道开始             |
|  rightEnd  |  rightEnd  | 右手轨道结束             |
|  leftEnd   |  leftEnd   | 左手轨道结束             |
|     //     |    //注释    | 本行为注释内容            |```

### 规则

1. 指令行每一行为一个指令,如生明major,PPQ等
2. 乐符为每一行一个小节,乐符之间以空格分开
3. 乐符录入前要先声明是左手还是右手rightStart,leftStart
4. 乐符左右手部分结束后需要声明结束
5. 左右手不可同时开启
6. 左右手声明结束后可以再次声明开始,从第一个节拍开始录,不会覆盖前边

---
#### 说明:

````
    目前本项目以钢琴为,只实现了部分钢琴技法,主要是针对简谱,
后续会增加五线谱以及JFugue以及xmlmusic的解析,以上只是设想,
目前没空,有空还能想起来的话,再写吧
````
