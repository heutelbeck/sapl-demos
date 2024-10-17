/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.demo.jwt.authorizationserver.keyserver;

import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.nimbusds.jose.JOSEException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PublicKeyController {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final KeyRepository keyRepo;

    @GetMapping(path = "/public-key/{keyId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getPublicKey(@PathVariable String keyId) throws JOSEException {
        return publicKey(keyId);
    }

    private String publicKey(String keyId) throws JOSEException {
        final var key = keyRepo.findById(keyId);
        if (key.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find key with ID " + keyId);
        return ENCODER.encodeToString(key.get().toRSAKey().toRSAPublicKey().getEncoded());
    }

}
