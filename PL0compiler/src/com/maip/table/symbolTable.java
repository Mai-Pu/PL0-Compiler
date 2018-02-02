package com.maip.table;

import java.util.Vector;

public class symbolTable {
	public int tablePtr = 0;
	public static final int levMax = 3;            //最大允许过程嵌套声明层数[0,levmax]
	public Vector<symbolItem> table=new Vector<symbolItem>();
	public class symbolItem {

	    public static final int con=1;
	    public static final int var=2;
	    public static final int proc=3;
	    public static final int prog=4;
	    public String name;                                            
	    public int type;                                               
	    public int value = 0;                                                
	    public int level;                                                
	    public int address;                                             
	    public int size;                                              

	    public symbolItem() {
	        super();
	        this.name = "";
	    }
	    public symbolItem(String ename,int etype, int elevel, int eaddress) {
	        this.name = ename;
	        this.type=etype;
	        this.level=elevel;
	        this.address=eaddress;
	    }
	}
	
	public symbolItem search(String sname) {
        if (table.size() != 0) {
            for(symbolItem sItem : table)
            {
            	if(sItem.name.equals(sname))
            		return sItem;
            }
        }
        return null;
    }
	
	public int searchindex(String sname) {
        if (table.size() != 0) {
        	int i=0;
            for(symbolItem sItem : table)
            {
            	if(sItem.name.equals(sname))
            		return i;
            	else
            		i++;
            }
        }
        return -1;
    }
	
	public symbolItem get(int index)
	{
		if(index<table.size())
			return table.get(index);
		else
			return null;
	}
	
	public void enter(Keyword ename,int etype, int elevel, int eaddress)
	{
		symbolItem en=new symbolItem(ename.name,etype,elevel,eaddress);
		en.value = ename.num;
		table.add(en);
	}
	
}

