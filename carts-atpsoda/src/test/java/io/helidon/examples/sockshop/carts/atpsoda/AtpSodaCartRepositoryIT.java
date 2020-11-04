/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.carts.atpsoda;

import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.CartRepositoryTest;

import static io.helidon.examples.sockshop.carts.atpsoda.AtpSodaProducers.*;

/**
 * Integration tests for {@link io.helidon.examples.sockshop.carts.mongo.MongoCartRepository}.
 */
class AtpSodaCartRepositoryIT extends CartRepositoryTest {
    public CartRepository getCartRepository() {
        String host = System.getProperty("db.host","localhost");
        int    port = Integer.parseInt(System.getProperty("db.port","27017"));

        return new AtpSodaCartRepository(carts(db(client(host, port))));
    }
}
