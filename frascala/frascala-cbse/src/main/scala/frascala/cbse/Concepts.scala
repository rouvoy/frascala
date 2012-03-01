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
package frascala.cbse {
  import scala.reflect.BeanProperty

  trait Gate extends NameController with AnnotationController with OwnerController with PathController

  trait Annotation extends OwnerController

  trait PortAnnotation extends Annotation with PathController {
    type OWNER <: Port
  }

  trait Port extends Gate {
    type ANNOTATION <: PortAnnotation
    type OWNER <: PortController
  }

  trait PropertyAnnotation extends Annotation {
    type OWNER <: Property
  }

  trait Property extends Gate {
    type ANNOTATION <: PropertyAnnotation
    type OWNER <: PropertyController
  }

  trait MembraneAnnotation extends Annotation {
    type OWNER <: Membrane
  }

  trait Membrane extends NameController with PathController with AnnotationController {
    type ANNOTATION <: MembraneAnnotation
  }

  trait SubMembrane extends Membrane with OwnerController {
    type OWNER <: CompositeMembrane
  }

  trait ComponentMembrane extends Membrane with PropertyController with PortController

  trait ConnectorAnnotation extends Annotation {
    type OWNER <: Connector
  }

  trait Connector extends AnnotationController with OwnerController {
    type ANNOTATION <: ConnectorAnnotation
    type OWNER <: ConnectorController
    type FROM <: Port
    type TO <: Port

    @BeanProperty
    var from: FROM = _

    @BeanProperty
    var to: TO = _
  }

  trait CompositeMembrane extends Membrane with CompositeController with ConnectorController
}
