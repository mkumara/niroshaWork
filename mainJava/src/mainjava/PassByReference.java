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
public class PassByReference {
    int OnetoZero (int arg[]) {
int count = 0;
for (int i = 0; i < arg.length; i++) {
 if (arg[i] == 1) {




count++;
arg[i] = 0; }
 }
return count;
 }


public static void main (String arg[]) {
 int arr[] = { 1, 3, 4, 5, 1, 1, 7 };
 PassByReference test = new PassByReference();


System.out.print("Values of the array: [ ");
for (int i = 0; i < arr.length; i++) {
 System.out.print(arr[i] + "");
 }
System.out.println("]");

int numOnes = test.OnetoZero(arr);
 System.out.println("Number of Ones = " + numOnes);
 System.out.print("New values of the array: [ ");
 for (int i = 0; i < arr.length; i++) {
 System.out.print(arr[i] + " ");
 }
 System.out.println("]");
}}