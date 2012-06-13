/**
 * END USER LICENSE AGREEMENT (“EULA”)
 *
 * READ THIS AGREEMENT CAREFULLY (date: 9/13/2011):
 * http://www.akiban.com/licensing/20110913
 *
 * BY INSTALLING OR USING ALL OR ANY PORTION OF THE SOFTWARE, YOU ARE ACCEPTING
 * ALL OF THE TERMS AND CONDITIONS OF THIS AGREEMENT. YOU AGREE THAT THIS
 * AGREEMENT IS ENFORCEABLE LIKE ANY WRITTEN AGREEMENT SIGNED BY YOU.
 *
 * IF YOU HAVE PAID A LICENSE FEE FOR USE OF THE SOFTWARE AND DO NOT AGREE TO
 * THESE TERMS, YOU MAY RETURN THE SOFTWARE FOR A FULL REFUND PROVIDED YOU (A) DO
 * NOT USE THE SOFTWARE AND (B) RETURN THE SOFTWARE WITHIN THIRTY (30) DAYS OF
 * YOUR INITIAL PURCHASE.
 *
 * IF YOU WISH TO USE THE SOFTWARE AS AN EMPLOYEE, CONTRACTOR, OR AGENT OF A
 * CORPORATION, PARTNERSHIP OR SIMILAR ENTITY, THEN YOU MUST BE AUTHORIZED TO SIGN
 * FOR AND BIND THE ENTITY IN ORDER TO ACCEPT THE TERMS OF THIS AGREEMENT. THE
 * LICENSES GRANTED UNDER THIS AGREEMENT ARE EXPRESSLY CONDITIONED UPON ACCEPTANCE
 * BY SUCH AUTHORIZED PERSONNEL.
 *
 * IF YOU HAVE ENTERED INTO A SEPARATE WRITTEN LICENSE AGREEMENT WITH AKIBAN FOR
 * USE OF THE SOFTWARE, THE TERMS AND CONDITIONS OF SUCH OTHER AGREEMENT SHALL
 * PREVAIL OVER ANY CONFLICTING TERMS OR CONDITIONS IN THIS AGREEMENT.
 */

package com.akiban.sql.types;

import com.akiban.sql.StandardException;

/** Character set and collation for character types. */
public final class CharacterTypeAttributes
{
    public static enum CollationDerivation {
        NONE, IMPLICIT, EXPLICIT
    }

    private String characterSet;
    private String collation;
    private CollationDerivation collationDerivation;

    private CharacterTypeAttributes(String characterSet,
                                    String collation, 
                                    CollationDerivation collationDerivation) {
        this.characterSet = characterSet;
        this.collation = collation;
        this.collationDerivation = collationDerivation;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public String getCollation() {
        return collation;
    }

    public CollationDerivation getCollationDerivation() {
        return collationDerivation;
    }

    public static CharacterTypeAttributes forCharacterSet(String characterSet) {
        return new CharacterTypeAttributes(characterSet, null, null);
    }

    public static CharacterTypeAttributes forCollation(CharacterTypeAttributes base,
                                                       String collation) {
        String characterSet = null;
        if (base != null)
            characterSet = base.characterSet;
        return new CharacterTypeAttributes(characterSet, 
                                           collation, CollationDerivation.EXPLICIT);
    }

    public static CharacterTypeAttributes mergeCollations(CharacterTypeAttributes ta1,
                                                          CharacterTypeAttributes ta2)
            throws StandardException {
        if ((ta1 == null) || (ta1.collationDerivation == null)) {
            return ta2;
        }
        else if ((ta2 == null) || (ta2.collationDerivation == null)) {
            return ta1;
        }
        else if (ta1.collationDerivation == CollationDerivation.EXPLICIT) {
            if (ta2.collationDerivation == CollationDerivation.EXPLICIT) {
                if (!ta1.collation.equals(ta2.collation))
                    throw new StandardException("Incompatible collations: " +
                                                ta1 + " " + ta1.collation + " and " +
                                                ta2 + " " + ta2.collation);
            }
            return ta1;
        }
        else if (ta2.collationDerivation == CollationDerivation.EXPLICIT) {
            return ta2;
        }
        else if ((ta1.collationDerivation == CollationDerivation.IMPLICIT) &&
                 (ta2.collationDerivation == CollationDerivation.IMPLICIT) &&
                 ta1.collation.equals(ta2.collation)) {
            return ta1;
        }
        else {
            return new CharacterTypeAttributes(null, null, CollationDerivation.NONE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CharacterTypeAttributes)) return false;
        CharacterTypeAttributes other = (CharacterTypeAttributes)o;
        return (((characterSet == null) ?
                 (other.characterSet == null) :
                 characterSet.equals(other.characterSet)) &&
                ((collation == null) ?
                 (other.collation == null) :
                 collation.equals(other.collation)));
    }

    @Override
    public String toString() {
        if ((characterSet == null) && (collation == null)) return "none";
        StringBuilder str = new StringBuilder();
        if (characterSet != null) {
            str.append("CHARACTER SET ");
            str.append(characterSet);
        }
        if (collation != null) {
            if (characterSet != null) str.append(" ");
            str.append("COLLATE ");
            str.append(collation);
        }
        return str.toString();
    }
    
}
