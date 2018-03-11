
import junit.framework.*;
import java.io.*;
import java.util.*;

public class Test extends TestCase{
	RandomWriter testobj = new RandomWriter();
	
	public void testAdd() {
		int result = testobj.add(10, 50);
		assertEquals(60, result, 0);
	}
	
	public void testGetParagraphNum() {
		testobj.getParagraphNum();
		
		ByteArrayInputStream testInput = new ByteArrayInputStream("".getBytes());
		System.setIn(testInput);
		
		ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
		System.setOut(new PrintStream(testOutput));
		assertEquals(testOutput.toString(),"N must be 2 or greater.","Illegal integar format. Try again.");
		
		System.setIn(System.in);
		System.setOut(System.out);
	}
	
	public void testParseLine() {
		String testStr = "A secret makes a woman woman.";
		testobj.parseLine(testStr);
		assertEquals(0,0,0);
	}
}
