/** 
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
**/
package org.bigbluebutton.deskshare.client;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.bigbluebutton.deskshare.client.encoder.FlvEncodeException;
import org.bigbluebutton.deskshare.client.encoder.ScreenVideoFlvEncoder;

public class FlvFileRecorder {
	private BlockingQueue<ByteArrayOutputStream> screenQueue = new LinkedBlockingQueue<ByteArrayOutputStream>();
	private final Executor exec = Executors.newSingleThreadExecutor();
	private Runnable capturedScreenSender;
	private volatile boolean sendCapturedScreen = false;
	private FileOutputStream fo;
	private ScreenVideoFlvEncoder svf ;

	public FlvFileRecorder(){
		svf = new ScreenVideoFlvEncoder();
	}
	
	public void init() {
    	try {
			//fo = new FileOutputStream(new File ("C://blueRascal/"+"ScreenVideo.flv"));
    		fo = new FileOutputStream("D://temp/" + "ScreenVideo2.flv");
			fo.write(svf.encodeHeader());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sendCapturedScreen = true;

		capturedScreenSender = new Runnable() {
			public void run() {
				while (sendCapturedScreen) {
					try {
						ByteArrayOutputStream frame = screenQueue.take();
						saveToFile(frame);
					} catch (InterruptedException e) {
						System.out.println("InterruptedExeption while taking event.");
					}
				}
			}
		};
		exec.execute(capturedScreenSender);
	}

	public void disconnect()  {
    	try {
    		System.out.println("Closing stream");
			fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void record(ByteArrayOutputStream frame) {
		try {
			screenQueue.put(frame);
		} catch (InterruptedException e) {
			System.out.println("InterruptedException while putting event into queue.");
		}
	}
	
	private void saveToFile(ByteArrayOutputStream videoData) {
		try {
			fo.write(svf.encodeFlvData(videoData));
			fo.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FlvEncodeException e) {
			e.printStackTrace();
		}
	}
}
