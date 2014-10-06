package com.me.assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Screen;

public class assemble implements Screen{
	
	private static final int GLOBAL = 0;
	private static final int LOCAL = 1;
	String[] fileName;
	int numFiles;
	Map<String, String> opcodeLengthTable = new HashMap<String, String>();
	Map<String, Integer> fileLength = new HashMap<String, Integer>();
	List<Map<String, Integer>> symbolTable = new ArrayList<Map<String, Integer>>();
	List<Map<String, Integer>> variableTable = new ArrayList<Map<String, Integer>>();
	List<Map<String, Integer>> variableScopeTable = new ArrayList<Map<String, Integer>>();
	boolean decodeAll = false;

	public assemble(int x, String[] y)
	{
		numFiles = x;
		fileName = new String[numFiles];
		
		for(int i = 0; i < numFiles; ++i)
		{
			fileName[i] = y[i];
			fileName[i] = fileName[i].split("\\.")[0] + "_pre.txt";
		}
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
	
	void createSymbolTable() throws IOException
	{
		boolean myDecode = false;
		createLengthTable(myDecode) ;
		int i = 0;
		
		Map<String, Integer> symbolTableMap = new HashMap<String, Integer>();
		Map<String, Integer> variableTableMap = new HashMap<String, Integer>();
		Map<String, Integer> variableScopeTableMap = new HashMap<String, Integer>();
		
		
		for(int j = 0; j < numFiles; ++j)
		{
			String code = getFileContent(fileName[j]);
			//fileName[j] = fileName[j].split("\\.")[0];
			fileLength.put(fileName[j], i);	
			i = 0;
			List<String> lines = Arrays.asList(code.split("\n"));	
			for(String line: lines)
			{
				if(myDecode || decodeAll)
				{
					System.out.println("line---------------");
					System.out.println(line);
					System.in.read();
				}
				line.trim();
				String tag = "";
				
				if(line.contains(":") && line.split(":").length > 0) // MAY GET SOME PROBLEM HERE  > 0
				{
					tag = line.split(":")[0].trim();
					if(myDecode || decodeAll)
					{
						System.out.println("Line split ; length > 1.. corres tag-----------------");
						System.out.println(tag);
						System.in.read();
					}
					symbolTableMap.put(tag, i);
					//symbolTable.add(tempMap);
				}
				if(line.contains("DS"))
				{
					tag = line.split("DS")[0].trim();
					tag = tag.split(" ")[tag.split(" ").length - 1];
					variableTableMap.put(tag, i);
					//variableTable.add(tempMap);
					
					variableScopeTableMap.put(tag, scopeVariable(line));
					//variableScopeTable.add(tempMap_);
					if(myDecode || decodeAll)
					{
						System.out.println("line contatins DS .. corres tag + scopeVariable--------------");
						System.out.println(tag + "  " + scopeVariable(line));
						System.in.read();
					}
					i = i + Integer.parseInt(line.split("DS")[1].trim());
				}
				if(line.contains("DB"))
				{
					tag = line.split("DB")[0].trim();
					tag = tag.split(" ")[tag.split(" ").length - 1];
					variableTableMap.put(tag, i);
					//variableTable.add(tempMap);
					
					variableScopeTableMap.put(tag, scopeVariable(line));
					//variableScopeTable.add(tempMap_);
					
					if(myDecode || decodeAll)
					{
						System.out.println("line contatins DB .. corres tag + scopeVariable--------------");
						System.out.println(tag + "  " + scopeVariable(line));
						System.in.read();
					}
					
					i = i + line.split(",").length;
				}
				String[] tags = line.split(" ");
				for(String tag2: tags)			
				{
					if(opcodeLengthTable.containsKey(tag2))
						i = i + Integer.parseInt(opcodeLengthTable.get(tag2));
				}
		}
			symbolTable.add(symbolTableMap);
			variableTable.add(variableTableMap);
			variableScopeTable.add(variableScopeTableMap);
			String tableFileName = "";
			tableFileName = fileName[j].split("_")[0]+"_table.txt";
			File tableFile = new File(tableFileName);
			 
			// if file doesn't exists, then create it
			if (!tableFile.exists()) {
				tableFile.createNewFile();
			}
 
			FileWriter fw = new FileWriter(tableFile.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			String symbols = "-------------SYMBOL-------------\n";
			for(String symbol: symbolTable.get(j).keySet())
			{
					symbols = symbols + symbol + "\t" + symbolTable.get(j).get(symbol) + "\n";
			}
			symbols = symbols + "-------------SYMBOL-------------\n";
			String variables = "------------VARIABLE------------\n";
			for(String variable: variableScopeTable.get(j).keySet())
			{
					variables = variables + variable + "\t" + variableTable.get(j).get(variable) + "\t" + variableTable.get(j).get(variable) + "\t" + ((variableScopeTable.get(j).get(variable)==1)?"LOCAL":"GLOBAL") + "\n";
			}
			variables = variables + "------------VARIABLE------------\n";
			bw.write(symbols + variables);
			if(myDecode || decodeAll)
			{
				System.out.println("Final Symbol + Variable String---------------");
				System.out.println(symbols + variables);
				System.in.read();
			}
			symbols = "";
			variables = "";
			bw.close();
		}
	}
	
	void replaceTable() throws IOException
	{
		boolean myDecode = false;
		int i = 0;
		for(int j = 0; j < numFiles; ++j)
		{
			String code = getFileContent(fileName[j]);
			List<String> lines = Arrays.asList(code.split("\n"));
			fileName[j] = fileName[j].split("\\.")[0];
			List<String> asCode = new ArrayList<String>();
			for(String line: lines)
			{
				line.trim();
				if(!line.equals(""))
				{
					if(line.contains(":"))
					{
						if(myDecode || decodeAll)
						{
							System.out.println("Line Contatins : ");
							System.out.println(line.split(":", 2)[1]);
							System.in.read();
						}
						line = line.split(":",2)[1];
					}
					if(line.contains("DS"))
					{
						if(myDecode || decodeAll)
						{
							System.out.println("Line Contatins DS ");
							System.out.println(line.split("DS", 2)[1]);
							System.in.read();
						}
						line = "DS " + line.split("DS",2)[1];
					}
					if(line.contains("DB"))
					{
						if(myDecode || decodeAll)
						{
							System.out.println("Line Contatins DB ");
							System.out.println(line.split("DB", 2)[1]);
							System.in.read();
						}
						line = "DB " + line.split("DB",2)[1];
					}
					String[] tags = line.split(" ");
					for(String tag: tags)
					{
						if(symbolTable.get(j).containsKey(tag))
						{
							line = line.replace(tag,"$" + symbolTable.get(j).get(tag));
						}
						else if(variableTable.get(j).containsKey(tag.split("\\+")[0].trim()))
						{
							String add = tag.split("\\+")[tag.split("\\+").length - 1];
							if(!isNumeric(add))
								add = "0";
							line = line.replace(tag,"$"+(variableTable.get(j).get(tag.split("\\+")[0].trim())+Integer.parseInt(add)));
						}
					}
					asCode.add(line.trim());
				}
			}
			String temporary = "";
			for(String temp: asCode)
			{	
				temporary += temp + "\n";
			}
			code =  temporary;
			
			fileName[j] = fileName[j].split("\\.")[0]+"_s.txt";
			File outputFile = new File(fileName[j]);
			// if file doesn't exists, then create it
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
 
			FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(code);
			
			if(myDecode || decodeAll)
			{
				System.out.println("Final Code printed after assemble pass2");
				System.out.println(code);
				System.in.read();
			}
			
			bw.close();
			
			i = i+1;
		}
	}

	int scopeVariable(String line)
	{
		if(line.contains("GLOBAL"))
			return GLOBAL;		
		else 
			return LOCAL;
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
	
	void createLengthTable(boolean myDecode) throws IOException
	{
		String opcode = getFileContent("config/opcodeslength.config");
		List<String> lines = Arrays.asList(opcode.split("\n"));
			
		for(String line: lines)
		{
			line = line.split(";")[0];
			line.trim();
			if(!line.equals(""))
			{
				String[] tags = line.split(" ");
				if(myDecode || decodeAll)
				{
					System.out.println("Length table first tag + second tag----------------");
					System.out.println(tags[0] + "  " + tags[1]);
					System.in.read();
				}
				opcodeLengthTable.put(tags[0], tags[1]);
			}
		}
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
