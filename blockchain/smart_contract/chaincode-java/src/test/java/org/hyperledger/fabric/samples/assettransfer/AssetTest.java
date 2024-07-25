package org.hyperledger.fabric.pecs.assettransfer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class AssetTest {

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            Asset asset = new Asset("privacyPolicy1", "digestPrivacyPolicy1", "publicKey1");

            assertThat(asset).isEqualTo(asset);
        }

        @Test
        public void isSymmetric() {
            Asset assetA = new Asset("privacyPolicy1", "digestPrivacyPolicy1", "publicKey1");
            Asset assetB = new Asset("privacyPolicy1", "digestPrivacyPolicy1", "publicKey1");

            assertThat(assetA).isEqualTo(assetB);
            assertThat(assetB).isEqualTo(assetA);
        }

        @Test
        public void isTransitive() {
            Asset assetA = new Asset("privacyPolicy1", "digestPrivacyPolicy1", "publicKey1");
            Asset assetB = new Asset("privacyPolicy1", "digestPrivacyPolicy1", "publicKey1");
            Asset assetC = new Asset("privacyPolicy1", "digestPrivacyPolicy1", "publicKey1");

            assertThat(assetA).isEqualTo(assetB);
            assertThat(assetB).isEqualTo(assetC);
            assertThat(assetA).isEqualTo(assetC);
        }

        @Test
        public void handlesInequality() {
            Asset assetA = new Asset("privacyPolicy1", "digestPrivacyPolicy1", "publicKey1");
            Asset assetB = new Asset("privacyPolicy2", "digestPrivacyPolicy2", "publicKey2");

            assertThat(assetA).isNotEqualTo(assetB);
        }

        @Test
        public void handlesOtherObjects() {
            Asset assetA = new Asset("privacyPolicy1", "digestPrivacyPolicy1", "publicKey1");
            String assetB = "not a asset";

            assertThat(assetA).isNotEqualTo(assetB);
        }

        @Test
        public void handlesNull() {
            Asset asset = new Asset("privacyPolicy1", "digestPrivacyPolicy1", "publicKey1");

            assertThat(asset).isNotEqualTo(null);
        }
    }

}
