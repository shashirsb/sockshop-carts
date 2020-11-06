/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.carts.atpsoda;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import io.helidon.examples.sockshop.carts.Cart;
import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.Item;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;

import org.eclipse.microprofile.opentracing.Traced;

import static com.mongodb.client.model.Filters.eq;
import static javax.interceptor.Interceptor.Priority.APPLICATION;

///////////////////
import io.helidon.examples.sockshop.carts.atpsoda.AtpSodaProducers;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.opentracing.Traced;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import java.io.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import oracle.soda.rdbms.OracleRDBMSClient;
import oracle.soda.OracleDatabase;
import oracle.soda.OracleCursor;
import oracle.soda.OracleCollection;
import oracle.soda.OracleDocument;
import oracle.soda.OracleException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import org.apache.commons.lang3.StringUtils;
import java.time.LocalDateTime;

import io.helidon.examples.sockshop.carts.atpsoda.AtpSodaProducers;
import com.google.gson.Gson;
import io.helidon.examples.sockshop.carts.atpsoda.*;

/**
 * An implementation of
 * {@link io.helidon.examples.sockshop.carts.CartRepository} that that uses
 * MongoDB as a backend data store.
 */

@ApplicationScoped
@Alternative
@Priority(APPLICATION)
@Traced
public class AtpSodaCartRepository implements CartRepository {

	Cart _cart = new Cart();
	public static AtpSodaProducers asp = new AtpSodaProducers();
	public static OracleDatabase db = asp.dbConnect();

	@Inject
	void AtpSodaCartRepository() {
		try {
			String UserResponse = createData();
			System.out.println(UserResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Cart getOrCreateCart(String customerId) {
		System.out.println("\n");
		System.out.println("---------------------------");
		System.out.println("AtpSodaCartRepository - getOrCreateCart()");
		System.out.println("customerId: " + customerId);

		////////////
		Cart cart = new Cart();

		try {

			// Get a collection with the name "socks".
			// This creates a database table, also named "socks", to store the collection.
			OracleCollection col = this.db.admin().createCollection("carts");

			// Find all documents in the collection.
			OracleDocument oraDoc, resultDoc = null;
			String jsonFormattedString = null;

			OracleDocument filterSpec = this.db.createDocumentFromString("{ \"customerId\" : \"" + customerId + "\"}");
			System.out.println("filterSpec: -------" + filterSpec.getContentAsString());

			resultDoc = col.find().filter(filterSpec).getOne();
			// System.out.println("resultDoc: -------" + resultDoc.getContentAsString());
			System.out.println(resultDoc.equals(null));

			if (resultDoc != null) {

				JSONParser parser = new JSONParser();
				Object obj = parser.parse(resultDoc.getContentAsString());
				JSONObject jsonObject = (JSONObject) obj;
				cart.customerId = jsonObject.get("customerId").toString();
				JSONArray _jsonArraytag = (JSONArray) jsonObject.get("items");

				List<Item> _items = new ArrayList<>();

				// Item(itemId=819e1fbf-8b7e-4f6d-811f-693534916a8b, quantity=1, unitPrice=14.0)

				for (int i = 0; i < _jsonArraytag.size(); i++) {
					Object _obj = parser.parse(_jsonArraytag.get(i).toString());
					JSONObject _jsonObject = (JSONObject) obj;
					_items.add(new Item(_jsonObject.get("itemId").toString(),
							Integer.parseInt(_jsonObject.get("quantity").toString()),
							Float.parseFloat(_jsonObject.get("unitPrice").toString())));
					System.out.println(_items.toString());
					System.out.println((_jsonObject.get("itemId").toString() + ","
							+ Integer.parseInt(_jsonObject.get("quantity").toString()) + ","
							+ Float.parseFloat(_jsonObject.get("unitPrice").toString())));

				}
				cart.items = _items;

			} else {

				System.out.println("------------INSIDE CART INSERTING START---------------");

				String _document = "{\"customerId\":\"" + customerId + "\",\"items\": [] }";
				System.out.println(_document);

				// Create a JSON document.
				OracleDocument doc = this.db.createDocumentFromString(_document);

				// Insert the document into a collection.
				col.insert(doc);
				System.out.println("saveShipment .... 200OK");

				System.out.println("------------INSIDE CART INSERTING END---------------");

			}

			System.out.println("---------------------------");
			System.out.println("\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cart;
	}

	@Override
	public void deleteCart(String customerId) {
		System.out.println("\n");
		System.out.println("---------------------------");
		System.out.println("AtpSodaCartRepository - deleteCart()");
		System.out.println("customerId: " + customerId);

		// carts.deleteOne(eq("customerId", customerId));

		try {

			// Get a collection with the name "socks".
			// This creates a database table, also named "socks", to store the collection.
			OracleCollection col = this.db.admin().createCollection("carts");

			// Find all documents in the collection.
			OracleDocument oraDoc = null;
			String jsonFormattedString = null;

			OracleDocument filterSpec = this.db.createDocumentFromString("{ \"customerId\" : \"" + customerId + "\"}");
			System.out.println("filterSpec: -------" + filterSpec.getContentAsString());

			col.find().filter(filterSpec).remove();

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("---------------------------");
		System.out.println("\n");
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean mergeCarts(String targetId, String sourceId) {
		System.out.println("\n");
		System.out.println("---------------------------");
		System.out.println("AtpSodaCartRepository - mergeCarts()");
		System.out.println("targetId: " + targetId);
		System.out.println("sourceId: " + sourceId);

		// Cart source = carts.findOneAndDelete(eq("customerId", sourceId));
		// if (source != null) {
		// Cart target = getOrCreateCart(targetId);
		// target.merge(source);
		// carts.replaceOne(eq("customerId", targetId), target);
		// return true;
		// }
		// return false;

		try {

			// Get a collection with the name "socks".
			// This creates a database table, also named "socks", to store the collection.
			OracleCollection col = this.db.admin().createCollection("carts");

			// Find all documents in the collection.
			OracleDocument oraDocSource, oraDocTarget, newDoc, resultDoc = null;
			String jsonFormattedString = null;
			Cart source = new Cart();
			Cart target = new Cart();

			OracleDocument filterSpec = this.db.createDocumentFromString("{ \"customerId\" : \"" + sourceId + "\"}");
			System.out.println("filterSpec: -------" + filterSpec.getContentAsString());

			// Gson gson = new Gson();
			// source = gson.fromJson(oraDocSource.getContentAsString(), Cart.class);
			oraDocSource = col.find().filter(filterSpec).getOne();
			col.find().filter(filterSpec).remove();

			if (oraDocSource != null) {

				resultDoc = oraDocSource;
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(resultDoc.getContentAsString());
				JSONObject jsonObject = (JSONObject) obj;
				source.customerId = jsonObject.get("customerId").toString();
				JSONArray _jsonArraytag = (JSONArray) jsonObject.get("items");

				List<Item> _items = new ArrayList<>();

				// Item(itemId=819e1fbf-8b7e-4f6d-811f-693534916a8b, quantity=1, unitPrice=14.0)

				for (int i = 0; i < _jsonArraytag.size(); i++) {
					Object _obj = parser.parse(_jsonArraytag.get(i).toString());
					JSONObject _jsonObject = (JSONObject) obj;
					_items.add(new Item(_jsonObject.get("itemId").toString(),
							Integer.parseInt(_jsonObject.get("quantity").toString()),
							Float.parseFloat(_jsonObject.get("unitPrice").toString())));
				}
				source.items = _items;

				if (source != null) {
					target = getOrCreateCart(targetId);
					target.merge(source);

					OracleDocument filterSpecTarget = this.db
							.createDocumentFromString("{ \"customerId\" : \"" + targetId + "\"}");
					oraDocTarget = col.find().filter(filterSpecTarget).getOne();

					obj = parser.parse(oraDocTarget.getContentAsString());

					JSONObject objCustomerId = new JSONObject();
					objCustomerId.put("customerId", target.customerId.toString());

					JSONArray arrayitems = new JSONArray();
					Collection<Item> items = target.items;
					for (Item item : items) {
						JSONObject objitems = new JSONObject();
						objitems.put("itemId", item.itemId.toString());
						objitems.put("quantity", item.quantity);
						objitems.put("unitPrice", item.unitPrice);
						arrayitems.add(objitems);
					}

					String _document = "{\"customerId\":" + objCustomerId.toString() + ",\"items\":"
							+ arrayitems.toString() + "}";

					newDoc = db.createDocumentFromString(_document);

					resultDoc = col.find().key(oraDocTarget.getKey()).version(oraDocTarget.getVersion())
							.replaceOneAndGet(newDoc);

					System.out.println(resultDoc.getContentAsString());

					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("---------------------------");
		System.out.println("\n");
		return false;
	}

	@Override
	public List<Item> getItems(String cartId) {
		System.out.println("\n");
		System.out.println("---------------------------");
		System.out.println("AtpSodaCartRepository - getItems()");
		System.out.println("cartId: " + cartId);
		System.out.println("---------------------------");
		System.out.println("\n");
		Cart cart = new Cart();
		return getOrCreateCart(cartId).getItems();
	}

	@Override
	public Item getItem(String cartId, String itemId) {
		System.out.println("\n");
		System.out.println("---------------------------");
		System.out.println("AtpSodaCartRepository - getItems()");
		System.out.println("cartId: " + cartId);
		System.out.println("itemId: " + itemId);
		System.out.println("---------------------------");
		System.out.println("\n");
		return getOrCreateCart(cartId).getItem(itemId);
	}

	@Override
	public Item addItem(String cartId, Item item) {
		System.out.println("\n");
		System.out.println("---------------------------");
		System.out.println("AtpSodaCartRepository - addItem()");
		System.out.println("cartId: " + cartId);
		System.out.println("item: " + item);
		// Cart cart = getOrCreateCart(cartId);
		Cart cart = getOrCreateCart(cartId);
		System.out.println("cart: " + cart);
		Item result = cart.add(item);
		System.out.println("result: " + result);

		OracleDocument outputDoc = this.replaceOne("customerId", cartId, cart);
		System.out.println("outputDoc :" + outputDoc);
		System.out.println("---------------------------");
		System.out.println("\n");
		return result;
	}

	@Override
	public Item updateItem(String cartId, Item item) {

		Cart cart = getOrCreateCart(cartId);
		Item result = cart.update(item);
		System.out.println("\n");
		System.out.println("---------------------------");
		System.out.println("AtpSodaCartRepository - updateItem()");
		System.out.println("cart: " + cart);
		System.out.println("result: " + result);
		System.out.println("cartId: " + cartId);
		System.out.println("item: " + item);
		System.out.println("---------------------------");
		System.out.println("\n");
		// carts.replaceOne(eq("customerId", cartId), cart);
		OracleDocument outputDoc = this.replaceOne("customerId", cartId, cart);
		System.out.println("outputDoc :" + outputDoc);

		return result;
	}

	@Override
	public void deleteItem(String cartId, String itemId) {
		Cart cart = getOrCreateCart(cartId);
		System.out.println("\n");
		System.out.println("---------------------------");
		System.out.println("AtpSodaCartRepository - deleteItem()");
		System.out.println("cartId: " + cartId);
		System.out.println("itemId: " + itemId);
		System.out.println("---------------------------");
		System.out.println("\n");
		cart.remove(itemId);
		// carts.replaceOne(eq("customerId", cartId), cart);

		OracleDocument outputDoc = this.replaceOne("customerId", cartId, cart);
		System.out.println("outputDoc :" + outputDoc);
	}

	public String createData() {

		try {
			OracleCollection col = this.db.admin().createCollection("carts");
			col.admin().truncate();
		} catch (OracleException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "successfully created carts collection !!!";
	}

	@SuppressWarnings("unchecked")
	public OracleDocument replaceOne(String key, String value, Cart cart) {

		System.out.println("\n");
		System.out.println("---------------------------");
		System.out.println("AtpSodaCartRepository - replaceOne()");
		System.out.println("key: " + key);
		System.out.println("value: " + value);

		OracleDocument returDoc;

		try {
			System.out.println("1---------------------------");
			// Get a collection with the name "socks".
			// This creates a database table, also named "socks", to store the collection.
			OracleCollection col = this.db.admin().createCollection("carts");
			System.out.println("2---------------------------");
			// Find all documents in the collection.
			OracleDocument oraDocTarget, newDoc, resultDoc = null;
			System.out.println("3---------------------------");

			System.out.println("4---------------------------{ \"" + key + "\" : \"" + value + "\"}");

			OracleDocument filterSpecTarget = this.db
					.createDocumentFromString("{ \"" + key + "\" : \"" + value + "\"}");
			System.out.println("5---------------------------" + filterSpecTarget.getContentAsString());
			oraDocTarget = col.find().filter(filterSpecTarget).getOne();

			System.out.println("6---------------------------" + oraDocTarget.getContentAsString());
			System.out.println("6---------------------------" + oraDocTarget.getKey());
			System.out.println("6---------------------------" + oraDocTarget.getVersion());
			System.out.println("\n");

			JSONObject objCustomerId = new JSONObject();
			objCustomerId.put("customerId", "\"" + value + "\"");

			JSONArray arrayitems = new JSONArray();
			Collection<Item> items = cart.items;
			for (Item item : items) {
				JSONObject objitems = new JSONObject();
				objitems.put("itemId", item.itemId.toString());
				objitems.put("quantity", item.quantity);
				objitems.put("unitPrice", item.unitPrice);
				arrayitems.add(objitems);
			}

			String _document = "{\"customerId\":" + objCustomerId.toString() + ",\"items\":" + arrayitems.toString()
					+ "}";

			newDoc = db.createDocumentFromString(_document);

			System.out.println("7---------------------------" + newDoc.toString());
			System.out.println(oraDocTarget.getKey().toString());
			System.out.println(oraDocTarget.getVersion().toString());
			resultDoc = col.insertAndGet(newDoc);
			if (resultDoc.getContentAsString() != null) {
				col.find().key(oraDocTarget.getKey()).version(oraDocTarget.getVersion()).remove();
			}
			System.out.println("8---------------------------");
			System.out.println(resultDoc);
			System.out.println("9--------------------------");
			System.out.println("---------------------------");
			System.out.println("\n");
			returDoc = resultDoc;

		} catch (Exception e) {
			System.out.println("Exception ---------------------------");
			e.printStackTrace();
			System.out.println("Exception ---------------------------");
			returDoc = null;
		}

		return returDoc;

	}

}