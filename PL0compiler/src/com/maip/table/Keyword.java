package com.maip.table;

public class Keyword {
	 	public static final int symnum = 34;  //一共有34种符号属性
	
	    public int type;  //符号的类型，类型表在词法分析器中
	   
	    public String name;  
	    
	    public int num=0;  //数字的值

	    public Keyword(int stype) {
	    	super();
	        type = stype;
	    }

}
