#!/bin/bash

#key pair (public key, private key) creation
keytool -genkeypair -alias senderKeyPair -keyalg RSA -keysize 2048 \
  -dname "CN=PECS" -validity 365 -storetype JKS \
  -keystore sender_keystore.jks -storepass changeit

#public key publication (self-signed certificate)
keytool -exportcert -alias senderKeyPair -storetype JKS \
  -keystore sender_keystore.jks -file \
  sender_certificate.cer -rfc -storepass changeit

#public key storing 
keytool -genkeypair -alias receiverKeyPair -keyalg RSA -keysize 2048 \
  -dname "CN=PECS" -validity 365 -storetype JKS \
  -keystore receiver_keystore.jks -storepass changeit 
 
keytool -delete -alias receiverKeyPair -storepass changeit -keystore receiver_keystore.jks 

#public key loading
keytool -importcert -alias receiverKeyPair -storetype JKS \
  -keystore receiver_keystore.jks -file \
  sender_certificate.cer -rfc -storepass changeit

