Запуск дев:
```
sbt -Dconfig.resource=application.conf -Dlogback.configurationFile=logback.xml user_service/run
```

Поднятие докер контейнера:
```
sbt -Dconfig.resource=application.conf -Dlogback.configurationFile=logback.xml user_service/universal:packageZipTarball
docker-compose build
docker-compose up
```



docker exec -it some-postgres psql -U postgres