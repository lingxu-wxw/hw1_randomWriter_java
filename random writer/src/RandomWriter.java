
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Random;

import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream; //可以创建InputStream类型
import java.io.BufferedReader;

//随机续写类
public class RandomWriter {
	
	static List<String> filenameList; //合法的文件名列表
	static String filename;	
	static int paragraphNum; //续写的段落数
	static int wordsNum; //续写每段的单词数
	
	static Map<String, HashMap<String, Integer>> wordGramMap; //后继单词频次表
    static Map<String, HashMap<String, Double>> wordRateMap; //后继单词频率表
    static Random random;
    
    static String line;
    static String preWord;
	static String postWord;
	
	static final int minParagraphNum = 2; //常量：最小段落数
	static final int minWordsNum = 4; //常量：最小段落单词数
	static final String START = "###START";  //标记：句首或段首
	static boolean isStartArtical; //判断：是否为文章开头

	//获取单词后继频次表
  	public static void getPostWordFreqMap() {
  		try {
  			//建立一个输入流对象
  			filename = "src\\" + filename;
  			File file = new File(filename);
  			InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
  			BufferedReader br = new BufferedReader(reader);
  			
  			line = br.readLine();
  			
  			while (line != null) {
  				parseLine(line); //解析一行句子
  				line = br.readLine();
  			}
  			  			
  			//计算单词后继频率表
  			parsePostRate();
  			br.close();
  		}
  		catch (Exception e){
  			e.printStackTrace();
  		}
  	}
  	
  	//解析一行句子
  	public static void parseLine(String line) {
		preWord = "";
		postWord = "";
  		
		line = line.trim(); //去除首尾空格
		String[] wordSplited = line.split("\\s+"); //将句子按照空格切分
		int length = wordSplited.length - 1;
		
		for (int i = 0; i < length ; i++) {
			preWord = wordSplited[i];
			postWord = wordSplited[i+1];
			
			//如果是文章开头，将pre设为标记START
			if (isStartArtical) {
				addPrePostPair(START, preWord);
				isStartArtical = false;
			}
			
			//如果pre末尾不是字母，说明一句话结束
			if (isFinalWord(preWord)) preWord = START;
			
			//向后继单词频次表中添加pre-post对
			addPrePostPair(preWord, postWord);
		}
	}
  	
  	//向后继单词频次表中添加pre-post对
  	public static void addPrePostPair(String preWord, String postWord) {
  		boolean hasPreWordKeyFlag = wordGramMap.containsKey(preWord); //判断preWord是否已经作为前驱存在
  		HashMap<String, Integer> postWordFreqMap = new HashMap<String, Integer>();	
  		
		if (hasPreWordKeyFlag) {
			postWordFreqMap = wordGramMap.get(preWord); //获取preWord的后继单词频次表
			boolean hasPostWordKeyFlag = postWordFreqMap.containsKey(postWord);	//判断postWord是否已经作为preWord后继存在
			
			if (hasPostWordKeyFlag) {
				int wordFreq = postWordFreqMap.get(postWord);
				postWordFreqMap.put(postWord, wordFreq + 1);
			}
			else { //postWord未作为preWord后继存在
				postWordFreqMap.put(postWord, 1);	
			}
			
			wordGramMap.put(preWord, postWordFreqMap);	//写回后继单词频次表
		}
		
		else{ //preWord未作为前驱存在
			postWordFreqMap.put(postWord, 1);
			wordGramMap.put(preWord, postWordFreqMap);
		}
  	}
  	
  	//获取后继单词频率表
  	public static void parsePostRate() {
		Set<String> preWordSet = wordGramMap.keySet();
		
		for (String preWord : preWordSet) {
			HashMap<String, Integer> postWordFreq = wordGramMap.get(preWord);
			Set<String> postWordSet = postWordFreq.keySet(); //获取后继单词集合
			
			int totalFrequence = 0; //计算preWord的总后继数
			for (String postWord : postWordSet) {
				totalFrequence += postWordFreq.get(postWord);
			}
			
			//计算比率，比率成递增，最大到1
			HashMap<String, Double> postWordRate = new HashMap<String, Double>();
			double ratio = 0;
			for (String postWord : postWordSet) {
				ratio += postWordFreq.get(postWord) / (double)totalFrequence;
				postWordRate.put(postWord, ratio);
			}
			
			wordRateMap.put(preWord, postWordRate); //写入后继单词频率表			
		}
	}
 
  	//续写函数
  	public static void continueWrite() { 
		while (paragraphNum != 0) {
			
			String nextWord = writeNextWord(START); //先写文章开头
			int tmpWordsNum = wordsNum - 1;
			
			while (tmpWordsNum != 0) {
				boolean isFinalWordFlag = isFinalWord(nextWord);
				if (! isFinalWordFlag)  
					nextWord = writeNextWord(nextWord);
				else 
					nextWord = writeNextWord(START);
				
				tmpWordsNum --;
			}
			
			paragraphNum--;
			System.out.println("");
		}
	}
  	
  	//续写下一个单词
	public static String writeNextWord(String preWord) {
		double randNum = random.nextDouble(); //随机数
		
		HashMap<String, Double> postWordRate =  wordRateMap.get(preWord);
		Set<String> postWordSet = postWordRate.keySet(); 
		
		for (String postWord : postWordSet) {
			double ratio = postWordRate.get(postWord);
			if (ratio > randNum) { //判断随机数落在哪个频率区间
				System.out.print(postWord+" ");
				return postWord;
			}
		}
		
		return " "; //完整性起见，不会运行至此
	}
  	
	//判断是否为字母
	public static boolean isLetter(char c) {
		if ( (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) return true;
		return false;
	}
  	
	//判断是否为一句中的最后一个单词
	public static boolean isFinalWord(String word) {
		int length = word.length();
		if (! isLetter(word.charAt(length-1))) return true;
		else if (word == "") return true;
		return false;
 	}
	
	//判断是否为句末/文末
	public static boolean isEnd(char c) {
		if (c == '\n') return true;
		return false;
	}
	
	//打印preWord的后继单词频次表
	public static void printFreqMap(Map<String, HashMap<String, Integer>> map, String preWord) {
		if (! map.containsKey(preWord)) {
			System.out.println("Do not has this key: " + preWord);
			return;
		}
		
		HashMap<String, Integer> objMap = map.get(preWord);
		Set<String> keySet = objMap.keySet();
		System.out.println("preWord: " + preWord);
		for (String key : keySet) {
			int value = objMap.get(key);
			System.out.println("key: "+ key +", value: "+ value);
		}
		System.out.println("");
	}
	
	//打印preWord的后继单词频率表
	public static void printRateMap(Map<String, HashMap<String, Double>> map, String preWord) {
		if (! map.containsKey(preWord)) {
			System.out.println("Do not has this key: " + preWord);
			return;
		}
		
		HashMap<String, Double> objMap = map.get(preWord);
		Set<String> keySet = objMap.keySet();
		System.out.println("preWord: " + preWord);
		for (String key : keySet) {
			double value = objMap.get(key);
			System.out.println("key: "+ key +", value: "+ value);
		}
		System.out.println("");
	}
	
	//判断用户输入的文件名是否合法
	public static void getFileName() {
		boolean isFileFlag = false;
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Input file name?");
		filename = sc.nextLine();
		isFileFlag = filenameList.contains(filename);	
			
		if (! isFileFlag) {
			System.out.println("Unable to open that file. Try again.");
			getFileName();
		}
	}
	
	//判断用户输入的段落数是否合法
	public static void getParagraphNum() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Value of N?");
		
		try {
			paragraphNum = Integer.valueOf(sc.nextLine()); //将输入转换成数字
		}
		catch (Exception exec) {
			System.out.println("Illegal integar format. Try again.");
			getParagraphNum();
		}
		
		if (paragraphNum < 2) {
			System.out.println("N must be 2 or greater.");
			getParagraphNum();
		}
	}
	
	//判断用户输入的单词数是否合法
	public static void getWordsNum() {
		Scanner sc = new Scanner(System.in);
		System.out.println("# of random words to generate(0 to exit)?");
		
		try {
			wordsNum = Integer.valueOf(sc.nextLine());
		}
		catch (Exception exec) {
			System.out.println("Illegal integar format. Try again.");
			getWordsNum();
		}
			
		if (wordsNum == 0) exit(); //退出程序
		if (wordsNum < 4) {
			System.out.println("Must be at least 4 words.");
			getWordsNum();
		}
			
	}

	//退出程序
	public static void exit() {
		System.exit(0);
	}
	
	//全局变量初始化
	public static void initial() {
		filenameList = Arrays.asList("cisneros.txt", "constitution.txt", "hamlet.txt", "hughes.txt", "hurston.txt", "isiguro.txt", 
				"ladygaga.txt", "mobydick.txt", "morrison.txt", "randomsample.txt", "short.txt", "tiny.txt", "tomsawyer.txt");
		
		filename = "";
		paragraphNum = 0; 
		wordsNum = 0;
		
		wordGramMap = new HashMap<String, HashMap<String, Integer>>();
		wordRateMap = new HashMap<String, HashMap<String, Double>>();
		random = new Random();
		
		line = "";
		isStartArtical = true;
	}
	
	//检测junit是否可使用
	public static int add(int num1, int num2) {
		return num1+num2;
	}
	
	//main函数
	public static void main(String args[]) {
		/*
		initial();
		getFileName();
		getParagraphNum();
		getWordsNum();
		getPostWordFreqMap();
		continueWrite();
		*/
	}
}
