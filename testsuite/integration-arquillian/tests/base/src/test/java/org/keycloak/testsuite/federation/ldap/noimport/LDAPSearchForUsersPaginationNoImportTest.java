/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.keycloak.testsuite.federation.ldap.noimport;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.federation.ldap.AbstractLDAPTest;
import org.keycloak.testsuite.federation.ldap.LDAPTestContext;
import org.keycloak.testsuite.util.LDAPRule;
import org.keycloak.testsuite.util.LDAPTestUtils;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPSearchForUsersPaginationNoImportTest extends AbstractLDAPTest {

    @ClassRule
    public static LDAPRule ldapRule = new LDAPRule();

    @Override
    protected LDAPRule getLDAPRule() {
        return ldapRule;
    }

    @Override
    public boolean isImportEnabled() {
        // always load users from ldap directly
        return false;
    }

    @Override
    protected void afterImportTestRealm() {
        testingClient.server().run(session -> {

            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();

            // Delete all local users to not interfere with federated ones
            session.users().searchForUserStream(appRealm, new HashMap<>()).collect(Collectors.toList()).forEach(u -> session.users().removeUser(appRealm, u));

            // Delete all LDAP users and add some new for testing
            LDAPTestUtils.removeAllLDAPUsers(ctx.getLdapProvider(), appRealm);

            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john", "Some", "Some", "john14@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john00", "john", "Doe", "john0@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john01", "john", "Doe", "john1@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john02", "john", "Doe", "john2@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john03", "john", "Doe", "john3@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john04", "john", "Doe", "john4@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john05", "Some", "john", "john5@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john06", "Some", "john", "john6@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john07", "Some", "john", "john7@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john08", "Some", "john", "john8@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john09", "Some", "john", "john9@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john10", "Some", "Some", "john10@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john11", "Some", "Some", "john11@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john12", "Some", "Some", "john12@email.org", null, "1234");
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), appRealm, "john13", "Some", "Some", "john13@email.org", null, "1234");
        });
    }

    @Test
    public void testPagination() {
        //tests LDAPStorageProvider.searchLDAP(...
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("John Some Doe", 0, 15), hasSize(15));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("John Some Doe", 7, 10), hasSize(8));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("*", null, null), hasSize(15));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("*", null, null), hasSize(15));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("*", 10, 8), hasSize(5));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("*", 0, 10), hasSize(10));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("*", 7, 10), hasSize(8));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("*", 15, 100), hasSize(0));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("*", 14, 2), hasSize(1));

        //tests LDAPStorageProvider.searchLDAP(...
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("John", null, null), hasSize(11));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("John", 10, 8), hasSize(1));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("John", 0, 10), hasSize(10));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("John", 0, 5), hasSize(5));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("John", 2, 10), hasSize(9));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("John", 0, 8), hasSize(8));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("Some", 0, 20), hasSize(10));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search("Some", 10, 20), hasSize(0));

        //tests LDAPStorageProvider.searchLDAPByAttributes(...
        assertThat(adminClient.realm(TEST_REALM_NAME).users().list(), hasSize(15));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().list(10, 8), hasSize(5));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().list(0, 10), hasSize(10));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().list(7, 10), hasSize(8));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().list(15, 100), hasSize(0));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().list(14, 2), hasSize(1));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search(null, "John", null, null, 0, 15), hasSize(5));
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search(null, "Some", "John", null, 0, 15), hasSize(5));        
        assertThat(adminClient.realm(TEST_REALM_NAME).users().search(null, "Some", "John", null, 2, 15), hasSize(3));        
    }

    @Test
    public void testReturnedOrder() {
        List<String> firstFive = adminClient.realm(TEST_REALM_NAME).users().search("*", 0, 5).stream().map(UserRepresentation::getUsername).collect(Collectors.toList());
        List<String> secondFive = adminClient.realm(TEST_REALM_NAME).users().search("*", 5, 5).stream().map(UserRepresentation::getUsername).collect(Collectors.toList());
        List<String> thirdFive = adminClient.realm(TEST_REALM_NAME).users().search("*", 10, 5).stream().map(UserRepresentation::getUsername).collect(Collectors.toList());

        firstFive.forEach(username -> assertThat(secondFive, not(hasItem(username))));
        firstFive.forEach(username -> assertThat(thirdFive, not(hasItem(username))));

        secondFive.forEach(username -> assertThat(firstFive, not(hasItem(username))));
        secondFive.forEach(username -> assertThat(thirdFive, not(hasItem(username))));

        thirdFive.forEach(username -> assertThat(firstFive, not(hasItem(username))));
        thirdFive.forEach(username -> assertThat(secondFive, not(hasItem(username))));
    }
}
