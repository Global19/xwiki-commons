/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.crypto.internal.asymmetric.generator;

import java.security.SecureRandom;

import javax.inject.Provider;

import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xwiki.component.util.DefaultParameterizedType;
import org.xwiki.crypto.KeyParametersGenerator;
import org.xwiki.crypto.internal.DefaultSecureRandomProvider;
import org.xwiki.crypto.internal.FixedSecureRandomProvider;
import org.xwiki.crypto.internal.asymmetric.BcAsymmetricKeyParameters;
import org.xwiki.crypto.internal.asymmetric.keyfactory.BcDHKeyFactory;
import org.xwiki.crypto.params.cipher.asymmetric.AsymmetricKeyPair;
import org.xwiki.crypto.params.generator.asymmetric.DHKeyGenerationParameters;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectComponentManager;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.mockito.MockitoComponentManager;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

// Skipping since this could be very long or blocking due to the amount of entropy needed, DH support is AS IS.
@Disabled
@ComponentTest
// @formatter:off
@ComponentList({
    BcDHKeyParameterGenerator.class,
    BcDHKeyFactory.class,
    DefaultSecureRandomProvider.class
})
// @formatter:on
class BcDHKeyPairGeneratorTest
{
    @InjectMockComponents
    private BcDHKeyPairGenerator generator;

    @InjectComponentManager
    private MockitoComponentManager componentManager;

    private KeyParametersGenerator parameterGenerator;

    @BeforeEach
    void configure() throws Exception
    {
        parameterGenerator = this.componentManager.getInstance(KeyParametersGenerator.class, "DH");

        // Reinitialize the random source
        Provider<SecureRandom> rndprov =
            this.componentManager.getInstance(new DefaultParameterizedType(null, Provider.class, SecureRandom.class));
        if (rndprov instanceof FixedSecureRandomProvider) {
            ((FixedSecureRandomProvider) rndprov).initialize();
        }
    }

    @Test
    void generateWithoutArgument()
    {
        AsymmetricKeyPair kp1 = generator.generate();
        AsymmetricKeyPair kp2 = generator.generate();

        assertThat(kp1, not(nullValue()));
        assertThat(kp2, not(nullValue()));
        assertThat(kp1.getPrivate(), instanceOf(BcAsymmetricKeyParameters.class));
        assertThat(kp2.getPrivate(), instanceOf(BcAsymmetricKeyParameters.class));
        assertThat(kp1.getPublic(), instanceOf(BcAsymmetricKeyParameters.class));
        assertThat(kp2.getPublic(), instanceOf(BcAsymmetricKeyParameters.class));
        assertThat(((BcAsymmetricKeyParameters) kp1.getPrivate()).getParameters(), instanceOf(
            DHPrivateKeyParameters.class));
        assertThat(((BcAsymmetricKeyParameters) kp2.getPrivate()).getParameters(), instanceOf(
            DHPrivateKeyParameters.class));
        assertThat(((BcAsymmetricKeyParameters) kp1.getPublic()).getParameters(), instanceOf(
            DHPublicKeyParameters.class));
        assertThat(((BcAsymmetricKeyParameters) kp2.getPublic()).getParameters(), instanceOf(
            DHPublicKeyParameters.class));
    }

    @Test
    void generateWithDefaultParameters()
    {
        DHKeyGenerationParameters params = (DHKeyGenerationParameters) parameterGenerator.generate();

        assertThat(params, not(nullValue()));

        AsymmetricKeyPair kp1 = generator.generate(params);

        assertThat(kp1, not(nullValue()));
        assertThat(kp1.getPrivate(), instanceOf(BcAsymmetricKeyParameters.class));
        assertThat(kp1.getPublic(), instanceOf(BcAsymmetricKeyParameters.class));
        assertThat(((BcAsymmetricKeyParameters) kp1.getPrivate()).getParameters(),
            instanceOf(DHPrivateKeyParameters.class));
        assertThat(((BcAsymmetricKeyParameters) kp1.getPublic()).getParameters(),
            instanceOf(DHPublicKeyParameters.class));

        AsymmetricKeyPair kp2 = generator.generate(params);

        assertThat(kp2, not(nullValue()));
        assertThat(kp2.getPrivate(), instanceOf(BcAsymmetricKeyParameters.class));
        assertThat(kp2.getPublic(), instanceOf(BcAsymmetricKeyParameters.class));
        assertThat(((BcAsymmetricKeyParameters) kp2.getPrivate()).getParameters(), instanceOf(
            DHPrivateKeyParameters.class));
        assertThat(((BcAsymmetricKeyParameters) kp2.getPublic()).getParameters(), instanceOf(
            DHPublicKeyParameters.class));
        assertThat(kp2, not(equalTo(kp1)));
    }
}

