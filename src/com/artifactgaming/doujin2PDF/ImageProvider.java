package com.artifactgaming.doujin2PDF;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageProvider {
	/*********************************************************************
	 * This will select the next chapter from the image source. Note that
	 * this must be called to select the first chapter.
	 * @return the name of the chapter we have just selected or null if there
	 *         are no more chapters.
	 * @throws IOException 
	 *********************************************************************/
	String nextChapter() throws IOException;
	
	/*********************************************************************
	 * This will get the next image in the chapter, or will return null
	 * if there are no more images left in the chapter.
	 * @return the image for the next page or null if there are no more pages.
	 *********************************************************************/
	BufferedImage nextPage() throws IOException;
	
	/*********************************************************************
	 * This will indicate the total number of pages from the source. It's
	 * intended to be used to provide an accurate progress bar.
	 * @return The number of images available from the source.
	 *********************************************************************/
	int totalPages() throws IOException;
	
	/*********************************************************************
	 * This will close any file streams that may need to be closed.
	 * @throws IOException 
	 *********************************************************************/	
	void close() throws IOException;

	/*********************************************************************
	 * If the PDFConverter is to make all images to have the same width
	 * or height, it needs to look through all images and determine what
	 * the max width or height is. It will then need to reset the image
	 * provider so it can iterate through all the images again.
	 * @throws IOException 
	 *********************************************************************/	
	void reset() throws IOException;
}
