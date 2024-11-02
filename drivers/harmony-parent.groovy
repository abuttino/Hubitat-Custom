/**
 *
 *  File: Logitech-Harmony-Hub-Parent.groovy
 *  Platform: Hubitat
 *
 *  https://raw.githubusercontent.com/ogiewon/Hubitat/master/Drivers/logitech-harmony-hub-parent.src/logitech-harmony-hub-parent.groovy
 *
 *  Requirements:
 *     1) Logitech Harmony Home Hub connected to same LAN as your Hubitat Hub.  Use router
 *        DHCP Reservation to prevent IP address from changing.
 *     2) HubDuino "Child Switch" Driver is also necessary.  This is available
 *        at https://github.com/DanielOgorchock/ST_Anything/tree/master/HubDuino/Drivers
 *
 *  Copyright 2018 Dan G Ogorchock 
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2018-12-25  Dan Ogorchock  Original Creation
 *    2018-12-27  Dan Ogorchock  Fixes to correct hub reboot issue
 *    2019-01-04  Dan Ogorchock  Faster updates to Child Switch Devices to prevent Alexa "Device is not repsonding" message
 *    2019-01-07  Dan Ogorchock  Changed log.warn to log.info for unhandled data from harmony hub
 *    2019-02-20  @corerootedxb  Fixed routine to obtain the remoteId due to firmware 4.15.250 changes by Logitech
 *    2019-06-09  Dan Ogorchock  Added importURL to definition
 *    2019-07-14  Dan Ogorchock  Added Harmony Volume and Channel control (for activities that support it) (with help from @aaron!)
 *    2019-07-15  Dan Ogorchock  Added setLevel and setVolume commands for greater compatability with Hubitat Dashboard and other Apps
 *    2019-07-23  Dan Ogorchock  Added Actuator Capability to allow RM Custom Actions to select this device
 *    2019-12-31  Dan Ogorchock  Changed volume control logic to be more robust and clear to users
 *    2020-01-14  Dan Ogorchock  Added Switch Capability and Default Activity user preference.  If the Parent switch is turned on, 
 *                               the default activity is turned on.  If the Parent switch is turned off, the current Activity is turned off.
 *    2020-01-21  Dan Ogorchock  Fixed bug in the Parent Switch's Status not updating when controlled via the physical remote control
 *    2020-01-28  Dan Ogorchock  Exposed "deviceCommand" as a custom command per idea from @Geoff_T