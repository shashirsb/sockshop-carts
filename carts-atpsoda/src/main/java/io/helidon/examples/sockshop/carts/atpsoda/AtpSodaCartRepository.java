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

/**
 * An implementation of {@link io.helidon.examples.sockshop.carts.CartRepository}
 * that that uses MongoDB as a backend data store.
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION)
@Traced
public class AtpSodaCartRepository implements CartRepository {

    private MongoCollection<Cart> carts;

    @Inject
    AtpSodaCartRepository(MongoCollection<Cart> carts) {
        this.carts = carts;
    }

    @PostConstruct
    void configure() {
        System.our.println("AtpSodaCartRepository - configure()");
        System.out.println("");
        carts.createIndex(Indexes.hashed("customerId"));
    }

    @Override
    public Cart getOrCreateCart(String customerId) {
        System.out.println("\n");
        System.out.println("---------------------------");
        System.our.println("AtpSodaCartRepository - getOrCreateCart()");
        System.out.println("customerId: " + customerId);
        Cart cart = carts.find(eq("customerId", customerId)).first();
        System.out.println("cart: " + cart);
        if (cart == null) {
            cart = new Cart(customerId);
            carts.insertOne(cart);
        }
        System.out.println("---------------------------");
        System.out.println("\n");
        return cart;
    }

    @Override
    public void deleteCart(String customerId) {
        carts.deleteOne(eq("customerId", customerId));
    }

    @Override
    public boolean mergeCarts(String targetId, String sourceId) {
        System.out.println("\n");
        System.out.println("---------------------------");
        System.our.println("AtpSodaCartRepository - mergeCarts()");
        System.out.println("targetId: " + targetId);
        System.out.println("sourceId: " + sourceId);

        Cart source = carts.findOneAndDelete(eq("customerId", sourceId));
        System.out.println("source: " + source);
        if (source != null) {
            Cart target = getOrCreateCart(targetId);
            target.merge(source);
            carts.replaceOne(eq("customerId", targetId), target);
            return true;
        }

        System.out.println("---------------------------");
        System.out.println("\n");
        return false;
    }

    @Override
    public List<Item> getItems(String cartId) {
        System.out.println("\n");
        System.out.println("---------------------------");
        System.our.println("AtpSodaCartRepository - getItems()");
        System.out.println("cartId: " + cartId);
        System.out.println("---------------------------");
        System.out.println("\n");
        return getOrCreateCart(cartId).getItems();
    }

    @Override
    public Item getItem(String cartId, String itemId) {
        System.out.println("\n");
        System.out.println("---------------------------");
        System.our.println("AtpSodaCartRepository - getItems()");
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
        System.our.println("AtpSodaCartRepository - addItem()");
        System.out.println("cartId: " + cartId);
        System.out.println("item: " + item);
        
        Cart cart = getOrCreateCart(cartId);
        System.out.println("cart: " + cart);
        Item result = cart.add(item);
        System.out.println("result: " + result);

        carts.replaceOne(eq("customerId", cartId), cart);
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
        System.our.println("AtpSodaCartRepository - updateItem()");
        System.out.println("cart: " + cart);
        System.out.println("result: " + result);
        System.out.println("cartId: " + cartId);
        System.out.println("item: " + item);
        System.out.println("---------------------------");
        System.out.println("\n");

        carts.replaceOne(eq("customerId", cartId), cart);
        return result;
    }

    @Override
    public void deleteItem(String cartId, String itemId) {
        Cart cart = getOrCreateCart(cartId);
        System.out.println("\n");
        System.out.println("---------------------------");
        System.our.println("AtpSodaCartRepository - deleteItem()");
        System.out.println("cartId: " + cartId);
        System.out.println("itemId: " + itemId);
        System.out.println("---------------------------");
        System.out.println("\n");
        cart.remove(itemId);
        carts.replaceOne(eq("customerId", cartId), cart);
    }
}
