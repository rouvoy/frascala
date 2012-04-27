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
 * This package isolates the definition of an SCA-compliant component model.
 */
package frascala.sca {
  import frascala.cbse._
  import scala.reflect.BeanProperty
  import scala.collection.JavaConversions.asJavaCollection
  import scala.xml.UnprefixedAttribute
  import scala.xml.Text
  import scala.xml.Null
  import java.io.File
  import scala.collection.GenTraversableLike

  trait XML {
    import scala.xml.Node
    def toXML: Node
  }

  trait URI {
    def toURI: String
  }

  trait Storable extends XML with NameController {
    def extension: String
    def toDocument = toXML

    lazy val filename = name + extension

    def toFile(directory: File) {
      directory.mkdirs
      val doc = toDocument
      scala.xml.XML.save(directory.getPath() + filename, doc)
    }
  }

  trait ScaInterface extends PortAnnotation with XML {
    type OWNER = ScaContract
  }

  trait ScaBinding extends PortAnnotation with XML {
    type OWNER = ScaContract
  }

  trait ScaInterfaceController extends AnnotationController {
    @BeanProperty
    var interface: Option[ScaInterface] = None
  }

  trait ScaBindingController extends AnnotationController {
    //@BeanProperty
    var bindings = Multiple.set[ScaBinding]
    def getBindings = bindings.asJavaCollection
  }

  trait ScaContract extends Port with ScaInterfaceController with ScaBindingController with ScaIntentController with ScaAutowireController with XML with URI {
    type OWNER <: ScaComponentMembrane
    type ANNOTATION = PortAnnotation

    def toURI = owner.name + "/" + name
  }

  trait ScaService extends ScaContract {
    def toXML = <service name={ name } requires={ require }>
                  { interface map { _.toXML } orNull }
                  { bindings map { _.toXML } }
                </service>
  }

  trait ScaReference extends ScaContract {
    @BeanProperty
    var target: Option[ScaService] = None

    @BeanProperty
    var isRequired = true
    @BeanProperty
    var isSingleton = true

    private def targetURI = target map { _.toURI } orNull
    private def multiplicity = (if (isRequired) "1" else "0") + ".." + (if (isSingleton) "1" else "n")

    def toXML = <reference name={ name } target={ targetURI } requires={ require } autowire={ autowire.toString() } multiplicity={ multiplicity }>
                  { interface map { _.toXML } orNull }
                  { bindings map { _.toXML } }
                </reference>
  }

  trait ScaProperty[T] extends frascala.cbse.Property with ValueController[T] with XML with URI {
    type OWNER <: ScaComponentMembrane

    var propertyType: Manifest[T]

    def xsdType = propertyType.toString match {
      case "java.lang.String" => "xsd:string"
      case "java.lang.Integer" => "xsd:integer"
      case "java.lang.Long" => "xsd:long"
      case "java.lang.Boolean" => "xsd:boolean"
      case "Int" => "xsd:int"
      case x => x
    }

    def toURI = owner.name + "/" + name
  }

  trait ScaServiceController extends PortController {
    type SERVICE <: ScaService

    //@BeanProperty
    var services = Multiple.namedSet[SERVICE]
    def getServices = services.asJavaCollection
    def getService = services.asJavaMap
  }

  trait ScaAutowireController {
    @BeanProperty
    var autowire: Boolean = false // To be replaced by autowire's behavior?
  }

  trait ScaReferenceController extends PortController {
    type REFERENCE <: ScaReference

    //@BeanProperty
    var references = Multiple.namedSet[REFERENCE]
    def getReferences = references.asJavaCollection
    def getReference = references.asJavaMap
  }

  trait ScaIntentController extends AnnotationController {
    //@BeanProperty
    var intents = Multiple.namedSet[ScaMembrane]
    def getIntents = intents.asJavaCollection
    def getIntent = intents.asJavaMap

    protected def require = intents.map({ _.name }).mkString(" ")
  }

  ////////////////////////////////////////////////////////////////////////////
  // SCA COMPONENT
  trait ScaMembrane extends ComponentMembrane with ScaServiceController with ScaReferenceController with ScaIntentController with ScaAutowireController with XML {
    type PORT <: ScaContract
    type PROPERTY <: ScaProperty[_]
  }

  trait ScaComponentMembrane extends ScaMembrane with SubMembrane {
    type OWNER <: ScaCompositeMembrane
  }

  trait ScaImplementation extends MembraneAnnotation with XML {
    type OWNER = ScaPrimitiveMembrane
  }

  trait ScaImplementationController {
    @BeanProperty
    var implementation: Option[ScaImplementation] = None
  }

  trait ScaPrimitiveProperty[T] extends ScaProperty[T] {
    type OWNER <: ScaPrimitiveMembrane

    var source: Option[ScaCompositeProperty[T]] = None

    def toXML = if (value != null)
      <property name={ name } source={ source map { "$" + _.name } orNull } type={ xsdType } xmlns:xsd="http://www.w3.org/2001/XMLSchema">{ value.toString }</property>
    else
      <property name={ name } source={ source map { "$" + _.name } orNull }/>
  }

  trait ScaPrimitiveMembrane extends ScaComponentMembrane with ScaImplementationController {
    type ANNOTATION = MembraneAnnotation
    type SERVICE <: ScaService
    type REFERENCE <: ScaReference

    def toXML = <component name={ name } requires={ require }>
                  { implementation map { _.toXML } orNull }
                  { ports map { _.toXML } }
                  { properties map { _.toXML } }
                </component>
  }

  trait ScaComponentTypeMembrane extends ScaPrimitiveMembrane {
    override def toXML = <componentType requires={ require }>
                           { implementation map { _.toXML } orNull }
                           { ports map { _.toXML } }
                           { properties map { _.toXML } }
                         </componentType>
  }

  ////////////////////////////////////////////////////////////////////////////
  // SCA DOMAIN
  trait ScaPromotable[T <: URI] {
    @BeanProperty
    var promoted: Option[T] = None

    protected def promote = promoted map { _.toURI } orNull
  }
  trait ScaCompositeService extends ScaService with ScaPromotable[ScaService] {
    override def toXML = <service name={ name } promote={ promote } requires={ require }>
                           { interface map { _.toXML } orNull }
                           { bindings map { _.toXML } }
                         </service>
  }
  trait ScaCompositeReference extends ScaReference with ScaPromotable[ScaReference] {
    override def toXML = <reference name={ name } promote={ promote } requires={ require }>
                           { interface map { _.toXML } orNull }
                           { bindings map { _.toXML } }
                         </reference>
  }
  trait ScaCompositeProperty[V] extends ScaProperty[V] with ScaPromotable[ScaProperty[V]] {
    def toXML = if (value != null)
      <property name={ name } promote={ promote } type={ xsdType } xmlns:xsd="http://www.w3.org/2001/XMLSchema">{ value.toString }</property>
    else
      <property name={ name } promote={ promote }/>
  }

  trait ScaWire extends Connector with XML {
    type OWNER <: ScaCompositeMembrane
    type FROM <: ScaReference
    type TO <: ScaService

    def toXML = <wire source={ from.toURI } target={ to.toURI }/>
  }

  trait ScaCompositeMembrane extends CompositeMembrane with ScaMembrane with Storable {
    type CONNECTOR <: ScaWire
    type CONTENT <: ScaComponentMembrane
    type SERVICE <: ScaCompositeService
    type REFERENCE <: ScaCompositeReference
    type PROPERTY <: ScaCompositeProperty[_]

    def toXML = <implementation.composite descriptor={ filename }/>

    val extension = ".composite"

    override def toDocument = <composite xmlns="http://www.osoa.org/xmlns/sca/1.0" name={ name } requires={ require }>
                                { ports map { _.toXML } }
                                { properties map { _.toXML } }
                                { components map { _.toXML } }
                                { connectors map { _.toXML } }
                              </composite>
  }

  trait ScaConstrainingTypeeMembrane extends ScaCompositeMembrane {
    override val extension = ".constrainingType"

    override def toDocument = <constrainingType xmlns="http://www.osoa.org/xmlns/sca/1.0" name={ name } requires={ require }>
                                { ports map { _.toXML } }
                                { properties map { _.toXML } }
                                { components map { _.toXML } }
                                { connectors map { _.toXML } }
                              </constrainingType>
  }
}
