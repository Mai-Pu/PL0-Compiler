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
 * �ʷ�������
 */
public class Analyzer {
	private Vector<String> readtxt = new Vector<String>();  //���������ı��ļ�
	private int gps = 0;
	private int linegps = 0;
	private char readchar = ' ';
	
	
	public Vector<String> getReadtxt() {  //��ö�ȡ���ı��ļ����Ա����
		return readtxt;
	}

	public int getLinegps() {  //��ȡ���ڽ��дʷ��������кţ�Ϊ�������Ӧ�Ĵ����к�
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
                        readtxt.add(lineTxt.trim()+' ');  //ȥ����ո�
                    }
                    read.close();
                }else{
                	System.out.println("�Ҳ���ָ�����ļ�");
                }
        	} catch (Exception e) {
        		System.out.println("��ȡ�ļ����ݳ���");
        		e.printStackTrace();
        	}
     }
     
	public boolean readch()  //ÿ�ζ�ȡһ���ַ�
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
	
	public Keyword readkeyword()  //ͨ����ȡһ�������ַ��������Ӧ�Ĺؼ���
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
			keyword = readword();                                     //�ؼ��ֻ���һ���ʶ��
        } else if (readchar >= '0' && readchar <= '9') {
        	keyword = readnumber();                                                       //����
        } else {
        	keyword = readoperator();                                                     //����
        }
		return keyword;
	}
	
	public Keyword readnumber()    //�ж϶�ȡ�Ĺؼ����Ƿ�Ϊһ������
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
	
	public Keyword readword()   //�ж϶�ȡ�Ĺؼ����Ǳ�ʶ������ϵͳ�ı�����
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
	
	public Keyword readoperator()  //�ж϶�ȡ�Ĺؼ�������һ��������
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
	
	//�ʷ��������õ��ĸ���keyword
    public static final int NULL = 0;                  //NULL
    public static final int prog = 1;                  //program
    public static final int id = 2;               //��ʶ��
    public static final int constkey = 3;       //const
    public static final int var = 4;           //var
    public static final int proc = 5;         //procedure
    public static final int begin = 6;        //��ʼ����begin
    public static final int end = 7;           //��������end
    public static final int setval = 8;         //��ֵ���� :=
    public static final int ifkey = 9;             //if
    public static final int thenkey = 10;         //then
    public static final int elsekey = 11;			//else
    public static final int whilekey = 12;        //while
    public static final int dokey = 13;            //do
    public static final int callkey = 14;          //call
    public static final int readkey = 15;         //read
    public static final int writekey = 16;        //write
    public static final int odd = 17; 				//odd
    public static final int add = 18;                //�Ӻ�+
    public static final int sub = 19;              //����-
    public static final int equ = 20;                  //���ں�=
    public static final int nequ = 21;                 //������<>
    public static final int less = 22;                 //С��<
    public static final int leq = 25;                //С�ڵ���<=
    public static final int bigger = 24;                //����>
    public static final int beq = 23;                 //���ڵ���>=
    public static final int mul = 26;                 //�˺�*
    public static final int div = 27;                  //����/
    public static final int integer = 28;           //����
    public static final int left = 29;            //������(
    public static final int right = 30;           //������ ) 
    public static final int comma = 31;           //����,
    public static final int semicolon = 32;       //�ֺ�;
    public static final int peroid = 33;            //���.
    
    public static final String[] reserved_words = new String[]{
    		"program","const","var","procedure","begin","end","call","while","do",
    		"if","else","then","read","write","odd"};
    
    //���汣���ֶ�Ӧ������
    public static final int[] nature = new int[]{
            prog, constkey, var,proc,begin,end,callkey,whilekey,dokey,ifkey,elsekey,
            thenkey,readkey,writekey,odd};
    
    public int search_in_reserved(String what)  //����ĳ��ʶ���Ƿ�Ϊ�����֣������򷵻�-1
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
