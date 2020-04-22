# Justified Solutions OpenPDF #

OpenPDF is a Java library for creating PDF files with a LGPL and MPL open source license. OpenPDF is the LGPL/MPL open source successor of iText, and is based on a fork of LibrePDF/OpenPDF. We welcome contributions from other developers. Please feel free to submit pull-requests and bugreports to this GitHub repository.

[![Build Status](https://travis-ci.org/justifiedsolutions/OpenPDF.svg?branch=master)](https://travis-ci.org/justifiedsolutions/OpenPDF)
[![Maven Central](https://img.shields.io/maven-central/v/com.justifiedsolutions/openpdf?color=green)](https://maven-badges.herokuapp.com/maven-central/com.justifiedsolutions/openpdf)
[![javadoc](https://javadoc.io/badge2/com.justifiedsolutions/openpdf/javadoc.svg)](https://javadoc.io/doc/com.justifiedsolutions/openpdf)
[![License (LGPL version 3.0)](https://img.shields.io/badge/license-GNU%20LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0-standalone.html) 
[![License (MPL)](https://img.shields.io/badge/license-Mozilla%20Public%20License%20v2-blue.svg)](https://www.mozilla.org/en-US/MPL/2.0/)

## OpenPDF version 1.1.2 released 2020-04-22 ##
 - Small fixes to API (accept `null` as argments to some method calls)
 - Fixed issue with page breaks creating empty page in some cases
 - Removed more unused code

## OpenPDF version 1.1.1 released 2020-04-12 ##
 - Small fixes to API (mostly to documentation)
 - Removed many features and classes as well as hundreds of lines of unused code

## OpenPDF version 1.1.0 released 2020-04-07 ##
 - Introduced new API (com.justifiedsolutions.sfspro.pdf.* packages)
 - Removed many features and classes as well as hundreds of lines of unused code

## OpenPDF version 1.0.0 released 2020-03-28 ##
 - Initial release of the "slimmed down" version of OpenPDF. 

## Use OpenPDF as Maven dependency
Add this to your pom.xml file to use the latest version of OpenPDF:

        <dependency>
            <groupId>com.justifiedsolutions</groupId>
            <artifactId>openpdf</artifactId>
            <version>1.1.2</version>
        </dependency>
        
## Project Goals ##
### Goals ###
 - Minimize functionality
 - Easy to understand and use API
 
### Non-Goals ###
 - Compatibility with iText or LibrePDF/OpenPDF
 - Supporting Forms
 - Supporting Encryption
 - Supporting Signing
 - Supporting Parsing

## License ##

`SPDX-License-Identifier: LGPL-3.0-only OR MPL-2.0`

All contributions to OpenPDF must be dual licensed as [LGPL v3](https://www.gnu.org/licenses/lgpl-3.0-standalone.html) and [MPL v2](https://www.mozilla.org/en-US/MPL/2.0/). When using this library you can use either license.


## Background ##

Justified Solutions OpenPDF is open source software with a LGPL and MPL license. It is a fork of LibrePDF which is a fork of iText version 4, more specifically iText svn tag 4.2.0, which was hosted publicly on sourceforge with LGPL and MPL license headers in the source code, and LGPL and MPL license documents in the SVN repository. Beginning with version 5.0 of iText, the developers have moved to the AGPL to improve their ability to sell commercial licenses. Justified Solutions OpenPDF was forked off of the LibrePDF version in March 2020. The goal of this version to provide a slimmed down version that focuses on the minimum requirements for creating a PDF document.

## Coding Style ##
- A coding style configuraton for [IntelliJ](https://www.jetbrains.com/idea/) is included in the repository.

## Dependencies ##
### Required Dependencies: ###
 - Java 11 or later 

### Testing Dependencies: ###
 - JUnit 5