package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FileIO
{	
	public File file;
	
	private boolean Readonly = false;
	private HashMap<String, String> ValueSet = new HashMap<>();
	
	
	public FileIO() {
		file = null;
		Readonly = false;
	}
	
	public FileIO(File _file) {
		if(!_file.exists()) {
			try
			{
				_file.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		file = _file;
		Readonly = false;
	}
	
	public FileIO(String path) {
		file = new File(path);
		if(!file.exists()) {
			try
			{
				file.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		Readonly = false;
	}
	
	public FileIO(File _file, boolean readonly) {
		file = _file;
		file.setWritable(!readonly);
		Readonly = readonly;
	}
	
	public FileIO(String path, boolean readonly) {
		file = new File(path);
		file.setWritable(!readonly);
		Readonly = readonly;
	}
	
	public void SetFile(File _file) {
		if(!_file.exists()) {
			try
			{
				_file.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		file = _file;
	}
	
	public void SetFile(String path) {
		SetFile(new File(path));
	}
	
	public String ReadAll() {
		Scanner scanner = null;
		try
		{
			scanner = new Scanner(file);
		} catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "";
		}
		try{
			String output = "";
			while(scanner.hasNextLine()) {
				output += scanner.nextLine();
			}
			scanner.close();
			return output;
		} catch (Exception e){
			// TODO Auto-generated catch block
			scanner.close();
			e.printStackTrace();
			return "";
		}
	}
	
	public ArrayList<String> ReadAlltoList() {
		Scanner scanner = null;
		try
		{
			scanner = new Scanner(file);
		} catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return new ArrayList<>();
		}
		try{
			ArrayList<String> output = new ArrayList<>();
			while(scanner.hasNextLine()) {
				output.add(scanner.nextLine());
			}
			scanner.close();
			return output;
		} catch (Exception e){
			// TODO Auto-generated catch block
			scanner.close();
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public void LoadSet() {
		ValueSet.clear();
		ArrayList<String> data = ReadAlltoList();
		String key = "";
		String value = "";
		for(String str : data) {
			if(str.contains(":")) {
				ValueSet.put(key, value);
				String[] raw = str.split(":");
				key = raw[0];
				value = "";
				for(int i =  1; i < raw.length; i++) {
					value += raw[i];
				}
			}
		}
	}
	
	public void SaveSet() {
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for(String key : ValueSet.keySet()) {
				writer.write(key + ":" + ValueSet.get(key));
			}
			writer.close();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String ReadSet(String key) {
		return ValueSet.get(key);
	}
	
	public boolean ContainKey(String key) {
		return ValueSet.containsKey(key);
	}
	
	public boolean ContainValue(String value) {
		return ValueSet.containsValue(value);
	}
	
	public void WriteSet(String key, String value) {
		if(Readonly) return;
		ValueSet.put(key, value);
	}
	
	public void ResetFile() {
		BufferedWriter writer;
		try
		{
			writer = new BufferedWriter(new FileWriter(file));
			writer.close();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void WriteLine(String str) {
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.newLine();
			writer.write(str);
			writer.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
