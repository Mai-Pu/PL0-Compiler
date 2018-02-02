package Junit;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.maip.Dao.Panalyzer;
import com.maip.table.Pcode.code;
import com.maip.table.symbolTable.symbolItem;

public class PanalyzerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStart() {
		Panalyzer test = new Panalyzer("f:\\test\\testPL8.txt");
		test.start(0,null);
		for(symbolItem aItem : test.table.table)
		{
			System.out.println(aItem.name+" "+aItem.value+" "+aItem.type+" "+aItem.address+" "+aItem.level+" "+aItem.size);
		}
		int i=0;
		for(code c : test.pcode.P)
		{
			System.out.println(i+" "+c.F+" "+c.l+" "+c.a);
			i++;
		}
		test.pcode.runP();
	}

}
