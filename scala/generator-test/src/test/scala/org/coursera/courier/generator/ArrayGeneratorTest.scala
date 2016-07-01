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

import org.coursera.arrays.WithCustomTypesArray
import org.coursera.arrays.WithCustomTypesArrayUnion
import org.coursera.arrays.WithCustomTypesArrayUnionArray
import org.coursera.arrays.WithPrimitivesArray
import org.coursera.arrays.WithRecordArray
import org.coursera.courier.data.BooleanArray
import org.coursera.courier.data.BytesArray
import org.coursera.courier.data.DoubleArray
import org.coursera.courier.data.FloatArray
import org.coursera.courier.data.IntArray
import org.coursera.courier.data.LongArray
import org.coursera.courier.data.StringArray
import org.coursera.courier.generator.customtypes.CustomInt
import org.coursera.courier.templates.DataTemplates.DataConversion
import org.coursera.customtypes.CustomIntArray
import org.coursera.enums.Fruits
import org.coursera.enums.FruitsArray
import org.coursera.fixed.Fixed8
import org.coursera.fixed.Fixed8Array
import org.coursera.records.test.Empty
import org.coursera.records.test.EmptyArray
import org.coursera.records.test.Simple
import org.coursera.records.test.SimpleArray
import org.coursera.records.test.SimpleArrayArray
import org.coursera.records.test.SimpleMap
import org.coursera.records.test.SimpleMapArray
import org.junit.Test

class ArrayGeneratorTest extends GeneratorTest with SchemaFixtures {

  @Test
  def testWithRecordArray(): Unit = {
    val json = load("WithRecordArray.json")
    val original = WithRecordArray(
      EmptyArray(Empty(), Empty(), Empty()),
      FruitsArray(Fruits.APPLE, Fruits.BANANA, Fruits.ORANGE))

    val roundTripped = WithRecordArray.build(roundTrip(original.data()), DataConversion.SetReadOnly)
    assert(original === roundTripped)

    Seq(original, roundTripped).foreach { record =>
      assertJson(record, json)
    }
  }

  @Test
  def testWithPrimitivesArray(): Unit = {
    val json = load("WithPrimitivesArray.json")
    val original = WithPrimitivesArray(
      IntArray(1, 2, 3),
      LongArray(10L, 20L, 30L),
      FloatArray(1.1f, 2.2f, 3.3f),
      DoubleArray(11.1d, 22.2d, 33.3d),
      BooleanArray(false, true),
      StringArray("a", "b", "c"),
      BytesArray(bytes1, bytes2))
    val roundTripped = WithPrimitivesArray.build(
      roundTrip(original.data()), DataConversion.SetReadOnly)

    assert(original === roundTripped)

    Seq(original, roundTripped).foreach { record =>
      assertJson(record, json)
    }
  }

  @Test
  def testWithCustomTypesArray(): Unit = {
    val json = load("WithCustomTypesArray.json")
    val original = WithCustomTypesArray(
      CustomIntArray(CustomInt(1), CustomInt(2), CustomInt(3)),
      SimpleArrayArray(SimpleArray(Simple(Some("a1")))),
      SimpleMapArray(SimpleMap("a" -> Simple(Some("m1")))),
      WithCustomTypesArrayUnionArray(
        WithCustomTypesArrayUnion.IntMember(1),
        WithCustomTypesArrayUnion.StringMember("str"),
        WithCustomTypesArrayUnion.SimpleMember(Simple(Some("u1")))),
      Fixed8Array(Fixed8(bytesFixed8))
    )
    val roundTripped = WithCustomTypesArray.build(
      roundTrip(original.data()), DataConversion.SetReadOnly)

    assert(original === roundTripped)

    Seq(original, roundTripped).foreach { record =>
      assertJson(original, json)
    }
  }

  @Test
  def testCopyDataTemplate(): Unit = {
    val original = WithRecordArray(EmptyArray(Empty()), FruitsArray(Fruits.APPLE))

    val mutableData = original.data().copy()
    mutableData.getDataList("fruits").add("BANANA")
    val replacement = original.copy(mutableData, DataConversion.SetReadOnly)
    assert(replacement.fruits(1) === Fruits.BANANA)
  }

  @Test
  def testWrapImplicitsArrayArray(): Unit = {
    val original: SimpleArrayArray = Seq(Seq(Simple(Some("a"))), Seq(Simple(Some("b"))))
    val roundTripped = SimpleArrayArray.build(
      roundTrip(original.data()), DataConversion.SetReadOnly)
    assert(original === roundTripped)
  }

  @Test
  def testWrapImplicitsMapArray(): Unit = {
    val original: SimpleMapArray = Seq(
      Map("k1" -> Simple(Some("a"))),
      Map("k2" -> Simple(Some("b"))))
    val roundTripped = SimpleMapArray.build(roundTrip(original.data()), DataConversion.SetReadOnly)
    assert(original === roundTripped)
  }
}
