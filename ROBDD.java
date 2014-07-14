

import java.util.*;
import java.lang.*;

public class ROBDD{

    private int[][] T;
    private int[] H;
    private int k;
    private int m;
    private int nodeCount;   
  
    // Constructor. Equivalent to the init(T),init(H) method in Notes 
    public ROBDD(int n){
        // Initializing T to contain 0 and 1
        nodeCount = 0;
        k = 1;
        m = 15485863;
        T = new int[n][3];
        
        // Adding 0 and 1 node. -1 indicates NULL.
        T[0][0] = 0;
        T[0][1] = -1;
        T[0][2] = -1;
        T[1][0] = 1;
        T[1][1] = -1;
        T[1][2] = -1;
        nodeCount += 2;
        
        // Initializing H 
        H = new int[Math.pow(2,k)];
        // -1 indicates not present or NULL
        H[0] = -1; H[1] = -1;
        
    }
    
    // Returns the node which is being made.
    public int mk(int i,int l,int h){
        // Return in case the node is redundant
        if(l == h) return l;            
        else if(member(i,l,h)) return lookup(i,l,h);
        else{ 
            int node = add(i,l,h);
            insert(i,l,h,node);
            return node;
        }
            
    } 
    
    // Prints the table T. Useful for debugging
    public void print(){
        for(int i=0;i<nodeCount;i++){
            System.out.println(i + " " + T[i][0] 
                                 + " " + T[i][1] 
                                 + " " + T[i][2]);
        }
    }   
    
    /* Supporting Operations on T*/
    // add(i,l,h) : Adds and returns a new node with var(u) = i, var(l) = l ...
    // Takes O(1) time
    public int add(int i,int l,int h){
        int curNodeIndex = nodeCount++;
        T[curNodeIndex][0] = i;
        T[curNodeIndex][1] = l;
        T[curNodeIndex][2] = h;
        return curNodeIndex;
    }
 
    /* Supporint Opeartions on H */
    // member(i,l,h) : Returns whether node with attributes is already in ROBDD.
    // Takes O(1) time
    public boolean member(int i, int l, int h){
        int hashCode = generateHash(i,l,h);
        return H[hashCode] != -1;
    }
 
    // lookup(i,l,h) : Returns the node with attributes in ROBDD 
    // Takes O(1) time
    public int lookup(int i,int l,int h){
        int hashCode = generateHash(i,l,h);
        return H[hashCode];    
    }     
 
    // insert(i,l,h) : Inserts node with attributes i,l,h,node into Hash Table
    public void insert(int i,int l,int h,int node){
        if(Math.pow(2,k) == nodeCount) renewHashTable();
        int hashCode = generateHash(i,l,h);
        if(H[hashCode] != -1) renewHashTable();
        H[hashCode] = node;
        return;    
    }
    
    // Private method to generate a HashCode
    private int generateHash(int i,int l,int h){
        int hashCode = (pair(i,pair(l,h))%m)%Math.pow(2,k);
        return hashCode;    
    }   
    
    // renewing hash table in case of fulfilled capacity
    private void renewHashTable(){
        k++;
        int newSize = Math.pow(2,k);
        int[] newH= new int[newSize];
        for(int j=2;j<nodeCount;j++){
            int i = T[j][0];
            int l = T[j][1];
            int h = T[j][2];
            int hashCode = generateHash(i,l,h);
            newH[hasCode] = j; 
        }
        H = newH;
        return;
    }
    
    // Test Program
    public static void main(String[] args){
        System.out.println("Hello");
    }   
}
