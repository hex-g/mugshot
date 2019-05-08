# mugshot
---
**The profile image API**

### First steps
(Configuring the environment):

1. git clone https://github.com/hex-g/mugshot.git
    1. cd mugshot
2. git checkout [branch]
3. git submodule init
4. git submodule update 
5. Run the project in your IDE
---
### Usage
> URL: `http://localhost:9500/`

#### ![#1589F0](https://placehold.it/15/1589F0/000000?text=+) `POST`
* `head`
    * *key*: `authenticated-user-id`
    * *value*: [identification for the folder where the image will be stored]
* `body`
    * *key*: `image`
    * *value*: [file]
#### ![#c5f015](https://placehold.it/15/c5f015/000000?text=+) `GET`
* `head`
    * *key*: `authenticated-user-id`
    * *value*: [identification for the folder where the image will be retrieved]
#### ![#f03c15](https://placehold.it/15/f03c15/000000?text=+) `DELETE`
* `head`
    * *key*: `authenticated-user-id`
    * *value*: [identification for the folder where the image will be deleted]

> URL: `http://localhost:9500/utils/generateRandomImage`

#### ![#1589F0](https://placehold.it/15/1589F0/000000?text=+) `POST`
* `head`
    * *key*: `authenticated-user-id`
    * *value*: [identification for the folder where the generated image will be stored]
