## Public Patch Notes:
- line 1
- line 2
- line 3

<br><br>

## Staff Patch Notes:
- staff line 1
- staff line 2
- staff line 3

<br><br>

## Notes:
### Other modules branch dependencies:
- some-module:feature/some_feature
- some-other-module:feature/some_other_feature
### Other notes:
- note 1
- note 2
- note 3

## Demo
[![Demo Video](https://img.youtube.com/vi/YOUTUBE_VIDEO_ID/0.jpg)](https://www.youtube.com/watch?v=YOUTUBE_VIDEO_ID)

## Dev Server Update Command
### Normal Start
#### Cobblemon
```Bash
cd /mnt/main/minecraft3/control_plane
git fetch
git pull
pip install -r requirements.txt --break-system-packages
cd /mnt/main/minecraft3
python3 -m control_plane.app \
--non-interactive \
--pull-docker-images=latest \
--stop-cobblemon-servers \
--build=example-module:feature/example-feature \
--start-cobblemon-servers
```
#### Pixelmon
```Bash
cd /mnt/main/minecraft3/control_plane
git fetch
git pull
pip install -r requirements.txt --break-system-packages
cd /mnt/main/minecraft3
python3 -m control_plane.app \
--non-interactive \
--pull-docker-images=latest \
--stop-pixelmon-servers \
--build=example-module:feature/example-feature \
--start-pixelmon-servers
```

### Advanced usage
Please consult the [documentation](https://dev-wiki.mmorealms.gg/en/manager/control-plane)