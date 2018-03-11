
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
import java.io.FileInputStream; //���Դ���InputStream����
import java.io.BufferedReader;

//�����д��
public class RandomWriter {
	
	static List<String> filenameList; //�Ϸ����ļ����б�
	static String filename;	
	static int paragraphNum; //��д�Ķ�����
	static int wordsNum; //��дÿ�εĵ�����
	
	static Map<String, HashMap<String, Integer>> wordGramMap; //��̵���Ƶ�α�
    static Map<String, HashMap<String, Double>> wordRateMap; //��̵���Ƶ�ʱ�
    static Random random;
    
    static String line;
    static String preWord;
	static String postWord;
	
	static final int minParagraphNum = 2; //��������С������
	static final int minWordsNum = 4; //��������С���䵥����
	static final String START = "###START";  //��ǣ����׻����
	static boolean isStartArtical; //�жϣ��Ƿ�Ϊ���¿�ͷ

	//��ȡ���ʺ��Ƶ�α�
  	public static void getPostWordFreqMap() {
  		try {
  			//����һ������������
  			filename = "src\\" + filename;
  			File file = new File(filename);
  			InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
  			BufferedReader br = new BufferedReader(reader);
  			
  			line = br.readLine();
  			
  			while (line != null) {
  				parseLine(line); //����һ�о���
  				line = br.readLine();
  			}
  			  			
  			//���㵥�ʺ��Ƶ�ʱ�
  			parsePostRate();
  			br.close();
  		}
  		catch (Exception e){
  			e.printStackTrace();
  		}
  	}
  	
  	//����һ�о���
  	public static void parseLine(String line) {
		preWord = "";
		postWord = "";
  		
		line = line.trim(); //ȥ����β�ո�
		String[] wordSplited = line.split("\\s+"); //�����Ӱ��տո��з�
		int length = wordSplited.length - 1;
		
		for (int i = 0; i < length ; i++) {
			preWord = wordSplited[i];
			postWord = wordSplited[i+1];
			
			//��������¿�ͷ����pre��Ϊ���START
			if (isStartArtical) {
				addPrePostPair(START, preWord);
				isStartArtical = false;
			}
			
			//���preĩβ������ĸ��˵��һ�仰����
			if (isFinalWord(preWord)) preWord = START;
			
			//���̵���Ƶ�α������pre-post��
			addPrePostPair(preWord, postWord);
		}
	}
  	
  	//���̵���Ƶ�α������pre-post��
  	public static void addPrePostPair(String preWord, String postWord) {
  		boolean hasPreWordKeyFlag = wordGramMap.containsKey(preWord); //�ж�preWord�Ƿ��Ѿ���Ϊǰ������
  		HashMap<String, Integer> postWordFreqMap = new HashMap<String, Integer>();	
  		
		if (hasPreWordKeyFlag) {
			postWordFreqMap = wordGramMap.get(preWord); //��ȡpreWord�ĺ�̵���Ƶ�α�
			boolean hasPostWordKeyFlag = postWordFreqMap.containsKey(postWord);	//�ж�postWord�Ƿ��Ѿ���ΪpreWord��̴���
			
			if (hasPostWordKeyFlag) {
				int wordFreq = postWordFreqMap.get(postWord);
				postWordFreqMap.put(postWord, wordFreq + 1);
			}
			else { //postWordδ��ΪpreWord��̴���
				postWordFreqMap.put(postWord, 1);	
			}
			
			wordGramMap.put(preWord, postWordFreqMap);	//д�غ�̵���Ƶ�α�
		}
		
		else{ //preWordδ��Ϊǰ������
			postWordFreqMap.put(postWord, 1);
			wordGramMap.put(preWord, postWordFreqMap);
		}
  	}
  	
  	//��ȡ��̵���Ƶ�ʱ�
  	public static void parsePostRate() {
		Set<String> preWordSet = wordGramMap.keySet();
		
		for (String preWord : preWordSet) {
			HashMap<String, Integer> postWordFreq = wordGramMap.get(preWord);
			Set<String> postWordSet = postWordFreq.keySet(); //��ȡ��̵��ʼ���
			
			int totalFrequence = 0; //����preWord���ܺ����
			for (String postWord : postWordSet) {
				totalFrequence += postWordFreq.get(postWord);
			}
			
			//������ʣ����ʳɵ��������1
			HashMap<String, Double> postWordRate = new HashMap<String, Double>();
			double ratio = 0;
			for (String postWord : postWordSet) {
				ratio += postWordFreq.get(postWord) / (double)totalFrequence;
				postWordRate.put(postWord, ratio);
			}
			
			wordRateMap.put(preWord, postWordRate); //д���̵���Ƶ�ʱ�			
		}
	}
 
  	//��д����
  	public static void continueWrite() { 
		while (paragraphNum != 0) {
			
			String nextWord = writeNextWord(START); //��д���¿�ͷ
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
  	
  	//��д��һ������
	public static String writeNextWord(String preWord) {
		double randNum = random.nextDouble(); //�����
		
		HashMap<String, Double> postWordRate =  wordRateMap.get(preWord);
		Set<String> postWordSet = postWordRate.keySet(); 
		
		for (String postWord : postWordSet) {
			double ratio = postWordRate.get(postWord);
			if (ratio > randNum) { //�ж�����������ĸ�Ƶ������
				System.out.print(postWord+" ");
				return postWord;
			}
		}
		
		return " "; //�����������������������
	}
  	
	//�ж��Ƿ�Ϊ��ĸ
	public static boolean isLetter(char c) {
		if ( (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) return true;
		return false;
	}
  	
	//�ж��Ƿ�Ϊһ���е����һ������
	public static boolean isFinalWord(String word) {
		int length = word.length();
		if (! isLetter(word.charAt(length-1))) return true;
		else if (word == "") return true;
		return false;
 	}
	
	//�ж��Ƿ�Ϊ��ĩ/��ĩ
	public static boolean isEnd(char c) {
		if (c == '\n') return true;
		return false;
	}
	
	//��ӡpreWord�ĺ�̵���Ƶ�α�
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
	
	//��ӡpreWord�ĺ�̵���Ƶ�ʱ�
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
	
	//�ж��û�������ļ����Ƿ�Ϸ�
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
	
	//�ж��û�����Ķ������Ƿ�Ϸ�
	public static void getParagraphNum() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Value of N?");
		
		try {
			paragraphNum = Integer.valueOf(sc.nextLine()); //������ת��������
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
	
	//�ж��û�����ĵ������Ƿ�Ϸ�
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
			
		if (wordsNum == 0) exit(); //�˳�����
		if (wordsNum < 4) {
			System.out.println("Must be at least 4 words.");
			getWordsNum();
		}
			
	}

	//�˳�����
	public static void exit() {
		System.exit(0);
	}
	
	//ȫ�ֱ�����ʼ��
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
	
	//���junit�Ƿ��ʹ��
	public static int add(int num1, int num2) {
		return num1+num2;
	}
	
	//main����
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
