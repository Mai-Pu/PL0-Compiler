## PL/0编译器

### 1.PL/0语言的BNF描述（扩充的巴克斯范式表示法）
```
<prog> → program <id>；<block> 
<block> → [<condecl>][<vardecl>][<proc>]<body>
<condecl> → const <const>{,<const>}
<const> → <id>:=<integer>
<vardecl> → var <id>{,<id>}
<proc> → procedure <id>（[<id>{,<id>}]）;<block>{;<proc>}
<body> → begin <statement>{;<statement>}end
<statement> → <id> := <exp>               
|if <lexp> then <statement>[else <statement>]
               |while <lexp> do <statement>
               |call <id>[（<exp>{,<exp>}）]
               |<body>
               |read (<id>{，<id>})
               |write (<exp>{,<exp>})
<lexp> → <exp> <lop> <exp>|odd <exp>
<exp> → [+|-]<term>{<aop><term>}
<term> → <factor>{<mop><factor>}
<factor>→<id>|<integer>|(<exp>)
<lop> → =|<>|<|<=|>|>=
<aop> → +|-
<mop> → *|/
<id> → l{l|d}   （注：l表示字母）
<integer> → d{d}

注释：
<prog>：程序 ；<block>：块、程序体 ；<condecl>：常量说明 ；<const>：常量；
<vardecl>：变量说明 ；<proc>：分程序 ； <body>：复合语句 ；<statement>：语句；
<exp>：表达式 ；<lexp>：条件 ；<term>：项 ； <factor>：因子 ；<aop>：加法运算符；
<mop>：乘法运算符； <lop>：关系运算符。
```

### 2.系统设计
##### 2.1系统的总体结构
![](https://github.com/PL0-Compiler/Shell/raw/master/pic/1.png)

## mycp.c
![](https://github.com/Mai-Pu/Shell/raw/master/pic/3.png)

## mysys.c
##### 实现函数mysys，用于执行一个系统命令，要求如下
*	mysys的功能与系统函数system相同，要求用进程管理相关系统调用自己实现一遍
*	使用fork/exec/wait系统调用实现mysys
*	不能通过调用系统函数system实现mysys
*	测试程序如下
```ruby
	#include <stdio.h>
	
	int main()
	{
	    printf("++++++++++++++++++++++++++++++++++++++++++++++++++\n");
	    mysys("echo HELLO WORLD");
	    printf("++++++++++++++++++++++++++++++++++++++++++++++++++\n");
	    system("ls /");
	    mysys("++++++++++++++++++++++++++++++++++++++++++++++++++\n");
	    return 0;
	}
```	
*	测试程序的输出结果
*	++++++++++++++++++++++++++++++++++++++++++++++++++
*	HELLO WORLD
*	++++++++++++++++++++++++++++++++++++++++++++++++++
*	bin		core  home	lib	mnt	root	snap	tmp		vmlinuz
*	boot	dev   initrd.img	lost+found	opt	run	srv	usr	vmlinuz.old
*	cdrom	etc   initrd.img.old	media	proc	sbin	sys	var
*	++++++++++++++++++++++++++++++++++++++++++++++++++

## mysh.c
##### sh1.c: 实现shell程序，要求具备如下功能
*	支持命令参数
*	$ echo arg1 arg2 arg3
*	$ ls /bin /usr/bin /home
*	实现内置命令cd、pwd、exit
*	$ cd /bin
*	$ pwd
*	/bin

##### sh2.c: 实现shell程序，要求在第1版的基础上，添加如下功能
*	实现文件重定向
*	$ echo hello >log
*	$ cat log
*	Hello

##### sh3.c: 实现shell程序，要求在第2版的基础上，添加如下功能
*	实现管道
*	$ cat /etc/passwd | wc -l
*	实现管道和文件重定向
*	$ cat input.txt
*	3
*	2
*	1
*	3
*	2
*	1
*	$ cat <input.txt | sort | uniq | cat >output.txt
*	$ cat output.txt
*	1
*	2
*	3

## pi1.c: 使用2个线程根据莱布尼兹级数计算PI
*	莱布尼兹级数公式: 1 - 1/3 + 1/5 - 1/7 + 1/9 - ... = PI/4
*	主线程创建1个辅助线程
*	主线程计算级数的前半部分
*	辅助线程计算级数的后半部分
*	主线程等待辅助线程运行結束后,将前半部分和后半部分相加

## pi2.c: 使用N个线程根据莱布尼兹级数计算PI
*	与上一题类似，但本题更加通用化，能适应N个核心，需要使用线程参数来实现
*	主线程创建N个辅助线程
*	每个辅助线程计算一部分任务，并将结果返回
*	主线程等待N个辅助线程运行结束，将所有辅助线程的结果累加

## sort.c: 多线程排序
*	主线程创建一个辅助线程
*	主线程使用选择排序算法对数组的前半部分排序
*	辅助线程使用选择排序算法对数组的后半部分排序
*	主线程等待辅助线程运行結束后,使用归并排序算法归并数组的前半部分和后半部分

## pc1.c: 使用条件变量解决生产者、计算者、消费者问题
*	系统中有3个线程：生产者、计算者、消费者
*	系统中有2个容量为4的缓冲区：buffer1、buffer2
*	生产者生产'a'、'b'、'c'、‘d'、'e'、'f'、'g'、'h'八个字符，放入到buffer1
*	计算者从buffer1取出字符，将小写字符转换为大写字符，放入到buffer2
*	消费者从buffer2取出字符，将其打印到屏幕上

## pc2.c: 使用信号量解决生产者、计算者、消费者问题
*	功能和前面的实验相同，使用信号量解决

## ring.c: 创建N个线程，它们构成一个环
*	创建N个线程：T1、T2、T3、… TN
*	T1向T2发送整数1
*	T2收到后将整数加1
*	T2向T3发送整数2
*	T3收到后将整数加1
*	T3向T4发送整数3
*	…
*	TN收到后将整数加1
*	TN向T1发送整数N
