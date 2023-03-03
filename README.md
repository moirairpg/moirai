# Malaquias: a GPT-3 bot that uses OpenAI's API

Malaquias is a Discord bot powered by JDA that is connected to OpenAI's GPT-3 API. It is cabaple of generating text using base or finetuned models, and was made to act primarily as a chatbot and as an RPG dungeon master. I was inspired by AI Dungeon, for which I developed a lot of scenarios and scripts, as well as KoboldAI, another GPT-powered API compatible with other GPT models. For a Discord bot that works with KoboldAI, please see [Jurandir](https://github.com/thaalesalves/jurandir.git), Malaquia's elder brother.

## Technologies used
* Java 19
* Spring Boot
* Spring WebFlux
* Spring Data
* JDA
* PostgreSQL

## What does it do?
Well, pretty much the same as ChatGPT, but for Discord. Same as Jurandir, but with GPT-3. Same as KoboldAI and AI Dungeon, but on Discord. Not much to say here.

## How to use it?
1. Clone the repo
2. Create an app on Discord API
3. Subscribe to OpenAI's API
4. Use both API keys from both APIs mentioned above as environment vars (check `src/main/resources/application.yaml` for the name of the vars)
5. Add the ID of the channels you want the bot to interact on discord (check `src/main/resources/bot-settings.json` for this info)
6. Play around with those settings to change bot behavior or even add new channels and personalities
7. Start the bot through your IDE, running `mvn spring-boot:run` on your shell while on the repo folder or by executing the JAR file directly after compiling the app with `java -jar malaquias-0.0.1-SNAPSHOT.jar`

PS: I will elaborate more on these instructions in the future.