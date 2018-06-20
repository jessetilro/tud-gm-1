#!/bin/sh
java -cp jars/javaview.jar:jars/jvx.jar:jars/vgpapp.jar:jars/Jama-1.0.3.jar:. -Xmx1024m javaview model="models/triangle.obj" codebase=. archive.dev=show
