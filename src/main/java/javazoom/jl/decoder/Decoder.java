package javazoom.jl.decoder;

/**
 * Created By: Ali Mohammadi
 * Date: 28 Dec, 2021
 */
public class Decoder implements DecoderErrors {
    static private final Params DEFAULT_PARAMS = new Params();

    /**
     * The Bistream from which the MPEG audio frames are read.
     */
    //private Bitstream				stream;

    /**
     * The Obuffer instance that will receive the decoded
     * PCM samples.
     */
    private Obuffer output;

    /**
     * Synthesis filter for the left channel.
     */
    private SynthesisFilter filter1;

    /**
     * Sythesis filter for the right channel.
     */
    private SynthesisFilter filter2;

    /**
     * The decoder used to decode layer III frames.
     */
    private LayerIIIDecoder l3decoder;
    private LayerIIDecoder l2decoder;
    private LayerIDecoder l1decoder;

    private int outputFrequency;
    private int outputChannels;

    private Equalizer equalizer = new Equalizer();

    private Params params;

    private boolean initialized;


    /**
     * Creates a new <code>Decoder</code> instance with default
     * parameters.
     */

    public Decoder() {
        this(null);
    }


    public Decoder(Params params0) {
        if (params0 == null)
            params0 = DEFAULT_PARAMS;

        params = params0;

        Equalizer eq = params.getInitialEqualizerSettings();
        if (eq != null) {
            equalizer.setFrom(eq);
        }
    }

    static public Params getDefaultParams() {
        return (Params) DEFAULT_PARAMS.clone();
    }

    public void setEqualizer(Equalizer eq) {
        if (eq == null)
            eq = Equalizer.PASS_THRU_EQ;

        equalizer.setFrom(eq);

        float[] factors = equalizer.getBandFactors();

        if (filter1 != null)
            filter1.setEQ(factors);

        if (filter2 != null)
            filter2.setEQ(factors);
    }


    public Obuffer decodeFrame(Header header, Bitstream stream)
            throws DecoderException {
        try {
            if (!initialized) {
                initialize(header);
            }

            int layer = header.layer();

            output.clear_buffer();

            FrameDecoder decoder = retrieveDecoder(header, stream, layer);

            decoder.decodeFrame();

            output.write_buffer(1);

            return output;

        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Changes the output buffer. This will take effect the next time
     * decodeFrame() is called.
     */
    public void setOutputBuffer(Obuffer out) {
        output = out;
    }


    public int getOutputFrequency() {
        return outputFrequency;
    }


    public int getOutputChannels() {
        return outputChannels;
    }

    public int getOutputBlockSize() {
        return Obuffer.OBUFFERSIZE;
    }


    protected DecoderException newDecoderException(int errorcode) {
        return new DecoderException(errorcode, null);
    }

    protected DecoderException newDecoderException(int errorcode, Throwable throwable) {
        return new DecoderException(errorcode, throwable);
    }

    protected FrameDecoder retrieveDecoder(Header header, Bitstream stream, int layer)
            throws DecoderException {
        try {
            FrameDecoder decoder = null;

            // REVIEW: allow channel output selection type
            // (LEFT, RIGHT, BOTH, DOWNMIX)
            switch (layer) {
                case 3:
                    if (l3decoder == null) {
                        l3decoder = new LayerIIIDecoder(stream,
                                header, filter1, filter2,
                                output, OutputChannels.BOTH_CHANNELS);
                    }

                    decoder = l3decoder;
                    break;
                case 2:
                    if (l2decoder == null) {
                        l2decoder = new LayerIIDecoder();
                        l2decoder.create(stream,
                                header, filter1, filter2,
                                output, OutputChannels.BOTH_CHANNELS);
                    }
                    decoder = l2decoder;
                    break;
                case 1:
                    if (l1decoder == null) {
                        l1decoder = new LayerIDecoder();
                        l1decoder.create(stream,
                                header, filter1, filter2,
                                output, OutputChannels.BOTH_CHANNELS);
                    }
                    decoder = l1decoder;
                    break;
            }

            if (decoder == null) {
                throw newDecoderException(UNSUPPORTED_LAYER, null);
            }

            return decoder;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private void initialize(Header header)
            throws DecoderException {

        // REVIEW: allow customizable scale factor
        float scalefactor = 32700.0f;

        int mode = header.mode();
        int layer = header.layer();
        int channels = mode == Header.SINGLE_CHANNEL ? 1 : 2;


        // set up output buffer if not set up by client.
        if (output == null)
            output = new SampleBuffer(header.frequency(), channels);

        float[] factors = equalizer.getBandFactors();
        filter1 = new SynthesisFilter(0, scalefactor, factors);

        // REVIEW: allow mono output for stereo
        if (channels == 2)
            filter2 = new SynthesisFilter(1, scalefactor, factors);

        outputChannels = channels;
        outputFrequency = header.frequency();

        initialized = true;
    }

    /**
     * The <code>Params</code> class presents the customizable
     * aspects of the decoder.
     * <p>
     * Instances of this class are not thread safe.
     */
    public static class Params implements Cloneable {
        private OutputChannels outputChannels = OutputChannels.BOTH;

        private Equalizer equalizer = new Equalizer();

        public Params() {
        }

        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException ex) {
                throw new InternalError(this + ": " + ex);
            }
        }

        public OutputChannels getOutputChannels() {
            return outputChannels;
        }

        public void setOutputChannels(OutputChannels out) {
            if (out == null)
                throw new NullPointerException("out");

            outputChannels = out;
        }

        /**
         * Retrieves the equalizer settings that the decoder's equalizer
         * will be initialized from.
         * <p>
         * The <code>Equalizer</code> instance returned
         * cannot be changed in real time to affect the
         * decoder output as it is used only to initialize the decoders
         * EQ settings. To affect the decoder's output in realtime,
         * use the Equalizer returned from the getEqualizer() method on
         * the decoder.
         *
         * @return The <code>Equalizer</code> used to initialize the
         * EQ settings of the decoder.
         */
        public Equalizer getInitialEqualizerSettings() {
            return equalizer;
        }

    }
}
