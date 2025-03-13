package com.archer.tools.test.bytecode;
/**
 * stack=4, locals=3, args_size=3
         0: aload_0
         1: getfield      #26                 // Field pool:Lcom/archer/tools/test/bytecode/AsyncPool;
         4: aload_0
         5: aload_1
         6: iload_2
         7: invokedynamic #28,  0             // InvokeDynamic #0:run:(Lcom/archer/tools/test/bytecode/ClassAImpl;Ljava/lang/String;I)Lcom/archer/tools/test/bytecode/AsyncTask;
        12: invokevirtual #32                 // Method com/archer/tools/test/bytecode/AsyncPool.submit:(Lcom/archer/tools/test/bytecode/AsyncTask;)V
        15: return
        
   stack=2, locals=3, args_size=3
         0: aload_0
         1: aload_1
         2: putfield      #20                 // Field name:Ljava/lang/String;
         5: aload_0
         6: iload_2
         7: putfield      #24                 // Field age:I
        10: return
 * 
 * */

public class ClassAImpl extends ClassA {
	
	public String nameCp;
	
	public void setName(String name, int age, ClassB b) {
		this.nameCp = name;
		super.setName(name, age, b);
	}
}
