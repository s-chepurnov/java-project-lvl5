### Task manager

### Hexlet tests and linter status:
[![Actions Status](https://github.com/s-chepurnov/java-project-lvl5/workflows/hexlet-check/badge.svg)](https://github.com/s-chepurnov/java-project-lvl5/actions)

### Code climate
[![Maintainability](https://api.codeclimate.com/v1/badges/0ea524721f7478c76746/maintainability)](https://codeclimate.com/github/s-chepurnov/java-project-lvl5/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/0ea524721f7478c76746/test_coverage)](https://codeclimate.com/github/s-chepurnov/java-project-lvl5/test_coverage)

### Heroku link to this app
https://immense-castle-41686.herokuapp.com/welcome

### Build, deploy

Логика нашего приложения будет подразумевать работу с данными, для хранения которых нужно будет использовать базу данных.
Взаимодействие с данными мы будем осуществлять с помощью ORM. Для управления миграциями будем использовать инструмент Liquibase.

CRUD Пользователей
По сравнению с большими системами, наша будет содержать только самый минимум: пользователей, задачи, статусы и метки. Если вам понравится процесс, то не останавливайте себя и допилите проект после окончания официального времени.
На этом шаге вам необходимо добавить логику работы с пользователями. Запросы должны возвращать JSON (примеры в описании маршрутов).



## locally

make generate-migrations

make start

http://localhost:5000/h2console/

## heroku

git push heroku main


