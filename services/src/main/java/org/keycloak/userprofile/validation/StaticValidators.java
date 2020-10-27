/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates
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
 */

package org.keycloak.userprofile.validation;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.validation.Validation;
import org.keycloak.userprofile.UserProfileContext;

import java.util.function.BiFunction;

/**
 * @author <a href="mailto:markus.till@bosch.io">Markus Till</a>
 */
public class StaticValidators {
    public static BiFunction<String, UserProfileContext, Boolean> isBlank() {
        return (value, context) ->
                !Validation.isBlank(value);
    }

    public static BiFunction<String, UserProfileContext, Boolean> isEmailValid() {
        return (value, context) ->
                Validation.isBlank(value) || Validation.isEmailValid(value);
    }

    public static BiFunction<String, UserProfileContext, Boolean> userNameExists(KeycloakSession session) {
        return (value, context) ->
                !(context.getCurrentProfile() != null
                        && !value.equals(context.getCurrentProfile().getAttributes().getFirstAttribute(UserModel.USERNAME))
                        && session.users().getUserByUsername(value, session.getContext().getRealm()) != null);
    }

    public static BiFunction<String, UserProfileContext, Boolean> isUserMutable(RealmModel realm) {
        return (value, context) ->
                !(!realm.isEditUsernameAllowed()
                        && context.getCurrentProfile() != null
                        && !value.equals(context.getCurrentProfile().getAttributes().getFirstAttribute(UserModel.USERNAME))
                );
    }

    public static BiFunction<String, UserProfileContext, Boolean> checkUsernameExists(boolean externalCondition) {
        return (value, context) ->
                !(externalCondition && Validation.isBlank(value));
    }


    public static BiFunction<String, UserProfileContext, Boolean> doesEmailExistAsUsername(KeycloakSession session) {
        return (value, context) -> {
            RealmModel realm = session.getContext().getRealm();
            if (!realm.isDuplicateEmailsAllowed()) {
                UserModel userByEmail = session.users().getUserByEmail(value, realm);
                return !(realm.isRegistrationEmailAsUsername() && userByEmail != null && context.getCurrentProfile() != null && !userByEmail.getId().equals(context.getCurrentProfile().getId()));
            }
            return true;
        };
    }

    public static BiFunction<String, UserProfileContext, Boolean> isEmailDuplicated(KeycloakSession session) {
        return (value, context) -> {
            RealmModel realm = session.getContext().getRealm();
            if (!realm.isDuplicateEmailsAllowed()) {
                UserModel userByEmail = session.users().getUserByEmail(value, realm);
                // check for duplicated email
                return !(userByEmail != null && (context.getCurrentProfile() == null || !userByEmail.getId().equals(context.getCurrentProfile().getId())));
            }
            return true;
        };
    }

    public static BiFunction<String, UserProfileContext, Boolean> doesEmailExist(KeycloakSession session) {
        return (value, context) ->
                !(value != null
                        && !session.getContext().getRealm().isDuplicateEmailsAllowed()
                        && session.users().getUserByEmail(value, session.getContext().getRealm()) != null);
    }

}
