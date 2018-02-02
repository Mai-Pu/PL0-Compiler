package com.maip.table;

import java.awt.EventQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * 目标代码
 */

public class Pcode {
	public Vector<code> P = new Vector<code>();
	private Stack<Integer> runstack = new Stack<Integer>();
	private Stack<Integer> datastack = new Stack<Integer>();
	public class code
	{
		public String F;
		public int l;
		public int a;
		public code(String sf,int sl,int sa)
		{
			F = sf;
			l = sl;
			a = sa;
		}
	}
	public void setP(String sf,int sl,int sa) //添加目标代码
	{
		P.add(new code(sf, sl, sa));
	}
	
	public void runP() //用于在控制台运行并输出
	{
		if(P.size()!=0)
		{
			 Scanner in=new Scanner(System.in);
			 //i :存放当前要执行的代码
			 int T = 0; //指向数据栈STACK的栈顶
			 int B = 0; //存放当前运行过程的数据区在STACK中的起始地址
			 int PP = 0; //存放下一条要执行的指令地址
			 runstack.removeAllElements();
			 datastack.removeAllElements();		
			 boolean sight = true;
			 int mi;
			 for(int i=0;i<P.size();)
			 {
				 //System.out.println(i);
				if(P.get(i).F.equals("JMP"))
				{
					i = P.get(i).a;
				}
				else if (P.get(i).F.equals("INT")) {
					mi = B;
					B = runstack.size();
					for(int j=0;j<P.get(i).a;j++)
						runstack.push(0);
					runstack.set(B, mi);
					runstack.set(B+1,PP);
					i++;
					//System.out.println(runstack);
				}
				else if (P.get(i).F.equals("LIT")) {
					datastack.push(P.get(i).a);
					i++;
				}
				else if (P.get(i).F.equals("WRT")) {
					System.out.println(datastack.pop());
					i++;
				}
				else if (P.get(i).F.equals("OPR")) {
					switch (P.get(i).a) {
					case 0:		
						i = runstack.get(B+1)-1;
						mi =runstack.size() - B;
						B = runstack.get(B);
						for(int j=0;j<mi;j++)
							runstack.pop();
						//System.out.println(runstack);
						break;
					case 1:
						mi = datastack.pop();
						mi = 0 - mi;
						datastack.push(mi);
						break;
					case 2:
						mi = datastack.pop();
						mi = datastack.pop() + mi;
						datastack.push(mi);
						break;
					case 3:
						mi = datastack.pop();
						mi = datastack.pop() - mi;
						datastack.push(mi);
						break;
					case 4:
						mi = datastack.pop();
						mi = datastack.pop() * mi;
						datastack.push(mi);
						break;
					case 5:
						mi = datastack.pop();
						mi = datastack.pop() / mi;
						datastack.push(mi);
						break;
					case 6:
						mi = datastack.pop();
						mi = mi % 2;
						if(mi == 1)
							sight = true;
						else
							sight = false;
						datastack.push(mi);
						break;
					case 8:
						mi = datastack.pop();
						if(datastack.pop() == mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					case 9:
						mi = datastack.pop();
						if(datastack.pop() != mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					case 10:
						mi = datastack.pop();
						if(datastack.pop() < mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					case 11:
						mi = datastack.pop();
						if(datastack.pop() >= mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					case 12:
						mi = datastack.pop();
						if(datastack.pop() > mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					case 13:
						mi = datastack.pop();
						if(datastack.pop() <= mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					default:
						break;
					}
					i++;
				}
				else if (P.get(i).F.equals("RED"))
				{
					mi = B;
					for(int j=0;j<P.get(i).l;j++)
					{
						B = runstack.get(B);
					}
					runstack.set(B+P.get(i).a,in.nextInt());
					B = mi;
					i++;
				}
				else if (P.get(i).F.equals("LOD"))
				{
					mi = B;
					for(int j=0;j<P.get(i).l;j++)
					{
						B = runstack.get(B);
					}
					datastack.push(runstack.get(B+P.get(i).a));
					B = mi;
					i++;
				}
				else if (P.get(i).F.equals("STO"))
				{
					mi = B;
					for(int j=0;j<P.get(i).l;j++)
					{
						B = runstack.get(B);
					}
					runstack.set(B+P.get(i).a,datastack.pop());
					B = mi;
					i++;
				}
				else if (P.get(i).F.equals("JPC"))
				{
					if(sight)
						i++;
					else
					{
						i = P.get(i).a;
					}
				}
				else if (P.get(i).F.equals("CAL"))
				{
					mi = B;
					for(int j=0;j<P.get(i).l;j++)
					{
						B = runstack.get(B);
					}
					B = mi;
					PP = i+1;
					i = P.get(i).a;
				}
			 }
			in.close();
		}
		else 
		{
			System.out.println("无代码可运行");
		}
	}
	public void runP(DefaultTableModel model) //用于在图形化界面运行并输出
	{
		if(P.size()!=0)
		{
			// Scanner in=new Scanner(System.in);
			 //i :存放当前要执行的代码
			 int T = 0; //指向数据栈STACK的栈顶
			 int B = 0; //存放当前运行过程的数据区在STACK中的起始地址
			 int PP = 0; //存放下一条要执行的指令地址
			 runstack.removeAllElements();
			 datastack.removeAllElements();		
			 boolean sight = true;
			 int mi;
			 for(int i=0;i<P.size();)
			 {
				 //System.out.println(i);
				if(P.get(i).F.equals("JMP"))
				{
					i = P.get(i).a;
				}
				else if (P.get(i).F.equals("INT")) {
					mi = B;
					B = runstack.size();
					for(int j=0;j<P.get(i).a;j++)
						runstack.push(0);
					runstack.set(B, mi);
					runstack.set(B+1,PP);
					i++;
				}
				else if (P.get(i).F.equals("LIT")) {
					datastack.push(P.get(i).a);
					i++;
				}
				else if (P.get(i).F.equals("WRT")) {
					Vector vi=new Vector();
					vi.add(datastack.pop());
					model.addRow(vi);
					i++;
				}
				else if (P.get(i).F.equals("OPR")) {
					switch (P.get(i).a) {
					case 0:
						i = runstack.get(B+1)-1;
						B = runstack.get(B);
						break;
					case 1:
						mi = datastack.pop();
						mi = 0 - mi;
						datastack.push(mi);
						break;
					case 2:
						mi = datastack.pop();
						mi = datastack.pop() + mi;
						datastack.push(mi);
						break;
					case 3:
						mi = datastack.pop();
						mi = datastack.pop() - mi;
						datastack.push(mi);
						break;
					case 4:
						mi = datastack.pop();
						mi = datastack.pop() * mi;
						datastack.push(mi);
						break;
					case 5:
						mi = datastack.pop();
						mi = datastack.pop() / mi;
						datastack.push(mi);
						break;
					case 6:
						mi = datastack.pop();
						mi = mi % 2;
						if(mi == 1)
							sight = true;
						else
							sight = false;
						datastack.push(mi);
						break;
					case 8:
						mi = datastack.pop();
						if(datastack.pop() == mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					case 9:
						mi = datastack.pop();
						if(datastack.pop() != mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					case 10:
						mi = datastack.pop();
						if(datastack.pop() < mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					case 11:
						mi = datastack.pop();
						if(datastack.pop() >= mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					case 12:
						mi = datastack.pop();
						if(datastack.pop() > mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					case 13:
						mi = datastack.pop();
						if(datastack.pop() <= mi)
						{
							sight = true;
							datastack.push(1);
						}
						else {
							sight = false;
							datastack.push(0);
						}
						break;
					default:
						break;
					}
					i++;
				}
				else if (P.get(i).F.equals("RED"))
				{
					mi = B;
					for(int j=0;j<P.get(i).l;j++)
					{
						B = runstack.get(B);
					}
					runstack.set(B+P.get(i).a,Integer.parseInt(JOptionPane.showInputDialog("Please input a value")));
					B = mi;
					i++;
				}
				else if (P.get(i).F.equals("LOD"))
				{
					mi = B;
					for(int j=0;j<P.get(i).l;j++)
					{
						B = runstack.get(B);
					}
					datastack.push(runstack.get(B+P.get(i).a));
					B = mi;
					i++;
				}
				else if (P.get(i).F.equals("STO"))
				{
					mi = B;
					for(int j=0;j<P.get(i).l;j++)
					{
						B = runstack.get(B);
					}
					runstack.set(B+P.get(i).a,datastack.pop());
					B = mi;
					i++;
				}
				else if (P.get(i).F.equals("JPC"))
				{
					if(sight)
						i++;
					else
					{
						i = P.get(i).a;
					}
				}
				else if (P.get(i).F.equals("CAL"))
				{
					mi = B;
					for(int j=0;j<P.get(i).l;j++)
					{
						B = runstack.get(B);
					}
					B = mi;
					PP = i+1;
					i = P.get(i).a;
				}
			 }
		}
		else 
		{
			System.out.println("无代码可运行");
		}
	}
}
