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
    import frascala.cbse._

    trait scaWeavable extends ScaIntentController {
        def weaves(intent: ScaMembrane): this.type = {
            intents += intent
            this
        }
    }

    trait scaAutowireable extends ScaAutowireController {
        def autowired: this.type = {
            autowire = true
            this
        }
    }

    trait scaComponent extends ScaComponentMembrane with scaAutowireable with scaWeavable {
        type OWNER <: scaComposite
        type PORT = scaContract
        type PROPERTY <: scaProperty[_]

        trait scaContract extends ScaContract with scaWeavable {
            type OWNER <: scaComponent
            type ANNOTATION <: PortAnnotation
            ports += scaContract.this

            def exposes[I <: ScaInterface](i: I): this.type = {
                annotations += i
                interface = Some(i)
                i.owner = this
                this
            }
            def as[B <: ScaBinding](b: B): this.type = {
                annotations += b
                bindings += b
                b.owner = this
                this
            }
        }

        trait scaProperty[T] extends ScaProperty[T] {
            type OWNER <: scaComponent

            def is(newValue: T): this.type = { update(newValue); this }
        }
    }

    trait scaPrimitive extends ScaPrimitiveMembrane with scaComponent {
        type PROPERTY = property[_]
        type SERVICE = service
        type REFERENCE = reference

        def uses[I <: ScaImplementation](i: I): this.type = {
            annotations += i
            implementation = Some(i)
            this
        }

        case class property[T](id: String)(implicit var propertyType: Manifest[T]) extends ScaPrimitiveProperty[T] with scaProperty[T] {
            type OWNER = scaPrimitive
            owner = scaPrimitive.this
            properties += property.this
            name = id

            def from(s: ScaCompositeProperty[T]): this.type = { source = Some(s); this }
        }
        case class service(id: String) extends ScaService with scaContract {
            type OWNER = scaPrimitive
            owner = scaPrimitive.this
            services += this
            name = id
        }
        case class reference(id: String) extends ScaReference with scaContract with scaAutowireable {
            type OWNER = scaPrimitive
            owner = scaPrimitive.this
            references += this
            name = id

            def targets[S <: ScaService](s: S): this.type = { target = Some(s); this }
            def optional: this.type = { this.isRequired = false; this }
            def multiple: this.type = { this.isSingleton = false; this }
        }
    }

    trait scaComposite extends ScaCompositeMembrane with scaComponent {
        type CONTENT = scaComponent
        type CONNECTOR = wire
        type PROPERTY = property[_]
        type SERVICE = service
        type REFERENCE = reference

        trait scaPromotable[T <: URI] extends ScaPromotable[T] {
            def promotes(t: T): this.type = { promoted = Some(t); this }
        }
        case class service(id: String) extends ScaCompositeService with scaContract with scaPromotable[ScaService] {
            type OWNER = scaComposite
            
            owner = scaComposite.this
            services += this
            name = id
        }
        case class reference(id: String) extends ScaCompositeReference with scaContract with scaPromotable[ScaReference] with scaAutowireable {
            type OWNER = scaComposite
            
            owner = scaComposite.this
            references += this
            name = id
        }
        case class property[T](id: String)(implicit var propertyType: Manifest[T]) extends ScaCompositeProperty[T] with scaProperty[T] with scaPromotable[ScaProperty[T]] {
            type OWNER = scaComposite
            
            owner = scaComposite.this
            properties += property.this
            name = id
        }

        case class component(id: String) extends ScaPrimitiveMembrane with scaPrimitive {
            type OWNER = scaComposite
            
            owner = scaComposite.this
            components += this
            name = id
        }

        case class composite(id: String) extends ScaCompositeMembrane with scaComposite {
            type OWNER = scaComposite
            owner = scaComposite.this
            components += this
            name = id
        }
        
        case class wire(source: ScaReference, target: ScaService) extends ScaWire {
            type OWNER = scaComposite
            type FROM = ScaReference
            type TO = ScaService
            
            owner = scaComposite.this
            connectors += this
            from = source
            to = target
        }
    }

    class Composite(id: String) extends ScaCompositeMembrane with scaComposite {
        name = id
        Composite.composites += this
    }

    object Composite {
        var composites = Multiple.namedSet[Composite]
    }
}