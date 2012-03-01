/**
 * FraSCAla Architecture Framwework
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
package frascala.cbse {
  import scala.collection.JavaConversions
  import scala.collection.JavaConversions.asScalaBuffer
  import scala.collection.JavaConversions.mapAsJavaMap
  import scala.collection.Iterable
  import scala.reflect.BeanProperty
  import org.apache.commons.jxpath.JXPathContext
  import scala.collection.generic.Growable
  import scala.collection.mutable.ListBuffer
  import scala.collection.mutable.HashSet
  import scala.collection.mutable.SetLike

  class Multiple[A, T <: Iterable[A] with Growable[A]](var iterable: T) extends Iterable[A] with Growable[A] {
    def iterator = iterable.iterator
    
    def +=(elt: A) = { iterable += elt; this }
    def clear { iterable.clear }

    def reduce[B](z: B)(op: (B, A) => B) = iterable.foldLeft(z)(op)
    def apply(cond: A => Boolean) = iterable filter cond
    def asJavaCollection = JavaConversions.asJavaCollection(iterable)
  }

  trait NameController {
    @BeanProperty var name: String = _
    def asTuple = name -> this
  }
  
  class NamedMultiple[A <: NameController, T <: HashSet[A]](iterable: T) extends Multiple[A, T](iterable) {
    def apply(name: String) = iterable.find({ _.name equals name }).get
    def asJavaMap = mapAsJavaMap(iterable.map({ _.asTuple }).toMap)
  }

  object Multiple {
    def list[T] = new Multiple[T, ListBuffer[T]](ListBuffer[T]())
    def set[T] = new Multiple[T, HashSet[T]](HashSet[T]())
    def namedSet[T<: NameController] = new NamedMultiple[T, HashSet[T]](HashSet[T]())
  }


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

  trait ValueController[T] {
    @BeanProperty protected var value: T = _

    private var getter: T => T = identity[T]
    def onGet(newGetter: T => T) = { getter = newGetter; this }
    def apply() = getter

    private var setter: T => T = identity[T]
    def onSet(newSetter: T => T) = { setter = newSetter; this }
    def update(newValue: T) = { value = setter(newValue) }
  }

  trait PathController {
    import org.apache.commons.jxpath.JXPathContext
    lazy val context = JXPathContext.newContext(this)

    def /(path: String) = {
      val nodes = asScalaBuffer(context.selectNodes(path)).toSet
      nodes.size match {
        case 0 => None
        case 1 => Some(nodes head)
        case x => Some(nodes)
      }
    }
  }
}
