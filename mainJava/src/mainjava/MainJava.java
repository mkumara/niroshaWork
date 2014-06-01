/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mainjava;

/**
 *
 * @author Onara
 */
public class MainJava {
    int t=10;
    int arr[]={1,2,3,4,5};
    int[] ar =new int[4];
    ar[0]=1;
   //ar ={1,2,3,4};
    
     //  
    int[] makeRange(int l,int u){
        int t=5;
        System.out.println("Local ="+t);
        System.out.println("instance"+this.t);
        
    int a[] =new int[(u-l)+1];
    
    for(int i=0; i<a.length;i++){
        a[i]=l++;
    }
    return a;
    }
    int OnetoZero (int arg[]) {
int count = 0;
for (int i = 0; i < arg.length; i++) {
if (arg[i] == 1)

 count++; arg[i] = 0;
}

return count;
}
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int  testA[];
        MainJava m = new MainJava();
        
        
       testA= m.makeRange(1,10);
       System.out.print("The array { ");
       for(int i=0; i<testA.length;i++){
       System.out.print(testA[i]+ " ");
       }
       System.out.println("}");
      // 
        // TODO code application logic here
    }
    
}
