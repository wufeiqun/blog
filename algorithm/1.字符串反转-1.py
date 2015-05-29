#coding=utf-8
#题目:给定一个随即字符串,把ta翻转过来.
#a = 'abcd' b = 'dcba'

#方法一,字符串的切片
def first(stra):
strb = stra[::-1]
print strb

#方法二,列表的reverse方法.
def second(stra):
	lista = list(stra)
	lista.reverse()
	strb = ''.join(lista)
	print strb

#方法三,
