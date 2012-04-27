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
	import org.ow2.frascati.FraSCAti
	import org.ow2.frascati.tinfi.api.{ IntentHandler, IntentJoinPoint }
	import scala.xml._
	import org.objectweb.fractal.api.Component
	import org.objectweb.fractal.util.Fractal
	import org.ow2.frascati.tinfi.api.control.{ SCABasicIntentController, SCAPropertyController }
	import java.lang.reflect.Method
	import org.ow2.frascati.tinfi.api.InterfaceMethodFilter
	import org.objectweb.fractal.api.control.BindingController
	import org.objectweb.fractal.api.Interface
	import adam.frascala.sca.Composite
	import java.io.File
	
	class ScaComponentController(val fcComponent: Component) {
	    def services = for (itf <- fcComponent.getFcInterfaces) yield ScaService(itf.asInstanceOf[Interface])
	    def services(name: String) = ScaService(fcComponent.getFcInterface(name).asInstanceOf[Interface])
	
	    case class ScaService(itf: Interface) {
	        def interface[T] = itf.asInstanceOf[T]
	        def name = itf.getFcItfName
	    }
	
	    def >[T](name: String) = services(name).interface[T]
	}
	
	trait ScaNameController extends ScaComponentController {
	    lazy val nc = Fractal.getNameController(fcComponent)
	
	    def name = nc.getFcName()
	    def name(newName: String) {
	        nc setFcName newName
	    }
	}
	
	trait ScaLifeCycleController extends ScaComponentController {
	    lazy val lcc = Fractal.getLifeCycleController(fcComponent)
	
	    private def start = lcc.startFc
	    private def stop = lcc.stopFc
	    def update(code: this.type => Unit) {
	        stop
	        code(this)
	        start
	    }
	}
	
	trait ScaBindingController extends ScaComponentController {
	    lazy val bc = Fractal.getBindingController(fcComponent)
	
	    def references = for (ref <- bc.listFc) yield ScaReference(ref)
	    def references(name: String) = ScaReference(name)
	
	    case class ScaReference(name: String) {
	        def targets[T](target: ScaComponentController#ScaService) = bc.bindFc(name, target.interface[T])
	        def disconnect = bc.unbindFc(name)
	    }
	}
	
	trait ScaIntentController extends ScaComponentController {
	    lazy val ic = services(SCABasicIntentController.NAME).interface[SCABasicIntentController]
	
	    def intents(interface: String, method: Method) = ic.listFcIntentHandler(interface, method)
	    def weaves(interface: String, method: Method, ih: IntentHandler) = ic.addFcIntentHandler(ih, interface, method)
	    def unweave(interface: String, method: Method, ih: IntentHandler) = ic.removeFcIntentHandler(ih, interface, method)
	}
	
	trait ScaPropertyController extends ScaComponentController {
	    lazy val pc = services(SCAPropertyController.NAME).interface[SCAPropertyController]
	
	    def property[T](name: String) = new ScaProperty[T](name)
	
	    case class ScaProperty[T](name: String) {
	        def is(value: T) {
	            pc.setType(name, value.getClass)
	            pc.setValue(name, value)
	        }
	        def apply = pc.getValue(name).asInstanceOf[T]
	    }
	}
	
	trait ScaContentController extends ScaComponentController {
	    lazy val cc = Fractal.getContentController(fcComponent)
	
	    def components: Array[ScaPrimitive] = for (comp <- cc.getFcSubComponents) yield new ScaPrimitive(comp)
	    def components(name: String): ScaPrimitive = components.filter({ _.name.equals(name) }).apply(0)
	    def /(name: String) = components(name)
	
	    def add(comp: Component) = cc.addFcSubComponent(comp)
	    def remove(comp: Component) = cc.removeFcSubComponent(comp)
	}
	
	trait ScaComponent extends ScaNameController with ScaLifeCycleController with ScaBindingController with ScaPropertyController
	trait ScaPrimitiveComponent extends ScaComponent
	trait ScaCompositeComponent extends ScaPrimitiveComponent with ScaContentController
	
	class ScaComposite(fcComponent: Component) extends ScaComponentController(fcComponent) with ScaCompositeComponent
	class ScaPrimitive(fcComponent: Component) extends ScaComponentController(fcComponent) with ScaPrimitiveComponent
	
	object FraSCAla {
	    Thread.currentThread.setContextClassLoader(this.getClass.getClassLoader)
	    val DIR = "target/generated/frascala/"
	    lazy val frascati = FraSCAti.newFraSCAti
	
	    def save(dir: File = new File("")) {
	        for (c <- Composite.composites)
	            c.toFile(dir)
	    }
	
	    def deploy(definition: String): ScaComposite = new ScaComposite(frascati.getComposite(definition))
	}
}