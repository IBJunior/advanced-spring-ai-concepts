# Implémentation de Retrieval Augmented Generation avec Spring AI

Ce repository contient des exemples montrant l'implémentation de RAG dans Spring AI en
utilisant [OpenAI](https://platform.openai.com/).

## Prérequis

Avant de démarrer le projet, il est impératif d'avoir une clé API d'OpenAI. Pour obtenir cette clé, vous pouvez
consulter la [documentation des clés API d'OpenAI](https://platform.openai.com/api-keys). Une fois obtenue, vous pouvez
soit l'exporter dans une variable d'environnement, soit la renseigner dans le fichier **application.properties**.

````
#Exporter la clé dans la variable d'environnement OPENAI_API_KEY
spring.ai.openai.api-key=${OPENAI_API_KEY:'ou renseignez la ici'}
````

**Note :** Ce projet utilise la version 1.0.0-SNAPSHOT de Spring AI, ce qui pourrait entraîner des divergences au
niveau des exemples si vous utilisez
une version ultérieure. En cas de divergences, vous avez deux options :

- Si vous générez le projet avec [Spring Initializer](https://start.spring.io/), vous pouvez vous assurer de travailler
  avec la même version que moi en consultant le fichier `pom.xml` du projet.
- Si cette version snapshot n'est plus disponible, vous pouvez simplement consulter la documentation de Spring AI de la
  version que vous utilisez, pour voir les divergences.

## Structure du projet

### Dépendances

Pour simplifier le test des différents concepts, j'ai utilisé spring-boot-starter-web, où les exemples sont répartis
dans des contrôleurs. De même, afin de faciliter la mise en
place des services (postgres sql dans le cas de ce projet) utilisés par le projet, j'ai ajouté la
dépendance [Docker Compose](https://spring.io/blog/2023/06/21/docker-compose-support-in-spring-boot-3-1)
de Spring Devtools et aussi la dépendance de postgresql (voir le `pom.xml`). Pour cela, il vous faut
avoir [Docker](https://docs.docker.com/engine/install/)
installé sur votre machine.

### VectorStoreInitializationRunner

Cette classe implémente [le pipeline ETL](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html) de Spring
pour pouvoir enregistrer les informations extraites des documents dans la base de données
vectorielle ([PGVector](https://github.com/pgvector/pgvector)). Elle
étend `ApplicationRunner` pour s'exécuter au
lancement de l'application et charger les informations.

### ChatController

Contient les exemples de chat en intégrant la récupération avec `QuestionAnswerAdvisor` des documents similaires dans la
base de données Vectorielle lors de la génération.

```` 
this.chatClient.prompt()
                .user(quest)
                .advisors(new QuestionAnswerAdvisor(this.vectorStore, SearchRequest.defaults()))
                .call().content();
````

### MemoryController

Contient l'exemple d'utilisation de `ChatMemory` avec `MessageChatMemoryAdvisor` pour sauvegarder l'historique de
conversation entre un utilisateur et un LLM.

````
this.chatClient.prompt()
                .user(message)
                .advisors(new MessageChatMemoryAdvisor(chatMemory))
                .advisors(a -> {
                    a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, userId)
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100);
                })
                .call().content();
````

### Pour aller plus loin

Pour aller plus loin je vous recommande la documentation de Spring AI qui est très détaillée et complète. Pour mieux
comprendre l'architecture RAG, je vous recommande
[cet article d'IBM.](https://research.ibm.com/blog/retrieval-augmented-generation-RAG)

- [Spring AI](https://docs.spring.io/spring-ai/reference/)
- [OpenAI](https://platform.openai.com/docs/overview)
- [Base de données vectorielle](https://www.ibm.com/topics/vector-database)