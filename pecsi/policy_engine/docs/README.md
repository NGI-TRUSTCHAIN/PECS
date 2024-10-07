# PECSi Service (Policy Engine) SDK Developers Documentation

## Overview

The PECSi Service is a privacy policies engine Software Development Kit. It was developed mainly for Android specific use, but it can be implemented also in other systems.

Please note: for detailed API JavaDoc documentation, please clone the repository and open the `index.html` file within `pecsi/policy_engine/docs/apidocs/` with your favorite web browser.

**Requirements:** This library has been developed using Java 11, so it's recommended to use Java >=11 for projects that implements this library.

**JVM-based languages usage note:** As normal Java code, this library can be used in Kotlin programs (especially for PECSi implementation in Android apps) and if desired also in Scala ones. Please refer to https://kotlinlang.org/docs/java-interop.html and https://docs.scala-lang.org/scala3/book/interacting-with-java.html for Kotlin and Scala inter-operability, respectively.

**Publication:** this library can be published to a public Java libraries repository, such as Maven Central or Google Maven Repository, so developers can easily import this library in their project and integrate it in build systems such as Maven or Gradle.

**Pre-configured EntryPoint:** Developers can use PECSi in 2 ways: using the pre-configured JAR file and the PECSi App, fully tested by the PECS team, or implement their own data flow using the APIs that this SDK exposes.

### Use of XACML 3.0 standard

For privacy policies operations, this library follows the OASIS XACML 3.0 standard. This can be seen on the generation of XML documents representing privacy policies and on the components that will be described in the next paragraph.

## Main components

- Policy Administration Point (PAP): generates and enforces policies, beyond keeping log of every privacy policy that has been enforced in the system
- Policy Decision Point (PDP): generates and logs JSON reports on potential privacy violations. In particular, this library exposes two differents kind of APIs for this work, one for Android specific use case and the other for generic use. We will delve deeper into these aspects in the next paragraphs
- Policy Retrieval Point (PRP): manages and retrieves privacy policy presets files to generate new policies

## Privacy violations check

In this chapter we will discuss about how to use exposed APIs to perform privacy violations checks, one of the core functionalities of PECSi. We will consider Android and generic scenarios separately. 

### Android scenario

In the context of the PECS project, we developed this library specifically for Android use case. In particular, Android applications developers can use it to implement privacy policies operations. We can schematize how the PECSi Service (integrating with a PECSi Client) works in 6 phases which are repeated cyclically:

#### User Interaction
In the PECSi Android application, users set a privacy policy according to their needs, choosing from 6 presets or defining a custom policy.

#### Policy Generation
Based on user preferences, the PECSi Policy Engine generates and validates a XACML 3.0 compliant policy document, alongside a JSON representation of the new policy.

#### Policy Enforcement
After generating the policy document, the PECSi Service proceeds to enforce the new policy in the system, alongside saving it both in XML and JSON format and adding it to the history of privacy policies that have been created/enforced in that system.

#### Application Permissions Check
After the enforcement of the first policy, PECSi Service starts to perform application permissions check rounds. In order to do this, it checks periodically for every third-party packages, then lists granted system permissions for that packages. After that, a cross-check between these data and the enforced user policy is performed.

#### Alerts Generation and Logging
If violations are found in the previous described check, PECSi Service proceeds to generated a JSON based alert file, that is saved locally and sent to the frontend in order to inform the user, alongside triggering the vehicle haptic feedback if needed.

#### User Interaction
At this point, users can see in the PECSi App if any potential privacy violations are found and they are guided to perform actions to resolve those alerts.

### Generic scenario using Request object

Although the PECSi Policy Engine was specifically designed and developed for Android Architecture, we additionally designed some parts so that them can be used to implement privacy operations in other operating systems such as desktop Linux / Windows. The main components that can be used in order to perform such operations are Request and Decision objects. They represent a data access request a general purpose PDP (see above), respectively.
Request object can be easily mapped from a JSON file representing a data access request (e.g.n using the Jackson library).

## Practical Android PECSi Service implementation

In this section we will cover the fundamental steps to a basic use of PECSi SDK. Starting from this entry point, there are virtually no limits to the level of customization of the control of privacy policy operations.

### Importing PECSi Policy Engine into your project

In order to use PECSi in your JVM-based language project (after that it has been published on a Central repository), you can import it as a dependency using a building system. Considering a Java + Maven project as an example, you can import this library declaring it as a dependency in your  Project Object Model file (`pom.xml`), using the snippet as follow:

```xml
<dependency>
    <groupId>com.pecs</groupId>
    <artifactId>pecsi</artifactId>
    <version>1.1.1</version> <!-- insert current version here -->
</dependency>
```

Subsequently, you can import PECSi in project files header:

```java
import com.pecs.pecsi.*; // import every PECSi component
```

### Initialization of main components

```java
Administration pap = new Administration();
FtpUploader ftp = new FtpUploader();
PermissionsChecker checker = new PermissionsChecker();
PolicyValidator validator = new PolicyValidator();

Thread checkerThread = new Thread(checker); // create a new thread for permissions checker
checkerThread.start();
```





