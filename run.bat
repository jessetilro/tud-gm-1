@echo off
set jv_jar=jars/javaview.jar;jars/jvx.jar;jars/vgpapp.jar;jars\Jama-1.0.3.jar;.
start java -cp %jv_jar% -Djava.library.path="dll" -Xmx4g javaview model="models/rabbit-100.jvx" codebase=. archive.dev=show %*
