package com.avro

import java.io.ByteArrayOutputStream

import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericDatumReader, GenericDatumWriter, GenericRecord}
import org.apache.avro.io.{DecoderFactory, EncoderFactory}

object Test {
  def main(args: Array[String]): Unit = {
    val parser = new Schema.Parser()
    val schema = parser.parse(getClass.getResourceAsStream("StringPair.avsc"))

    val datum = new GenericData.Record(schema)
    datum.put("left", "L")
    datum.put("right", "R")

    val out = new ByteArrayOutputStream()
    val writer = new GenericDatumWriter[GenericRecord](schema)

    val encoder = EncoderFactory.get.binaryEncoder(out, null)
    writer.write(datum, encoder)
    encoder.flush()
    out.close()

    val reader = new GenericDatumReader[GenericRecord](schema)
    val decoder = DecoderFactory.get.binaryDecoder(out.toByteArray, null)
    val result = reader.read(null, decoder)
    require(result.get("left").toString == "L")
    require(result.get("right").toString == "R")
  }
}
