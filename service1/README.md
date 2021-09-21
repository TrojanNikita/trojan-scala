Запуск дев:
```
sbt -Dconfig.resource=application.conf -Dlogback.configurationFile=logback.xml service1/run
```

Поднятие докер контейнера:
```
sbt -Dconfig.resource=application.conf -Dlogback.configurationFile=logback.xml service1/universal:packageZipTarball
docker-compose build
docker-compose up
```