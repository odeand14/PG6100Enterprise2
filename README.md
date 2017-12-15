# PG6100Enterprise2
Project task Enterprise2, this is our project for the Enterprise 2 exam.
Our task was to build a microservice game using Kotlin and Spring, along with various assets. We decided to make a tic-tac-toe game.
Our goal was to create it with a react GUI, however we spent more time than expected on other functionality, so as it stands we do not have a GUI.
Instead we have focused on creating extensive end-to-end tests to simulate the different functionalities working together, and how the game works.

## Getting started
* Due to the Maven/Docker combo it should be fairly easy to get up and running

### Installing
* Clone the repo on [our GitHub page](https://github.com/odeand14/PG6100Enterprise2 "Our GitHub") or use the one provided
* Run ```mvn install``` in the root folder, this command should create all the jars and run all the tests.

* For Swagger documentation and running application do a ```docker-compose build``` and then a ```docker-compose up```
* Now the application should be up and running and you can view the Swagger documentation on [http://localhost/swagger-ui.html](http://localhost/swagger-ui.html)
* From there you can navigate to all the different modules API.

### Prerequisites
* General knowledge about Docker and how to install this yourself
* System dependencies, none beside Docker and Maven dependencies


## Project report
Below is a little report on our project
### Project structure
The project is structured so that all modules are on the same level, directly below the root-folder. All the modules have their own Dockerfile and necessary kotlin/spring/other files, depending on their needs.
### Testing and technologies
We have mainly focused on trying to get good coverage with our tests here, also on trying modules together with end to end tests. It has been a most entertaining/frustrating/useful experience, working with microservices in docker containers, along with Zuul, Redis, Eureka, Swagger and more. We have found that it is not easy getting everything to communicate correctly especially with Zuul to consider. However, we are positively surprised by swagger. It was a most useful and powerful documentation tool which we are sure to see again.
### Comments
We experienced some difficulties on the way to understanding how it all worked together, and making the modules communicate correctly with zuul as the concerned mother watching after her microservices.

## Collaborators and their modules
* **Andreas Waadeland - github: xIntern**

Has worked on the *friendslist* module and everything related to that

* **Marius Rikheim - github: rikmar15/Mariusrik**

Has worked on the *game* module and everything related to that

* **Andreas Ødegaard - github: odeand14**

I have worked on the *highscore* module and everything related to that, I have also implemented the Swagger documentation in my own and the other APIs except for the ones owned by the other group members. I have implemented the Swagger class and method for collecting all the pages under the main page and the security settings, except in the other members modules. I have been working on the e2e test related to users and login and the zuul/docker settings. Also documentation.

## Acknowledgments
* [Beginning Java EE 7](https://www.amazon.com/Beginning-Java-EE-Expert-Voice/dp/143024626X)
* Andrea's massive maven project [testing_security_development_enterprise_systems](https://github.com/arcuri82/testing_security_development_enterprise_systems)
* Spring
* Stackoverflow



Documentation is going to play a significant role in the marking of your project, i.e. 20% of it. You
need to have a “readme.md” file (in Markdown notation) in the root folder of your project. To avoid
having a too long file, you can have extra “.md” files under a “doc” folder, linked from the
“readme.md” file.

In the documentation, you need to explain what your project does, how it is structured, how you
implemented it and which different technologies you did use. Think about it like a “pitch sale” in
which you want to show a potential employer what you have learned in this course. This will be
particularly important for when you apply for jobs once done with your degree. Furthermore, in the
“readme.md” you also MUST have the following:

* Link to where you have your Git repository (make sure examiners can have read access to it
after the submission deadline is passed).
* If you deploy your system on a cloud provider, then give links to where you have deployed
it.
* Any special instruction on how to run your application.
* If you have special login for users (eg, an admin), write down login/password, so it can be
used. If you do not want to write it in the documentation, just provide a separated file in
your delivered zip file on Its Learning.
* For each student, a brief description of your individual contributions to the project. Also
make sure to specify your Git usernames so examiners can verify what you wrote based on
the Git history. Furthermore, make sure that, for each student, you specify which is the main
service s/he is responsible for.