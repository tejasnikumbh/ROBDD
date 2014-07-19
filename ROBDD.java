/* =========================================================================== *
 * File : ROBDD.java                                                           *                     
 * =========================================================================== *
 * Class ROBDD to build an ROBDD DataStructure from a Boolean expression given *
 * as a string. Note that in the Boolean Expression, NOT is expressed as NOT(*)*
 * and not as !. Apart from that, all other expressions are expressed normally.*
 * Prime Methods : build(booleanExp,rootVar)                                   *     
 *               : print()                                                     *
 * Limitations   : Hashing strategy inefficient (memorywise) for H             *              
 * =========================================================================== */    

import java.util.*;
import java.lang.*;
import com.udojava.evalex.*; //Boolean expression evaluator

public class ROBDD{

    private int[][] T;
    private int[] H;
    private int nodeCount;   
    private int capacity;
    private int vars;
    
    // Constructor. Equivalent to the init(T),init(H) method in Notes 
    // Here n is the number of variables.
    public ROBDD(int n){
        // Initializing T to contain 0 and 1
        nodeCount = 0;
        capacity = n;
        vars = n;
        T = new int[capacity][3];
        
        // Adding 0 and 1 node. -1 indicates NULL.
        T[0][0] = n+1;
        T[0][1] = -1;
        T[0][2] = -1;
        T[1][0] = n+1;
        T[1][1] = -1;
        T[1][2] = -1;
        nodeCount += 2;
        
        // Initializing H 
        H = new int[capacity];
        for(int i=0;i<capacity;i++)
            H[i] = -1; 
    }
    
    // Getter-Setter Methods
    // getNodeCount : Returns the number of nodes in ROBDD
    public int getNodeCount(){
        return nodeCount;
    }
    
    // getROBDDTable : Returns the ROBDD table of the ROBDD.
    public int[][] getROBDDTable(){
        return T;
    }
    
    // Public Interface
    // mk(i,l,h) : Returns the node which is being made.
    public int mk(int i,int l,int h){
        // Return in case the node is redundant
        if(l == h){
            return l;            
        }else if(member(i,l,h)){
            return lookup(i,l,h);
        }else{ 
            int node = add(i,l,h);
            insert(i,l,h,node);
            return node;
        }
            
    } 
    
    // print : Prints the table T and H. Useful for debugging
    public void print(){
        System.out.println("Current State of T Table");    
        for(int i=0;i<nodeCount;i++){
            System.out.println(i + " " + T[i][0] 
                                 + " " + T[i][1] 
                                 + " " + T[i][2]);                      
        }
        System.out.println("Current State of H Table");              
        for(int i=0;i<H.length;i++){
            if(H[i] != -1) System.out.println(i + " " + H[i]);
        }
    }   
    
    // build(booleanExp,RootVar) : Recursively builds the ROBDD
    public int build(String exp,int i){
        if(i > vars){
            if(eval(exp)) return 1;
            else return 0;
        }else{
            int l = build(newExp(exp,i,0),i + 1);
            int h = build(newExp(exp,i,1),i + 1);
            return mk(i,l,h);
        }
    }
     
     
    // Private Implementation 
    /* Supporting Operations on T*/
    // add(i,l,h) : Adds and returns a new node with var(u) = i, var(l) = l ...
    // Takes O(1) time
    private int add(int i,int l,int h){
        int curNodeIndex = nodeCount++;
        if(curNodeIndex == capacity) expandArray();
        T[curNodeIndex][0] = i;
        T[curNodeIndex][1] = l;
        T[curNodeIndex][2] = h;
        return curNodeIndex;
    }
    
    /* Supporint Opeartions on H */
    // member(i,l,h) : Returns whether node with attributes is already in ROBDD.
    // Takes O(1) time
    private boolean member(int i, int l, int h){
        int hashCode = generateHash(i,l,h);
        // Not present in extended capacity
        if(hashCode>(H.length-1)) return false;
        // Not present in current capacity
        return H[hashCode] != -1 ;
    }
 
    // lookup(i,l,h) : Returns the node with attributes in ROBDD 
    // Takes O(1) time
    private int lookup(int i,int l,int h){
        int hashCode = generateHash(i,l,h);
        return H[hashCode];    
    }     
 
    // insert(i,l,h) : Inserts node with attributes i,l,h,node into Hash Table
    private void insert(int i,int l,int h,int node){
        int hashCode = generateHash(i,l,h);
        if(hashCode>(H.length+1)) renewHashTable(hashCode);
        if(H[hashCode] != -1) renewHashTable(hashCode);
        H[hashCode] = node;
        return;    
    }
    
    private void expandArray(){
        capacity *= 2;
        int[][] temp = new int[capacity][3];
        for(int i=0;i<T.length;i++){
            temp[i][0] = T[i][0];
            temp[i][1] = T[i][1];
            temp[i][2] = T[i][2];
        }
        T = temp;
    }
   
    // Private method to generate a HashCode
    private int generateHash(int i,int l,int h){
        int hashCode = (pair(i,pair(l,h)));
        return hashCode;    
    }   
   
    // pair(i,j) useful function for generation of hash code
    private int pair(int i,int j){
        return (((i+j)*(i+j+1))/2 + i);
    } 
    
    // renewing hash table in case of fulfilled capacity
    private void renewHashTable(int hashCode){
        int newSize = hashCode + 1;
        int[] newH= new int[newSize];
        for(int j=0;j<newSize;j++){
            newH[j] = -1;
        }
        for(int j=2;j<nodeCount;j++){
            int i = T[j][0];
            int l = T[j][1];
            int h = T[j][2];
            int hashCodeCur = generateHash(i,l,h);
            newH[hashCodeCur] = j; 
        }
        H = newH;
        return;
    }
 
    // Evaluates boolean expressions passed in as strings
    private boolean eval(String exp){
        Expression e = new Expression(exp);
        String result = String.valueOf(e.eval());
        int resultInt = Integer.parseInt(result);
        if(resultInt == 1) return true;
        else return false;
    }
    
    // Implements Shannon expansion by replacing var with val
    private String newExp(String exp,int var,int val){
        String varStr = "x" + String.valueOf(var);
        String valStr = String.valueOf(val);
        String newExpStr = exp.replaceAll(varStr,valStr);
        return newExpStr;
    }   
    
    // Test Program
    public static void main(String[] args){
    
        System.out.println("Hello. Program to demonstrate" +  
                           "ROBDD implementation");
        ROBDD test = new ROBDD(4);
        String boolExp = "NOT(x1&&NOT(x2) || NOT(x1)&&x2)" + 
                         "&& NOT(x3&&NOT(x4) || NOT(x3)&&x4)";
        test.build(boolExp,1);
        test.print();   
                     
    }   
}
