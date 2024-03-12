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
package io.sapl.demo.jwt.authorizationserver.jose;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
final class KeyGeneratorUtils {

    @SneakyThrows
    static SecretKey generateSecretKey() {
        return KeyGenerator.getInstance("HmacSha256").generateKey();
    }

    @SneakyThrows
    static KeyPair generateRsaKey() {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    @SneakyThrows
    static KeyPair generateEcKey() {
        EllipticCurve   ellipticCurve   = new EllipticCurve(
                new ECFieldFp(new BigInteger(
                        "115792089210356248762697446949407573530086143415290314195533631308867097853951")),
                new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948"),
                new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291"));
        ECPoint         ecPoint         = new ECPoint(
                new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"),
                new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109"));
        ECParameterSpec ecParameterSpec = new ECParameterSpec(ellipticCurve, ecPoint,
                new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"), 1);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(ecParameterSpec);
        return keyPairGenerator.generateKeyPair();
    }

}
