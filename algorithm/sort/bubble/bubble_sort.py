#!/usr/bin/env python
#coding:utf-8

def bubble_sort(array):
    n = len(array)
    for i in range(1,n):
        exchange = False
        for j in range(n-i):
            if array[j] > array[j+1]:
                array[j], array[j+1] = array[j+1], array[j]
                exchange = True
        if not exchange:
            break
    return array

array  = [9,8,7,6,5,4,3,2,1]
print bubble_sort(array)

