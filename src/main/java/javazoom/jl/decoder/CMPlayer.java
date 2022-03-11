package javazoom.jl.decoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CMPlayer {


    static List<Song> detailsSongs;

    static volatile AtomicBoolean isPlayer = new AtomicBoolean(false);
    static volatile AtomicInteger CURRENT_PLAY = new AtomicInteger(0);
    static volatile Integer PREVIOUS_PLAY=-1;
    static volatile boolean CMPLAYED = false;
    static volatile AtomicBoolean STATE_PAUSE = new AtomicBoolean(true);

    static volatile Boolean STATE_STOP = true;

    private static CMPlayer cmPlayer = new CMPlayer();

    private CMPlayer() {

        try {
            detailsSongs = new ArrayList<>();
            STATE_PAUSE.set(true);
            STATE_STOP = true;

        } catch (Exception e) {
        }


    }

    public static Boolean isCMPlayed() {
        return CMPLAYED;
    }
    private synchronized static void initPlayer() {

        if (detailsSongs.get(CURRENT_PLAY.get()).getContent() == null) {
            ByteArrayOutputStream content = CMPlayer.getContentOfFile(detailsSongs.get(CURRENT_PLAY.get()).getName());
            detailsSongs.get(CURRENT_PLAY.get()).setContent(content);
            detailsSongs.get(CURRENT_PLAY.get()).setLen((long)content.size());
        }
        if(PREVIOUS_PLAY==-1){
            PREVIOUS_PLAY=CURRENT_PLAY.get();
        }else {
            if(PREVIOUS_PLAY!=CURRENT_PLAY.get()) {
                detailsSongs.get(PREVIOUS_PLAY).setContent(null);
                detailsSongs.get(PREVIOUS_PLAY).setLen(null);
                PREVIOUS_PLAY=CURRENT_PLAY.get();
                System.gc();
            }
        }
    }
    public static int GetCurrentPlay(){
        return CURRENT_PLAY.get();
    }

    public static void PlayMusic() {
        CPlayer.Clear();
        STATE_STOP = false;
        STATE_PAUSE.set(false);
        //if (!isPlayer.get()) {
        Runnable runnablePlay = new Runnable() {
            @Override
            public void run() {
                while (!STATE_PAUSE.get() && !STATE_STOP) {

                    CheckToPlay();
                }
            }
        };
        Thread threadPlay = new Thread(runnablePlay);
        threadPlay.start();
        isPlayer.set(true);
        //  }
    }

    public synchronized static int GetTotalBytesOfCurrentFile() {
        try {
            //initPlayer();
            if(detailsSongs.get(CURRENT_PLAY.get())!=null) {
                if(detailsSongs.get(CURRENT_PLAY.get()).getLen()>0)
                    return detailsSongs.get(CURRENT_PLAY.get()).getLen().intValue();
            }

        } catch (Exception e) {
            //  e.printStackTrace();
        }
        return 0;

    }

    public synchronized static long GetReadBytesOfCurrentFile() {
        try {
            return (detailsSongs.get(CURRENT_PLAY.get()).getLen().intValue() - CPlayer.AvailableCurrent());
        }catch (Exception ex){
            //    ex.printStackTrace();
        }
        return 0l;
        //return CPlayer.AvailableCurrent();
    }

    public  static synchronized void SelectedMusic(int i) {
        StopMusic();
        CURRENT_PLAY.set(i);
        try {
            Thread.sleep(10);
        }catch (Exception ex){}
        PlayMusic();
        System.out.printf("cu="+i);
        CURRENT_PLAY.set(i);
    }

    private static String fileTime(byte[] fileName) {

        return "--";
    }

    private static void CheckToPlay() {
        if (ExistsSong()) {
            initPlayer();
            Song song = detailsSongs.get(CURRENT_PLAY.get());
            long skip = song.skip!=0?(long)song.skip:0;
            CMPLAYED = true;
            song.setSkip(0);
            boolean isComplete=CPlayer.Play(detailsSongs.get(CURRENT_PLAY.get()).getContent(), skip);
            CMPLAYED = false;
            System.gc();
            if (!STATE_PAUSE.get() && !STATE_STOP && isComplete)
                ChangeCurrentPlayNext();
        }
    }

    public synchronized static String GetReadTimeOfCurrentPlay(){
        try{
            long totalLen= GetTotalBytesOfCurrentFile();
            long curentPlay= GetReadBytesOfCurrentFile();
            long totalTime= GetTotalTimeOfCurrentPlay();
            long result=(totalTime*curentPlay)/totalLen;
            return ConvertSecondToMinute(result);
        }catch (Exception ex){

        }
        return "0:0";
    }

    public synchronized static long GetTotalTimeOfCurrentPlay(){
        try {

            String path = detailsSongs.get(CURRENT_PLAY.get()).getName();

            Header h = null;
            FileInputStream file = null;
            try {
                file = new FileInputStream(path);
            } catch (FileNotFoundException ex) {
            }
            Bitstream bitstream = new Bitstream(file);
            try {
                h = bitstream.readFrame();
            } catch (BitstreamException ex) {
            }
            long tn = 0;
            try {
                tn = file.getChannel().size();
            } catch (IOException ex) {
            }
            int min = h.min_number_of_frames(500);
            long s=(long)((h.total_ms((int) tn)/1000));


            return s-12;

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0l;

    }

    public synchronized static String ConvertSecondToMinute(long s){

        long minute = TimeUnit.SECONDS.toMinutes(s) - (TimeUnit.SECONDS.toHours(s)* 60);
        long second = TimeUnit.SECONDS.toSeconds(s) - (TimeUnit.SECONDS.toMinutes(s) *60);
        return minute+":"+(second);

    }

    private synchronized static Boolean ExistsSong() {
        try {
            if(CURRENT_PLAY.get()<detailsSongs.size()) {
                return detailsSongs.get(CURRENT_PLAY.get()) != null ? true : false;
            }
        } catch (Exception ex) {
            return false;
        }
        return false;

    }

    private synchronized static void ChangeCurrentPlayNext() {
        CURRENT_PLAY.set(CURRENT_PLAY.get() + 1);
        if(CURRENT_PLAY.get()==detailsSongs.size()){
            CURRENT_PLAY.set(0);
        }
    }

    private synchronized static void ChangeCurrentPlayPreviews() {
        CURRENT_PLAY.set(CURRENT_PLAY.get() - 1);
        if(CURRENT_PLAY.get()<0){
            CURRENT_PLAY.set(detailsSongs.size()-1);
        }
    }

    public synchronized static void AddMusicToList(String path) {

        // ByteArrayOutputStream out = getContentOfFile(path);

        //create new song
        Song song = new Song(null, path,null);

        //Add to list
        detailsSongs.add(song);

    }

    public synchronized static int NextMusic() {
        try {
            ChangeCurrentPlayNext();
            if (ExistsSong()) {
                detailsSongs.get(CURRENT_PLAY.get()).setSkip(0);
            }
            if (STATE_PAUSE.get() || STATE_STOP) {
                if (CURRENT_PLAY.get() >= detailsSongs.size() - 1) {
                    CURRENT_PLAY.set(0);
                }

                return CURRENT_PLAY.get();
            }
            if(CURRENT_PLAY.get()==detailsSongs.size()){
                CURRENT_PLAY.set(0);
                return CURRENT_PLAY.get();
            }
            if (CURRENT_PLAY.get() >= detailsSongs.size()-1) {
                CPlayer.Stop(detailsSongs.get(CURRENT_PLAY.get()-1));
                detailsSongs.get(CURRENT_PLAY.get()-1).setSkip(0);

                return CURRENT_PLAY.get();
            }

            if (ExistsSong()) {
                CMPLAYED = false;
                detailsSongs.get(CURRENT_PLAY.get()).setSkip(0);
                CPlayer.Stop(detailsSongs.get(CURRENT_PLAY.get()));
                //getSelectedMusic();
                return CURRENT_PLAY.get() ;
            }
            return CURRENT_PLAY.get();
        } catch (Exception ex) {
            CURRENT_PLAY.set(0);
            return CURRENT_PLAY.get();
        }

    }

    public synchronized static int PreviousMusic() {
        try {
            ChangeCurrentPlayPreviews();
            if (ExistsSong()) {
                CPlayer.Stop(detailsSongs.get(CURRENT_PLAY.get()));
                detailsSongs.get(CURRENT_PLAY.get()).setSkip(0);
            }
            if (STATE_PAUSE.get() || STATE_STOP) {
                if (CURRENT_PLAY.get() == 0) {
                    CURRENT_PLAY.set(detailsSongs.size() - 1);
                }
                return CURRENT_PLAY.get();
            }

            if (ExistsSong()) {
                CMPLAYED = false;
                return CURRENT_PLAY.get() ;
            }
        }catch (Exception ex){
            CURRENT_PLAY.set(0);
            return CURRENT_PLAY.get();
        }
        return 0;
    }
    public synchronized static String getSelectedMusic(int i) {
        return detailsSongs.get(i).getName();
    }

    public synchronized static String GetNameOfCurrentPlayed() {
        return (detailsSongs.get(CURRENT_PLAY.get()).getName());
    }

    public synchronized static String getSelectedMusic() {
        File file = new File(GetNameOfCurrentPlayed());
        return fileTime(file.getName().getBytes());
    }



    public synchronized static void StopMusic() {
        CMPLAYED = true;
        CPlayer.Stop(detailsSongs.get(CURRENT_PLAY.get()));
        STATE_STOP = true;
    }

    public synchronized static void ResumeMusic() {
        if (ExistsSong()) {
            STATE_PAUSE.set(false);
            CMPLAYED = true;
            PlayMusic();
        }
    }
    public synchronized static void ResumeMusicForSkip() {
        if (ExistsSong()) {
            STATE_PAUSE.set(false);
            CMPLAYED = true;
        }
    }
    public synchronized static void PauseMusic() {
        if (ExistsSong()) {
            STATE_PAUSE.set(true);
            CMPLAYED = false;
            CPlayer.Pause(detailsSongs.get(CURRENT_PLAY.get()));
        }
    }

    public static void ListClear() {
        detailsSongs = new ArrayList<>();
    }

    private static byte[] AppendData(byte[] input, int index, int size) {
        ByteBuffer Storage = ByteBuffer.allocate(size);
        Storage.put(input, index, size);
        return Storage.array();
    }

    private static byte[] decrypt(byte[] data, String key) throws Exception {

        return null;
    }

    private static ByteArrayOutputStream getContentOfFile(String path) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[700000];
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(path));
            int read = 0;
            while ((read = reader.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
        } catch (Exception ex) {

        }
        return baos;
    }

    public static void SkipCurrent(int skip) {

        PauseMusic();
        detailsSongs.get(CURRENT_PLAY.get()).setSkip(skip);
        ResumeMusicForSkip();

    }

    static class Song {
        private Long len;
        private String name;
        private int skip = 0;
        private ByteArrayOutputStream content;

        public Song(Long len, String name, ByteArrayOutputStream content) {
            this.len = len;
            this.name = name;
            this.content = content;
            this.skip = 0;
        }

        public Long getLen() {
            return len;
        }

        public void setLen(Long len){ this.len=len;}

        public String getName() {
            return name;
        }

        public int getSkip() {
            return skip;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setContent(ByteArrayOutputStream content) {
            this.content = content;
        }

        public void setSkip(int skip) {
            this.skip = skip;
        }


        public ByteArrayOutputStream getContent() {
            return content;
        }
    }

}