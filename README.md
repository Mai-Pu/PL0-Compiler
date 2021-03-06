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
![](https://github.com/Mai-Pu/PL0-Compiler/raw/master/pic/1.png)
##### 2.2主要功能模块的设计
###### (1)设计符号表
* 符号表每一个表项包括名字name，值value，种类type（其中种类有4种：var，con，proc，prog）,所属层级level，地址address，以及大小size；其中名字作为关键字。同时可以完成如下操作：
* a.通过名字查找并返回位置：根据给定的名字，在符号表中查找其信息。如果该名字在符号表中不存在，则返回-1，否则返回该名字在表中的位置；
* b.通过名字查找并返回符号表项：根据给定的名字，在符号表中查找其信息。如果该名字在符号表中不存在，则返回null，否则返回指向该名字的指针；
* c.通过位置查找并返回符号表项：根据给定的index，在符号表中查找其信息。如果能找到，则返回指向该index的指针，否则返回null。

###### (2)设计词法分析器
* 通过函数实现各单词的状态转换，并为不同的单词设计种别码。将词法分析器设计成供语法分析器调用的子程序。功能包括：
* a. 具有过滤多于空格的预处理功能；
* b. 能够拼出语言中的各个单词；
* c. 将拼出的标识符填入符号表；
* d. 返回（种别码， 属性值）。

###### (3)语法分析与中间代码产生器
* 是使用预测分析法，设计了两个栈，一个状态栈，一个层次栈，再结合单独处理各种语句的函数进行语法分析。
* 若语法正确，则用语法制导翻译法进行语义翻译：对说明语句，将说明的各符号记录到相应符号表中；
* 对可执行语句，应产生出四元式中间代码并填写到三地址码表中；
* 若语法错误，则指出出错性质和出错位置（行号）。

###### (4)中间代码生成
* 生成虚拟机规定的汇编语言代码。

###### (5)中间代码的执行
* 对生成的中间代码进行解释并执行。

##### 2.3系统运行流程
* 本系统以语法分析程序为核心，
* （1）将prog压入状态栈；
* （2）进行语法分析，先使用词法分析程序获得一个关键字，然后根据栈中的状态和关键字的属性进行相应的操作，本过程通过switch语句完成。
* （3）重复执行步骤（2），直到状态栈为空或者文本读到末尾。
![](https://github.com/Mai-Pu/PL0-Compiler/raw/master/pic/2.png)

### 3.系统实现

##### 3.1系统主要函数说明（主要功能、输入\输出、实现思想）

###### 词法分析模块函数：
![](https://github.com/Mai-Pu/PL0-Compiler/raw/master/pic/3.png)
* getReadtxt() :调整过的文本文件
* getLinegps():获得读取的文本文件，以便调试
* Analyzer():构造函数
* Analyzer(String):构造函数
* readch :每次读取一个字符
* readkeyword :通过读取一个或多个字符来组成相应的关键字
* readnumber():判断读取的关键字是否为一个数字
* readword():判断读取的关键字是标识符还是系统的保留字
* Readoperator():判断读取的关键字是哪一个操作符

###### 语法分析模块函数：
![](https://github.com/Mai-Pu/PL0-Compiler/raw/master/pic/4.png)
* searchfirst(int [],int) ：查找某个关键字是否在某个first集中，用于判断出错等
* nextword()：获取下一个keyword
* start(int,DefaultTableModel)：开始进行语法分析，同时进行词法分析以及中间代码生成model用于在图形化界面输出错误原因
* statementdeclare(int,DefaultTableModel)：对statement的类型进行判断，以及进行目标代码翻译
* panduan(int,DefaultTableModel)：用于条件判断，并生成相应的目标代码
* expression(int,DefaultTableModel)：进行表达式的运算以及生成相应的目标代码
* term(int,DefaultTableModel):乘除运算
* factor(int,DefaultTableModel):对每种因子进行相应的处理并生成目标代码
* vardeclare(int,DefaultTableModel):变量声明
* constdeclare(int,DefaultTableModel):常量声明

###### 中间代码生成以及执行模块函数：
![](https://github.com/Mai-Pu/PL0-Compiler/raw/master/pic/5.png)
* setP(String,int,int):添加目标代码
* runP():用于在控制台运行并输出
* runP(DefaultTableModel):用于在图形化界面运行并输出
