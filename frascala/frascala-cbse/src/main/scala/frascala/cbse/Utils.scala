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
 * This package isolates reusable structures and controllers that can be useful
 * when defining an ADL.
 */
package frascala.cbse {
  import scala.collection.generic.Growable
  import scala.collection.JavaConversions
  import scala.reflect.BeanProperty
  import scala.collection.mutable.HashSet
  import scala.collection.mutable.ListBuffer

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
    def asJavaMap = JavaConversions.mapAsJavaMap(iterable.map({ _.asTuple }).toMap)
  }

  object Multiple {
    def list[T] = new Multiple[T, ListBuffer[T]](ListBuffer[T]())
    def set[T] = new Multiple[T, HashSet[T]](HashSet[T]())
    def namedSet[T <: NameController] = new NamedMultiple[T, HashSet[T]](HashSet[T]())
  }

  trait PathController {
    import org.apache.commons.jxpath.JXPathContext
    lazy val context = JXPathContext.newContext(this)

    def /[T](path: String) = {
      var set = new HashSet[T]() with Concepts[T]
      for (n <- JavaConversions.asScalaBuffer(context.selectNodes(path)))
        set += n.asInstanceOf[T]
      set
    }
  }

  trait Concepts[T] extends scala.collection.mutable.Set[T] {
    import org.apache.commons.jxpath.JXPathContext

    def /[T](path: String) = {
      var set = new HashSet[T]() with Concepts[T]
      this foreach { e =>
        val context = JXPathContext.newContext(e)
        for (n <- JavaConversions.asScalaBuffer(context.selectNodes(path)))
          set += n.asInstanceOf[T]
      }
      set
    }
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
}