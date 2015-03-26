# kdrlivestream
Live streaming app for Red5 with authentication using a MySQL database.

## How to use
Unzip the release, copy *kdrlivestream.ini* to Red5's conf subdirectory, and create the needed MySQL tables in your database according to the SQL sample file *kdrlivestream.sql*.
Copy the kdrlivestream directory from the .zip file to Red5's webapps subdirectory.

### kdrlivestream.ini settings

General section:
- allowonlyoneinstanceperuser - set this to 1 to allow only one playback instance per user
- storelastseeninfoindb - set this to 1 to have kdrlivestream store to the database who streams and plays what (every 5 seconds)

MySQL DB section:
- host - MySQL host (and optionally the port)
- dbname - MySQL database name
- user
- password
- tableprefix - each table used by kdrlivestream has this prefix
