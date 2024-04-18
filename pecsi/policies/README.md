# Privacy policies for PECSi

## Paranoia levels

Within PECSi, the user will be able to choose between 5 presets "Paranoia Levels" and also create a new policy by operating on the GUI of PECSi. Here's the list of the 5 policy presets (in order of level of shared data):

- **MAX**: quite all data shared, very-low level of privacy, max level of infotainment customization
- **HIGH**: some basic adjustment of the amount of shared data, low level of privacy, high level infotainment customization
- **MID**: most balanced preset, mid privacy level, mid infotainment customization
- **LOW**: strongly-reduced amount of shared data. This results in medium-high level of privacy and low-medium level of infotainment customization
- **VERY LOW**: only few data are accessible by the applications, resulting in high level of privacy and a very-low level of infotainment customization

In addition to these, we want to develop a "Zero-Share Policy", that aims to reduce to zero the shared data for users that want maximum level of privacy, regardless to the infotainment customization.

## Policies description

To describe policies, we'll use [ALFA](https://alfa.guide/) (Abbreviated Language For Authorization), that compile directly to XACML 3.0.

## Policy Processing

To process policies and evaluate data access requests from the applications, we'll use [AuthZForce](https://authzforce.ow2.org/) (Java API), a XACML 3.0 fully-compliant ABAC framework.

## Data collected by the applications

Here's a comprehensive list of data collected by applications, based on:

- [Google data safety guide](https://developer.android.com/privacy-and-security/declare-data-use)
- [ANT Data Privacy Paper](https://antprivacy.org/paper.pdf)

#### List of collected data (currently updating)

- Location:
    - Approximate location
    - Precise location

- Personal Information:
    - Name
    - Email address
    - User ID
    - Address
    - Phone number
    - Race and Ethnicity
    - Political or Religious Beliefs
    - Sexual Orientation

- Financial Information:
    - User payment info
    - Purchase history
    - Credit score
    - Other financial info

- Health and Fitness:
    - Health info
    - Fitness info

- Messages:
    - Emails
    - SMS or MMS

- Photos or Videos:
    - Photos
    - Videos

- Files and Documents

- Calendar

- Contacts

- App Activity

- App Interactions

- In-App Search History

- Installed Apps

- Other User-Generated Content

- Web Browsing

- App Info and Performance

- Device or Other IDs:
    - IMEI
    - MAC address
    - Advertising identifier
