/*
 * Copyright (C) 2014 Andrew Comminos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.uniguard.humla.audio.javacpp;

import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Platform;

import java.nio.ByteBuffer;

import com.uniguard.humla.audio.IDecoder;
import com.uniguard.humla.exception.NativeAudioException;
import com.uniguard.humla.protocol.AudioHandler;

/**
 * Created by andrew on 20/10/13.
 */
@Platform(library="jnicelt7", cinclude={"<celt.h>","<celt_types.h>"})
public class CELT7 {
    public static final int CELT_GET_BITSTREAM_VERSION = 2000;
    public static final int CELT_SET_VBR_RATE_REQUEST = 6;
    public static final int CELT_SET_PREDICTION_REQUEST = 4;

    static {
        Loader.load();
    }

    public static native Pointer celt_mode_create(int sampleRate, int frameSize, IntPointer error);
    public static native int celt_mode_info(@Cast("const CELTMode*") Pointer mode, int request, IntPointer value);
    public static native void celt_mode_destroy(@Cast("CELTMode*") Pointer mode);

    public static native Pointer celt_decoder_create(@Cast("CELTMode*") Pointer mode, int channels, IntPointer error);
    public static native int celt_decode(@Cast("CELTDecoder*") Pointer st, @Cast("const unsigned char*") ByteBuffer data, int len, short[] pcm);
    public static native int celt_decode_float(@Cast("CELTDecoder*") Pointer st, @Cast("const unsigned char*") ByteBuffer data, int len, float[] pcm);
    public static native int celt_decoder_ctl(@Cast("CELTDecoder*") Pointer st, int request, Pointer val);
    public static native void celt_decoder_destroy(@Cast("CELTDecoder*") Pointer st);

    public static native Pointer celt_encoder_create(@Cast("const CELTMode *") Pointer mode, int channels, IntPointer error);
    public static native int celt_encoder_ctl(@Cast("CELTEncoder*")Pointer state, int request, Pointer val);
    public static native int celt_encoder_ctl(@Cast("CELTEncoder*")Pointer state, int request, int val);
    public static native int celt_encode(@Cast("CELTEncoder *") Pointer state, @Cast("const short *") short[] pcm, @Cast("short *") short[] optionalSynthesis, @Cast("unsigned char *") byte[] compressed, int nbCompressedBytes);
    public static native void celt_encoder_destroy(@Cast("CELTEncoder *") Pointer state);

    /**
     * @return an integer describing the CELT bitstream version.
     */
    public static int getBitstreamVersion() {
        IntPointer versionPtr = new IntPointer();
        Pointer modePtr = celt_mode_create(AudioHandler.SAMPLE_RATE, AudioHandler.FRAME_SIZE, null);
        celt_mode_info(modePtr, CELT_GET_BITSTREAM_VERSION, versionPtr);
        celt_mode_destroy(modePtr);
        return versionPtr.get();
    }

    public static class CELT7Decoder implements IDecoder {

        private Pointer mMode;
        private Pointer mState;

        public CELT7Decoder(int sampleRate, int frameSize, int channels) throws NativeAudioException {
            IntPointer error = new IntPointer(1);
            error.put(0);
            mMode = celt_mode_create(sampleRate, frameSize, error);
            if(error.get() < 0) throw new NativeAudioException("CELT 0.7.0 decoder initialization failed with error: "+error.get());
            mState = celt_decoder_create(mMode, channels, error);
            if(error.get() < 0) throw new NativeAudioException("CELT 0.7.0 decoder initialization failed with error: "+error.get());
        }

        @Override
        public int decodeFloat(ByteBuffer input, int inputSize, float[] output, int frameSize) throws NativeAudioException {
            int result = celt_decode_float(mState, input, inputSize, output);
            if(result < 0) throw new NativeAudioException("CELT 0.7.0 decoding failed with error: "+result);
            return frameSize;
        }

        @Override
        public int decodeShort(ByteBuffer input, int inputSize, short[] output, int frameSize) throws NativeAudioException {
            int result = celt_decode(mState, input, inputSize, output);
            if(result < 0) throw new NativeAudioException("CELT 0.7.0 decoding failed with error: "+result);
            return frameSize;
        }

        @Override
        public void destroy() {
            celt_decoder_destroy(mState);
            celt_mode_destroy(mMode);
        }
    }
}
