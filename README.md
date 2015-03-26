# kdrlivestream
Live streaming app for Red5 with authentication using a MySQL database.

## Introduction
If you want to stream live content, you can use Red5 and it's built-in webapp called *live*, but it doesn't use authentication. Everyone will be able to publish and watch streams using your private Red5 server.

This Red5 webapp always authenticates publishers using usernames and passwords stored in a MySQL database. By default, every viewer has to be authenticated too, but publishers can specify that their streams are public. Public streams can be watched without any authentication.

Optionally the webapp can periodically store active publisher and viewer info into the database (who watches and publishes what).

### How to publish using *kdrlivestream*
You can use any publisher client which supports RTMP protocol. Example command line for ffmpeg:

```
ffmpeg -i example.avi -vcodec h264 -vb 250k -force_key_frames "expr:gte(t,n_forced*1)" -acodec mp3 -ar 44100 -ab 128k -f flv "rtmp://localhost:1935/kdrlivestream?u=user1@ha5kdr.hu&p=password/stream1"
```

Publishing a public stream:

```
ffmpeg -i example.avi -vcodec h264 -vb 250k -force_key_frames "expr:gte(t,n_forced*1)" -acodec mp3 -ar 44100 -ab 128k -f flv "rtmp://localhost:1935/kdrlivestream?u=user1@ha5kdr.hu&p=password&public=1/stream1"
```

### How to watch a stream published through *kdrlivestream*
Streams can be watched with any client which supports RTMP protocol, for example VLC:

```
vlc "rtmp://localhost:1935/kdrlivestream?u=user2@ha5kdr.hu&p=password/stream1"
```

Watching a public stream:

```
vlc "rtmp://localhost:1935/kdrlivestream/stream1"
```

### How to install *kdrlivestream*
Unzip the release, copy *kdrlivestream.ini* to Red5's conf subdirectory, and create the needed MySQL tables in your database according to the SQL sample file *kdrlivestream.sql*.
Copy the kdrlivestream directory from the .zip file to Red5's webapps subdirectory.

#### kdrlivestream.ini settings

General section:
- allowonlyoneinstanceperuser - set this to 1 to allow only one playback instance per user
- storelastseeninfoindb - set this to 1 to have kdrlivestream store to the database who streams and plays what (every 5 seconds)

MySQL DB section:
- host - MySQL host (and optionally the port)
- dbname - MySQL database name
- user
- password
- tableprefix - each table used by kdrlivestream has this prefix
