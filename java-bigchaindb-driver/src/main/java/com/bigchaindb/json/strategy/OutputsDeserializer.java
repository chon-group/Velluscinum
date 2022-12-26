/*
 * Copyright BigchainDB GmbH and BigchainDB contributors
 * SPDX-License-Identifier: (Apache-2.0 AND CC-BY-4.0)
 * Code is Apache-2.0 and docs are CC-BY-4.0
 */
package com.bigchaindb.json.strategy;

import com.bigchaindb.model.Output;
import com.bigchaindb.model.Outputs;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Iterator;



/**
 * The Class OutputsDeserializer.
 */
public class OutputsDeserializer implements JsonDeserializer<Outputs> {

	/* (non-Javadoc)
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public Outputs deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		Outputs outputs = new Outputs();
		Iterator<JsonElement> jsonIter = json.getAsJsonArray().iterator();
		while(jsonIter.hasNext()) {
			Output output = new Output();
			JsonElement jElement = jsonIter.next();
			output.setTransactionId(jElement.getAsJsonObject().get("transaction_id").toString().replace("\"", ""));
			output.setOutputIndex(Integer.parseInt(jElement.getAsJsonObject().get("output_index").toString().replace("\"", "")));
			outputs.addOutput(output);
		}
		return outputs;
	}
	
}
