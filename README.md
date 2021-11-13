# Blip Blop
My own version of [Pong](https://www.ponggame.org), an old-time arcade game.
>Pong is a two-dimensional sports game that simulates table tennis. The player controls an in-game paddle by moving it vertically across the left or right side of the screen. They can compete against another player controlling a second paddle on the opposing side. Players use the paddles to hit a ball back and forth. The goal is for each player to reach eleven points before the opponent; points are earned when one fails to return the ball to the other.
>
>[_-Wikipedia_](https://en.wikipedia.org/wiki/Pong#Gameplay)

1. [Game Design Document](#game-design-document)
2. [Audio Licences Used](#audio-licences-used)
2. [Project Comments](#project-comments)
4. [Project Kanban Board](https://github.com/Slideshow776/Blip-Blop/projects/1?add_cards_query=is%3Aopen)

TODO: 
* <del>Youtube trailer</del>
* <del>Click here to play on Google Play!</del>
* <del>Click here to play for free on Desktop!</del>


![screenshot image of basic game play](https://user-images.githubusercontent.com/4059636/128635517-9c09e98f-2bc8-41ca-9de9-ef243da500e4.png)

![screenshot image of classic game play](https://user-images.githubusercontent.com/4059636/141648731-59d1f9de-316f-46f9-a1c1-35e2efd132bd.png)
![images of splash, menu and options](https://user-images.githubusercontent.com/4059636/141648729-b82ca1be-b73a-46ff-a907-8caf47a08c74.png)


# Game Design Document

1. Overall Vision
    * **Write a short paragraph explaining the game:**
    Blip Blop is a remaster of one of the top ten oldest video games in world history. The player controls a paddle on their side of the screen to try to hit the ball away from themselves, and score a goal by bypassing the other players' paddle.
    The game may be played solo, or with a friend sharing the same device. The game will also be integrated with `Google Play Services in Android, which only features some simple achievements.
        
    * **Describe the genre:**
    The game is a table tennis-themed arcade sports video game.    
    
    * **What is the target audience?**
    The game is appropriate for children and older and is thought to be enjoyed by casual gamers who want to share a game with their friends. The target platform is Android phones, but it will also be available for free on desktop.
    
    * **Why play this game?**
    The game tests your mettle against an A.I or a friend. The simple gameplay is easy to get into but may prove challenging to maintain. The modern theme is complemented by visual effects and other telltale gameplay information.
    
    
2. Mechanics: the rules of the game world
    * **What are the character's goals?**
    The players' goals are to hit the ball away from their goal post. They may do so by moving their paddle along a strict one-dimensional scale.    
        
    * **What abilities does the character have?**
    The players have no other abilities.    
    
    * **What obstacles or difficulties will the character face?**
    The game will provide classic and challenge gameplay. In _classic mode_ there are no other difficulties other than besting your opponent by avoiding them to score a goal and to score your own goals against them.
    In _challenge mode_, various new challenges per goal will be presented that change the gameplay ever so slightly in various interesting new ways: like a gravity field, extra-long paddles, fog of war in the middle of the playing field, etc.
    
    * **What items can the character obtain**
    The players may obtain no items.    
    
    * **What resources must be managed?**
    The first player to score 11 goals wins.     
    
    * **Describe the game-world environment.**
    In classic mode, the playing field is empty from any obstruction, and the ball will bounce off the walls.
    The background will feature GLSL graphic visualizations that have no impact on gameplay.
    
    
3. Dynamics: the interaction between the player and the game mechanics
    * **What hardware is required by the game?** 
        Android devices will be required to have a functional touch screen.
        Desktop menu controls are decided by the _mouse_, the `ESC` and `BACK` keys may also be used for navigation. Gameplay will be controlled with the `A` and `D` keys for the top player, and the `Left` and `Right` keys for the bottom player. The desktop will also feature controller support.
    
    * **What type of proficiency will the player need to develop to become proficient at the game?**
    Players will need to outsmart the opponent by shooting the ball to where it is most likely to not be caught. This requires a basic understanding of the game's physics and mechanics. Letting the ball hit the edges of the paddle generates the most unpredictable angles as well as increases the risk of missing the ball entirely.
        
    * **What gameplay data is displayed during the game?**
    Each players' score is shown on the right side of their own space in a tilted fashion for all to read equally.    
    
    * **What menus, screens, or overlays will there be?**
    A splash screen showing the author of the game. A menu screen showing the start game in classic or challenge mode, as well as options.
    The options screen features control over audio and music, as well as in Android the ability to turn on/off `Google Play Services.
    
    * **How does the player interact with the game at the software level?**
    The players may elect to leave the game by not playing, this will prompt the A.I to continue to play for them. The player may quit the game or navigate by the operating system controls `BACK` button, or `ESC` on desktop.
    
4. Aesthetics: the visual, audio, narrative, and psychological aspects of the game
    * **Describe the style and feel of the game.**
    The style features a modern variation of the classical arcade feel with a beautiful font, simplistic controls, UI, and options menu. The player paddles' and the ball are in 3D while everything else is in 2D. The background of the game shows simple non-disruptive ever-changing GLSL graphics of colors variations. The music and audio are simple, repetitive, and crude in a typical classical experience.    

    * **Does the game use pixel art, line art, or realistic graphics?**
    The player paddles' and ball are in 3D, everything else is in 2D. The graphics are in a smooth blocky fashion.
    
    
    * **What style of background music, ambient sounds will the game use?**
    Arcade or 8-bit music and audio effects.
        
    * **What is the relevant backstory for the game?**
    There is no backstory or history other than playing an old game remastered.
        
    * **What emotional state(s) does the game try to provoke?**
    Thoughtful relaxed concentration or flow-like state.
        
    * **What makes the game fun?**
    The novelty of a long-forgotten game, besting the A.I or your friends in competitive gameplay, and the challenge mode features more modern challenges.
    
    
5. Development
    
    * **List the team members and their roles, responsibilities, and skills.**    
    This project will be completed individually; graphics and audio will be obtained from third-party websites that make their assets available under the Creative Commons license, and so the main task will be programming and creating some graphics.
    
    * **What equipment is needed for this project?**    
    A computer (with keyboard, mouse, and speakers) and internet access will be necessary to complete this project.
    
    * **What are the tasks that need to be accomplished to create this game?**    
    This project will use a simple Kanban board hosted on the project's GitHub page.
    The main sequence of steps to complete this project is as follows:    
        * Setting up a project scaffold
        * **Programming game mechanics and UI**
        * **Creating and obtaining graphical assets**
        * Obtaining audio assets
        * Controller support
        * **Polishing**
        * Deployment

    * **What points in the development process are suitable for playtesting?**    
    The main points for playtesting are when the basic game mechanics of the level screen are implemented, and when it is visualised. The questions that will be asked are: 
        * Is the gameplay and UI understandable?
        * Is the gameplay interesting?
        * How do the controls feel?
        * How is the pace of the game?
        * Are there any improvement suggestions?        
    
    * **What are the plans for publication?**
    This game will be made available for free on desktop. It will be deployed on the Google Play store for a small fee and advertised to various indie game-portal websites (LibGDX, Reddit, ...). Gameplay images and a trailer video will be posted and marketed via social media.

## Audio Licences Used
* Audio Effect: [LittleRobotSoundFactory](https://freesound.org/s/270333/)
* Audio Effect: [LittleRobotSoundFactory](https://freesound.org/s/270331/)
* Music: [joshuaempyre](https://freesound.org/s/251461/)

# Project comments
## Google Play Services (GPS)
These three resources helped me to a great extent implement GPS.
* Leaderboards in Android Game: https://developers.google.com/games/services/android/leaderboards
* Google Play Services for Libgdx: https://stackoverflow.com/questions/48135531/signinsilently-failure-when-trying-to-sign-in-to-googleplay-game-services-w/48135617#48135617
* Implementing and using an interface for cross-platform usability: https://github.com/libgdx/libgdx/wiki/Interfacing-with-platform-specific-code
* The SHA-1 fingerprint is your general android development environment key found in `C:\Users\Sandra Moen\.android\debug.keystore`. To get the SHA-1 run `$keytool -list -v -keystore .\debug.keystore`, this is to be used in both `https://play.google.com/console` and `https://console.cloud.google.com`.
* How to implement [achievements in Android games](https://developers.google.com/games/services/android/achievements).

## LibGDX sound
I haven't seen this until now, you can change the pitch and pan when playing audio.
```
long play(float volume,
          float pitch,
          float pan)
Plays the sound. If the sound is already playing, it will be played again, concurrently.
Parameters:
volume - the volume in the range [0,1]
pitch - the pitch multiplier, 1 == default, >1 == faster, <1 == slower, the value has to be between 0.5 and 2.0
pan - panning in the range -1 (full left) to 1 (full right). 0 is center position.
Returns:
the id of the sound instance if successful, or -1 on failure.
```
This means you can do something like this: `explosionSound.play(soundVolume, MathUtils.random(0f, 2f), 0f)`, which produces a lot of different explosion sounds from just one audio file.

For other project specifics check out the [commits](https://github.com/Slideshow776/Blip-Blop/commits/master).

[Go back to the top](#blip-blop).
