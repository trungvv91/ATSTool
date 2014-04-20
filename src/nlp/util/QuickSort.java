/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.util;

import java.util.Arrays;

/**
 *
 * @author Trung
 */
public class QuickSort {

    public static void swap(double A[], int B[], int x, int y) {
        double temp = A[x];
        A[x] = A[y];
        A[y] = temp;
        int itemp = B[x];
        B[x] = B[y];
        B[y] = itemp;
    }

    /**
     * left is the index of the leftmost element of the subarray right is the
     * index of the rightmost element of the subarray (inclusive) number of
     * elements in subarray = right-left+1
     *
     * @param A
     * @param B
     * @param left
     * @param right
     * @return index of the selected element (pivot)
     */
    public static int partition(double A[], int B[], int left, int right) {
        int pivotIndex = (left + right) / 2;
        double pivotValue = A[pivotIndex];
        swap(A, B, pivotIndex, right);
        int storeIndex = left;
        for (int i = left; i < right; i++) {
            if (A[i] >= pivotValue) {
                swap(A, B, storeIndex, i);
                storeIndex++;
            }
        }
        swap(A, B, storeIndex, right);
        return storeIndex;
    }

    /**
     * Quick sort array A from left to right, inclusive. Array B stores original
     * index of A after sorted.
     *
     * @param A - sorting array
     * @param B - index
     * @param left
     * @param right
     */
    public static void QuickSort(double A[], int B[], int left, int right) {
        if (left < right) {
            int pivotNewIndex = partition(A, B, left, right);
            QuickSort(A, B, left, pivotNewIndex - 1);
            QuickSort(A, B, pivotNewIndex + 1, right);
        }
    }

    public static void main(String[] args) {
        double A[] = {4, 1, 9, 4.625, 8};
//        swap(A, 0, 1);
        Arrays.sort(A);
        for (int i = 0; i < A.length; i++) {
            System.out.print(A[i] + " ");
        }

//        int B[] = new int[A.length];
//
//        for (int i = 0; i < A.length; i++) {
//            System.out.print(A[i] + " ");
//            B[i] = i;
//        }
//        System.out.println("\n");
//        QuickSort(A, B, 0, A.length - 1);
//        for (int i = 0; i < 5; i++) {
//            System.out.print(A[i] + " ");
//        }
//        System.out.println("");
//        for (int i = 0; i < 5; i++) {
//            System.out.print(B[i] + "   ");
//        }
    }
}
