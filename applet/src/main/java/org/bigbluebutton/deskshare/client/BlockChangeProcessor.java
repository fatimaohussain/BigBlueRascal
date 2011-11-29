/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bigbluebutton.deskshare.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
//import net.jcip.annotations.ThreadSafe;

import org.bigbluebutton.deskshare.client.ExitCode;
import org.bigbluebutton.deskshare.client.blocks.BlockManager;
import org.bigbluebutton.deskshare.common.Dimension;
import org.bigbluebutton.deskshare.client.net.Message;
import org.bigbluebutton.deskshare.client.net.SequenceNumberGenerator;
import org.bigbluebutton.deskshare.client.net.EncodedBlockData;
/**
 *
 * @author fatima hussain
 */
public class BlockChangeProcessor {

    private final BlockingQueue<Message> blockDataQ = new LinkedBlockingQueue<Message>();
    private Dimension screenDim;
	private Dimension blockDim;
	private BlockManager blockManager;
	//private NetworkConnectionListener listener;
	private final SequenceNumberGenerator seqNumGenerator = new SequenceNumberGenerator();


        public BlockChangeProcessor(BlockManager blockManager, Dimension screenDim, Dimension blockDim) {
		this.blockManager = blockManager;
		//this.host = host;
		//this.port = port;
		//this.room = room;
		this.screenDim = screenDim;
		this.blockDim = blockDim;
		//this.httpTunnel = httpTunnel;

	}

        public void send(Message message) {
		blockDataQ.offer(message);
	}

        public void blockSent(int position) {
		//blockManager.blockSent(position);
	}

//	public EncodedBlockData getBlockToSend(int position) {
//		return blockManager.getBlock(position).encode();
//	}

	public Message getNextMessageToSend() throws InterruptedException {
		try {
			return (Message) blockDataQ.take();
		} catch (InterruptedException e) {
			//e.printStackTrace();
			throw e;
		}
	}


}


