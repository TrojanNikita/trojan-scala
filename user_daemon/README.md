Запуск дев
```
sbt -Dconfig.resource=application-dev.conf -Dlogback.configurationFile=logback.xml user_daemon/run
```
Поднятие докер контейнера

```
sbt user_daemon/universal:packageZipTarball
docker-compose build
docker-compose up
```