package com.auth8.persistent.auto;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

/**
 * Class _User was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _User extends CayenneDataObject {

    @Deprecated
    public static final String PRIVATE_KEY_PROPERTY = "privateKey";
    @Deprecated
    public static final String PUBLIC_KEY_PROPERTY = "publicKey";

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<String> PRIVATE_KEY = new Property<String>("privateKey");
    public static final Property<String> PUBLIC_KEY = new Property<String>("publicKey");

    public void setPrivateKey(String privateKey) {
        writeProperty("privateKey", privateKey);
    }
    public String getPrivateKey() {
        return (String)readProperty("privateKey");
    }

    public void setPublicKey(String publicKey) {
        writeProperty("publicKey", publicKey);
    }
    public String getPublicKey() {
        return (String)readProperty("publicKey");
    }

}
