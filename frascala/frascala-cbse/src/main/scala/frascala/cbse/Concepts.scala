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
 * This package isolates the definition of the core concepts of an abstract 
 * component model, which can be further refined to address domain-specific
 * concerns.
 */
package frascala.cbse {
  import scala.reflect.BeanProperty

  /** Definition of an interaction gate */
  trait Gate extends NameController with AnnotationController with OwnerController with PathController

  trait Annotation extends OwnerController

  trait PortAnnotation extends Annotation with PathController {
    type OWNER <: Port
  }

  /** Definition of a component port (as an extension of a gate) */
  trait Port extends Gate {
    type ANNOTATION <: PortAnnotation
    type OWNER <: PortController
  }

  trait PropertyAnnotation extends Annotation {
    type OWNER <: Property
  }

  /** Definition of a component property (as an extension of a gate) */
  trait Property extends Gate {
    type ANNOTATION <: PropertyAnnotation
    type OWNER <: PropertyController
  }

  trait MembraneAnnotation extends Annotation {
    type OWNER <: Membrane
  }

  /** Definition of a membrane */
  trait Membrane extends NameController with PathController with AnnotationController {
    type ANNOTATION <: MembraneAnnotation
  }

  /** Definition of an enclosed membrane (as an extension of a membrane) */
  trait SubMembrane extends Membrane with OwnerController {
    type OWNER <: CompositeMembrane
  }

  /** Definition of a component membrane */
  trait ComponentMembrane extends Membrane with PropertyController with PortController

  trait ConnectorAnnotation extends Annotation {
    type OWNER <: Connector
  }

  /** Definition of a port connector */
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

  /** Definition of a composite membrane (as an extension of a membrane) */
  trait CompositeMembrane extends Membrane with CompositeController with ConnectorController
}
