# ATLAS Backend

## Big Picture
<pre>

+--------------+                        +-------+
| Lidi-Backend | ---------------------> | Kafka |
+--------------+                        +-------+
                                            ^
                                            |
                                    +---------------+          +------------------------+               +----------------+
                                    | ATLAS-Backend |<---------| API-Management Gateway |<--------------| ATLAS-Frontend |
                                    | └LiDi-Modul   |          | CloudWAF Container     |               +----------------+                  
                                    +---------------+          +------------------------+               
                                            |
                                            |
                                            v
                                      +------------+
                                      | PostgreSQL |
                                      +------------+
</pre>
## Tech Stack 
| Layer     |  Techno    |  Link     |
|-----------|------------|-----------|
|Frontend   | Angular9 + | [ESTA-Web](https://confluence.sbb.ch/display/CLEW/ESTA-Web) |
|Backend    |Java Spring Boot 2.4 | [ESTA-Backend (Spring-Boot)](https://confluence.sbb.ch/pages/viewpage.action?pageId=1306395091) |
|           |Lombok | https://projectlombok.org/ |
|Database	|PostGreSQL| [Service PostgreSQL](https://confluence.sbb.ch/display/PLA/Service+PostgreSQL)|
|Messaging	|Apache Kafka| [KAFKA Home](https://confluence.sbb.ch/display/KAFKA/KAFKA+Home)|
|Infrastructure|	Openshift AWS 4.0| [ESTA-Cloud](https://confluence.sbb.ch/display/CLEW/ESTA-Cloud)|
|Deployment	|ESTA Cloud Pipeline| [Esta Cloud Pipeline](https://confluence.sbb.ch/display/CLEW/Esta+Cloud+Pipeline)|
|Interface|  API Management oder ähnliches Tool nach Entscheid KISPF-198 <br> REST & Json| [KISPF-198](https://flow.sbb.ch/browse/KISPF-198) - SKI/SKI+ API Strategie|        

## Development
### PostgreSQL Docker
Run PostgreSQL in docker:
~~~
    docker-compose up
~~~

Stop PostgreSQL container:
~~~
    docker-compose down
~~~

Stop PostgreSQL container and remove volume:
~~~
   docker-compose down -v 
~~~

## Links
* Developer Portal Prod:
* Developer Portal Int:
* Openshift Prod:
* Openshift Int:
* Openshift Test:
* Openshift Dev: 
* Jenkins:
* Infrastruktur: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+7.1.+Infrastruktur+Ebene+1


## Decision History
07.2021: Spring-Boot Initializer used for template