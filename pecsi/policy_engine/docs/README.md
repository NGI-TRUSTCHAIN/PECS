# PECSi Service (Policy Engine) SDK Developers Documentation

## Overview

The PECSi Service is a privacy policies engine Software Development Kit. It was developed mainly for Android specific use, but it can be implemented also in other systems.

Please note: for detailed API JavaDoc documentation, please clone the repository and open the `index.html` file within `pecsi/policy_engine/docs/apidocs/` with your favorite web browser.

**Requirements:** This library has been developed using Java 11, so it's recommended to use Java >=11 for projects that implements this library.

**JVM-based languages usage note:** As normal Java code, this library can be used in Kotlin programs (especially for PECSi implementation in Android apps) and if desired also in Scala ones. Please refer to https://kotlinlang.org/docs/java-interop.html and https://docs.scala-lang.org/scala3/book/interacting-with-java.html for Kotlin and Scala inter-operability, respectively.

**Publication:** this library can be published to a public Java libraries repository, such as Maven Central or Google Maven Repository, so developers can easily import this library in their project and integrate it in build systems such as Maven or Gradle.

### Use of XACML 3.0 standard

For privacy policies operations, this library follows the OASIS XACML 3.0 standard. This can be seen on the generation of XML documents representing privacy policies and on the components that will be described in the next paragraph.

## Main components

- Policy Administration Point (PAP): generates and enforces policies, beyond keeping log of every privacy policy that has been enforced in the system
- Policy Decision Point (PDP): generates and logs JSON reports on potential privacy violations. In particular, this library exposes two differents kind of APIs for this work, one for Android specific use case and the other for generic use. We will delve deeper into these aspects in the next paragraphs
- Policy Retrieval Point (PRP): manages and retrieves privacy policy presets files to generate new policies

## Privacy violations check

In this chapter we will discuss about how to use exposed APIs to perform privacy violations checks, one of the core functionalities of PECSi. We will consider Android and generic scenarios separately. 

### Android scenario

In the context of the PECS project, we developed this library specifically for Android use case. In particular, Android applications developers can use it to implement privacy policies operations.


### Generic scenario using Request object

## Practical PECSi Service implementation

<!-- importing
initialize pecsi service
initialize pap and pdp -->

### Base flow diagram

The diagram below shows the essential parts and data flows that a correct implementation of the PECSi Policy Engine must have.



<!-- insert flows for android and generic scenarios -->






