# openapm
仅供学习交流使用，详见[APM之原理篇](http://blog.csdn.net/sgwhp/article/details/50239747)、[APM之实现篇](http://blog.csdn.net/sgwhp/article/details/50438666)

## 关于Sample
运行sample工程之后查看Logcat，如果有IndexOutOfBoundsException堆栈打印出来，说明编译成功。

## 日志
查看所有日志可通过命令行运行gradlew build，日志文件路径在每个module目录的log.log。如自定义日志路径，可修改gradlew.bat（windows）或graldew（linux）文件的set DEFAULT_JVM_OPTS一行，将logfile指定为你的路径，或在运行gradlew命令时加上-Dopenapm.agentArgs=logfile=你的路径.
