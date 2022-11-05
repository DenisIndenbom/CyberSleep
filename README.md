# CyberSleepPlugin
CyberSleep is a plugin for skipping boring nights.

When a certain percentage (for example, 40%) of players are asleep, the plugin skips the night.

The plugin also shows how many players and how many players are needed to skip the night.

![image](https://user-images.githubusercontent.com/59681620/145094592-b717290f-3b73-47f5-ab92-ec6459915186.png)

### Example config.yml
```yml
# <playerName> - player name
# <c > - specifying the color
# <c0> - black
# <c1> - dark blue
# <c2> - dark green
# <c3> - dark aqua
# <c4> - dark red
# <c5> - dark purple
# <c6> - gold
# <c7> - gray
# <c8> - dark gray
# <c9> - blue
# <ca> - green
# <cb> - aqua
# <cc> - red
# <cd> - light purple
# <ce> - yellow
# <cf> - white

messages:
  help_message: This is a <c5>CyberSleepPlugin. <cf>The plugin helps to skip boring nights!
  enter_bed_message: <c7>Good night <playerName>!
  exit_bed_message: <c7>Good morning <playerName>!
  reload_message: <c5>CyberSleep <cf>reloaded!

time:
  morning: 0   # the time when the morning begins (ticks)
  night: 13000 # the time when the night begins (ticks)

sleeping_bar:
  title: <c9>Sleeping
  color: blue # blue, red, yellow, green, pink, purple - available colors
  delay_hiding: 100 # delay hiding the bar (ticks)

skip_night:
  delay: 100 # delay in skipping the night (ticks)
  players_sleeping_percentage: 0.3 # range from 0 to 1

```
