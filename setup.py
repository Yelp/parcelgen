#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Copyright (c) 2011-2018, Yelp, Inc.
import os

from setuptools import setup

setup(
    name='parcelgen',
    version='1.0.2',
    license='Apache Software License',
    description='Library for simplifying generation of Android objects to share between activities',
    long_description=open(os.path.join(os.path.dirname(__file__), 'README.md')).read(),
    author='=Yelp, Inc.',
    author_email='opensource+parcelgen@yelp.com',
    url='https://github.com/Yelp/parcelgen',
    py_modules=['parcelgen'],
    classifiers=[
        'Development Status :: 5 - Production/Stable',
        'Intended Audience :: Developers',
        'Topic :: Software Development :: Libraries :: Python Modules',
        'License :: OSI Approved :: BSD License',
        'Operating System :: OS Independent',
        'Programming Language :: Python :: 2.7',
        'Programming Language :: Python :: 3.4',
        'Programming Language :: Python :: 3.5',
        'Programming Language :: Python :: 3.6',
        'Programming Language :: Python :: 3.7',
    ],
    entry_points={'console_scripts': ['parcelgen = parcelgen:main']},
)
