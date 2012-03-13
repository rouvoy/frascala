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

/**
 * This package isolates the definition of an abstract component model defining
 * the controllers for handling annotations, properties, ports, connectors, and
 * components.
 * @author Romain Rouvoy
 */
package frascala.cbse {
  import scala.reflect.BeanProperty
  
  trait AnnotationController {
    type ANNOTATION <: Annotation

    //@BeanProperty
    var annotations = Multiple.set[ANNOTATION]
    def getAnnotations = annotations.asJavaCollection
  }

  trait OwnerController {
    type OWNER

    @BeanProperty var owner: OWNER = _
  }

  // PROPERTY CONTROLLER
  trait PropertyController {
    type PROPERTY <: Property

    //@BeanProperty
    var properties = Multiple.namedSet[PROPERTY]
    def getProperties = properties.asJavaCollection
    def getProperty = properties.asJavaMap
  }

  // PORT CONTROLLER
  trait PortController {
    type PORT <: Port

    //@BeanProperty
    var ports = Multiple.namedSet[PORT]
    def getPorts = ports.asJavaCollection
    def getPort = ports.asJavaMap
  }

  // COMPOSITE CONTROLLERS
  trait ConnectorController {
    type CONNECTOR <: Connector

    //@BeanProperty
    var connectors = Multiple.list[CONNECTOR]
    def getConnectors = connectors.asJavaCollection
  }

  trait CompositeController {
    type CONTENT <: SubMembrane

    //@BeanProperty
    var components = Multiple.namedSet[CONTENT]
    def getComponents = components.asJavaCollection
    def getComponent = components.asJavaMap
  }
}
