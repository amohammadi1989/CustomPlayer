package javazoom.jl.decoder;


import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class CPlayer {
    private static final int ZERO = 0;
    private static final int TWO_HUNDRED_EIGHTY = 280;
    private static final int ONE = 1;
    //private static AtomicBoolean flagShare=new AtomicBoolean(false);
    private static Player player;
    private static ByteArrayInputStream input;
    private static ByteArrayOutputStream out = new ByteArrayOutputStream();
    public volatile List<Streams> inputStreams = Collections.synchronizedList(new LinkedList<>());
    public Streams currentStreams;
    public long currentTime = 0;
    public long preTime = 0;
    private int k = 0;
    private Bitstream bitstream;
    private Decoder decoder;
    private javazoom.jl.decoder.CPlayer CPlayer;
    private AudioDevice audio;
    private AtomicInteger TOTAL_PLAYS = new AtomicInteger(15);
    private AtomicInteger COUNT_INSTANCE = new AtomicInteger(288);
    private static boolean closed = false;
    private boolean complete = false;
    private int lastPosition = 0;
    private static final Integer MAX_COUNT_INSTANCE=288;
    private final Integer TWO_HUNDRED=200;
    private final Integer MAX_BUFFER_LEN=1024;

 /*   public static void ChangeFlag(){
        flagShare.set(true);
    }*/
    public CPlayer(Bitstream stream) throws JavaLayerException {
        this(stream, null);
    }

    public CPlayer() {
        try {
            closed = false;
            out = new ByteArrayOutputStream();
            CPlayer = this;

            executePlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Boolean isCPlayed(){
        return !closed;
    }

    public CPlayer(Bitstream stream, AudioDevice device) throws JavaLayerException {
        fillStream(stream, device);
    }

    public static boolean Play(ByteArrayOutputStream out, long skip) {
        try {
            if(skip!=0) {
                player.close();
            }
            input = new ByteArrayInputStream(out.toByteArray());

            if (skip != 0) {
                input.skip(skip);
            }
            //input.skip(skip);
            player = new Player(input);
            player.play();
        } catch (Exception ex) {
        }
        return player.isComplete();
    }

    public static int AvailableCurrent() {
        return input.available();
    }

    public static void Pause(CMPlayer.Song song) {
        try {
            song.setSkip(song.getLen().intValue()-input.available());
            player.close();
        } catch (Exception ex) {

        }
    }

    public static void Stop(CMPlayer.Song song) {
        try {
            song.setSkip(0);
            player.close();
        } catch (Exception ex) {

        }
    }
    public static void Clear(){
        try {
            player.close();
        }catch (Exception ex){

        }
    }

    public void executePlayer() {
        Runnable play = new Runnable() {
            @Override
            public void run() {
                try {
                    CPlayer.play();
                } catch (Exception e) {

                }
            }
        };
        Thread executePlayer = new Thread(play);
        executePlayer.start();
    }


    public synchronized void addBuffers(byte[] buf) {
        if (!closed) {
          //  getDelayOfTransfer(buf);
          //  if(flagShare.get()==false) {
                try {
                    if (TOTAL_PLAYS.get() == -1) {
                        if (COUNT_INSTANCE.get() == 0) {
                            currentStreams.closeStream();
                            currentStreams = new Streams();
                            inputStreams.add(currentStreams);
                            COUNT_INSTANCE.set(MAX_COUNT_INSTANCE);
                            currentStreams.addBuffers(buf);
                        } else {
                            currentStreams.addBuffers(buf);
                        }
                    }
                    if (TOTAL_PLAYS.get() > 0) {
                        out.write(buf, 0, buf.length);
                        TOTAL_PLAYS.set(TOTAL_PLAYS.get() - 1);
                    } else if (TOTAL_PLAYS.get() == 0) {
                        out.write(buf, 0, buf.length);
                        currentStreams = new Streams(out.toByteArray());
                        inputStreams.add(currentStreams);
                        TOTAL_PLAYS.set(-1);
                    }
                    COUNT_INSTANCE.set(COUNT_INSTANCE.get() - 1);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
         /*   }else {
                if(TOTAL_PLAYS.get()==-1){
                    TOTAL_PLAYS.set(16);
                    out=new ByteArrayOutputStream();
                }
                if (TOTAL_PLAYS.get() > 0) {
                    out.write(buf, 0, buf.length);
                    TOTAL_PLAYS.set(TOTAL_PLAYS.get() - 1);
                } else if (TOTAL_PLAYS.get() == 0) {
                    out.write(buf, 0, buf.length);
                    currentStreams = new Streams(out.toByteArray());
                    currentStreams.closeStream();
                    inputStreams.add(currentStreams);
                    TOTAL_PLAYS.set(15);
                    out=new ByteArrayOutputStream();
                }
            }*/
        }
    }

    private void getDelayOfTransfer(byte[] buf) {
        if (currentTime == 0) {
            currentTime = new Date().getTime();
            preTime = currentTime;
        } else {
            currentTime = preTime;
            preTime = new Date().getTime();
        }
        System.out.println("k=" + k++ + "==output stream=" + buf.length + "=>totalTime:" + ((new Date().getTime()) - currentTime));
    }

    public void addBuffers(ByteArrayInputStream in) {
        //TODO: This section with implement with  inputstream
    }

    private void fillStream(Bitstream stream, AudioDevice device) throws JavaLayerException {
        bitstream = stream;
        decoder = new Decoder();
        if (device != null) {
            audio = device;
        } else {
            FactoryRegistry r = FactoryRegistry.systemRegistry();
            audio = r.createAudioDevice();
        }
        audio.open(decoder);
    }

    public void play() throws JavaLayerException {
        play(Integer.MAX_VALUE);
    }

    public void play(int frames) throws JavaLayerException {
        decodeFrame();
    }

    public synchronized void stop() {
        try {
            AudioDevice out = audio;
            closed = true;
            inputStreams.removeAll(new ArrayList<>());
            if (out != null) {
                audio = null;
                out.close();
                lastPosition = out.getPosition();
                bitstream.close();
            }
        } catch (Exception ex) {
            System.out.println(MessageFormat.format("When stop online steam player exception has raise.{0}!", ex.toString()));
        }
    }

    public synchronized boolean isComplete() {
        return complete;
    }


    public int getPosition() {
        int position = lastPosition;

        AudioDevice out = audio;
        if (out != null) {
            position = out.getPosition();
        }
        return position;
    }


  /*  protected void decodeFrame() throws JavaLayerException {

        SampleBuffer output = null;
        Header h = null;
        try {
            AudioDevice out = audio;
            while (true && !closed) {
                if (inputStreams.size() > ZERO && !closed) {
                    if (inputStreams.get(ZERO) != null && !closed) {
                        Streams i = getStreams();
                        //while ((i.available()!=ZERO || inputStreams.get(0).buf.length>=242688 || !i.flag.get()) && !closed) {
                        if(flagShare.get()==false) {
                            while (true && !closed) {
                                if (flagShare.get() == false) {
                                    if ((((inputStreams.get(ZERO).len.get() / 1024) > 200) && i.flag.get() && i.available() == 0 && inputStreams.size() > 1)) {
                                        extracted(i);
                                        break;
                                    } else {
                                        h = getHeader(h);
                                        checkedAndPlay(h, i);
                                    }
                                } else break;

                            }
                        }else {
                            while (true && !closed) {
                                if (i.flag.get()) {
                                    Player cPlayer = new Player(inputStreams.get(ZERO));
                                    cPlayer.play();
                                    extracted(i);
                                    break;
                                }
                            }



                        }


                        optimizeOnlineStream();
                    }
                } else {
                    Sleep(ONE);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }*/

    protected void decodeFrame() throws JavaLayerException {

        //SampleBuffer output = null;
        Header h = null;
        try {
            AudioDevice out = audio;
            while (!closed) {
                if (inputStreams.size() > ZERO) {
                    if (inputStreams.get(ZERO) != null) {
                        Streams i = getStreams();
                        while (true && !closed)
                            if ((((inputStreams.get(ZERO).len.get() / MAX_BUFFER_LEN) > TWO_HUNDRED) && i.flag.get() && i.available() == ZERO && inputStreams.size() > ONE)) {
                                break;
                            } else {
                                h = getHeader(h);
                                checkedAndPlay(h, i);

                            }

                        optimizeOnlineStream();
                    }
                } else {
                    Sleep(ONE);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

   /* private void extracted(Streams i) {
        System.out.println("avalible=" + i.available());
        System.out.println("count=" + i.count);
        System.out.println("pos=" + i.pos);
        System.out.println("inputStreams=" + inputStreams.size());
        System.out.println("buf_size=" + inputStreams.get(0).buf.length);
        System.out.println("buf_len=" + inputStreams.get(0).len);
        System.out.println("buf_flag=" + inputStreams.get(0).flag);
    }*/

    /*protected void decodeFrame() throws JavaLayerException {

        SampleBuffer output = null;
        Header h = null;
        try {
            AudioDevice out = audio;
            while (true && !closed) {
                if (inputStreams.size() > ZERO && !closed) {
                    if (inputStreams.get(ZERO) != null && !closed) {
                        Streams i = getStreams();
                        while ((i.available() != ZERO || i.flag.get()) && !closed) {
                            h = getHeader(h);
                            checkedAndPlay(h, i);

                        }
                        optimizeOnlineStream();
                    }
                } else {
                    Sleep(ONE);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }*/


    private Streams getStreams() throws JavaLayerException {
        Sleep(TWO_HUNDRED_EIGHTY);
        Streams i = inputStreams.get(ZERO);
        Bitstream streams = new Bitstream(i);
        fillStream(streams, null);
        return i;
    }

    private void optimizeOnlineStream() {
        inputStreams.remove(ZERO);
        System.gc();
    }

    private void Sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkedAndPlay(Header h, Streams i) {
        SampleBuffer output;
        AudioDevice out;
        if (h != null) {
            try {
                output = (SampleBuffer) decoder.decodeFrame(h, bitstream);
                if (output != null) {

                    synchronized (this) {
                        out = audio;

                        if (out != null && !closed) {
                            out.write(output.getBuffer(), ZERO, output.getBufferLength());
                        }
                    }
                    bitstream.closeFrame();
                } else {
                    Bitstream s = new Bitstream(i);
                    fillStream(s, null);
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }

        }
    }

    private Header getHeader(Header h) {
        try {
            h = bitstream.readFrame();
            if (h == null) {
                Thread.sleep(1024);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return h;
    }


}