package com.me.assembler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.Game;

public class Assembler extends Game {
	
	preprocess myPreprocessor;
	assemble myAssembler;
	link myLinker;
	load myLoader;
	
	@Override
	public void create() {		
		try {
			main();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void main() throws IOException
	{
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    String[] fileNames;
	    String temp = "";
	    int numFiles = 0;
	  
	    System.out.print("Enter number of files: \n");
	    temp = bufferRead.readLine();
		numFiles = Integer.parseInt(temp);
	    
	    fileNames = new String[numFiles];
	   
	    for(int i = 0; i < numFiles; ++i)
	    {
	    	System.out.print("Enter file name No." + i + ": \n");
	    	fileNames[i] = bufferRead.readLine();
	    }
	    
	    //Preprocessing
	    myPreprocessor = new preprocess(numFiles, fileNames);
	    myPreprocessor.macroPreprocess();
	    myPreprocessor.opCodePreprocess();
	    
	    //Two Pass Assembler
	    myAssembler = new assemble(numFiles, fileNames);
	    myAssembler.createSymbolTable(); // Pass1
	    myAssembler.replaceTable(); // Pass2
	    
	    //Linker
	    myLinker = new link(numFiles, fileNames, myAssembler);
	    myLinker.linkCode();
	    
	    //Loader
	    myLoader = new load(numFiles, fileNames, myAssembler);
	    myLoader.loadCode();
	    
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void render() {		
		
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
