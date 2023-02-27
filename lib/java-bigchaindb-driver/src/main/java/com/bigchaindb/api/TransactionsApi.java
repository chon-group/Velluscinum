/*
 * Copyright BigchainDB GmbH and BigchainDB contributors
 * SPDX-License-Identifier: (Apache-2.0 AND CC-BY-4.0)
 * Code is Apache-2.0 and docs are CC-BY-4.0
 */
package com.bigchaindb.api;

import com.bigchaindb.constants.BigchainDbApi;
import com.bigchaindb.constants.Operations;
import com.bigchaindb.exceptions.TransactionNotFoundException;
import com.bigchaindb.model.BigChainDBGlobals;
import com.bigchaindb.model.GenericCallback;
import com.bigchaindb.model.Transaction;
import com.bigchaindb.model.Transactions;
import com.bigchaindb.util.JsonUtils;
import com.bigchaindb.util.NetworkUtils;

import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The Class TransactionsApi.
 */
public class TransactionsApi extends AbstractApi {

	private static final Logger log = LoggerFactory.getLogger( TransactionsApi.class );
	
	/**
	 * Send transaction.
	 *
	 * @param transaction
	 *            the transaction
	 * @param callback
	 *            the callback
	 */
	public static void sendTransaction(Transaction transaction, final GenericCallback callback) {
		log.debug( "sendTransaction Call :" + transaction );
		RequestBody body = RequestBody.create(JSON, transaction.toString());
		NetworkUtils.sendPostRequest(BigChainDBGlobals.getBaseUrl() + BigchainDbApi.TRANSACTIONS, body, callback);
	}

	/**
	 * Sends the transaction.
	 *
	 * @param transaction
	 *            the transaction
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void sendTransaction(Transaction transaction) throws IOException {
		log.debug( "sendTransaction Call :" + transaction );
		RequestBody body = RequestBody.create(JSON, JsonUtils.toJson(transaction));
		Response response = NetworkUtils.sendPostRequest(BigChainDBGlobals.getBaseUrl() + BigchainDbApi.TRANSACTIONS, body);
		response.close();
	}

	/**
	 * Gets the transaction by id.
	 *
	 * @param id
	 *            the id
	 * @return the transaction by id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Transaction getTransactionById(String id)
			throws IOException, TransactionNotFoundException {
		log.debug( "getTransactionById Call :" + id );
		Response response = NetworkUtils.sendGetRequest(BigChainDBGlobals.getBaseUrl() + BigchainDbApi.TRANSACTIONS + "/" + id);
		if(!response.isSuccessful()){
			if(response.code() == HttpStatus.SC_NOT_FOUND)
				throw new TransactionNotFoundException("Transaction with id " + id + " not present");
		}
		String body = response.body().string();
		response.close();
		return JsonUtils.fromJson(body, Transaction.class);
	}

	/**
	 * Gets the transactions by asset id.
	 *
	 * @param assetId
	 *            the asset id
	 * @param operation
	 *            the operation
	 * @return the transactions by asset id
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Transactions getTransactionsByAssetId(String assetId, Operations operation)
			throws IOException {
		log.debug( "getTransactionsByAssetId Call :" + assetId + " operation " + operation );
		Response response = NetworkUtils.sendGetRequest(
				BigChainDBGlobals.getBaseUrl() + BigchainDbApi.TRANSACTIONS + "?asset_id=" + assetId + "&operation=" + operation);
		String body = response.body().string();
		response.close();
		return JsonUtils.fromJson(body, Transactions.class);
	}
}
