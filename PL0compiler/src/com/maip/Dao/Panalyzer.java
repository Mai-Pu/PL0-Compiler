package com.maip.Dao;

import java.awt.event.MouseWheelEvent;
import java.io.PushbackInputStream;
import java.util.BitSet;
import java.util.Stack;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.maip.table.Keyword;
import com.maip.table.Pcode;
import com.maip.table.Pcode.code;
import com.maip.table.symbolTable;
import com.maip.table.symbolTable.symbolItem;

/**
 * 语法分析器
 */

public class Panalyzer {
	
	 private int errorline = 0;
	 private int level=0;
	 private Keyword keyword;                               //符号
	 private Analyzer lex;                              //词法分析器
	 public symbolTable table;                               //符号表
	 public Pcode pcode = new Pcode();
	 private Stack<Integer> stack = new Stack<Integer>();							//符号栈
	 private Stack<Integer> levelstack = new Stack<Integer>();							//层次栈
	 private int dx[]={3,3,3,3};    //每层变量的偏移
	 private int cx[]={0,0,0,0};    //每层代码的初始位置
	 private int px[]={0,0,0,0};    //每层代码id在table中的位置
	 private Vector<Integer> v_1 = new Vector<Integer>();
	 private Vector<Integer> v_2 = new Vector<Integer>();
	 private Vector<Integer> v_3 = new Vector<Integer>();
	 private Vector<Vector<Integer>> vx = new Vector<Vector<Integer>>();
	 //各个非终结符的属性值，从35开始
	 public static final int _prog = 35; 
	 public static final int _block = 36; 
	 public static final int _condecl = 37;  
	 public static final int _const = 38; 
	 public static final int _vardecl = 39; 
	 public static final int _proc = 40; 
	 public static final int _body = 41; 
	 public static final int _statement = 42; 
	 public static final int _lexp =43;
	 public static final int _exp =44;
	 public static final int _trem =45; 
	 public static final int _factor =46;
	 public static final int _lop =47;
	 public static final int _aop =48; 
	 public static final int _mop = 49; 
	 //各个非终结符的first集
	 public static final int []prog_first ={Analyzer.prog}; 
	 public static final int []block_first ={Analyzer.constkey,Analyzer.var,Analyzer.proc,Analyzer.begin}; 
	 public static final int []condecl_first ={Analyzer.constkey}; 
	 public static final int []const_first ={Analyzer.id}; 
	 public static final int []vardecl_first ={Analyzer.var}; 
	 public static final int []proc_first ={Analyzer.proc}; 
	 public static final int []body_first ={Analyzer.begin}; 
	 public static final int []statement_first ={Analyzer.id,Analyzer.ifkey,Analyzer.whilekey,Analyzer.callkey,Analyzer.begin,Analyzer.readkey,Analyzer.whilekey}; 
	 public static final int []lexp_first ={Analyzer.add,Analyzer.sub,Analyzer.id,Analyzer.left,Analyzer.integer,Analyzer.odd}; 
	 public static final int []exp_first ={Analyzer.add,Analyzer.sub,Analyzer.id,Analyzer.left,Analyzer.integer}; 
	 public static final int []trem_first ={Analyzer.id,Analyzer.left,Analyzer.integer}; 
	 public static final int []factor_first ={Analyzer.id,Analyzer.left,Analyzer.integer}; 
	 public static final int []lop_first ={Analyzer.equ,Analyzer.nequ,Analyzer.less,Analyzer.leq,Analyzer.bigger,Analyzer.beq}; 
	 public static final int []aop_first ={Analyzer.add,Analyzer.sub}; 
	 public static final int []mop_first ={Analyzer.mul,Analyzer.div}; 
	 
	 public Panalyzer(String filePath)
	 {
		 lex = new Analyzer(filePath);
		 table = new symbolTable();
		 stack.push(_prog);
		 levelstack.push(0);
	 }
	 public Panalyzer()
	 {
		 lex = new Analyzer();
		 table = new symbolTable();
		 stack.push(_prog);
		 levelstack.push(0);
	 }
	 
	 public Analyzer getLex() {
		return lex;
	}
	public int searchfirst(int []source ,int what) //查找某个关键字是否在某个first集中，用于判断出错等
	 {
		 int index;
		 for(index = 0;index < source.length;index++)
			 if(what == source[index])
				 return index;
		 return -1;
	 }
	 
	 public void nextword()  //获取下一个keyword
	 {
		 keyword = lex.readkeyword();
	 }
	 
	 public void start(int lev , DefaultTableModel model)  //开始进行语法分析，同时进行词法分析以及中间代码生成model用于在图形化界面输出错误原因
	 {
		 vx.add(v_1);
		 vx.add(v_2);
		 vx.add(v_3);
		 int top;
		 nextword();	 
		 do{
			 if (lev > symbolTable.levMax) 
		     {
		         System.out.println("嵌套层数过大");                                         
		     }
			 if(keyword == null || stack.empty()==true)
				 break;
			 top = stack.peek();
			 if(top<34)
			 {
				 if(top == keyword.type)
				 {	 
					 stack.pop();
					 levelstack.pop();
					 nextword();
					 continue;
				}
				 else
				 {
					 nextword();
					 if(model != null)
					 {
						 Vector vi = new Vector();
						 vi.add("line:"+lex.getLinegps());
						 vi.add("终结符不匹配");
						 model.addRow(vi);
					 }
					 else
						 System.out.println("error");
				 }
			 }
			 else
			 {
				 switch(top)
				 {
					 case _prog:
						 if(keyword.type == Analyzer.prog)
						 {
							 stack.pop();
							 levelstack.pop();
							 stack.push(_block);
							 levelstack.push(lev);
							 stack.push(Analyzer.semicolon);
							 levelstack.push(lev);
							 nextword();
							 px[lev] = table.table.size();
							 table.enter(keyword, symbolTable.symbolItem.prog, lev, 0);
							 nextword();
						 }
						 else 
						 {
							if(searchfirst(block_first, keyword.type)!=-1)
							{
								stack.pop();
								levelstack.pop();
								stack.push(_block);
								levelstack.push(lev);
							}
							else
							{
								if(model != null)
								 {
									 Vector vi = new Vector();
									 vi.add("line:"+lex.getLinegps());
									 vi.add("不是一个程序");
									 model.addRow(vi);
								 }
								else
									System.out.println("error");
							}
						 }
						 break;
					 case _block:
						 stack.pop();
						 lev=levelstack.pop();
						 stack.push(_body);
						 levelstack.push(lev);
						 cx[lev] = pcode.P.size();
						 table.table.get(px[lev]).address = pcode.P.size();
						 pcode.setP("JMP", 0, 0);
						 if(keyword.type == Analyzer.constkey)
						 {
							 nextword();
				             constdeclare(lev,model);                        
				             while(keyword.type == Analyzer.comma) {
				                    nextword();
				                    constdeclare(lev,model);
				             }

				            if(keyword.type == Analyzer.semicolon) 
				            {
				                  nextword();
				            } 
				            else 
				            {
				            	if(model != null)
								{
									 Vector vi = new Vector();
									 errorline = lex.getLinegps()+1;
									 vi.add("line:"+errorline);
									 vi.add("缺少分号");
									 model.addRow(vi);
								}
				            	else {
				            		errorline = lex.getLinegps()+1;
		    	                    System.out.println("error:"+errorline+" "+"缺少分号"); 
								}                                     
				            }
						 }
						 if(keyword.type == Analyzer.var)
						 {
							 nextword();
				             vardeclare(lev,model);                            
				             while(keyword.type == Analyzer.comma) 
				             {
				                    nextword();
				                    vardeclare(lev,model);
				             }
				             if(keyword.type == Analyzer.semicolon) 
					         {
					                nextword();
					         } 
				             else 
				             {
				            	 if(model != null)
								 {
									 Vector vi = new Vector();
									 errorline = lex.getLinegps()+1;
									 vi.add("line:"+errorline);
									 vi.add("缺少分号");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
		    	                     System.out.println("error:"+errorline+" "+"缺少分号");  
								}                                 
					         }
						 }
						 if(keyword.type == Analyzer.proc)//如果是procedure
						 {
							 nextword();
							 stack.push(_proc);
							 levelstack.push(lev);
						 }
						break;
					 case _body:
						 lev = levelstack.pop();
						 stack.pop();
						 pcode.P.get(cx[lev]).a = pcode.P.size();
						 pcode.setP("INT", 0, dx[lev]);
						 if(keyword.type == Analyzer.begin)
						 {
							 nextword();
							 statementdeclare(lev,model);
							 while (searchfirst(statement_first, keyword.type)>-1 || keyword.type == Analyzer.semicolon)
							 {
								 if(keyword.type == Analyzer.semicolon)
									 nextword();
								 else 
								 {
									 if(model != null)
									 {
										 Vector vi = new Vector();
										 errorline = lex.getLinegps()+1;
										 vi.add("line:"+errorline);
										 vi.add("缺少分号");
										 model.addRow(vi);
									 }
					            	 else {
					            		 errorline = lex.getLinegps()+1;
			    	                     System.out.println("error:"+errorline+" "+"缺少分号");  
									}  
								 }
								 statementdeclare(lev,model);
							 }
							 table.table.get(px[lev]).size = pcode.P.size() - cx[lev];
							 if(lev > 0)
								 pcode.setP("OPR", 0, 0);
							 if(keyword.type == Analyzer.end)
							 {
								 
								 nextword();
								 if(keyword == null)
								 {
									 /*if(model != null)
									 {
										 Vector vi = new Vector();
										 errorline = lex.getLinegps()+1;
										 vi.add("line:"+errorline);
										 vi.add("缺少分号或者end");
										 model.addRow(vi);
									 }
					            	 else {
					            		 errorline = lex.getLinegps()+1;
			    	                     System.out.println("error:"+errorline+" "+"缺少分号或者end");  
									}  
									 break;*/
								 	break;
								 }
								if(keyword.type == Analyzer.end)
								 {
									if(model != null)
									 {
										 Vector vi = new Vector();
										 errorline = lex.getLinegps()+1;
										 vi.add("line:"+errorline);
										 vi.add("多了一个end");
										 model.addRow(vi);
									 }
					            	 else {
					            		 errorline = lex.getLinegps()+1;
				    	                 System.out.println("error:"+errorline+" "+"多了一个end");
									}  	
								 }
								 if(keyword.type == Analyzer.semicolon )
								 {	
									 nextword();
									 if(keyword.type == Analyzer.proc)
									 {
										 nextword();
										 stack.push(_proc);
										 levelstack.push(lev-1);
									 }
									 else 
									 {
										 if(model != null)
										 {
											 Vector vi = new Vector();
											 errorline = lex.getLinegps()+1;
											 vi.add("line:"+errorline);
											 vi.add("end;后应为同层procedure");
											 model.addRow(vi);
										 }
						            	 else {
						            		 errorline = lex.getLinegps()+1;
					    	                 System.out.println("error:"+errorline+" "+"end;后应为同层procedure");
										}  										 
									 }
								 }
							 }
							 else 
							 {
								 if(model != null)
								 {
									 Vector vi = new Vector();
									 errorline = lex.getLinegps()+1;
									 vi.add("line:"+errorline);
									 vi.add("缺少end");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
			    	                 System.out.println("error:"+errorline+" "+"缺少end");
								}  	
							 }
						 }
						 else 
						 {
							 if(model != null)
							 {
								 Vector vi = new Vector();
								 errorline = lex.getLinegps()+1;
								 vi.add("line:"+errorline);
								 vi.add("缺少end");
								 model.addRow(vi);
							 }
			            	 else {
			            		 errorline = lex.getLinegps()+1;
		    	                 System.out.println("error:"+errorline+" "+"缺少end");
							}  
						 }
						 break;
					 case _proc:
						 stack.pop();
						 lev = levelstack.pop();
						 if (keyword.type == Analyzer.id) 
						 { 
							 if(lev < symbolTable.levMax)
								 px[lev+1] = table.table.size(); 
				             table.enter(keyword, symbolTable.symbolItem.proc, lev, 0);  
				             nextword();
				         } 
				         else 
				          {
				        	 if(model != null)
							 {
								 Vector vi = new Vector();
								 errorline = lex.getLinegps()+1;
								 vi.add("line:"+errorline);
								 vi.add("procedure后面应为标识符");
								 model.addRow(vi);
							 }
			            	 else {
			            		 errorline = lex.getLinegps()+1;
		    	                 System.out.println("error:"+errorline+" "+"procedure后面应为标识符"); 
							}  
				          }
						 if(keyword.type == Analyzer.left)
						 {
							 nextword();
							 if(keyword.type == Analyzer.id)
							 {
								 vx.get(lev).add(dx[lev]);
								 vardeclare(lev,model);                            
								 while(keyword.type == Analyzer.comma) 
								 {
									 nextword();
									 vx.get(lev).add(dx[lev]);
									 vardeclare(lev,model);
								 }
							 }
						 }
						 else 
						 {
							 if(model != null)
							 {
								 Vector vi = new Vector();
								 errorline = lex.getLinegps()+1;
								 vi.add("line:"+errorline);
								 vi.add("缺少左括号");
								 model.addRow(vi);
							 }
			            	 else {
			            		 errorline = lex.getLinegps()+1;
		    	                 System.out.println("error:"+errorline+" "+"缺少左括号"); 
							}  
						 }
						 if(keyword.type == Analyzer.right)
						 {
							 nextword();
						 }
						 else 
						 {
							 if(model != null)
							 {
								 Vector vi = new Vector();
								 errorline = lex.getLinegps()+1;
								 vi.add("line:"+errorline);
								 vi.add("缺少右括号");
								 model.addRow(vi);
							 }
			            	 else {
			            		 errorline = lex.getLinegps()+1;
		    	                 System.out.println("error:"+errorline+" "+"缺少右括号"); 
							}  
						 }
				          if (keyword.type == Analyzer.semicolon)               
				          {
				             nextword();
				          } 
				          else 
				          {
				        	  if(model != null)
								 {
									 Vector vi = new Vector();
									 errorline = lex.getLinegps()+1;
									 vi.add("line:"+errorline);
									 vi.add("缺少分号");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
			    	                 System.out.println("error:"+errorline+" "+"缺少分号"); 
								}
				          }
				          stack.push(_block);   //嵌套层 + 1
				          levelstack.push(lev+1);
						  break;
				 }
			 }
		 }while(true);
		 if(stack.empty()&&keyword==null)
			 System.out.println("分析成功");
	 }
	
	private void statementdeclare(int lev,DefaultTableModel model) { //对statement的类型进行判断，以及进行目标代码翻译
			int index;
	        switch (keyword.type) {
	            case Analyzer.id:
	            	index = table.searchindex(keyword.name);
	            	if (index > -1) 
	            	{
	                    symbolItem item = table.get(index);
	                    if (item.type == symbolItem.var) 
	                    {                            
	                       nextword();
	                       if (keyword.type == Analyzer.setval) 
	                       {
	                            nextword();
	                       } 
	                       else 
	                       {
	                    	   if(model != null)
								 {
									 Vector vi = new Vector();
									 errorline = lex.getLinegps()+1;
									 vi.add("line:"+errorline);
									 vi.add("缺少赋值符号");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
			    	                 System.out.println("error:"+errorline+" "+"缺少赋值符号"); 
								}
	                       }
	                        expression(lev,model);                                         //解析表达式
	                        if((lev - item.level)>-1)
	                        	pcode.setP("STO", lev - item.level, item.address);
	                        else
	                        {
	                        	if(model != null)
								 {
									 Vector vi = new Vector();
									 errorline = lex.getLinegps()+1;
									 vi.add("line:"+errorline);
									 vi.add("不可向高层变量赋值");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
			    	                 System.out.println("error:"+errorline+" "+"不可向高层变量赋值");   
								}                
	                        }
	                    } 
	                    else 
	                    {
	                    	if(model != null)
							 {
								 Vector vi = new Vector();
								 errorline = lex.getLinegps()+1;
								 vi.add("line:"+errorline);
								 vi.add("不可向常量或过程名赋值");
								 model.addRow(vi);
							 }
			            	 else {
			            		 errorline = lex.getLinegps()+1;
		    	                    System.out.println("error:"+errorline+" "+"不可向常量或过程名赋值	");   
							}                    	                                               	
	                    }
	                } 
	            	else 
	            	{
	            		if(model != null)
						 {
							 Vector vi = new Vector();
							 errorline = lex.getLinegps()+1;
							 vi.add("line:"+errorline);
							 vi.add("标识符尚未声明");
							 model.addRow(vi);
						 }
		            	 else {
		            		 errorline = lex.getLinegps()+1;
			                 System.out.println("error:"+errorline+" "+"标识符尚未声明");             
						}   	            		                                            
	                }
	                break;
	            case Analyzer.readkey:
	            	nextword();
	                if (keyword.type == Analyzer.left) 
	                {                                          
	                    index = -1;
	                    do {
	                        nextword();
	                        if (keyword.type == Analyzer.id) 
	                        {
	                            index = table.searchindex(keyword.name);
	                        }
	                        if (index == -1) 
	                        {
	                        	if(model != null)
	   						 	{
	   							 Vector vi = new Vector();
	   							 errorline = lex.getLinegps()+1;
	   							 vi.add("line:"+errorline);
	   							 vi.add("变量尚未声明");
	   							 model.addRow(vi);
	   						 	}
	                        	else {
	   		            		 errorline = lex.getLinegps()+1;
	   			                 System.out.println("error:"+errorline+" "+"变量尚未声明");             
	                        	}                                                                        
	                        } 
	                        else 
	                        {
	                            symbolItem item = table.get(index);
	                            if (item.type != symbolItem.var) 
	                            { 
	                            	if(model != null)
	       						 {
	       							 Vector vi = new Vector();
	       							 errorline = lex.getLinegps()+1;
	       							 vi.add("line:"+errorline);
	       							 vi.add("()中应为变量");
	       							 model.addRow(vi);
	       						 }
	       		            	 else {
	       		            		errorline = lex.getLinegps()+1;
		    	                    System.out.println("error:"+errorline+" "+"()中应为变量");              
	       		            	 	}                             	                                      
	                            } 
	                            else 
	                            {
	                            	if((lev - item.level)>-1)
	                            		pcode.setP("RED", lev - item.level, item.address);
	                            	else
	    	                        {
	    	                        	if(model != null)
	    								 {
	    									 Vector vi = new Vector();
	    									 errorline = lex.getLinegps()+1;
	    									 vi.add("line:"+errorline);
	    									 vi.add("不可向高层变量赋值");
	    									 model.addRow(vi);
	    								 }
	    				            	 else {
	    				            		 errorline = lex.getLinegps()+1;
	    			    	                 System.out.println("error:"+errorline+" "+"不可向高层变量赋值");   
	    								}                
	    	                        }
	                            }
	                        }
	                        nextword();
	                    } while (keyword.type == Analyzer.comma);
	                } 
	                else 
	                {
	                	if(model != null)
  						 {
  							 Vector vi = new Vector();
  							 errorline = lex.getLinegps()+1;
  							 vi.add("line:"+errorline);
  							 vi.add("缺少左括号");
  							 model.addRow(vi);
  						 }
  		            	 else {
  		            		errorline = lex.getLinegps()+1;
  		                    System.out.println("error:"+errorline+" "+"缺少左括号");              
  		            	 }                                                       
	                }
	                if (keyword.type == Analyzer.right) 
	                {
	                    nextword();
	                } 
	                else 
	                {
	                	if(model != null)
 						 {
 							 Vector vi = new Vector();
 							 errorline = lex.getLinegps()+1;
 							 vi.add("line:"+errorline);
 							 vi.add("缺少右括号");
 							 model.addRow(vi);
 						 }
 		            	 else {
 		            		errorline = lex.getLinegps()+1;
 		                    System.out.println("error:"+errorline+" "+"缺少右括号");              
 		            	 }                                                        
	                }
	                break;
	            case Analyzer.writekey:
	            	nextword();
	                if (keyword.type == Analyzer.left) 
	                {
	                    do 
	                    {
	                        nextword();
	                        expression(lev,model);
	                        pcode.setP("WRT", 0, 0);
	                    } while (keyword.type == Analyzer.comma);

	                    if (keyword.type == Analyzer.right) 
	                    {
	                        nextword();
	                    } 
	                    else 
	                    {
	                    	if(model != null)
	 						 {
	 							 Vector vi = new Vector();
	 							 errorline = lex.getLinegps()+1;
	 							 vi.add("line:"+errorline);
	 							 vi.add("缺少右括号");
	 							 model.addRow(vi);
	 						 }
	 		            	 else {
	 		            		errorline = lex.getLinegps()+1;
	 		                    System.out.println("error:"+errorline+" "+"缺少右括号");              
	 		            	 }                                                       
	                    }
	                } 
	                else 
	                {
	                	if(model != null)
						 {
							 Vector vi = new Vector();
							 errorline = lex.getLinegps()+1;
							 vi.add("line:"+errorline);
							 vi.add("缺少左括号");
							 model.addRow(vi);
						 }
		            	 else {
		            		errorline = lex.getLinegps()+1;
		                    System.out.println("error:"+errorline+" "+"缺少左括号");              
		            	 }                                                         
	                }
	                break;
	            case Analyzer.callkey:
	            	nextword();
	                if (keyword.type == Analyzer.id) 
	                {                                 
	                    index = table.searchindex(keyword.name);
	                    if (index > -1) 
	                    {                                                    
	                    	symbolItem item = table.get(index);               
	                        if (item.type == symbolItem.proc) 
	                        {
	                        	pcode.setP("CAL", lev - item.level, item.address);
	                        } 
	                        else 
	                        {
	                        	if(model != null)
	    						 {
	    							 Vector vi = new Vector();
	    							 errorline = lex.getLinegps()+1;
	    							 vi.add("line:"+errorline);
	    							 vi.add("call后标识符应为过程");
	    							 model.addRow(vi);
	    						 }
	    		            	 else {
	    		            		 errorline = lex.getLinegps()+1;
	 	    	                    System.out.println("error:"+errorline+" "+"call后标识符应为过程");             
	    		            	 }                         	               
	                        }
	                    } 
	                    else 
	                    {
	                    	if(model != null)
   						 	{
	                    		Vector vi = new Vector();
	                    		errorline = lex.getLinegps()+1;
	                    		vi.add("line:"+errorline);
	                    		vi.add("此过程尚未定义");
	                    		model.addRow(vi);
   						 	}
	                    	else {
   		            		errorline = lex.getLinegps()+1;
		                    System.out.println("error:"+errorline+" "+"此过程尚未定义");             
	                    	}                     	                       
	                    }
	                    nextword();
	                    if(keyword.type == Analyzer.left)
	                    {
	                    	nextword();
							if(keyword.type == Analyzer.id || keyword.type == Analyzer.integer)
							{
								System.out.println("1231321321");
								 int sum = 0;
								 if(sum < vx.get(lev).size())
								 {
									int now = pcode.P.size()-1;
								 	String nows = new String(pcode.P.get(now).F);
								 	int nowl = pcode.P.get(now).l;
								 	int nowa = pcode.P.get(now).a;
								 	pcode.P.remove(now);
								 	expression(lev,model);
								 	pcode.setP("STO", 0, vx.get(lev).get(sum));	
								 	sum++;
								 	while(keyword.type == Analyzer.comma) 
								 	{
								 		nextword();
								 		if(sum < vx.get(lev).size())
								 		{
								 			expression(lev,model);
								 			pcode.setP("STO", 0, vx.get(lev).get(sum));	
								 			sum++;
								 		}
								 		else {
								 			if(model != null)
				   						 	{
					                    		Vector vi = new Vector();
					                    		errorline = lex.getLinegps()+1;
					                    		vi.add("line:"+errorline);
					                    		vi.add("该过程参数过少");
					                    		model.addRow(vi);
				   						 	}
					                    	else {
				   		            		errorline = lex.getLinegps()+1;
						                    System.out.println("error:"+errorline+" "+"该过程参数过少");             
					                    	}  
										}
								 	}
								 	pcode.setP(nows, nowl, nowa);
								 }
								 else {
									 if(model != null)
			   						 	{
				                    		Vector vi = new Vector();
				                    		errorline = lex.getLinegps()+1;
				                    		vi.add("line:"+errorline);
				                    		vi.add("该过程没有参数");
				                    		model.addRow(vi);
			   						 	}
				                    	else {
			   		            		errorline = lex.getLinegps()+1;
					                    System.out.println("error:"+errorline+" "+"该过程没有参数");             
				                    	}   
								}
							}
	                    }
	                    else 
		                {
	                    	if(model != null)
   						 	{
	                    		Vector vi = new Vector();
	                    		errorline = lex.getLinegps()+1;
	                    		vi.add("line:"+errorline);
	                    		vi.add("缺少左括号");
	                    		model.addRow(vi);
   						 	}
	                    	else {
   		            		errorline = lex.getLinegps()+1;
		                    System.out.println("error:"+errorline+" "+"缺少左括号");             
	                    	}                                                       
		                }
	                    if(keyword.type == Analyzer.right)
	                    {
	                    	nextword();
	                    }
	                    else 
		                {
	                    	if(model != null)
   						 	{
	                    		Vector vi = new Vector();
	                    		errorline = lex.getLinegps()+1;
	                    		vi.add("line:"+errorline);
	                    		vi.add("缺少右括号");
	                    		model.addRow(vi);
   						 	}
	                    	else {
   		            		errorline = lex.getLinegps()+1;
		                    System.out.println("error:"+errorline+" "+"缺少右括号");             
	                    	}                                                    
		                }
	                } 
	                else 
	                {
	                	if(model != null)
						 	{
                    		Vector vi = new Vector();
                    		errorline = lex.getLinegps()+1;
                    		vi.add("line:"+errorline);
                    		vi.add("call后应为标识符");
                    		model.addRow(vi);
						 	}
                    	else {
                    		errorline = lex.getLinegps()+1;
    	                    System.out.println("error:"+errorline+" "+"call后应为标识符");               
                    	}                	                
	                }
	                break;
	            case Analyzer.ifkey:
	            	nextword();   
	                panduan(lev,model);
	                int codegps = pcode.P.size();
	                pcode.setP("JPC", 0, 0);
	                if (keyword.type == Analyzer.thenkey) 
	                {
	                    nextword();
	                } 
	                else 
	                {
	                	if(model != null)
					 	{
                		Vector vi = new Vector();
                		errorline = lex.getLinegps()+1;
                		vi.add("line:"+errorline);
                		vi.add("缺少then");
                		model.addRow(vi);
					 	}
	                	else {
	                		errorline = lex.getLinegps()+1;
	                		System.out.println("error:"+errorline+" "+"缺少then");               
	                	}                                  
	                }                    
	                statementdeclare(lev,model); 
	                int codegps2;
	                if (keyword.type == Analyzer.elsekey) 
	                {
	                	codegps2 = pcode.P.size();
	                	pcode.setP("JMP", 0, 0);
	                	pcode.P.get(codegps).a = pcode.P.size();
	                    nextword();
	                    statementdeclare(lev,model); 
	                    pcode.P.get(codegps2).a = pcode.P.size();
	                }
	                else 
	                {
						pcode.P.get(codegps).a = pcode.P.size();
					}
	                break;
	            case Analyzer.begin:
	            	 nextword();
	                 statementdeclare(lev,model);
	                 while (searchfirst(statement_first, keyword.type)>-1 || keyword.type == Analyzer.semicolon) 
	                 {
	                     if (keyword.type == Analyzer.semicolon) 
	                     {
	                         nextword();
	                     } 
	                     else 
	                     {
	                    	 if(model != null)
							 	{
	                    		Vector vi = new Vector();
	                    		errorline = lex.getLinegps()+1;
	                    		vi.add("line:"+errorline);
	                    		vi.add("缺少分号");
	                    		model.addRow(vi);
							 	}
	                    	else {
	                    		errorline = lex.getLinegps()+1;
	    	                    System.out.println("error:"+errorline+" "+"缺少分号");               
	                    	}                               
	                     }
	                     statementdeclare(lev,model);
	                 }
	                 if (keyword.type == Analyzer.end)
	                 {
	                     nextword();
	                 }
	                 else 
	                 {
	                	 if(model != null)
						 {
	                		 Vector vi = new Vector();
	                		 errorline = lex.getLinegps()+1;
	                		 vi.add("line:"+errorline);
	                		 vi.add("缺少end");
	                		 model.addRow(vi);
						 }
	                	 else {
	                		 errorline = lex.getLinegps()+1;
	                		 System.out.println("error:"+errorline+" "+"缺少end");               
	                	 }                          
	                 }
	                break;
	            case Analyzer.whilekey:
	            	int codegps3 = pcode.P.size();                               
	                nextword();
	                panduan(lev,model);                           
	                int codegps4 = pcode.P.size();                               //跳出指令的地址
	                pcode.setP("JPC", 0, 0);
	                if (keyword.type == Analyzer.dokey) {
	                    nextword();
	                } 
	                else 
	                {
	                	if(model != null)
						 {
	                		 Vector vi = new Vector();
	                		 errorline = lex.getLinegps()+1;
	                		 vi.add("line:"+errorline);
	                		 vi.add("缺少do");
	                		 model.addRow(vi);
						 }
	                	 else {
	                		 errorline = lex.getLinegps()+1;
	                		 System.out.println("error:"+errorline+" "+"缺少do");               
	                	 }   
	                }
	                statementdeclare(lev,model); 
	                pcode.setP("JMP", 0, codegps3);
	                pcode.P.get(codegps4).a = pcode.P.size();
	                break;
	            default:
	            	if(model != null)
					 {
               		 	Vector vi = new Vector();
               		 	errorline = lex.getLinegps()+1;
               		 	vi.add("line:"+errorline);
               		 	vi.add("关键字不存在");
               		 	model.addRow(vi);
					 }
	            	else {
               		 	errorline = lex.getLinegps()+1;
               		 System.out.println("error:"+errorline+" "+"关键字不存在");              
               	 	}    
	                break;
	     }
	}
	private void panduan(int lev,DefaultTableModel model) { //用于条件判断，并生成相应的目标代码
		if (keyword.type == Analyzer.odd) 
		{                        
            nextword();
            expression(lev,model);    
            pcode.setP("OPR",0,6);                        
        } 
		else 
		{                                                           
            expression(lev,model);
            if (searchfirst(lop_first, keyword.type)>-1) 
            {
                int rOperator = keyword.type;                                                  
                nextword();
                expression(lev,model);
                pcode.setP("OPR", 0, rOperator-12);
            } 
            else 
            {
            	if(model != null)
				 {
          		 	Vector vi = new Vector();
          		 	errorline = lex.getLinegps()+1;
          		 	vi.add("line:"+errorline);
          		 	vi.add("缺少关系运算符");
          		 	model.addRow(vi);
				 }
            	else {
            		errorline = lex.getLinegps()+1;
                    System.out.println("error:"+errorline+" "+"缺少关系运算符");             
          	 	}             	                         
            }
        }
	}
	private void expression(int lev,DefaultTableModel model) {   //进行表达式的运算以及生成相应的目标代码
		if (keyword.type == Analyzer.add || keyword.type == Analyzer.sub) {            
            int Operator = keyword.type;
            nextword();
            term(lev,model);
            if (Operator == Analyzer.sub) 
            {
            	pcode.setP("OPR", 0, 1);
            }
        } 
		else 
		{
            term(lev,model);
        }
        while (keyword.type == Analyzer.add || keyword.type == Analyzer.sub) {
            int Operator = keyword.type;
            nextword();
            term(lev,model);
            pcode.setP("OPR", 0, Operator-16);
        }
	}
	
	private void term(int lev,DefaultTableModel model) {   //乘除运算
        factor(lev,model);                                                                                        
        while (keyword.type == Analyzer.mul || keyword.type == Analyzer.div) {
            int Operator = keyword.type;                                                                 
            nextword();
            factor(lev,model);
            pcode.setP("OPR",0, Operator-22);                                    
        }
	}
	
	private void factor(int lev,DefaultTableModel model) { //对每种因子进行相应的处理并生成目标代码
        if (searchfirst(factor_first, keyword.type)!=-1) 
        {
            if (keyword.type == Analyzer.id) 
            {                            
                int index = table.searchindex(keyword.name);
                if (index > -1) 
                {                                               
                    symbolItem item = table.get(index);
                    switch (item.type) {
                        case symbolItem.con: 
                        	pcode.setP("LIT", 0, item.value);
                            break;
                        case symbolItem.var:
                        	if((lev - item.level)>-1)
                        		pcode.setP("LOD", lev - item.level, item.address);
                        	else
	                        {
	                        	if(model != null)
								 {
									 Vector vi = new Vector();
									 errorline = lex.getLinegps()+1;
									 vi.add("line:"+errorline);
									 vi.add("不可使用高层变量");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
			    	                 System.out.println("error:"+errorline+" "+"不可使用高层变量");   
								}                
	                        }
                            break;
                        case symbolItem.proc:  
                        	if(model != null)
           				 	{
                     		 	Vector vi = new Vector();
                     		 	errorline = lex.getLinegps()+1;
                     		 	vi.add("line:"+errorline);
                     		 	vi.add("标识符不能为过程");
                     		 	model.addRow(vi);
           				 	}
                        	else {
                        		errorline = lex.getLinegps()+1;
                                System.out.println("error:"+errorline+" "+"标识符不能为过程");           
                     	 	}                                  
                            break;
                    }
                } 
                else {
                	if(model != null)
   				 	{
             		 	Vector vi = new Vector();
             		 	errorline = lex.getLinegps()+1;
             		 	vi.add("line:"+errorline);
             		 	vi.add("未声明的标识符");
             		 	model.addRow(vi);
   				 	}
                	else {
                		errorline = lex.getLinegps()+1;
                        System.out.println("error:"+errorline+" "+"未声明的标识符");            
             	 	}     
                }
                nextword();
            } 
            else if (keyword.type == Analyzer.integer) { 
            	pcode.setP("LIT", 0, keyword.num);
                nextword();
            } 
            else if (keyword.type == Analyzer.left)  
            {                 
                nextword();
                expression(lev,model);
                if (keyword.type == Analyzer.right) 
                {
                    nextword();
                } 
                else 
                {
                	if(model != null)
   				 	{
             		 	Vector vi = new Vector();
             		 	errorline = lex.getLinegps()+1;
             		 	vi.add("line:"+errorline);
             		 	vi.add("缺少右括号");
             		 	model.addRow(vi);
   				 	}
                	else {
                		errorline = lex.getLinegps()+1;
                        System.out.println("error:"+errorline+" "+"缺少右括号");               
             	 	}                                                
                }
            } 																		 
        }
	}
	
	private void vardeclare(int lev,DefaultTableModel model) {  //变量声明
		if (keyword.type == Analyzer.id) 
		{
            table.enter(keyword, symbolTable.symbolItem.var, lev, dx[lev]);
            dx[lev]++;
            nextword();
        } 
		else {
			if(model != null)
			 	{
     		 	Vector vi = new Vector();
     		 	errorline = lex.getLinegps()+1;
     		 	vi.add("line:"+errorline);
     		 	vi.add("var后应是标识符");
     		 	model.addRow(vi);
			 	}
        	else {
        		errorline = lex.getLinegps()+1;
                System.out.println("error:"+errorline+" "+"var后应是标识符");              
     	 	}
			nextword();
        }
	}
	private void constdeclare(int lev,DefaultTableModel model) {  //常量声明
		if (keyword.type == Analyzer.id) 
		{                                                 
            String id = keyword.name;													//先保存起来
            nextword();
            if (keyword.type == Analyzer.equ || keyword.type == Analyzer.setval) 
            {     																			//等于或者赋值符号
                if (keyword.type == Analyzer.setval) 
                {
                	if(model != null)
    			 	{
                		Vector vi = new Vector();
         		 		errorline = lex.getLinegps()+1;
         		 		vi.add("line:"+errorline);
         		 		vi.add("此处应为‘=’");
         		 		model.addRow(vi);
    			 	}
                	else {
                		errorline = lex.getLinegps()+1;
                        System.out.println("error:"+errorline+" "+"此处应为‘=’");               
                	}               	                                                          
                }
                nextword();																	  
                if (keyword.type == Analyzer.integer) 
                {
                    keyword.name = id;
                    table.enter(keyword, symbolTable.symbolItem.con, lev, dx[lev]);				//将常量填入符号表
                    nextword();
                } 
                else 
                {
                	if(model != null)
    			 	{
                		Vector vi = new Vector();
         		 		errorline = lex.getLinegps()+1;
         		 		vi.add("line:"+errorline);
         		 		vi.add("=后面应该是数字");
         		 		model.addRow(vi);
    			 	}
                	else {
                		errorline = lex.getLinegps()+1;
                        System.out.println("error:"+errorline+" "+"=后面应该是数字");               
                	}           	                                               
                }
            } 
            else {
            	if(model != null)
			 	{
            		Vector vi = new Vector();
     		 		errorline = lex.getLinegps()+1;
     		 		vi.add("line:"+errorline);
     		 		vi.add("常量说明标志后应是=");
     		 		model.addRow(vi);
			 	}
            	else {
            		errorline = lex.getLinegps()+1;
                    System.out.println("error:"+errorline+" "+"常量说明标志后应是=");                
            	}            	                                                   
            }
        }
		else 
		{
			if(model != null)
		 	{
        		Vector vi = new Vector();
 		 		errorline = lex.getLinegps()+1;
 		 		vi.add("line:"+errorline);
 		 		vi.add("const后应是标识符");
 		 		model.addRow(vi);
		 	}
        	else {
        		errorline = lex.getLinegps()+1;
                System.out.println("error:"+errorline+" "+"const后应是标识符");               
        	}                                                               
        }
	}
	 
}
