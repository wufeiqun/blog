#include <stdio.h>

/*默认版本的冒泡排序*/
void bubble_default(int array[], int length)
{
    int i,j,tmp;
    for (i=1;i<length;i++)
    {
        for (j=0;j<length-i;j++)
        {
            if (array[j] > array[j+1])
            {
                tmp = array[j];
                array[j] = array[j+1];
                array[j+1] = tmp;
            }
        }
    }
}

/*优化版的冒泡排序:每一趟扫描之前设定一个flag,当这一趟扫描中有交换发生的时候flag值变更,当没有发生
 * 任何交换的时候,flag不变,每一趟扫描结束后判断flag是否有变化,没有变化直接退出,避免无用的扫描*/
void bubble_flag(int array[], int length)
{
    int i,j,tmp,exchange;
    for (i=1;i<length;i++)
    {
        exchange = 0;
        for (j=0;j<length-i;j++)
        {
            if (array[j] > array[j+1])
            {
                tmp = array[j];
                array[j] = array[j+1];
                array[j+1] = tmp;
                exchange = 1;
            }
        }
        if (exchange==0)
        {
            break;
        }
    }
}

int main()
{
    int array[5] = {5,4,3,2,1};
    int length = (int) sizeof(array) / sizeof(*array);
    /* bubble_default(array, length); */
    bubble_default(array, length);
    int i;
    for (i=0;i<5;i++)
    {
        printf("%d\n", array[i]);
    }
    return 0;
}
