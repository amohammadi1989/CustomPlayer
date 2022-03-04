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

    static AtomicBoolean isPlayer = new AtomicBoolean(false);
    static AtomicInteger CURRENT_PLAY = new AtomicInteger(0);
    static volatile boolean CMPLAYED = false;
    static AtomicBoolean STATE_PAUSE = new AtomicBoolean(true);

    static Boolean STATE_STOP = true;

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

    public static int GetCurrentPlay(){
        return CURRENT_PLAY.get();
    }

    public static void PlayMusic() {
        STATE_STOP = false;
        STATE_PAUSE.set(false);
        if (!isPlayer.get()) {
            Runnable runnablePlay = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (!STATE_PAUSE.get() && !STATE_STOP) {
                            CheckToPlay();
                        }
                    }
                }
            };
            Thread threadPlay = new Thread(runnablePlay);
            threadPlay.start();
            isPlayer.set(true);
        }
    }

    public static Long GetTotalBytesOfCurrentFile() {
        try {
            return detailsSongs.get(CURRENT_PLAY.get()).getLen();
        }catch (Exception e){
            //  e.printStackTrace();
        }
        return 0l;

    }

    public static long GetReadBytesOfCurrentFile() {
        try {
            return (detailsSongs.get(CURRENT_PLAY.get()).getLen().intValue() - CPlayer.AvailableCurrent());
        }catch (Exception ex){
            //    ex.printStackTrace();
        }
        return 0l;
        //return CPlayer.AvailableCurrent();
    }

    public static void SelectedMusic(int i) {
        StopMusic();
        CURRENT_PLAY.set(i);
        PlayMusic();
        System.out.printf("cu="+i);
        CURRENT_PLAY.set(i);
    }

    private static String fileTime(byte[] fileName) {

        return "--";
    }

    private static void CheckToPlay() {
        if (ExistsSong()) {
            Song song = detailsSongs.get(CURRENT_PLAY.get());
            long skip = song.skip!=0?(long)song.skip:0;
            CMPLAYED = true;
            song.setSkip(0);
            boolean isComplete=CPlayer.Play(song.content, skip);
            CMPLAYED = false;
            if (!STATE_PAUSE.get() && !STATE_STOP && isComplete)
                ChangeCurrentPlayNext();
        }
    }

    public static String GetReadTimeOfCurrentPlay(){
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

    public static long GetTotalTimeOfCurrentPlay(){
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
          //  int size = h.calculate_framesize();
           // float ms_per_frame = h.ms_per_frame();
            //int maxSize = h.max_number_of_frames(10000);
           // float t = h.total_ms(size);
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

    public static String ConvertSecondToMinute(long s){

        long minute = TimeUnit.SECONDS.toMinutes(s) - (TimeUnit.SECONDS.toHours(s)* 60);
        long second = TimeUnit.SECONDS.toSeconds(s) - (TimeUnit.SECONDS.toMinutes(s) *60);
        return minute+":"+(second);

    }

    private static Boolean ExistsSong() {
        try {
            if(CURRENT_PLAY.get()<detailsSongs.size()) {
                return detailsSongs.get(CURRENT_PLAY.get()) != null ? true : false;
            }
        } catch (Exception ex) {
            return false;
        }
        return false;

    }

    private static void ChangeCurrentPlayNext() {
        CURRENT_PLAY.set(CURRENT_PLAY.get() + 1);
        if(CURRENT_PLAY.get()==detailsSongs.size()){
            CURRENT_PLAY.set(0);
        }
    }

    private static void ChangeCurrentPlayPreviews() {
        CURRENT_PLAY.set(CURRENT_PLAY.get() - 1);
        if(CURRENT_PLAY.get()<0){
            CURRENT_PLAY.set(detailsSongs.size()-1);
        }
    }

    public static void AddMusicToList(String path) {

        ByteArrayOutputStream out = getContentOfFile(path);

        //create new song
        Song song = new Song((long) out.size(), path, out);

        //Add to list
        detailsSongs.add(song);

    }

    public static int NextMusic() {
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

    public static int PreviousMusic() {
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
    public static String getSelectedMusic(int i) {
        return detailsSongs.get(i).getName();
    }

    public static String GetNameOfCurrentPlayed() {
        return (detailsSongs.get(CURRENT_PLAY.get()).getName());
    }

    public static String getSelectedMusic() {
        File file = new File(GetNameOfCurrentPlayed());
        return fileTime(file.getName().getBytes());
    }



    public static void StopMusic() {
        CMPLAYED = true;
        CPlayer.Stop(detailsSongs.get(CURRENT_PLAY.get()));
        STATE_STOP = true;
    }

    public static void ResumeMusic() {
        if (ExistsSong()) {
            STATE_PAUSE.set(false);
            CMPLAYED = true;
        }
    }

    public static void PauseMusic() {
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
        ResumeMusic();

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

        public String getName() {
            return name;
        }

        public int getSkip() {
            return skip;
        }

        public void setSkip(int skip) {
            this.skip = skip;
        }


        public ByteArrayOutputStream getContent() {
            return content;
        }
    }

}