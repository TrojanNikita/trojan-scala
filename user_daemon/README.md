Запуск дев
```
sbt -Dconfig.resource=application.conf -Dlogback.configurationFile=logback.xml user_daemon/run
```
Поднятие докер контейнера

```
sbt -Dconfig.resource=application.conf -Dlogback.configurationFile=logback.xml user_daemon/universal:packageZipTarball
docker-compose build
docker-compose up
```