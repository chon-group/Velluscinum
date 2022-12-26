package group.chon.velluscinum;

import java.io.Serializable;
public class PublicKeyAttributes implements Serializable {
    private static final long serialVersionUID = -299482035708790407L;

    private int b;

    private byte[] Q;

    private byte[] d;

    private byte[] I;

    private byte[] s;

    public PublicKeyAttributes(int b, byte[] q, byte[] d, byte[] i, byte[] s) {
        this.b = b;
        Q = q;
        this.d = d;
        I = i;
        this.s = s;
    }

    public int getB() {
        return b;
    }

    public byte[] getQ() {
        return Q;
    }

    public byte[] getD() {
        return d;
    }

    public byte[] getI() {
        return I;
    }

    public byte[] getS() {
        return s;
    }
}
