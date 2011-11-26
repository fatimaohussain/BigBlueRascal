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
//import org.bigbluebutton.deskshare.common.ScreenVideoEncoder;
import org.bigbluebutton.deskshare.common.ScreenVideoEncoder;
import org.bigbluebutton.deskshare.common.Dimension;



public class PBlockManager extends PBlockFactory
{

    private ConcurrentHashMap<Integer,PBlock> blocksMap;

    private int numberOfRows;
    private int numberOfColumns;
    //private String room;
    private Dimension screenDim;
    private Dimension blockDim;
    private long lastFrameTime = 0L;
    private long lastKeyFrameTime = 0L;
    private long KEYFRAME_INTERVAL = 20000;


            public PBlockManager ( Dimension screenDim, Dimension blockDim){

                blocksMap = new ConcurrentHashMap<Integer, PBlock>();

                //this.room = room;
                this.screenDim = screenDim;
                this.blockDim = blockDim;

                numberOfRows = getNumberOfRows(screenDim, blockDim);
                numberOfColumns = getNumberOfColumns(screenDim, blockDim);


            }






            public void initialize() {
                System.out.println("Initialize Processor BlockManager");
                int numberOfBlocks = numberOfRows * numberOfColumns;

                //for (int position: Int <- 1 to numberOfBlocks) {
                for (int position = 1; position <= numberOfBlocks; position++) {
                    PBlock block = createBlock(screenDim, blockDim, position);
                    Dimension dim = block.getDimension();
                    int [] blankPixels = new int[dim.getWidth() * dim.getHeight()];
                         for (int i = 0; i <  blankPixels.length; i++) {
                            blankPixels[i] = 0xFFFF;
                         }
                byte[] encodedPixels = ScreenVideoEncoder.encodePixels(blankPixels, dim.getWidth(), dim.getHeight());

                block.update(encodedPixels, true, 0);

                blocksMap.put(position, block);
            }
        }

        public void updateBlock(int position, byte [] videoData,boolean keyFrame, int seqNum)  {
            PBlock block = blocksMap.get(position);
            block.update(videoData, keyFrame, seqNum);
        }

        byte [] generateFrame(boolean genKeyFrame) {
            ByteArrayOutputStream screenVideoFrame = new ByteArrayOutputStream();

            byte [] encodedDim = ScreenVideoEncoder.encodeBlockAndScreenDimensions(blockDim.getWidth(), screenDim.getWidth(), blockDim.getHeight(), screenDim.getHeight());

            int numberOfBlocks = numberOfRows * numberOfColumns;
            byte videoDataHeader = ScreenVideoEncoder.encodeFlvVideoDataHeader(genKeyFrame);

            screenVideoFrame.write(videoDataHeader);
            try {
                screenVideoFrame.write(encodedDim);
            } catch (Exception e) {
                System.out.println("An IO exception occured");
            }
            for (int position = 1; position < numberOfBlocks; position++) {
                PBlock block = blocksMap.get(position);
                byte [] encodedBlock = ScreenVideoEncoder.encodeBlockUnchanged();
                if (block.hasChanged || genKeyFrame) {
                    encodedBlock = block.getEncodedBlock();
                }
                screenVideoFrame.write(encodedBlock, 0, encodedBlock.length);
            }

            return screenVideoFrame.toByteArray();
        }
    }














