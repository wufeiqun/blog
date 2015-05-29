#coding=utf-8
#题目:给定一个随即字符串,把她翻转过来.

#方法一,循环迭代,最笨的办法.
def first(stra):	
	strb = ''
	lista = stra.split()
	i = 1
	while i <= len(lista):
		strb+=(lista[-i]+' ')
		i+=1
	
	print '原始字符串:',stra
	print '旋转后的字符串:',strb

#方法二,利用python list的reverse方法.此方法只适用于list本身,不能新建一个list对象.
def second(stra):
	strb = ''
	lista = stra.split()
	lista.reverse()
	strb = ' '.join(lista)
	
	print '原始字符串:',stra
	print '旋转后的字符串:',strb

def third(stra):
	


if __name__ =='__main__':
	stra = 'this is a desk'
	print '------------------------------------方法一-------------------------------------'
	first(stra)
	print '------------------------------------方法二-------------------------------------'
	second(stra)
