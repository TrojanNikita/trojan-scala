Запуск дев
```
sbt service1/run
```
Поднятие докер контейнера

```
sbt -Dlogback.configurationFile=service1/src/main/resources/logback.xml service1/universal:packageZipTarball
docker-compose build
docker-compose up
```