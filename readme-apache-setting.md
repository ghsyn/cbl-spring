### 1.build 방법
```cmd
    mvn clean install -U
```
### 2.서비스 파일 작성
```shell
$ sudo vi /etc/systemd/system/smd3100.service
# 내용작성
[Unit]
Description=DR Socket Server

[Service]
WorkingDirectory=/home/itman/
ExecStart=/bin/bash -c "exec java -jar /home/사용자/jar파일명.jar" 
Type=simple
User=root
Group=root
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```
### 3.서비스 파일 등록 및 실행
```shell
$ sudo systemctl enable smd3100.service
$ sudo systemctl daemon-reload
```
### 4.아파치 연동 
```shell
#################################################################################
#  -아파치 기본 설정    : sudo vi /etc/apache2/apache2.conf
#  -포트 설정          :  /etc/apache2/ports.conf                         
#  -public 파일 설정   :  /etc/apache2/mods-enabled/dir.conf
#  -언어셋관련 설정     :  /etc/apache2/mods-enabled/autoindex.conf
#  -파일타입 설정       :  /etc/apache2/mods-enabled/mime.conf
#  -웹서비스 설정       :    /etc/apache2/sites-enabled/000-default 
#################################################################################
$ sudo apt-get update
$ sudo apt-get install apache2

1. apache2.conf 수정
  $ sudo vi /etc/apache2.conf
    # virtualHost 설정
    <VirtualHost *:80>
        ServerName yourdomain.com
        ProxyPass         /  ajp://localhost:9090/
        ProxyPassReverse  /  ajp://localhost:9090/
    </VirtualHost>

  
    # mod_js.so 위치
    LoadModule jk_module modules/mod_jk.so
     
    # mod_jk.c 설정
    <IfModule mod_jk.c>
    # 다음과 같은 경로는 tomcat으로 연결
    JKMount /* tomcat
    JKMount /*.jsp tomcat
    JkMount /jkmanager/* jkstatus
     
    JkMountCopy All
    <Location /jkmanager/>
            JkMount jkstatus
            Require ip 127.0.0.1
    </Location>
     
    # workers.properties 위치
    JkWorkersFile "apache/conf/workers.properties"
    # 로스 위치
    JkLogFile "| apache/bin/rotatelogs -l apache/mod_jk.log.%y%m%d 86400 "
    # 로그 레벨
    JkLogLevel error
    # 로그 포멧
    JkLogStampFormat "[%a %b %d %H:%M:%S %Y] "
    JkShmFile  "apache/mod_jk.shm"
    #JkRequestLogFormat     "%w %V %T"
    </IfModule>
2. works.properties 추가
    $vi apache/conf/workers.properties
    
    worker.list=tomcat
    worker.tomcat.type=ajp13
    worker.tomcat.host=127.0.0.1
    worker.tomcat.port=8001
    worker.tomcat.retries=1 
    worker.tomcat.socket_timeout=10
    worker.tomcat.connection_pool_timeout=10
    worker.list=jkstatus
    worker.jkstatus.type=status

```
