/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bigbluebutton.deskshare.client.net;
import java.io.ByteArrayInputStream;

/**
 *
 * @author fatima
 */
public class CaptureEventInfo {

       //start stepping through the array from the beginning

        private String seqNum;
        private String screenInfo;
        private String blockInfo;
        private String event;

        private String position;
        private String keyframe;
        private String mousex;
        private String mousey;

        ByteArrayInputStream block;

	public CaptureEventInfo (String seq, String screen, String block, String event){

            seqNum = seq;
            event = this.event;
            screenInfo = screen;
            blockInfo = block;
        }

        public CaptureEventInfo (String seq,  String x, String y){

            seqNum = seq;

            mousex = x;
            mousey = y;
        }



        public CaptureEventInfo (String seq, String event){

            seqNum = seq;
            this.event = event;

        }




        public ByteArrayInputStream getBlockData(){
            return block;
        }

        public void setBlockData(BlockVideoData blockData){
            block = new ByteArrayInputStream(blockData.getVideoData());
        }

        public String getPosition(){
            return position;
        }

        public String getKeyFrame(){
            return keyframe;
        }
        public String getMouseY(){
            return mousey;
        }
        public String getMousex(){
            return mousex;
        }


         public void setMouseY(String y){
            mousey = y;
        }
        public void setMousex(String x){
            mousex = x;
        }


        public void setPosition(String p){
            position = p;
        }

        public void setKeyFrame(String frame){
            keyframe = frame;
        }
       



        public String getSeqNum() {
            return seqNum;
        }

        public String getScreenInfo() {
            return screenInfo;
        }
        public String getBlockInfo() {
            return blockInfo;
        }
        public String getEvent() {
            return event;
        }

        public void setSeqNum(String seq) {
            seqNum = seq;
        }

        public void setScreenInfo(String screen) {
            screenInfo = screen;
        }
        public void setBlockInfo(String block) {
            blockInfo = block;
        }
        public void setEvent(String e) {
            event = e;
        }

    }
