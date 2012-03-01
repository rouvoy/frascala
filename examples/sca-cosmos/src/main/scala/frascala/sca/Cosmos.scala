/**
 * FraSCAla Architecture Framework
 * Copyright (C) 2012 University Lille 1, Inria
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: romain.rouvoy@univ-lille1.fr
 *
 * Author: Romain Rouvoy
 *
 * Contributor(s):
 */
package frascala.sca {

//  object WiFiDetector extends Policy {
//    val WiFiMgr = new sensor[WirelessInterfaceRM] {
//      parameter("resourceName","eth1")
//    } isActiveObserver isBlockingObserver
//  
//    val WiFiConnect = new node[ConnectivityDetectorCO] {
//      node[AverageCO] consumes WiFiMgr.get("link-quality")
//      
//      val AvgWiFiBitRate = new node[AverageIfCO] {
//        consumes(WiFiMgr.get("bit-rate", "is-variable"))
//    } } isActiveObserver isActiveNotifier
//  
//    new node[AdjustedBitRateCO] { 
//      consumes(WiFiConnect)
//      consumes(WiFiConnect.AvgWiFiBitRate)
//    }
//  }
  
  object Cosmos extends App {
    println("Describing WiFi Detector Policy...")
//    println(WiFiDetector.toDocument)
  }
}