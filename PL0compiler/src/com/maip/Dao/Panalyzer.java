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
 * �﷨������
 */

public class Panalyzer {
	
	 private int errorline = 0;
	 private int level=0;
	 private Keyword keyword;                               //����
	 private Analyzer lex;                              //�ʷ�������
	 public symbolTable table;                               //���ű�
	 public Pcode pcode = new Pcode();
	 private Stack<Integer> stack = new Stack<Integer>();							//����ջ
	 private Stack<Integer> levelstack = new Stack<Integer>();							//���ջ
	 private int dx[]={3,3,3,3};    //ÿ�������ƫ��
	 private int cx[]={0,0,0,0};    //ÿ�����ĳ�ʼλ��
	 private int px[]={0,0,0,0};    //ÿ�����id��table�е�λ��
	 private Vector<Integer> v_1 = new Vector<Integer>();
	 private Vector<Integer> v_2 = new Vector<Integer>();
	 private Vector<Integer> v_3 = new Vector<Integer>();
	 private Vector<Vector<Integer>> vx = new Vector<Vector<Integer>>();
	 //�������ս��������ֵ����35��ʼ
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
	 //�������ս����first��
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
	public int searchfirst(int []source ,int what) //����ĳ���ؼ����Ƿ���ĳ��first���У������жϳ����
	 {
		 int index;
		 for(index = 0;index < source.length;index++)
			 if(what == source[index])
				 return index;
		 return -1;
	 }
	 
	 public void nextword()  //��ȡ��һ��keyword
	 {
		 keyword = lex.readkeyword();
	 }
	 
	 public void start(int lev , DefaultTableModel model)  //��ʼ�����﷨������ͬʱ���дʷ������Լ��м��������model������ͼ�λ������������ԭ��
	 {
		 vx.add(v_1);
		 vx.add(v_2);
		 vx.add(v_3);
		 int top;
		 nextword();	 
		 do{
			 if (lev > symbolTable.levMax) 
		     {
		         System.out.println("Ƕ�ײ�������");                                         
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
						 vi.add("�ս����ƥ��");
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
									 vi.add("����һ������");
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
									 vi.add("ȱ�ٷֺ�");
									 model.addRow(vi);
								}
				            	else {
				            		errorline = lex.getLinegps()+1;
		    	                    System.out.println("error:"+errorline+" "+"ȱ�ٷֺ�"); 
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
									 vi.add("ȱ�ٷֺ�");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
		    	                     System.out.println("error:"+errorline+" "+"ȱ�ٷֺ�");  
								}                                 
					         }
						 }
						 if(keyword.type == Analyzer.proc)//�����procedure
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
										 vi.add("ȱ�ٷֺ�");
										 model.addRow(vi);
									 }
					            	 else {
					            		 errorline = lex.getLinegps()+1;
			    	                     System.out.println("error:"+errorline+" "+"ȱ�ٷֺ�");  
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
										 vi.add("ȱ�ٷֺŻ���end");
										 model.addRow(vi);
									 }
					            	 else {
					            		 errorline = lex.getLinegps()+1;
			    	                     System.out.println("error:"+errorline+" "+"ȱ�ٷֺŻ���end");  
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
										 vi.add("����һ��end");
										 model.addRow(vi);
									 }
					            	 else {
					            		 errorline = lex.getLinegps()+1;
				    	                 System.out.println("error:"+errorline+" "+"����һ��end");
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
											 vi.add("end;��ӦΪͬ��procedure");
											 model.addRow(vi);
										 }
						            	 else {
						            		 errorline = lex.getLinegps()+1;
					    	                 System.out.println("error:"+errorline+" "+"end;��ӦΪͬ��procedure");
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
									 vi.add("ȱ��end");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
			    	                 System.out.println("error:"+errorline+" "+"ȱ��end");
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
								 vi.add("ȱ��end");
								 model.addRow(vi);
							 }
			            	 else {
			            		 errorline = lex.getLinegps()+1;
		    	                 System.out.println("error:"+errorline+" "+"ȱ��end");
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
								 vi.add("procedure����ӦΪ��ʶ��");
								 model.addRow(vi);
							 }
			            	 else {
			            		 errorline = lex.getLinegps()+1;
		    	                 System.out.println("error:"+errorline+" "+"procedure����ӦΪ��ʶ��"); 
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
								 vi.add("ȱ��������");
								 model.addRow(vi);
							 }
			            	 else {
			            		 errorline = lex.getLinegps()+1;
		    	                 System.out.println("error:"+errorline+" "+"ȱ��������"); 
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
								 vi.add("ȱ��������");
								 model.addRow(vi);
							 }
			            	 else {
			            		 errorline = lex.getLinegps()+1;
		    	                 System.out.println("error:"+errorline+" "+"ȱ��������"); 
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
									 vi.add("ȱ�ٷֺ�");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
			    	                 System.out.println("error:"+errorline+" "+"ȱ�ٷֺ�"); 
								}
				          }
				          stack.push(_block);   //Ƕ�ײ� + 1
				          levelstack.push(lev+1);
						  break;
				 }
			 }
		 }while(true);
		 if(stack.empty()&&keyword==null)
			 System.out.println("�����ɹ�");
	 }
	
	private void statementdeclare(int lev,DefaultTableModel model) { //��statement�����ͽ����жϣ��Լ�����Ŀ����뷭��
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
									 vi.add("ȱ�ٸ�ֵ����");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
			    	                 System.out.println("error:"+errorline+" "+"ȱ�ٸ�ֵ����"); 
								}
	                       }
	                        expression(lev,model);                                         //�������ʽ
	                        if((lev - item.level)>-1)
	                        	pcode.setP("STO", lev - item.level, item.address);
	                        else
	                        {
	                        	if(model != null)
								 {
									 Vector vi = new Vector();
									 errorline = lex.getLinegps()+1;
									 vi.add("line:"+errorline);
									 vi.add("������߲������ֵ");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
			    	                 System.out.println("error:"+errorline+" "+"������߲������ֵ");   
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
								 vi.add("�����������������ֵ");
								 model.addRow(vi);
							 }
			            	 else {
			            		 errorline = lex.getLinegps()+1;
		    	                    System.out.println("error:"+errorline+" "+"�����������������ֵ	");   
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
							 vi.add("��ʶ����δ����");
							 model.addRow(vi);
						 }
		            	 else {
		            		 errorline = lex.getLinegps()+1;
			                 System.out.println("error:"+errorline+" "+"��ʶ����δ����");             
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
	   							 vi.add("������δ����");
	   							 model.addRow(vi);
	   						 	}
	                        	else {
	   		            		 errorline = lex.getLinegps()+1;
	   			                 System.out.println("error:"+errorline+" "+"������δ����");             
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
	       							 vi.add("()��ӦΪ����");
	       							 model.addRow(vi);
	       						 }
	       		            	 else {
	       		            		errorline = lex.getLinegps()+1;
		    	                    System.out.println("error:"+errorline+" "+"()��ӦΪ����");              
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
	    									 vi.add("������߲������ֵ");
	    									 model.addRow(vi);
	    								 }
	    				            	 else {
	    				            		 errorline = lex.getLinegps()+1;
	    			    	                 System.out.println("error:"+errorline+" "+"������߲������ֵ");   
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
  							 vi.add("ȱ��������");
  							 model.addRow(vi);
  						 }
  		            	 else {
  		            		errorline = lex.getLinegps()+1;
  		                    System.out.println("error:"+errorline+" "+"ȱ��������");              
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
 							 vi.add("ȱ��������");
 							 model.addRow(vi);
 						 }
 		            	 else {
 		            		errorline = lex.getLinegps()+1;
 		                    System.out.println("error:"+errorline+" "+"ȱ��������");              
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
	 							 vi.add("ȱ��������");
	 							 model.addRow(vi);
	 						 }
	 		            	 else {
	 		            		errorline = lex.getLinegps()+1;
	 		                    System.out.println("error:"+errorline+" "+"ȱ��������");              
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
							 vi.add("ȱ��������");
							 model.addRow(vi);
						 }
		            	 else {
		            		errorline = lex.getLinegps()+1;
		                    System.out.println("error:"+errorline+" "+"ȱ��������");              
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
	    							 vi.add("call���ʶ��ӦΪ����");
	    							 model.addRow(vi);
	    						 }
	    		            	 else {
	    		            		 errorline = lex.getLinegps()+1;
	 	    	                    System.out.println("error:"+errorline+" "+"call���ʶ��ӦΪ����");             
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
	                    		vi.add("�˹�����δ����");
	                    		model.addRow(vi);
   						 	}
	                    	else {
   		            		errorline = lex.getLinegps()+1;
		                    System.out.println("error:"+errorline+" "+"�˹�����δ����");             
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
					                    		vi.add("�ù��̲�������");
					                    		model.addRow(vi);
				   						 	}
					                    	else {
				   		            		errorline = lex.getLinegps()+1;
						                    System.out.println("error:"+errorline+" "+"�ù��̲�������");             
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
				                    		vi.add("�ù���û�в���");
				                    		model.addRow(vi);
			   						 	}
				                    	else {
			   		            		errorline = lex.getLinegps()+1;
					                    System.out.println("error:"+errorline+" "+"�ù���û�в���");             
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
	                    		vi.add("ȱ��������");
	                    		model.addRow(vi);
   						 	}
	                    	else {
   		            		errorline = lex.getLinegps()+1;
		                    System.out.println("error:"+errorline+" "+"ȱ��������");             
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
	                    		vi.add("ȱ��������");
	                    		model.addRow(vi);
   						 	}
	                    	else {
   		            		errorline = lex.getLinegps()+1;
		                    System.out.println("error:"+errorline+" "+"ȱ��������");             
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
                    		vi.add("call��ӦΪ��ʶ��");
                    		model.addRow(vi);
						 	}
                    	else {
                    		errorline = lex.getLinegps()+1;
    	                    System.out.println("error:"+errorline+" "+"call��ӦΪ��ʶ��");               
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
                		vi.add("ȱ��then");
                		model.addRow(vi);
					 	}
	                	else {
	                		errorline = lex.getLinegps()+1;
	                		System.out.println("error:"+errorline+" "+"ȱ��then");               
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
	                    		vi.add("ȱ�ٷֺ�");
	                    		model.addRow(vi);
							 	}
	                    	else {
	                    		errorline = lex.getLinegps()+1;
	    	                    System.out.println("error:"+errorline+" "+"ȱ�ٷֺ�");               
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
	                		 vi.add("ȱ��end");
	                		 model.addRow(vi);
						 }
	                	 else {
	                		 errorline = lex.getLinegps()+1;
	                		 System.out.println("error:"+errorline+" "+"ȱ��end");               
	                	 }                          
	                 }
	                break;
	            case Analyzer.whilekey:
	            	int codegps3 = pcode.P.size();                               
	                nextword();
	                panduan(lev,model);                           
	                int codegps4 = pcode.P.size();                               //����ָ��ĵ�ַ
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
	                		 vi.add("ȱ��do");
	                		 model.addRow(vi);
						 }
	                	 else {
	                		 errorline = lex.getLinegps()+1;
	                		 System.out.println("error:"+errorline+" "+"ȱ��do");               
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
               		 	vi.add("�ؼ��ֲ�����");
               		 	model.addRow(vi);
					 }
	            	else {
               		 	errorline = lex.getLinegps()+1;
               		 System.out.println("error:"+errorline+" "+"�ؼ��ֲ�����");              
               	 	}    
	                break;
	     }
	}
	private void panduan(int lev,DefaultTableModel model) { //���������жϣ���������Ӧ��Ŀ�����
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
          		 	vi.add("ȱ�ٹ�ϵ�����");
          		 	model.addRow(vi);
				 }
            	else {
            		errorline = lex.getLinegps()+1;
                    System.out.println("error:"+errorline+" "+"ȱ�ٹ�ϵ�����");             
          	 	}             	                         
            }
        }
	}
	private void expression(int lev,DefaultTableModel model) {   //���б��ʽ�������Լ�������Ӧ��Ŀ�����
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
	
	private void term(int lev,DefaultTableModel model) {   //�˳�����
        factor(lev,model);                                                                                        
        while (keyword.type == Analyzer.mul || keyword.type == Analyzer.div) {
            int Operator = keyword.type;                                                                 
            nextword();
            factor(lev,model);
            pcode.setP("OPR",0, Operator-22);                                    
        }
	}
	
	private void factor(int lev,DefaultTableModel model) { //��ÿ�����ӽ�����Ӧ�Ĵ�������Ŀ�����
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
									 vi.add("����ʹ�ø߲����");
									 model.addRow(vi);
								 }
				            	 else {
				            		 errorline = lex.getLinegps()+1;
			    	                 System.out.println("error:"+errorline+" "+"����ʹ�ø߲����");   
								}                
	                        }
                            break;
                        case symbolItem.proc:  
                        	if(model != null)
           				 	{
                     		 	Vector vi = new Vector();
                     		 	errorline = lex.getLinegps()+1;
                     		 	vi.add("line:"+errorline);
                     		 	vi.add("��ʶ������Ϊ����");
                     		 	model.addRow(vi);
           				 	}
                        	else {
                        		errorline = lex.getLinegps()+1;
                                System.out.println("error:"+errorline+" "+"��ʶ������Ϊ����");           
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
             		 	vi.add("δ�����ı�ʶ��");
             		 	model.addRow(vi);
   				 	}
                	else {
                		errorline = lex.getLinegps()+1;
                        System.out.println("error:"+errorline+" "+"δ�����ı�ʶ��");            
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
             		 	vi.add("ȱ��������");
             		 	model.addRow(vi);
   				 	}
                	else {
                		errorline = lex.getLinegps()+1;
                        System.out.println("error:"+errorline+" "+"ȱ��������");               
             	 	}                                                
                }
            } 																		 
        }
	}
	
	private void vardeclare(int lev,DefaultTableModel model) {  //��������
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
     		 	vi.add("var��Ӧ�Ǳ�ʶ��");
     		 	model.addRow(vi);
			 	}
        	else {
        		errorline = lex.getLinegps()+1;
                System.out.println("error:"+errorline+" "+"var��Ӧ�Ǳ�ʶ��");              
     	 	}
			nextword();
        }
	}
	private void constdeclare(int lev,DefaultTableModel model) {  //��������
		if (keyword.type == Analyzer.id) 
		{                                                 
            String id = keyword.name;													//�ȱ�������
            nextword();
            if (keyword.type == Analyzer.equ || keyword.type == Analyzer.setval) 
            {     																			//���ڻ��߸�ֵ����
                if (keyword.type == Analyzer.setval) 
                {
                	if(model != null)
    			 	{
                		Vector vi = new Vector();
         		 		errorline = lex.getLinegps()+1;
         		 		vi.add("line:"+errorline);
         		 		vi.add("�˴�ӦΪ��=��");
         		 		model.addRow(vi);
    			 	}
                	else {
                		errorline = lex.getLinegps()+1;
                        System.out.println("error:"+errorline+" "+"�˴�ӦΪ��=��");               
                	}               	                                                          
                }
                nextword();																	  
                if (keyword.type == Analyzer.integer) 
                {
                    keyword.name = id;
                    table.enter(keyword, symbolTable.symbolItem.con, lev, dx[lev]);				//������������ű�
                    nextword();
                } 
                else 
                {
                	if(model != null)
    			 	{
                		Vector vi = new Vector();
         		 		errorline = lex.getLinegps()+1;
         		 		vi.add("line:"+errorline);
         		 		vi.add("=����Ӧ��������");
         		 		model.addRow(vi);
    			 	}
                	else {
                		errorline = lex.getLinegps()+1;
                        System.out.println("error:"+errorline+" "+"=����Ӧ��������");               
                	}           	                                               
                }
            } 
            else {
            	if(model != null)
			 	{
            		Vector vi = new Vector();
     		 		errorline = lex.getLinegps()+1;
     		 		vi.add("line:"+errorline);
     		 		vi.add("����˵����־��Ӧ��=");
     		 		model.addRow(vi);
			 	}
            	else {
            		errorline = lex.getLinegps()+1;
                    System.out.println("error:"+errorline+" "+"����˵����־��Ӧ��=");                
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
 		 		vi.add("const��Ӧ�Ǳ�ʶ��");
 		 		model.addRow(vi);
		 	}
        	else {
        		errorline = lex.getLinegps()+1;
                System.out.println("error:"+errorline+" "+"const��Ӧ�Ǳ�ʶ��");               
        	}                                                               
        }
	}
	 
}
