#!/bin/bash

openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 365 -nodes -subj '/CN=jPasskittest'
openssl pkcs12 -export -in cert.pem -inkey key.pem -out jpasskittest.p12 -name "jpasskit"
rm key.pem cert.pem

