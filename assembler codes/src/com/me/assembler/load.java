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
import java.util.Scanner;

import com.badlogic.gdx.Screen;


public class load implements Screen{

	String[] fileName;
	int numFiles;
	assemble myAssembler;
	int[] loadFile;
	boolean decodeAll = false;
	
	public load(int x, String[] y, assemble z)
	{
		numFiles = x;
		fileName = new String[numFiles];
		
		for(int i = 0; i < numFiles; ++i)
		{
			fileName[i] = y[i];
			fileName[i] = fileName[i].split("\\.")[0] + "_pre_s.txt";
		}
		
		myAssembler = z;
		loadFile = new int[numFiles];
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
	
	int indCorres2File(String file)
	{
		for(int j = 0; j < numFiles; ++j)
		{
			if(file.equals(fileName[j]))
				return j;
		}
		return -1;
	}
	
	void loadCode() throws IOException
	{
		int i = 0;
		List<String> asCode = new ArrayList<String>();
		boolean myDecode = false;
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		
		for(int j = 0; j < numFiles; ++j)
		{
			fileName[j] = fileName[j].split("\\.")[0];
			System.out.print("Where to Load " + fileName[j] + " : ");
			int num = in.nextInt();
			loadFile[j] = num;
		}

		//Generates file which run on Simulator
		for(int j = 0; j < numFiles; ++j)
		{
			fileName[j] = fileName[j].split("\\.")[0];
			String code = getFileContent(fileName[j] + "_l_8085.txt");
			List<String> lines = Arrays.asList(code.split("\n"));	
			for(String line: lines)
			{
				line.trim();
				if(!line.equals(""))
				{
					if(myDecode || decodeAll)
					{
						System.out.println("line is : -------------");
						System.out.println(line);
						System.in.read();
					}
					
					String[] tags = line.split(" ");
					for(String tag: tags) 
					{
						if(tag.contains("$"))
						{
							int val = Integer.parseInt(tag.split("\\$")[1])+loadFile[j]+myAssembler.fileLength.get(fileName[j].split("_")[0] + "_pre.txt");
							if(myDecode || decodeAll)
							{
								System.out.println("tag contains $.. tag split + loadfile + filelength + val-------------");
								System.out.println(tag.split("\\$")[1] + " , " + loadFile[j] + " , " + myAssembler.fileLength.get(fileName[j].split("_")[0] + "_pre.txt") + " , " + val);
								System.in.read();
							}
							line = line.replace(tag, Integer.toString(val));
						}
						if(tag.contains("#"))
						{
							String add = tag.split("#")[1];
							add = add.split("\\+")[add.split("\\+").length - 1];
							if(!isNumeric(add))
								add = "0";
							String lnFile = tag.split("#")[0];
							int val = loadFile[j]+myAssembler.fileLength.get(lnFile + "_pre.txt") + myAssembler.variableTable.get(indCorres2File(lnFile)).get(tag.split("#")[1].split("\\+")[0]) + Integer.parseInt(add); 
							line = line.replace(tag,Integer.toString(val));
						}
					}
					asCode.add(line.trim());
				}
			}		
		}
		String temporary = "";
		for(String temp: asCode)
		{	
			temporary += temp + "\n";
		}
		String code =  temporary;
		
		String outputFileName = fileName[0].split("\\.")[0]+"_s_8085.txt";
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
				System.out.println("Final Code printed after load1");
			System.out.println(code);
			System.in.read();
		}
		bw.close();

		// Generates file which represent vitrual memory
		asCode = new ArrayList<String>();
		for(int j = 0; j < numFiles; ++j)
		{
			fileName[j] = fileName[j].split("\\.")[0];
			code = getFileContent(fileName[j] + "_l_8085.txt");
			List<String> lines = Arrays.asList(code.split("\n"));	
			
			while(i != loadFile[j])
			{
				i = i+1;
				asCode.add("");
			}
				
			for(String line: lines)
			{
				line.trim();
				if(!line.equals(""))
				{
					String[] tags = line.split(" ");
					for(String tag: tags)
					{
						if(tag.contains("$"))
						{
							int val = Integer.parseInt(tag.split("\\$")[1]) + loadFile[j];
							line = line.replace(tag,Integer.toString(val));
						}
						if(tag.contains("#"))
						{
							String add = tag.split("#")[1];
							add = add.split("\\+")[add.split("\\+").length - 1];
							if(!isNumeric(add))
								add = "0";
							String lnFile = tag.split("#")[0];
							int val = loadFile[indCorres2File(lnFile)] + myAssembler.variableTable.get(indCorres2File(lnFile)).get(tag.split("#")[1].split("\\+")[0]) + Integer.parseInt(add);
							line = line.replace(tag,Integer.toString(val));
						}
					}
					asCode.add(line.trim());
				}
				i = i+1;
			}
		}
		temporary = "";
		for(String temp: asCode)
		{	
			temporary += temp + "\n";
		}
		code =  temporary;
		outputFileName = fileName[0].split("\\.")[0]+".8085";
		outputFile = new File(outputFileName);
		// if file doesn't exists, then create it
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
 
			fw = new FileWriter(outputFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(code);
			if(myDecode || decodeAll)
			{
				System.out.println("Final Code printed after load2");
			System.out.println(code);
			System.in.read();
		}
		bw.close();
			
	}
	public static boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c))
	        	return false;
	    }
	    return true;
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

