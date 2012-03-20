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
  import frascala.frascati.intent
  import org.osoa.sca.annotations.{ Property, Reference, Scope }
  import org.ow2.frascati.tinfi.api.{ IntentHandler, IntentJoinPoint }

  /**
   * Source code of the primitive components
   */
  trait Service {
    def print(msg: String)
  }
  class Server extends Service {
    @Property
    var count = 0

    def print(msg: String) {
      for (i <- 1 to count) println(msg)
    }
  }
  class Client extends Runnable {
    @Property
    var header: String = _
    @Reference
    var s: Service = _

    def run {
      s print header + "Hello, world!"
    }
  }

  /**
   * Description of the software architecture
   */
  class HelloworldArch extends Composite("Helloworld") {
    val cnt = property[Int]("counter") is 3

    val srv = new component("Server") {
      property[Int]("count") from cnt
      val s = service("s") exposes Java[Service]
    } uses Bean[Server]

    new component("Client") {
      property[String]("header") is ">> "
      service("r") exposes Java[Runnable]
      reference("s") targets srv.s
    } uses Bean[Client]

    service("run") promotes components("Client").services("r")
  }

  /**
   * Definition of an intent (a simple logger)
   */
  class Logger extends IntentHandler {
    def invoke(ijp: IntentJoinPoint) = {
      println(">> BEFORE invoking method " + ijp.getMethod.getName + "() on component " + ijp.getComponent)
      val res = ijp.proceed
      println(">> AFTER invoking method " + ijp.getMethod.getName + "() on component " + ijp.getComponent)
      res
    }
  }

  object LoggedHelloworldArch extends HelloworldArch {
    val log = intent("Logger", Bean[Logger])

    srv.s weaves log
    components("Client") weaves log
    weaves(log)
  }

  object Helloworld extends App {
    println("Describing Helloworld...")
    val arch = new HelloworldArch()
    println(arch.toDocument)

    println("\nDescribing Helloworld with Logging...")
    println(LoggedHelloworldArch.toDocument)

    // Testing the JXPath query API
    val comps = arch / "components"

    println("\nExtracting all the primitive components...")
    comps foreach { s: XML => println(s.toXML) }

    println("\nExtracting all the properties...")
    (arch / "components/properties") foreach { s: XML => println(s.toXML) }

    println("\nExtracting all the ports...")
    (comps / "ports") foreach { s: XML => println(s) }
  }
}