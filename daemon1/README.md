Запуск дев
```
sbt daemon1/run
```
Поднятие докер контейнера

```
sbt daemon1/universal:packageZipTarball
docker-compose build
docker-compose up
```