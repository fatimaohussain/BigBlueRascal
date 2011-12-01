/** 
* ===License Header===
*
* BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
*
* Copyright (c) 2010 BigBlueButton Inc. and by respective authors (see below).
*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License as published by the Free Software
* Foundation; either version 2.1 of the License, or (at your option) any later
* version.
*
* BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License along
* with BigBlueButton; if not, see <http://www.gnu.org/licenses/>.
* 
* ===License Header===
*/
package org.bigbluebutton.deskshare.client.blocks;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.bigbluebutton.deskshare.client.FlvFileRecorder;
import org.bigbluebutton.deskshare.common.Dimension;
import java.io.ByteArrayOutputStream;
import org.bigbluebutton.deskshare.common.ScreenVideoEncoder;


public class BlockManager {
	private final Executor exec = Executors.newSingleThreadExecutor();
	private Runnable capturedScreenSender;
	private volatile boolean sendCapturedScreen = false;
	
    private final Map<Integer, Block> blocksMap;
    private int numColumns;
    private int numRows;
    
    private BlockFactory factory;
    private Dimension screenDim, blockDim;
    private int frameCount = 0;
    
    private FlvFileRecorder fileCapture;
    
    public BlockManager() {
    	blocksMap = new HashMap<Integer, Block>();
        fileCapture = new FlvFileRecorder();
    }
    
    public void initialize(Dimension screen, Dimension tile) {
    	screenDim = screen;
    	blockDim = tile;

    	/** Initialize the recorder **/
    	try {
            fileCapture.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("FatiledToOpenFile: " + e.getMessage());
        }
        
        /** Initialize the blocks for this screen capture **/
    	factory = new BlockFactory(screen, tile);
        numColumns = factory.getColumnCount();
        numRows = factory.getRowCount();
        int numberOfBlocks = numColumns * numRows;
        
        for (int position = 1; position <= numberOfBlocks; position++) {
        	Block block = factory.createBlock(position);
        	blocksMap.put(new Integer(position), block);
        }
        
        /** Start the recorder */
        sendCapturedScreen = true;
		capturedScreenSender = new Runnable() {
			public void run() {
				while (sendCapturedScreen) {
					saveFrameToFile();
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		exec.execute(capturedScreenSender);
    }
    
    int count = 0;
    
    public void processCapturedScreen(BufferedImage capturedScreen) {    	
    	long start = System.currentTimeMillis();	
		int numberOfBlocks = numColumns * numRows;
		for (int position = 1; position <= numberOfBlocks; position++) {
			Block block = blocksMap.get(new Integer(position));
			block.processBlock(capturedScreen);
		}
    }


    private void saveFrameToFile() {
    	/** Generate a key frame every 15th frame **/
    	boolean keyFrame = false;
    	if (frameCount % 15 == 0) {
    		keyFrame = true;
    		frameCount = 0;
    	}
    	frameCount ++;

        fileCapture.record(generateFrame(keyFrame));

    }

    private ByteArrayOutputStream generateFrame(boolean genKeyFrame){
    	ByteArrayOutputStream screenVideoFrame = new ByteArrayOutputStream();
    	byte[] encodedDim = ScreenVideoEncoder.encodeBlockAndScreenDimensions(blockDim.getWidth(), screenDim.getWidth(), blockDim.getHeight(), screenDim.getHeight());
    	byte videoDataHeader = ScreenVideoEncoder.encodeFlvVideoDataHeader(genKeyFrame);
    	screenVideoFrame.write(videoDataHeader);
    	screenVideoFrame.write(encodedDim, 0, encodedDim.length);
       
    	int numberOfBlocks = numRows * numColumns;
    	for (int pos = 1; pos <= numberOfBlocks; pos++) {
    		Block block = blocksMap.get(new Integer(pos));
    		byte[] encodedBlock = block.encode(genKeyFrame);
    		screenVideoFrame.write(encodedBlock, 0, encodedBlock.length);
    	}

    	return screenVideoFrame;
	}

   	
	public Block getBlock(int position) {
		return (Block) blocksMap.get(new Integer(position));
	}
	
    public int getRowCount() {
        return numRows;
    }
    
    public int getColumnCount() {
        return numColumns;
    }
}
