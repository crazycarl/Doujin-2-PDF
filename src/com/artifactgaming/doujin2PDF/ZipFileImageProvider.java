package com.artifactgaming.doujin2PDF;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

public class ZipFileImageProvider implements ImageProvider {
	
	private int totalPages;
	
	private ZipFile zipFile;
	
	private HashMap<String, ArrayList<String>> chapters = new HashMap<String, ArrayList<String>>();

	private int chapterIndex = 0;
	private int pageIndex = 0;
	private ArrayList<String> chapterNames;
	private ArrayList<String> currentChapter = null;
	
	public ZipFileImageProvider(File zipFile) throws IOException {
		this.zipFile = new ZipFile(zipFile);
		
		Enumeration<? extends ZipEntry> entries = this.zipFile.entries();
		
		
        while(entries.hasMoreElements()) {
        	ZipEntry entry = entries.nextElement();
        	
        	String name = entry.getName();
        	int indexOfExtension = name.lastIndexOf('.');
			
	        //Make sure there was an extension.
        	if (indexOfExtension <= 0 || indexOfExtension >= name.length() - 1) {
				continue;//If no extension, skip this.
        	}
        	
        	//If it's not a compatable file (like thumbs.db) we don't include it.
        	if (!PDFConverter.isCompatableFormat(name.substring(indexOfExtension + 1).toLowerCase())) {
        		continue;
        	}
        	
        	int indexOfChapter= name.lastIndexOf('/');
        	
        	String nameOfChapter = null;
        	if (indexOfChapter <= 0 || indexOfChapter >= name.length() - 1) {
        		nameOfChapter = "root";
        	} else {
        		//Replace '/' with '_' to make sure we don't have filesystem compatibility issues.
        		nameOfChapter = name.substring(0, indexOfChapter).replace('/', '_');
        	}
        	
        	ArrayList<String> chapter = chapters.get(nameOfChapter);
        	
        	//If this chapter hasen't been seen yet, add it to the list.
        	if (chapter == null) {
        		chapter = new ArrayList<String>();
        		chapters.put(nameOfChapter, chapter);
        	}
        	
        	chapter.add(name);
        	totalPages++;
        }
        
        //Sort the chapters into an order that makes sense.
        chapterNames = new ArrayList<String>(chapters.keySet());
        
        Collections.sort(chapterNames, new Comparator<String>() {
            public int compare(String name1, String name2) {
            	//These two if statements insure that root goes to the top if present.
            	if (name1.equals("root")) return -1;
            	if (name2.equals("root")) return 1;
            	
            	//This will just alphabetize as always.
                return name1.compareTo(name2);
            }
        });
	}

	@Override
	public String nextChapter() throws IOException {
		if (chapterIndex < chapterNames.size()) {
			String name = chapterNames.get(chapterIndex++);
			currentChapter = chapters.get(name);
			Collections.sort(currentChapter);//Alphabetize it.
			pageIndex = 0;
			return name;
		}

		return null;
	}

	@Override
	public BufferedImage nextPage() throws IOException {
		if (pageIndex < currentChapter.size()) {
			ZipEntry zipEntry = zipFile.getEntry(currentChapter.get(pageIndex++));
			InputStream stream = zipFile.getInputStream(zipEntry);
			BufferedImage page = ImageIO.read(stream);
			return page;
		}
		
		return null;
	}

	@Override
	public int totalPages() throws IOException {
		return totalPages;
	}

	@Override
	public void close() throws IOException {
		zipFile.close();
	}

	@Override
	public void reset() throws IOException {
		chapterIndex = 0;
		pageIndex = 0;
		currentChapter = null;
	}

}
