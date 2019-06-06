# BungeeWebServer [![Build Status](https://ci.jackz.me/job/BungeeWebServer/badge/icon)](https://ci.jackz.me/job/BungeeWebServer/)
Creates a JSON web server to get servers & player count 

### Configuration
* **server.enabled** - enable/disable web server
* **server.ip** - ip to listen on
* **server.port** - port to listen on

### Example Response

```json
{
  "total_players": 1,
  "version": "git:Waterfall-Bootstrap:1.14-SNAPSHOT:57a7f98:284",
  "players": [
    {
      "uuid": "b0c16432-67a6-4e3d-b49a-61b323c49b03",
      "username": "Jackz",
      "server": "survival"
    }
  ],
  "servers": [
    {
      "name": "lobby",
      "player_count": 0,
      "motd": "A lobby server"
    },
    {
      "name": "survival",
      "player_count": 1,
      "motd": "A survival server"
    },
    {
      "name": "games",
      "player_count": 0,
      "motd": "Games"
    },
    {
      "name": "testingserver",
      "player_count": 0,
      "motd": "A Testing Server"
    }
  ]
}```
