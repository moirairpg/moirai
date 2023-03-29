# ChatRPG: a virtual Dungeon Master for RPG adventures
[![Maintainability](https://api.codeclimate.com/v1/badges/9c5b99e778dc1b68830a/maintainability)](https://codeclimate.com/github/thaalesalves/chatrpg/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/9c5b99e778dc1b68830a/test_coverage)](https://codeclimate.com/github/thaalesalves/chatrpg/test_coverage) ![GitHub Workflow Status (with branch)](https://img.shields.io/github/actions/workflow/status/thaalesalves/chatrpg/maven-workflow.yaml?branch=master) ![GitHub issues](https://img.shields.io/github/issues/thaalesalves/chatrpg) ![GitHub](https://img.shields.io/github/license/thaalesalves/chatrpg)

ChatRPG is a Discord bot powered by JDA that is connected to OpenAI's GPT-3 API. It is cabaple of generating text using base or finetuned models, and was made to act primarily as a chatbot and as an RPG dungeon master. Developed by veteran AI Dungeon and NovelAI players and contributors, aimed to make text-based RPGs more flexible and easier to use on Discord.

## Technologies used
* Java 17
* Maven
* Spring Boot
* Spring WebFlux
* Spring Data
* JDA
* PostgreSQL

## What does it do?
ChatRPG is mainly focused on RPG DM'ing, but it can also be used as a normal chatbot. It has the ability to work with different Personas, which are configs that are bound to specific channels so the bot behaves in specific ways given the Persona described to it. Personas can be set with two intents, which are `chat` and `rpg`, and the bot will behave accordingly.

## Is it free?
Yes and no. The code is free for usage, and so is Discord's API. But OpenAI's completion APIs are not free. You pay by usage, and you're charged for every 1K tokens processed by the API. Do take that into consideration before running ChatRPG or choosing a model to run it.

## Does it work with other GPT models?
For now, only the ones in the GPT-3 and ChatGPT model families in OpenAI's API. ChatRPG does not support Eleuther AI's models as of now, nor does it support usage of OpenAI's GPT-2 models locally.

## Building from source
To run ChatRPG, you can clone the code and execute him locally. To do so, you'll need to have JDK 17, a PostgreSQL database (ChatRPG comes with a `docker-compose.yaml` for convenience) and Maven. You will also need to have a Discord Developer account set up with an app create with the proper permissions, as well as an OpenAI API account.

### Discord app permissions
To have ChatRPG work, your Discord app needs to have both the `Server Members` and `Message Content` intents allowed in the `Bot` menu of the Discord Developer dashboard. When inviting the bot to your server, make sure to add the `bot` and `applications.commands` scopes added in the `OAuth > URL Generator` menu of the Discord Developer dashboard. The bot also needs to have permission to read, send and delete messages in the channels specified in its personas, because when a comment is problematic, it will try to delete it.

### API keys
You need to have both yout Discord API Key and your OpenAI API key in hand to set up ChatRPG.

### Building
1. Clone the repo
2. Create copies of `channel-config-sample.yaml` and `worlds-sample.yaml` and tweak as you see fit.
    - Follow the proper format to create configurations and worlds with their own lorebooks
3. Add both keys to the bot's `application.yaml`
    - The recommended way of doing this is setting the keys to the `DISCORD_BOT_API_TOKEN` and `OPENAI_API_TOKEN` environment variables respectively, in which case you won't need to modify those values in the YAML; and everything is also safer that way
4. Enable Developer Mode on your Discord client and right click > copy ID of the channels you want the bot to have access to
5. Add those IDs to the desired persona by following the proper format described.
6. Set up the database and either rewrite the values in the YAML or add the values to their respective environment variable
    - Same advice as for the API keys: instead of rewriting the config file, add the proper environment variables
7. Tweak the persona as you'd like, always keeping in mind that it has restrictions and a proper way to be used
    - The `personality` field can be tricky because the way to describe a persona varies by model used. A persona that works well for `text-davinci-003` might not work well for `text-babbage-001` or `gpt-3.5-turbo`. Keep that in mind.
    - Tweak the moderation values as you see fit. For more info on those, check OpenAI's moderation API documentation. Moderation can either be by topic value or absolute.
    - For info on the other configs such as model names, values for temperature and such, refer to OpenAI's completion API documentation.
8. Compile the code with `mvn clean package`
9. Run the bot
    - Through the IDE of your choice
    - Through the console with Maven by running `mvn spring-boot:run` on the bot's root folder
    - Through the console by running the JAR file directly with `java -jar chatrpg-0.0.1-SNAPSHOT.jar`
10. Run `/chconf set <config-id>` to link a configuration from the YAML to the channel where the command is run

## Features
ChatRPG was made with RPG DM'ing in mind, so we're striving to add commands and features that make that experience richer.

* Slash commands to manage the bot.
* Custom personas with their own model settings.
* Custom worlds for the bot to generate adventures in.
* Lorebook with regex capabilities to improve the AI's context on the adventure.
* Lorebook entries that can be set as player characters so the AI knows who's who and refrains from speaking on behalf of players.
* Moderation filters powered by OpenAI's API to avoid problematic and abusive topics, with custom thresholds per topic.
* Compatibility with GPT-3, GPT-3.5 (ChatGPT) and GPT-4.
* Can also be used as a normal chatbot if the right intent is used.
