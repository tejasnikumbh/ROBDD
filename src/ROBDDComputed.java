/* =========================================================================== *
 * File : ROBDDComputed                                                        *
 * =========================================================================== *
 * Class that represents and ROBDD Computed by performing given operation on   *
 * two given ROBDDs.                                                           *
 * Prime Operations : apply(operation,first ROBDD, second ROBDD)               *
 *                  : print()                                                  *
 * Limitations      : G is 2D Array and not hash Table                         *
 * =========================================================================== */  

import java.util.*;
import java.lang.*;
import com.udojava.evalex.*; //Boolean expression evaluator

public class ROBDDComputed{

    private int[][] T;
    private int[] H;
    private int nodeCount;
    private int capacity;
    private int[][] G;
    private int[][] T1;
    private int[][] T2;
    private int T1NodeCount;
    private int T2NodeCount;
    private String op;
    
    // Constructor. Equivalent to the init(T),init(H) method in Notes
    // Here n is the number of variables.
    public ROBDDComputed(int n){
        // Initializing T to contain 0 and 1
        nodeCount = 0;
        capacity = n;
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
    
    // Returns the node which is being made.
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
    
    // Prints the table T and H. Useful for debugging
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
    
    // Method to compute ROBDD performing operation op on u1,u2
    public void apply(String op,ROBDD u1, ROBDD u2){
        // Initializing the G Table. -1 indicates empty
        T1 = u1.getROBDDTable();
        T2 = u2.getROBDDTable();
        T1NodeCount = u1.getNodeCount();
        T2NodeCount = u2.getNodeCount();
        G = new int[T1NodeCount][T2NodeCount];
        this.op = op;
        for(int i=0;i<T1NodeCount;i++){
            for(int j=0;j<T2NodeCount;j++){
                G[i][j] = -1;
            }
        }
                
        app(T1NodeCount-1,T2NodeCount-1);        
    }
    
    // Private method that recursively computes the ROBDD
    private int app(int l,int h){
        int u;
        if(G[l][h] != -1){ 
            return G[l][h];
        }else if( ((l==0)||(l==1)) && ((h==0)||(h==1)) ) {
            u = eval(String.valueOf(l) + op + String.valueOf(h))?1:0;
        }else if(T1[l][0] == T2[h][0]){
            u = mk(T1[l][0], app(T1[l][1],T2[h][1]) , app(T1[l][2],T2[h][2]));
        }else if(T1[l][0] < T2[h][0]){
            u = mk(T1[l][0], app(T1[l][1],h), app(T1[l][2],h) );
        }else{
            u = mk(T1[h][0], app(l,T2[h][1]), app(l,T2[h][2]) );
        }
        G[l][h] = u;
        return u;
    }
    
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
    // member(i,l,h) : Returns if node with attributes is already in ROBDD.
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
 
    // insert(i,l,h) : Inserts node with attributes i,l,h,node in Hash Table
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
    
        String welcomeMsg = "Hello. Program to demonstrate" +
                            "ROBDDComputed implementation";
        System.out.println(welcomeMsg);
        
        ROBDD u1 = new ROBDD(5);
        u1.mk(5,1,0);
        u1.mk(4,2,0);
        u1.mk(4,0,2);
        u1.mk(3,3,4);
        u1.mk(2,5,0);
        u1.mk(2,0,5);
        u1.mk(1,6,7);
        //u1.print();
        ROBDD u2 = new ROBDD(5);
        u2.mk(5,1,0);
        u2.mk(3,2,0);
        u2.mk(3,0,2);
        u2.mk(1,3,4);
        //u2.print();
        
        ROBDDComputed result = new ROBDDComputed(5);
        result.apply("&&",u1,u2);
        result.print();
              
    }
}
