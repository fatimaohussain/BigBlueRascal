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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bigbluebutton.deskshare.common.PixelExtractException;
import org.bigbluebutton.deskshare.common.ScreenVideoEncoder;
import org.bigbluebutton.deskshare.common.Dimension;



public final class Block {   
	Random random = new Random();
    private final BlockChecksum checksum;
    private final Dimension dim;
    private final int position;
    private final Point location;    
    private int[] capturedPixels;
    private final Object pixelsLock = new Object();
    private AtomicBoolean dirtyBlock = new AtomicBoolean(false);
    private long lastSent = System.currentTimeMillis();
    
    Block(Dimension dim, int position, Point location) {
        checksum = new BlockChecksum();
        this.dim = dim;
        this.position = position;
        this.location = location;
        
        int length = dim.getWidth() * dim.getHeight();
		capturedPixels = new int[length];
		for (int i = 0; i < length; i++) {
			capturedPixels[i] = 0xFF00;
		}
    }
    
    public void processBlock(BufferedImage capturedScreen) {	     	
    	synchronized(pixelsLock) {
            try {
            	capturedPixels = ScreenVideoEncoder.getPixels(capturedScreen, getX(), getY(), getWidth(), getHeight()); 	
            } catch (PixelExtractException e) {
            	System.out.println(e.toString());
        	}  
    	}
    }
       
    public byte[] encode(boolean keyFrame) {   
    	int[] pixelsCopy = new int[capturedPixels.length];
    	byte[] encodedBlock;
    	synchronized (pixelsLock) {     		
            System.arraycopy(capturedPixels, 0, pixelsCopy, 0, capturedPixels.length);
	    	
	    	if (! checksumSame(capturedPixels) || keyFrame) {
//	    		System.out.println("Pixels changed ." + position + " keyframe " + keyFrame);
	    		encodedBlock = ScreenVideoEncoder.encodePixels(pixelsCopy, getWidth(), getHeight()); 
	    	} else {
//	    		System.out.println("Pixels unchanged ." + position + " keyframe " + keyFrame);
	    		encodedBlock = ScreenVideoEncoder.encodeBlockUnchanged();
	    	}
		}
        	     	
        return encodedBlock;
    }
    
    private boolean checksumSame(int[] pixels) {
    	return checksum.isChecksumSame(convertIntPixelsToBytePixels(pixels)); 
    }
          
    private byte[] convertIntPixelsToBytePixels(int[] pixels) {
    	byte[] p = new byte[pixels.length * 3];
    	int position = 0;
		
		for (int i = 0; i < pixels.length; i++) {
			byte red = (byte) ((pixels[i] >> 16) & 0xff);
			byte green = (byte) ((pixels[i] >> 8) & 0xff);
			byte blue = (byte) (pixels[i] & 0xff);

			// Sequence should be BGR
			p[position++] = blue;
			p[position++] = green;
			p[position++] = red;
		}
		
		return p;
    }
    
    public int getWidth() {
        return new Integer(dim.getWidth()).intValue();
    }
    
    public int getHeight() {
        return new Integer(dim.getHeight()).intValue();
    }
    
    public int getPosition() {
		return new Integer(position).intValue();
	}
    
    public int getX() {
		return new Integer(location.x).intValue();
	}

    public int getY() {
		return new Integer(location.y).intValue();
	}
	
    Dimension getDimension() {
		return new Dimension(dim.getWidth(), dim.getHeight());
	}
	
    Point getLocation() {
		return new Point(location.x, location.y);
	}
}
