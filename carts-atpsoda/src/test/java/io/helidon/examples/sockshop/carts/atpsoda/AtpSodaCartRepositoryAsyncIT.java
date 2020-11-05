/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.carts.atpsoda;

import io.helidon.examples.sockshop.carts.CartRepository;
import io.helidon.examples.sockshop.carts.CartRepositoryTest;
import io.helidon.examples.sockshop.carts.SyncCartRepository;

// import static io.helidon.examples.sockshop.carts.atpsoda.AtpSodaProducers.asyncCarts;
// import static io.helidon.examples.sockshop.carts.atpsoda.AtpSodaProducers.asyncClient;
// import static io.helidon.examples.sockshop.carts.atpsoda.AtpSodaProducers.asyncDb;

/**
 * Integration tests for {@link MongoCartRepositoryAsync}.
 */
class AtpSodaCartRepositoryAsyncIT extends CartRepositoryTest {
    public CartRepository getCartRepository() {
        // String host = System.getProperty("db.host","localhost");
        // int    port = Integer.parseInt(System.getProperty("db.port","27017"));

        // return new SyncCartRepository(new AtpSodaCartRepositoryAsync(asyncCarts(asyncDb(asyncClient(host, port)))));
        return null;
    }
}
