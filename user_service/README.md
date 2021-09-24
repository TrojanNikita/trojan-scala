Запуск дев:
```
sbt -Dconfig.resource=application-dev.conf -Dlogback.configurationFile=logback.xml user_service/run
```

Поднятие докер контейнера:
```
sbt user_service/universal:packageZipTarball
docker-compose build
docker-compose up
```



docker exec -it some-postgres psql -U postgres