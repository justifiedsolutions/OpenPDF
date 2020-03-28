# Justified Solutions OpenPDF #

OpenPDF is a Java library for creating PDF files with a LGPL and MPL open source license. OpenPDF is the LGPL/MPL open source successor of iText, and is based on a fork, of a fork, of iText 4 svn tag. We welcome contributions from other developers. Please feel free to submit pull-requests and bugreports to this GitHub repository.

 [![Build Status](https://travis-ci.org/LibrePDF/OpenPDF.svg?branch=master)](https://travis-ci.org/LibrePDF/OpenPDF) 
 [![Join the chat at https://gitter.im/LibrePDF/OpenPDF](https://badges.gitter.im/LibrePDF/OpenPDF.svg)](https://gitter.im/LibrePDF/OpenPDF) 
 [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.librepdf/openpdf/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.librepdf/openpdf) 
 [![License (LGPL version 3.0)](https://img.shields.io/badge/license-GNU%20LGPL%20version%203.0-blue.svg?style=flat-square)](http://opensource.org/licenses/LGPL-3.0) 
 [![License (MPL)](https://img.shields.io/badge/license-Mozilla%20Public%20License-yellow.svg?style=flat-square)](http://opensource.org/licenses/MPL-2.0)

## OpenPDF version 1.0.0 released ??? ##


## Use OpenPDF as Maven dependency
Add this to your pom.xml file to use the latest version of OpenPDF:

        <dependency>
            <groupId>com.justifiedsolutions</groupId>
            <artifactId>openpdf</artifactId>
            <version>1.0.0</version>
        </dependency>

## License ##

GNU General Lesser Public License (LGPL) version 3.0 - http://www.gnu.org/licenses/lgpl.html

Mozilla Public License Version 2.0 - http://www.mozilla.org/MPL/2.0/

We want OpenPDF to consist of source code which is consistently licensed with the LGPL and MPL licences only. This also means that any new contributions to the project must have a dual LGPL and MPL license only.

## Documentation ##
- [JavaDoc](https://librepdf.github.io/OpenPDF/docs-1-3-3/?)


## Background ##

OpenPDF is open source software with a LGPL and MPL license. It is a fork of iText version 4, more 
specifically iText svn tag 4.2.0, which was hosted publicly on sourceforge with LGPL and MPL license 
headers in the source code, and lgpl and mpl license documents in the svn repository. Beginning with 
version 5.0 of iText, the developers have moved to the AGPL to improve their ability to sell commercial 
licenses. Justified Solutions OpenPDF was forked off of the LibrePDF version in March 2020. The goal 
of this version to provide a slimmed down version that focuses on the minimum requirements for creating 
a PDF document.

## Contributing ##
Release the hounds!  Please send all pull requests. Make sure your contributions can be released with 
a dual LGPL and MPL license. In particular, pull requests to the OpenPDF project must only contain 
code that you have written yourself. GPL or AGPL licensed code will not be accepted.

### Coding Style ###
- Code indentation style is 4 spaces.
- Generally try to preserve the coding style in the file you are modifying.

## Dependencies ##
### Required Dependencies: ###
 - Java 11 or later 
 - [Juniversalchardet](https://github.com/albfernandez/juniversalchardet)

### Testing Dependencies: ###
 - JUnit 5
 - AssertJ
 - Hamcrest
 - Mockito
 - Apache Commons IO

### Optional: ###
  - [BouncyCastle](https://www.bouncycastle.org/) (BouncyCastle is used to sign PDF files, so it's a recommended dependency)
    - Provider
    - PKIX/CMS
 - [TwelveMonkeys imageio-tiff](https://github.com/haraldk/TwelveMonkeys/) - optional by default, but required if TIFF image support is needed.    


## Credits ##
Significant [Contributors to OpenPDF](https://github.com/LibrePDF/OpenPDF/graphs/contributors) on GitHub:

  [@andreasrosdal](https://github.com/andreasrosdal) - Andreas Røsdal - Maintainer of OpenPDF from 1.0 to 1.3.11, now retired from OpenPDF development.  
  [@daviddurand](https://github.com/daviddurand) -  David G. Durand  
  [@tlxtellef](https://github.com/tlxtellef) - Tellef  
  [@asturio](https://github.com/asturio) - Claudio Clemens     
  [@ymasory](https://github.com/ymasory)  
  [@albfernandez](https://github.com/albfernandez) - Alberto Fernández  
  [@noavarice](https://github.com/noavarice)  
  [@bengolder](https://github.com/bengolder)  - Benjamin Golder  
  [@glarfs](https://github.com/glarfs)  
  [@Kindrat](https://github.com/Kindrat)  
  [@syakovyn](https://github.com/syakovyn)  
  [@ubermichael](https://github.com/ubermichael) - Michael Joyce  
  [@weiyeh](https://github.com/weiyeh)  
  [@SuperPat45](https://github.com/SuperPat45)  
  [@lapo-luchini](https://github.com/lapo-luchini)  
  [@MartinKocour](https://github.com/MartinKocour)  - Martin Kocour  
  [@jokimaki](https://github.com/jokimaki)  
  [@sullis](https://github.com/sullis)  
  [@lapo-luchini](https://github.com/lapo-luchini)  
  [@jeffrey-easyesi](https://github.com/jeffrey-easyesi)  
  [@V-F](https://github.com/V-F)     
  [@sixdouglas](https://github.com/sixdouglas) - Douglas Six  
  [@razilein](https://github.com/razilein) - Sita Geßner  
  [@PalAditya](https://github.com/PalAditya) - Aditya Pal       
  [@rammetzger](https://github.com/rammetzger)   
  [@codecracker2014](https://github.com/codecracker2014)   
  [@mluppi](https://github.com/mluppi)   
  [@vic0075](https://github.com/vic0075)   
 

Also, a very special thanks to the iText developers ;)
