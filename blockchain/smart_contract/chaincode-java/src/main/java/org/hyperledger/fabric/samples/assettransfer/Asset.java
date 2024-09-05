package org.hyperledger.fabric.pecs.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Asset {

    @Property()
    private final String privacyPolicy;

    @Property()
    private final String digestPrivacyPolicy;

    @Property()
    private final String publicKey;

    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    public String getDigestPrivacyPolicy() {
        return digestPrivacyPolicy;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Asset(@JsonProperty("privacyPolicy") final String privacyPolicy, @JsonProperty("digestPrivacyPolicy") final String digestPrivacyPolicy,
    @JsonProperty("publicKey") final String publicKey) {
        this.privacyPolicy = privacyPolicy;
        this.digestPrivacyPolicy = digestPrivacyPolicy;
        this.publicKey = publicKey;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Asset other = (Asset) obj;

        return Objects.deepEquals(
                new String[] {getPrivacyPolicy(), getDigestPrivacyPolicy(), getPublicKey()},
                new String[] {other.getPrivacyPolicy(), other.getDigestPrivacyPolicy(), other.getPublicKey()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrivacyPolicy(), getDigestPrivacyPolicy(), getPublicKey());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [privacyPolicy=" + privacyPolicy
                + ", digestPrivacyPolicy=" + digestPrivacyPolicy + ", publicKey=" + publicKey + "]";
    }
}
