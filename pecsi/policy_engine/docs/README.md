# PECSi Service (Policy Engine) SDK Developers Documentation

## Overview

The PECSi Service is a privacy policies engine Software Development Kit. It was developed mainly for Android specific use, but it can be implemented also in other systems.

Please note: for detailed API JavaDoc documentation, please clone the repository and open the `index.html` file within `pecsi/policy_engine/docs/apidocs/` with your favorite web browser.

**Requirements:** This library has been developed using Java 11, so it's recommended to use Java >=11 for projects that implements this library.

**JVM-based languages usage note:** As normal Java code, this library can be used in Kotlin programs (especially for PECSi implementation in Android apps) and if desired also in Scala ones. Please refer to https://kotlinlang.org/docs/java-interop.html and https://docs.scala-lang.org/scala3/book/interacting-with-java.html for Kotlin and Scala inter-operability, respectively.

### Use of XACML 3.0 standard

For privacy policies operations, this library follows the OASIS XACML 3.0 standard. This can be seen on the generation of XML documents representing privacy policies and on the base architecture that will be described in the next paragraph.

## Base PECSi Service architecture

Architecture diagram will be inserted here

<!-- insert also client side in diagram -->

## Privacy violations check

In this chapter we will discuss about how to use exposed APIs to perform privacy violations checks, one of the core functionalities of PECSi. We will consider Android and generic scenarios separately. 

### Android scenario

In the context of the PECS project, we developed this library specifically for Android use case. In particular, Android applications developers can use it to implement privacy policies operations.


### Generic scenario using Request object

## Practical PECSi Service implementation

<!-- importing
initialize pecsi service
initialize pap and pdp -->

### Essential components diagram

#### Android implementation

#### Generic implementation






