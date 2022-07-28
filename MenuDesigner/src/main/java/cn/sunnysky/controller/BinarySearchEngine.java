package cn.sunnysky.controller;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class BinarySearchEngine {

    /**
     * Binary search without recursion.
     *title:commonBinarySearch
     *@param arr The array you want to search in.
     *@param key The object you want to look for.
     *@return The object you sent itself if it exists, otherwise it would be null.
     */
    @Nullable
    public static <T> T commonBinarySearch(T[] arr, T key, Comparator<T> comparator){
        int low = 0;
        int high = arr.length - 1;
        int middle = 0;   //定义middle

        if(arr.length < 1
                ||comparator.compare(arr[low],key) > 0
                || comparator.compare(arr[high],key) < 0)
            return null;


        while(low <= high){
            middle = (low + high) / 2;
            if(comparator.compare(arr[middle],key) > 0){
                //比关键字大则关键字在左区域
                high = middle - 1;
            }else if(comparator.compare(arr[middle],key) < 0){
                //比关键字小则关键字在右区域
                low = middle + 1;
            }else{
                return arr[middle];
            }
        }

        return null;
    }


}
