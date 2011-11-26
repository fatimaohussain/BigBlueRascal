/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bigbluebutton.deskshare.processor;

/**
 *
 * @author fatima
 */

import java.io.ByteArrayOutputStream;
//import java.awt.Dimension;

import java.util.concurrent.ConcurrentHashMap;
import org.bigbluebutton.deskshare.common.ScreenVideoEncoder;
import org.bigbluebutton.deskshare.common.ScreenVideoEncoder;
import org.bigbluebutton.deskshare.client.blocks.Block;
import org.bigbluebutton.deskshare.common.Dimension;
//import org.bigbluebutton.deskshare.server.session.ScreenVideoFrame;


import java.util.Random;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;

public class PBlock  {
private Logger log;

    private Dimension dim;
    private  int position;
    public final int nextForceUpdate = 10000;
    public final int MIN_DURATION = 5000;
    public final int MAX_DURATION = 10000;

    boolean isKeyFrame = false;
    boolean hasChanged = false;
    long lastChanged = 0L;
    byte [] encodedBlock;
    //ArrayList <Byte>encodedBlock;

    Random random;
    private int sequenceNumber = 0;

    public PBlock(Dimension dim, int position){

        encodedBlock = new byte[10];
        this.dim = dim;
        this.position = position;
        random = new Random();
    }

    public void update(byte [] videoData, boolean isKeyFrame, int seqNum) {
        if (seqNum >= sequenceNumber) {
            sequenceNumber = seqNum;
            this.isKeyFrame = isKeyFrame;


             encodedBlock = videoData;
             hasChanged = true;
        } else {
            //log.warning("Block[" + position + "[: Delayed sequence number [%s < %s]", seqNum, sequenceNumber);
        }
   }

    public byte[] getEncodedBlock() {
        hasChanged = false;
        return encodedBlock;
    }

    public Dimension getDimension(){
        return dim;
    }

    public int getPosition(){
        return position;
    }


    public boolean hasBlockChanged() {
        return (hasChanged || forceUpdate());
    }

    private boolean forceUpdate(){
        Long now = System.currentTimeMillis();
        boolean update = ((now - lastChanged) > nextForceUpdate);
        if (update) {
            nextUpdate();
            lastChanged = now;
        }
        return update;
    }

    private void nextUpdate(){
        //get the range, casting to long to avoid overflow problems
// val range: Long = MAX_DURATION - (MIN_DURATION + 1)
        // compute a fraction of the range, 0 <= frac < range
// val fraction: Long = (range * random.nextDouble())
// nextForceUpdate = (int)(fraction + 5000);
 // nextForceUpdate = 5000
    }
}

