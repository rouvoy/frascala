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
package frascala.frascati {
	import frascala.sca._
    import scala.reflect.BeanProperty
    
    case class WADL(@BeanProperty var uri: String) extends ScaInterface {
        def toXML = <wadl:interface.wadl schema={ uri } xmlns:wadl="http://frascati.ow2.org/xmlns/wadl/1.0"/>
    }

    case class HTTP(@BeanProperty var uri: String) extends ScaBinding {
        def toXML = <rest:binding.rest uri={ uri } xmlns:rest="http://frascati.ow2.org/xmlns/sca/1.1"/>
    }    
}
