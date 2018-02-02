package com.maip.Dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Vector;
import java.util.Arrays;

import com.maip.table.Keyword;

/**
 * 词法分析器
 */
public class Analyzer {
	private Vector<String> readtxt = new Vector<String>();  //调整过的文本文件
	private int gps = 0;
	private int linegps = 0;
	private char readchar = ' ';
	
	
	public Vector<String> getReadtxt() {  //获得读取的文本文件，以便调试
		return readtxt;
	}

	public int getLinegps() {  //获取正在进行词法分析的行号，为了输出相应的错误行号
		return linegps;
	}
	
	public Analyzer()
	{
		super();
	}
	public Analyzer(String filePath){
        try {
                String encoding="GBK";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ 
                    InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	//if(lineTxt.equals("")==false)
                        readtxt.add(lineTxt.trim()+' ');  //去除左空格
                    }
                    read.close();
                }else{
                	System.out.println("找不到指定的文件");
                }
        	} catch (Exception e) {
        		System.out.println("读取文件内容出错");
        		e.printStackTrace();
        	}
     }
     
	public boolean readch()  //每次读取一个字符
	{
		if(gps < readtxt.get(linegps).length())
		{
			readchar = readtxt.get(linegps).charAt(gps);
			gps++;
			return true;
		}
		else
		{
			if(linegps >= (readtxt.size()-1)) 
				return false;
			else
			{
				linegps++;
				while(readtxt.get(linegps).equals(""))
				{
					linegps++;
					if(linegps > (readtxt.size()-1)) 
						return false;
				}
				gps = 0;
				readchar = readtxt.get(linegps).charAt(gps);
				gps++;
				return true;
			}
		}	
	}
	
	public Keyword readkeyword()  //通过读取一个或多个字符来组成相应的关键字
	{
		Keyword keyword = null;
		boolean tf;
		while(readchar == ' ')
		{
			tf = readch();
			if(tf == false)
				return null;
		}
		if ((readchar >= 'a' && readchar <= 'z')||(readchar >= 'A' && readchar <= 'Z')) {
			keyword = readword();                                     //关键字或者一般标识符
        } else if (readchar >= '0' && readchar <= '9') {
        	keyword = readnumber();                                                       //数字
        } else {
        	keyword = readoperator();                                                     //符号
        }
		return keyword;
	}
	
	public Keyword readnumber()    //判断读取的关键字是否为一个数字
	{
		Keyword keyword = null;
		StringBuffer sb = new StringBuffer();
		while(readchar >= '0' && readchar <= '9')
		{
			sb.append(readchar);
			readch();
		}
		keyword = new Keyword(integer);
		keyword.name = sb.toString();
		keyword.num = Integer.parseInt(sb.toString());
		return keyword;
	}
	
	public Keyword readword()   //判断读取的关键字是标识符还是系统的保留字
	{
		Keyword keyword = null;
		StringBuffer sb = new StringBuffer();
		while((readchar >= 'a' && readchar <= 'z')||(readchar >= 'A' && readchar <= 'Z')||(readchar >= '0' && readchar <= '9'))
		{
			sb.append(readchar);
			readch();
		}
		int index = search_in_reserved(sb.toString());
		if(index == -1)
		{
			keyword = new Keyword(id);
			keyword.name = sb.toString();
		}
		else
		{
			keyword = new Keyword(nature[index]);
			keyword.name = sb.toString();
		}
		return keyword;
	}
	
	public Keyword readoperator()  //判断读取的关键字是哪一个操作符
	{
		Keyword keyword = null;
		StringBuffer sb = new StringBuffer();
		switch(readchar)
		{
			case ':':
				sb.append(readchar);
				readch();
				if(readchar == '=')
				{
					sb.append(readchar);
					keyword = new Keyword(setval);
					keyword.name = sb.toString();
					readch();
				}
				else
				{
					keyword = new Keyword(NULL);
					keyword.name = sb.toString();
				}
				break;
			case '+':
				sb.append(readchar);
				readch();
				keyword = new Keyword(add);
				keyword.name = sb.toString();
				break;
			case '-':
				sb.append(readchar);
				readch();
				keyword = new Keyword(sub);
				keyword.name = sb.toString();
				break;
			case '*':
				sb.append(readchar);
				readch();
				keyword = new Keyword(mul);
				keyword.name = sb.toString();
				break;
			case '/':
				sb.append(readchar);
				readch();
				keyword = new Keyword(div);
				keyword.name = sb.toString();
				break;
			case '=':
				sb.append(readchar);
				readch();
				keyword = new Keyword(equ);
				keyword.name = sb.toString();
				break;
			case '>':
				sb.append(readchar);
				readch();
				if(readchar == '=')
				{
					sb.append(readchar);
					keyword = new Keyword(beq);
					keyword.name = sb.toString();
					readch();
				}
				else
				{
					keyword = new Keyword(bigger);
					keyword.name = sb.toString();
				}
				break;
			case '<':
				sb.append(readchar);
				readch();
				if(readchar == '=')
				{
					sb.append(readchar);
					keyword = new Keyword(leq);
					keyword.name = sb.toString();
					readch();
				}
				else if(readchar == '>')
				{
					sb.append(readchar);
					keyword = new Keyword(nequ);
					keyword.name = sb.toString();
					readch();
				}
				else
				{
					keyword = new Keyword(less);
					keyword.name = sb.toString();
				}
				break;
			case '(':
				sb.append(readchar);
				readch();
				keyword = new Keyword(left);
				keyword.name = sb.toString();
				break;
			case ')':
				sb.append(readchar);
				readch();
				keyword = new Keyword(right);
				keyword.name = sb.toString();
				break;
			case ',':
				sb.append(readchar);
				readch();
				keyword = new Keyword(comma);
				keyword.name = sb.toString();
				break;
			case ';':
				sb.append(readchar);
				readch();
				keyword = new Keyword(semicolon);
				keyword.name = sb.toString();
				break;
			case '.':
				sb.append(readchar);
				readch();
				keyword = new Keyword(peroid);
				keyword.name = sb.toString();
				break;
			default:
				sb.append(readchar);
				readch();
				keyword = new Keyword(NULL);
				keyword.name = sb.toString(); 
		}
		return keyword;
	}
	
	//词法分析所得到的各类keyword
    public static final int NULL = 0;                  //NULL
    public static final int prog = 1;                  //program
    public static final int id = 2;               //标识符
    public static final int constkey = 3;       //const
    public static final int var = 4;           //var
    public static final int proc = 5;         //procedure
    public static final int begin = 6;        //开始符号begin
    public static final int end = 7;           //结束符号end
    public static final int setval = 8;         //赋值符号 :=
    public static final int ifkey = 9;             //if
    public static final int thenkey = 10;         //then
    public static final int elsekey = 11;			//else
    public static final int whilekey = 12;        //while
    public static final int dokey = 13;            //do
    public static final int callkey = 14;          //call
    public static final int readkey = 15;         //read
    public static final int writekey = 16;        //write
    public static final int odd = 17; 				//odd
    public static final int add = 18;                //加号+
    public static final int sub = 19;              //减号-
    public static final int equ = 20;                  //等于号=
    public static final int nequ = 21;                 //不等于<>
    public static final int less = 22;                 //小于<
    public static final int leq = 25;                //小于等于<=
    public static final int bigger = 24;                //大于>
    public static final int beq = 23;                 //大于等于>=
    public static final int mul = 26;                 //乘号*
    public static final int div = 27;                  //除号/
    public static final int integer = 28;           //数字
    public static final int left = 29;            //左括号(
    public static final int right = 30;           //右括号 ) 
    public static final int comma = 31;           //逗号,
    public static final int semicolon = 32;       //分号;
    public static final int peroid = 33;            //句号.
    
    public static final String[] reserved_words = new String[]{
    		"program","const","var","procedure","begin","end","call","while","do",
    		"if","else","then","read","write","odd"};
    
    //上面保留字对应的属性
    public static final int[] nature = new int[]{
            prog, constkey, var,proc,begin,end,callkey,whilekey,dokey,ifkey,elsekey,
            thenkey,readkey,writekey,odd};
    
    public int search_in_reserved(String what)  //查找某标识符是否为保留字，不是则返回-1
    {
    	int i=0;
    	for(String what2 : reserved_words)
        {
        	if(what2.equals(what))
        		return i;
        	else {
				i++;
			}
        }
    	return -1;
    }
}
