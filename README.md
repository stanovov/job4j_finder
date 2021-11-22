[![Build Status](https://travis-ci.com/stanovov/job4j_finder.svg?branch=master)](https://travis-ci.com/stanovov/job4j_finder)

![](https://img.shields.io/badge/Maven-=_3-red)
![](https://img.shields.io/badge/Java-=_14-orange)
![](https://img.shields.io/badge/Checkstyle-lightgrey)

# job4j_finder

+ [Об утилите](#0б-утилите)
+ [Сборка и запуск](#Сборка-и-запуск)
+ [Использование](#Использование)
+ [Контакты](#Контакты)

## Об утилите

Данная утилита умеет искать файлы в заданном каталоге и подкаталогах.

## Сборка и запуск

### Запуск через терминал

1.Собрать jar через Maven

`mvn install`

2.Запустить jar файл

`java -jar target/finer.jar`

### Запуск через IDE

Перейти к папке `src/main/java` и файлу `ru.job4j.finder.CriterionFinder`

## Использование

Программа ищет данные в заданном каталоге и подкаталогах. Имя файла может задаваться целиком, по маске, по регулярному
выражению (необязательно).

Ключи:

+ **-d** - директория, в которой начинать поиск.
+ **-n** - имя файла, маска, либо регулярное выражение.
+ **-t** - тип поиска: mask искать по маске, name по полному совпадению имени, regex по регулярному выражению.
+ **-o** - результат записать в файл.

## Контакты

Становов Семён Сергеевич

Email: sestanovov@gmail.com

Telegram: [@stanovovss](https://t.me/stanovovss)