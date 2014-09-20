##phantom-dms
Auth8 is a RESTful API for SOLiD DMS units.

####Compatablity
* 1200 v4.3

####Documentation
Public documentation source is located on the `gh-pages` branch.
iIt can be viewed at https://solid-software.github.io/phantom-dms/ .

Private documentation can be found in the Github wiki.

####Adding SSL SOLiD CA Certificate

**Generate a key for the new API address:**<br>
`openssl genrsa -out phantom.key 2048`

**Generate a CSR from the key making sure Common Name (eg, YOUR name) [ ]: 10.0.0.1:**<br>
`openssl req -new -key phantom.key -out phantom.csr`

**Sign the request:**<br>
`openssl x509 -req -in phantom.csr -CA solid-ca-cert.pem -CAkey solid-ca-key.key -CAcreateserial -out phantom.crt -days 7300`

**Export to a p12 keystore - Make sure export password is not null:**<br>
`openssl pkcs12 -export -in phantom.crt -inkey phantom.key -out keystore.p12 -name phantom -certfile solid-ca-cert.pem`

**Export the keypair from the generated keystore and import it into the resources `keystore.jks`.**

**NB. The password for each individual certificate _MUST_ be the same as the keystore password otherwise the certificate cannot be decrypted at runtime.**<br>
`11aaBB**`

####Usage
Auth8 can be controlled using a single command line:
```
java -D<flag> -jar <file.war> [start|stop|restart|status]
```

| Argument  | Action                                                   |  
| --------- | -------------------------------------------------------- |  
| `start`   | Starts Auth8 (default)                                 |  
| `stop`    | Stops Auth8 if it is online                            |  
| `restart` | Restarts Auth8 or starts it if it is currently offline |  
| `status`  | Displays the current operational Auth8 status          |  

If no argument is specified it will default to `start`. Any other argument will display the usage instructions. _(e.g. `java -jar *.war help`)_

Additionally the first time Auth8 is run a DMS type must be specified using the `dms` flag.

```
java -Ddms=1200 -jar .war start
```

| DMS Types |  
| --------- |  
| `1200`    |  
| `600`     |  
| `R6`      |  
 
If you running Auth8 on non test system you must run it under `nohup`.

```
nohup java -Ddms=1200 -jar .war start &
```

Once the process is running in the background it can be bought back into the foreground by checking the jobs:

```
jobs
```

And then using the job number:

```
fg %<job_number>
```

The job can then be quit. If you try to quit the application whilst it is running in the background the JPA connection pool will stay open and lock the database. If you need to send the process back to the background:

```
fg %<job_number> &
```

####Building
TODO

####Installing
TODO