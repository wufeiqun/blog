#!/usr/bin/env python
#coding:utf-8
import time
import random

#默认版本
#比如[5,4,3,2,1],第一趟比较的时候,需要获取的索引的范围为0,1,2,3.
def bubble_sort_a(array):
    n = len(array)              # 获取数组长度
    for i in range(1,n):        # 总共需要比较n-1趟
        for j in range(n-i):    # 当比较第i趟的时候,需要遍历索引从0到n-i-1.
            if array[j] > array[j+1]:
                array[j], array[j+1] = array[j+1], array[j]
    return array

# 优化版本一,每一趟比较开始的时候设置一个变量,当这一趟有交换发生的时候,变量改变,当没有交换发生
# 的时候,变量不变,当这一趟比较完成的时候,根据变量来判断这一趟是否有交换,如果没有交换则代表已经
# 排序完成,退出循环,这样可以不用做一些无用的比较.
def bubble_sort_b(array):
    n = len(array)
    for i in range(1,n):
        exchange = 0
        for j in range(n-i):
            if array[j] > array[j+1]:
                array[j], array[j+1] = array[j+1], array[j]
                exchange = 1
        if not exchange: # 根据变量判断本趟是否有交换,如果没有就直接退出
            break
    return array

if __name__ == "__main__":
    start = time.time()
    #array = [x for x in xrange(1000, 0, -1)]
    array = []
    for i in xrange(9000):
        array.append(random.randint(0, 10000))
    #bubble_sort_a(array)
    # bubble_sort_b(array)
    end = time.time()
    print "Using %0.2fs." % (end - start)
