#!/usr/bin/env python
#coding=utf-8
import os
from fabric.api import *

current_dir = os.path.dirname(os.path.abspath(__file__))

def deploy(filename,commit):
	with lcd(current_dir):
		local('git pull')
		local('git add '+filename)
		local('git commit -m '+'\''+commit+'\'')
		local('git push')
		print '-----------------------------------successlly pushed------------------------------------------------'
	
