package com.wufeiqun.provider.service;

import java.text.MessageFormat;

/**
 * 学习Java中的位运算
 * 程序中的所有数在计算机内存中都是以二进制的形式储存的。
 * 位运算说到底，就是直接对整数在内存中的二进制位进行操作。
 * 使用位运算，主要目的是节约内存，使你的程序速度更快，还有就是对内存要求苛刻的地方使用。
 */
public class BitwiseOperatorService {
    /**
     * 与运算
     * 符号: &
     * 规则: 两者都为1时结果为1, 其他情况为0
     * 示例:
     *      1 & 1 = 1
     *      1 & 0 = 0
     *      0 & 1 = 0
     *      0 & 0 = 0
     */
    public static void andCalc(int A, int B){
        System.out.println(MessageFormat.format("A 和 B 的与运算(&)结果为: {0}", A & B));
    }
    /**
     * 或运算
     * 符号: |
     * 规则: 两者有一个为1时结果为1, 其他情况为0
     * 示例:
     *      1 | 1 = 1
     *      1 | 0 = 1
     *      0 | 1 = 1
     *      0 | 0 = 0
     */
    public static void orCalc(int A, int B){
        System.out.println(MessageFormat.format("A 和 B 的或运算(|)结果为: {0}", A | B));
    }
    /**
     * 非运算
     * 符号: ~
     * 规则: 0变1，1变0
     * 注意:
     *   1. 该符号只需要一个参数, 属于单目运算符
     *   2. 根据参数类型先转成多少位的二进制, 然后计算, 比如int 1, 转成二进制以后为 `00000000 00000000 00000000 00000001`
     *   3. Java中数字的二进制都是以补码的形式表示, 正数和0的原码/反码/补码都是一样的, 负数的补码则是将其对应正数按位取反再加1
     * 负数十进制 ---> 补码:
     *   示例数字: -5
     *   对应正数: 5
     *   对应正数原码: 00000000 00000000 00000000 00000101
     *   对应正数反码: 11111111 11111111 11111111 11111010
     *   示例数字补码(反码+1): 11111111 11111111 11111111 11111011
     *   System.out.println(Integer.toBinaryString(-5));
     *   11111111111111111111111111111011
     *
     * 负数补码 --> 对应的十进制:(正数的直接计算, 因为正数的原码/反码/补码都一样)
     *   示例数字: 11111111111111111111111111111011
     *   示例数字-1: 11111111111111111111111111111010
     *   (示例数字-1)求反: 00000000 00000000 00000000 00000101
     *   上述结果加一个负号即可: -5
     *
     * 参考链接: https://zh.wikipedia.org/wiki/%E4%BA%8C%E8%A3%9C%E6%95%B8
     */
    public static void notCalc(int A){
        System.out.println(MessageFormat.format("A 的非运算(~)结果为: {0}", ~A));
    }

    /**
     * 左移运算符
     * value << N，左移N位
     * 丢弃左边指定位数，右边补0
     * 规律:
     *   1. 有可能正数变成了负数, 负数变成了正数
     *   2. 通俗来说, M << N 相当于 M * 2**N, 比如有人喜欢使用 M << 1来表示×2
     */
    public static void signLeftShift(int A, int B){
        System.out.println(MessageFormat.format("A 的有符号左移 B 运算(<<)结果为: {0}", A << B));
    }
    /**
     * 右移运算符
     * value >> N，右移N位
     * 丢弃右边指定位数，左边补上符号位。正数补0, 负数补1
     * 规律:
     *   1. 右移1位表示/2, 比如有人喜欢使用 M >> 1来表示/2
     */
    public static void signRightShift(int A, int B){
        System.out.println(MessageFormat.format("A 的有符号右移 B 运算(>>)结果为: {0}", A >> B));
    }
    /**
     * 无符号右移运算符
     * value >> N，右移N位
     * 无符号右移运算符>>>和右移运算符>>是一样的，只不过无符号右移运算符是补上0，也就是说，对于正数移位来说等同于：>>，负数通过此移位运算符能移位成正数。
     * 规律:
     *   1. 右移1位表示/2, 比如有人喜欢使用 M << 1来表示/2
     */
    public static void unsignRightShift(int A, int B){
        System.out.println(MessageFormat.format("A 的无符号右移 B 运算(>>>)结果为: {0}", A >>> B));
    }
    public static void main(String[] args) {
        int A = 10;
        int B = 44;
        System.out.println(MessageFormat.format("A是: {0}({1})", A,  Integer.toBinaryString(A)));
        System.out.println(MessageFormat.format("B是: {0}({1})", B,  Integer.toBinaryString(B)));
        andCalc(A, B);
        orCalc(A, B);
        notCalc(A);
        System.out.println(Integer.toBinaryString(-5));
        signLeftShift(1, 31);
        signRightShift(-10, 1);
    }
}
