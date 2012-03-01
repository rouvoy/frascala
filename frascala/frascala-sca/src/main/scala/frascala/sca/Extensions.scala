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
    import scala.reflect.BeanProperty
    
    case class Java[T](@BeanProperty implicit var interface: Manifest[T]) extends ScaInterface {
        def toXML = <interface.java interface={ interface.toString }/>
    }
    case class WSDL(@BeanProperty var port: String) extends ScaInterface {
        def toXML = <interface.wsdl port={ port }/>
    }


    case class SOAP(@BeanProperty var location: String, @BeanProperty var element: String) extends ScaBinding {
        def toXML = <binding.ws wsdli:wsdlLocation={ location } wsdlElement={ element } xmlns:wsdli="http://www.w3.org/2004/08/wsdl-instance"/>
    }

    
    case class Bean[T](@BeanProperty implicit var cls: Manifest[T]) extends ScaImplementation {
        def toXML = <implementation.java class={ cls.toString }/>
    }
    case class Script(@BeanProperty var script: String) extends ScaImplementation {
        def toXML = <implementation.script script={ script }/>
    }
    case class BPEL(@BeanProperty var uri: String) extends ScaImplementation {
        def toXML = <implementation.bpel process={ uri }/>
    }
}
