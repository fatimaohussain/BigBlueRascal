/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bigbluebutton.deskshare.processor;
//import java.awt.Dimension;
import org.bigbluebutton.deskshare.common.Dimension;

/**
 *
 * @author fatima
 */
public class PBlockFactory {


    public int getNumberOfColumns(Dimension screenDim, Dimension blockDim ){
        int columns = screenDim.getWidth() / blockDim.getWidth();
        if (hasPartialColumnBlock(screenDim, blockDim)) {
            columns += 1;
        }
     
        return columns;
    }
    
    public boolean hasPartialColumnBlock(Dimension screenDim , Dimension blockDim) {
        return (screenDim.getWidth() % blockDim.getWidth()) != 0;
    }
    
    public int getNumberOfRows(Dimension screenDim, Dimension blockDim){
        int  rows = screenDim.getHeight() / blockDim.getHeight();
        if (hasPartialRowBlock(screenDim, blockDim)) {
            rows += 1;
        }
        return rows;
    }
    
    public boolean hasPartialRowBlock(Dimension screenDim, Dimension blockDim) {
        return (screenDim.getHeight() % blockDim.getHeight()) != 0;
    }
  
    public PBlock createBlock(Dimension screenDim , Dimension blockDim, int position) {
        int numRows = getNumberOfRows(screenDim, blockDim);
        int numColumns = getNumberOfColumns(screenDim, blockDim);
    
        int col = computeColumn(position, numColumns);
        int row = computeRow(position, numRows, numColumns);
        int w = computeTileWidth(col, screenDim, blockDim);
        int h = computeTileHeight(row, screenDim, blockDim);

        return new PBlock(new Dimension(w, h), position);
    }
       
    private int computeRow(int position, int numRows, int numColumns) 
    {
        return -(position - (numRows * numColumns)) / numColumns;
    }
    
    private int computeColumn(int position, int numColumns){
        return (position - 1) % numColumns;
    }
    
    private int computeTileWidth(int col, Dimension screenDim, Dimension blockDim) {
        int numColumns = getNumberOfColumns(screenDim, blockDim);
        if (isLastColumnTile(col, numColumns)) {
        if (hasPartialColumnBlock(screenDim, blockDim)) {
            return partialTileWidth(screenDim, blockDim);
        }
     }
        return blockDim.getWidth();
    }
    
    private int partialTileWidth(Dimension screenDim, Dimension blockDim) {
        return screenDim.getWidth() % blockDim.getWidth();
    }
    
    private int computeTileHeight(int row, Dimension screenDim, Dimension blockDim)
    {
        if (isTopRowTile(row)) {
                if (hasPartialRowBlock(screenDim, blockDim)) {
                return partialTileHeight(screenDim, blockDim);
            }
        }
        return blockDim.getWidth();
    }
    
    private int partialTileHeight(Dimension screenDim, Dimension blockDim)
    {
        return screenDim.getHeight() % blockDim.getHeight();
    }

    private boolean isLastColumnTile(int col, int numColumns){
     return ((col+1) % numColumns) == 0;
    }
    
    private boolean isTopRowTile(int row){
        return (row == 0);
    }
    
}


  

    
    
    
    
    
    
