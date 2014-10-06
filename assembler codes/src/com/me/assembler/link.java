package com.me.assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Screen;

public class link implements Screen{
	
	String[] fileName;
	int numFiles;
	ArrayList<ArrayList<String>> externTable = new ArrayList<ArrayList<String>>();
	assemble myAssembler;
	private static final int GLOBAL = 0;
	//private static final int LOCAL = 1;
	List<Integer> fileLengthTable = new ArrayList<Integer>();
	boolean decodeAll = false;

	public link(int x, String[] y, assemble z)
	{
		numFiles = x;
		fileName = new String[numFiles];
		
		for(int i = 0; i < numFiles; ++i)
		{
			fileName[i] = y[i];
			fileName[i] = fileName[i].split("\\.")[0] + "_pre_s.txt";
		}
		
		myAssembler = z;
	}
	
	@SuppressWarnings("resource")
	public String getFileContent(String path) throws IOException {
		
	    BufferedReader reader = new BufferedReader(new FileReader(path));
	    String line = null;
	    String out = "";
	    while ((line = reader.readLine()) != null) {
	        out = out + line;
	        out = out + "\n";
	    }
	    return out;
	}
	
	void linkCode() throws IOException
	{
		boolean myDecode = false;
		List<String> asCode = new ArrayList<String>();
		int i = 0;
		ArrayList<String> externList;
		for(int j = 0; j < numFiles; ++j)
		{
			String code = getFileContent(fileName[j]);
			fileName[j] = fileName[j].split("\\.")[0];
			externList = new ArrayList<String>();
			List<String> lines = Arrays.asList(code.split("\n"));	
			for(String line: lines)
			{
				line.trim();
				if(!line.equals(""))
					if(line.contains("EXTERN"))
					{
						if(validExtern(line.split(" ")[1]))
							externList.add(line.split(" ")[1]);
						else
						{
							System.out.println("ERROR :" + line);
							System.out.println("Files Required for Linking not found");
							System.exit(0);
						}
					}
					else
					{
						asCode.add(line);
						i = i+1;
					}
			}
			externTable.add(externList);
			fileLengthTable.add(i);
			
			String temporary = "";
			for(String temp: asCode)
			{	
				temporary += temp + "\n";
			}
			code =  temporary;
			
			String outputFileName = fileName[j]+"_l_8085.txt";
			File outputFile = new File(outputFileName);
			// if file doesn't exists, then create it
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
 
			FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(code);
			if(myDecode || decodeAll)
			{
				System.out.println("Final Code printed after link1");
				System.out.println(code);
				System.in.read();
			}
			asCode = new ArrayList<String>();
			bw.close();
		}

		for(int j = 0; j < numFiles; ++j)
		{
			fileName[j] = fileName[j].split("\\.")[0];
			String inputFileName = fileName[j] + "_l_8085.txt";
			String code = getFileContent(inputFileName);
			List<String> lines = Arrays.asList(code.split("\n"));
			asCode = new ArrayList<String>();
			for(String line: lines)
			{
				line.trim();
				String temporary = "";
				int myInt = 0;
				for(String temp: line.split(" "))
				{
					if(myInt < 1)
						++myInt;
					else
						temporary += temp + "";
				}
				String[] tags = temporary.split(",");
				for(String tag: tags)
				{
					if(tagPresent(tag,j))
						line =  line.replace(tag, externAddress(tag)+'#'+tag);
				}
				asCode.add(line);
			}
			String temporary = "";
			for(String temp: asCode)
			{	
				temporary += temp + "\n";
			}
			code =  temporary;
			
			String outputFileName = fileName[j]+"_l_8085.txt";
			File outputFile = new File(outputFileName);
			// if file doesn't exists, then create it
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
 
			FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(code);
			if(myDecode || decodeAll)
			{
				System.out.println("Final Code printed after link2");
				System.out.println(code);
				System.in.read();
			}
			asCode = new ArrayList<String>();
			bw.close();
		}
	}
			
	String externAddress(String tag)
	{
		for(int j = 0; j < numFiles; ++j)
		{
			for(String extern: myAssembler.variableTable.get(j).keySet())
			{
				if(extern.equals(tag.split("\\+")[0].trim()))
					return fileName[j].split("_")[0];
			}
		}
		return "";
	}
	boolean tagPresent(String tag, int j)
	{
		for(String extern: externTable.get(j))
		{
			if(extern.equals(tag.split("\\+")[0].trim()))
				return true;
		}
		return false;		
	}
	
	boolean validExtern(String tag)
	{
		for(int j = 0; j < numFiles; ++j)
		{
			for(String extern: myAssembler.variableScopeTable.get(j).keySet())
			{
				if(extern.equals(tag) && myAssembler.variableScopeTable.get(j).get(tag) == GLOBAL)
					return true;
			}
		}
		return false;
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
}
