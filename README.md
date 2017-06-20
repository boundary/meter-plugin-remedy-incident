TrueSight Pulse Remedy Incident Integration Plugin
=========================================

Collects tickets(Incident management) from Remedy servers and collected tickets are ingested to Truesight Intelligence. The plugin allows multiple Remedy incidents data to be ingested periodically based on poll interval.

### Prerequisites

#### Supported OS

|     OS    | Linux | Windows | SmartOS | OS X |
|:----------|:-----:|:-------:|:-------:|:----:|
| Supported |   v   |    v    |    -    |  v   |

#### Runtime Environment

|  Runtime | node.js | Python | Java |
|:---------|:-------:|:------:|:----:|
| Required |         |        |    v*  |

* java 1.8+ 

* [How to install java?](https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html)


#### TrueSight Pulse Meter versions v4.6.2-835 or later

- To install new meter go to Settings->Installation or [see instructions](https://help.boundary.com/hc/en-us/sections/200634331-Installation).
- To upgrade the meter to the latest version - [see instructions](https://help.boundary.com/hc/en-us/articles/201573102-Upgrading-the-Boundary-Meter).

#### Plugin Configuration Fields

|Field Name        |Description                                                                    |
|:-----------------|:------------------------------------------------------------------------------|
|Host              |The host of Remedy server                                            		   |
|Port              |The port of Remedy server                                            		   |
|Username          |The user of Remedy server                                            		   |
|Password          |The password of Remedy server                                        		   |
|Poll Interval     |How often (in milliseconds) to poll for collect the tickets                    |
|Remedy Fields     |Type of fields will be collected(more info please check in template section)   |

### Templates
[Incident Default Template](https://github.com/boundary/meter-plugin-remedy-incident/blob/master/template/incidentDefaultTemplate.json)

### References

TODO: Need to add documentation link here.

