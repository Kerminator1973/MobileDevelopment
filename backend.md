# Расходы на поддержание работоспособности продукта #

Приведена условная оценка расходов на оплату услуг хостинга севера и баз данных. Расходы на персонал, налоги, и т.д. - вне данного обзора.

## Основные статьи расходов ##

* Хостинг VPS сервера для работы приложения  
* SSL-сертификаты, аренда доменного имени  
* Хостинг базы данных. Может выполняться VPS сервером, но отдельный специализированный хостинг может быть более эффективным по причине оптимизации оборудования под задачи баз данных (много ОЗУ, быстрая дисковая подсистема, кэширующие кластеры, и т.д.)  
* Использование облачных API для выполнения специализированных задач (например, Google Maps)  
* Лицензия разработчика для мобильных платформ

## Оценка расходов на хостинг (минимальные, уровень - hobby)##

4 ARMv8, 2 GB за 3 фунта/месяц (UK)
https://www.scaleway.com/pricing/#anchor_arm

1 Core, 2GB за 3 евро/месяц (Germany, Finland)
https://www.hetzner.com/cloud-ru

Россия: 1 Core 1Gb - 400 руб/месяц
https://vscale.io/

Россия: 1 Core 1Gb - 220 руб/месяц
https://neoserver.ru/

Heroku: 512 Mb, Never Sleeps - $7/месяц
https://www.heroku.com/pricing

AWS: 1 Core 1Gb t2.micro + DynamoDB - оценка в $27/месяц.

База данных на mLab.com (куплен MongoDB): https://mlab.com/plans/pricing/ - SHARED - $15/Gb

База данных на MongoDB Atlas: https://www.mongodb.com/cloud/atlas/pricing - $9/месяц. Есть ограниченный бесплатный тариф.

## Альтернативы ##

[Firebase](https://firebase.google.com/pricing/) - Flame Plan - $25/месяц  

[Parse Server](https://parseplatform.org/) на AWS - аренда VPS на Amazon (комплексная формула)

## Сертификаты и доменное имя ##

Стоимость доменного имени для зоны ".com" при покупке на AWS составляет $12, а для ".net" - $11. Дополнительные затраты на хостинг DNS (AWS Route53) составляют $0.50/месяц за одну зону хостинга. Цены [Zenon (Россия)](https://www.zenon.net/domains/): 280 руб/год за ".ru" и 882 руб/год за ".com".

Дополнительно нужно оценить ежегодные расходы на SSL-сертификат для сайта. Статья о "бесплатном" получении SSL сертификата на AWS: https://hackernoon.com/getting-a-free-ssl-certificate-on-aws-a-how-to-guide-6ef29e576d22.

Стоимость SSL-сертификата **Thawte SSL WebServer** на [Zenon](https://www.zenon.net/ssl/) составляет 6384 руб/год.

## Стоимость использования сервиса доступа к картам ##

https://developers.google.com/maps/billing/understanding-cost-of-use

Нужно вчитываться - услуг очень много и цена до $0.10/запрос. Из рекламы Google для коммерческих проектов: "*For most of our users, the $200 monthly credit is enough to support their needs. You can also set daily quotas to protect against unexpected increases*".

Бесплатный лимит вызовов Geocoder - 10.000/месяц.

По Yandex.Maps: https://tech.yandex.com/maps/commercial/

С дневным лимитом в 1000 запросов, стоимость - 120 тысяч рублей/год.

## Лицензия разработчика (возможность публикации в магазине приложений) ##

Google Play - $25/единожды  
Apple AppStore - $100/год
