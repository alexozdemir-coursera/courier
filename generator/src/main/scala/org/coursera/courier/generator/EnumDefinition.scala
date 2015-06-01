/*
 Copyright 2015 Coursera Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.coursera.courier.generator

import com.linkedin.data.schema.EnumDataSchema
import com.linkedin.pegasus.generator.spec.EnumTemplateSpec
import scala.collection.JavaConverters._

case class EnumDefinition(spec: EnumTemplateSpec) extends Definition(spec) {
  def schema: EnumDataSchema = spec.getSchema

  /**
   * The enumeration's type.
   *
   * It's important to note that the type for the Enumerations we generate is
   * different than the enumeration object's name.
   *
   * A enumeration type is `SomeEnum.SomeEnum`, where the first `SomeEnum` is the name of the
   * enumeration scala object, and the second `SomeEnum` is a member type defined inside the
   * object for the actual type of the enumeration.
   */
  override def scalaType: String = s"$enumName.$enumName"

  /**
   * Because the scalaType is not the same as the enumeration object name, we have separate fields
   * for the enumeration object name.
   */
  def enumName: String = ScalaEscaping.escape(spec.getClassName)
  def enumFullname: String = s"${namespace.map(_ + ".").getOrElse("")}$enumName"
  override def namespace: Option[String] = Option(spec.getNamespace)

  /**
   * The scaladoc for the entire enumeration.
   */
  def scalaDoc: Option[String] = Option(schema.getDoc).flatMap(ScaladocEscaping.stringToScaladoc)

  /**
   * For enumerations, each symbol may have it's own documentation, this is provided as map
   * from symbol name to documentation string.
   */
  def symbolScalaDocs = schema.getSymbolDocs.asScala.mapValues(ScaladocEscaping.stringToScaladoc)

  /**
   * Enumeration symbol strings, not including $UNKNOWN.
   */
  def symbols: Seq[String] = schema.getSymbols.asScala.map(ScalaEscaping.escape)

  override def memberName: String = enumName + "Member"
}